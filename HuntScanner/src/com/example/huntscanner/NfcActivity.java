package com.example.huntscanner;

import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.view.View;

public class NfcActivity extends Activity implements GetDataTaskCallback {
	
	enum ScanMode {
		SINGLE,
		SHELF,
		DISABLED
	}
	
	private static final String serverUriString = "http://192.168.43.236:3000/query";
	private static final int HANDLE_SHELF_TAG = 0;
	private static final int HANDLE_MISPLACED_BOOK = 1;
	
	private static ScanMode scanMode = ScanMode.DISABLED;
	private static OrderChecker orderChecker = null;
	private static TagData lastTag = null;
	private Vibrator vibrator;
	
	public static void setScanMode(ScanMode sm) {
		scanMode = sm;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		
		TagData td = getTagData(intent);
		if(td != null) {
			System.out.println("Got TagData object: id=" + td.id + ", type=" + td.type);
			
			lastTag = td;
			
			if(scanMode == ScanMode.SINGLE && td.type == TagData.BOOK) {
				System.out.println("calling singleModeTagHandler()");
				singleModeTagHandler(td);
			}
			else if(scanMode == ScanMode.SHELF) {
				System.out.println("calling shelfModeTagHandler()");
				shelfModeTagHandler(td);
			}
			else {
				System.out.println("scanMode is invalid, ignoring");
			}
		}
		
		finish();
	}
	
	private TagData getTagData(Intent intent) {
		if(intent.getType() != null && intent.getType().equals("application/com.example.huntscanner")) {
			Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			
			String json = null;
			for(Parcelable message : messages) {
				NdefMessage ndefMessage = (NdefMessage) message;
				for(NdefRecord record : ndefMessage.getRecords()) {
					if(record.toMimeType().equals("application/com.example.huntscanner")) {
						json = new String(record.getPayload());
						System.out.println("Read json data from tag: " + json);
						
						TagData tagData = TagData.fromJSON(json);
						return tagData;
					}
				}
			}
		}
		
		return null;
	}
	
	private void singleModeTagHandler(TagData td) {
		try {
			URI uri = new URI(serverUriString);
			GetDataTask gdt = new GetDataTask(this, uri, td.id, GetDataTask.RequestType.BOOK);
			new Thread(gdt).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void shelfModeTagHandler(TagData td) {
		if(td.type == TagData.SHELF) {
			try {
				orderChecker = null;
				URI uri = new URI(serverUriString);	
				GetDataTask gdt = new GetDataTask(this, uri, td.id, GetDataTask.RequestType.SHELF, HANDLE_SHELF_TAG);
				
				new Thread(gdt).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(td.type == TagData.BOOK) { 
			if(orderChecker != null) {
				Intent intent = new Intent(this, ShelfScan.class);
				BookData bd = orderChecker.currentBook();
				
				switch(orderChecker.nextTag(td)) {
				case CORRECT:
					System.out.println("Correct book scanned");
					
					intent.putExtra("PREV_TITLE", bd.title);
					intent.putExtra("PREV_AUTHOR", bd.author);
					intent.putExtra("SCAN_STATUS", "OK");
					intent.putExtra("COLOR_RED", false);
					startActivity(intent);
					vibrator.vibrate(100);
					break;
				case END_OF_SHELF:
					System.out.println("End of shelf has been reached");
					
					intent.putExtra("PREV_TITLE", "");
					intent.putExtra("PREV_AUTHOR", "");
					intent.putExtra("SCAN_STATUS", "DONE");
					intent.putExtra("COLOR_RED", false);
					startActivity(intent);
					
					break;
				case OUT_OF_ORDER:
					System.out.println("Out of order book scanned");
					
					try {
						URI uri = new URI(serverUriString);
						GetDataTask gdt = new GetDataTask(this, uri, td.id, GetDataTask.RequestType.BOOK, HANDLE_MISPLACED_BOOK);
						new Thread(gdt).start();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					intent.putExtra("PREV_TITLE", "");
					intent.putExtra("PREV_AUTHOR", "");
					intent.putExtra("SCAN_STATUS", "MISPLACED");
					intent.putExtra("COLOR_RED", true);
					startActivity(intent);
					vibrator.vibrate(600);
					break;
				case SAME_BOOK:
					System.out.println("Duplicate scan, ignoring");
					break;
				}
			}
			else {
				System.out.println("Need shelf tag first");
				
				Intent intent = new Intent(this, ShelfScan.class);
				intent.putExtra("PREV_TITLE", "");
				intent.putExtra("PREV_AUTHOR", "");
				intent.putExtra("SCAN_STATUS", "SCAN A SHELF");
				intent.putExtra("COLOR_RED", false);
				startActivity(intent);
			}
		}
	}

	@Override
	public void bookDataReceived(BookData bd, boolean error, int handle) {
		if(!error && bd != null) {
			System.out.println("Received BookData: " + bd.toString());
			
			if(scanMode == ScanMode.SINGLE) {
				System.out.println("Updating BookScan activity");
				
				Intent intent = new Intent(this, BookScan.class);
				intent.putExtra("EXTRA_TITLE", bd.title);
				intent.putExtra("EXTRA_AUTHOR", bd.author);
				intent.putExtra("EXTRA_BOOKSHELF", Integer.toString(bd.bookshelf));
				startActivity(intent);
				
				// Load shelf to get left and right books
				try {
					URI uri = new URI(serverUriString);	
					GetDataTask gdt = new GetDataTask(this, uri, bd.bookshelf, GetDataTask.RequestType.SHELF);
					
					new Thread(gdt).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(scanMode == ScanMode.SHELF && handle == HANDLE_MISPLACED_BOOK) {
				try {
					URI uri = new URI(serverUriString);	
					GetDataTask gdt = new GetDataTask(this, uri, bd.bookshelf, GetDataTask.RequestType.SHELF, HANDLE_MISPLACED_BOOK);
					
					new Thread(gdt).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else {
			System.out.println("Something went wront in NfcActivity.bookDataReceived()");
		}
	}

	@Override
	public void shelfDataReceived(BookData[] shelf, boolean error, int handle) {
		if(!error && shelf != null) {
			System.out.println("Received BookData array: ");
			for(BookData bd : shelf) {
				System.out.println(bd.toString());
			}
			
			if(scanMode == ScanMode.SINGLE || (scanMode == ScanMode.SHELF && handle == HANDLE_MISPLACED_BOOK)) {
				// Get left and right books
				for(int i=0; i<shelf.length; i++) {
					if(lastTag.id == shelf[i].id) {
						BookData leftBook = (i > 0) ? shelf[i-1] : null;
						BookData rightBook = (i < shelf.length-1) ? shelf[i+1] : null;
						System.out.println("leftBook=" + leftBook + " rightBook=" + rightBook);
						
						Intent intent = null;
						if(scanMode == ScanMode.SINGLE) {
							intent = new Intent(this, BookScan.class);			
						}
						else if(scanMode == ScanMode.SHELF) {
							intent = new Intent(this, ShelfScan.class);
						}
						
						if(intent != null) {
							String leftTitle = (leftBook != null) ? leftBook.title : null;
							String rightTitle = (rightBook != null) ? rightBook.title : null;
							intent.putExtra("LEFT_TITLE", leftTitle);
							intent.putExtra("RIGHT_TITLE", rightTitle);
							startActivity(intent);
						}
						
						break;
					}
				}
			}
			else if(scanMode == ScanMode.SHELF) {
				System.out.println("Creating new OrderChecker");
				orderChecker = new OrderChecker(shelf);
				
				Intent intent = new Intent(this, ShelfScan.class);
				intent.putExtra("PREV_TITLE", "");
				intent.putExtra("PREV_AUTHOR", "");
				intent.putExtra("SCAN_STATUS", "SHELF");
				intent.putExtra("COLOR_RED", false);
				startActivity(intent);
			}
		}
		else {
			System.out.println("Something went wront in NfcActivity.shelfDataReceived()");
		}
	}
}
