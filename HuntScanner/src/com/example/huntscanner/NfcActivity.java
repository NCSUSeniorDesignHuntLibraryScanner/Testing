package com.example.huntscanner;

import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
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
	
	public static void setScanMode(ScanMode sm) {
		scanMode = sm;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
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
				switch(orderChecker.nextTag(td)) {
				case CORRECT:
					System.out.println("Correct book scanned");
					break;
				case END_OF_SHELF:
					System.out.println("End of shelf has been reached");
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
					break;
				case SAME_BOOK:
					System.out.println("Duplicate scan, ignoring");
					break;
				}
			}
			else {
				System.out.println("Need shelf tag first");
			}
		}
	}

	@Override
	public void bookDataReceived(BookData bd, boolean error, int handle) {
		if(!error && bd != null) {
			System.out.println("Received BookData: " + bd.toString());
			
			if(scanMode == ScanMode.SINGLE) {
				System.out.println("Updating BookScan activity");
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
			
			if(scanMode == ScanMode.SINGLE) {
				System.out.println("Received shelf data in single book mode - ignoring");
			}
			else if(scanMode == ScanMode.SHELF) {
				if(handle == HANDLE_MISPLACED_BOOK) {
					// Get left and right books
					for(int i=0; i<shelf.length; i++) {
						if(lastTag.id == shelf[i].id) {
							BookData leftBook = (i > 0) ? shelf[i-1] : null;
							BookData rightBook = (i < shelf.length-1) ? shelf[i+1] : null;
							System.out.println("leftBook=" + leftBook + " rightBook=" + rightBook);
						}
					}
				}
				else {
					System.out.println("Creating new OrderChecker");
					orderChecker = new OrderChecker(shelf);
				}
			}
		}
		else {
			System.out.println("Something went wront in NfcActivity.shelfDataReceived()");
		}
	}
}
