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
	
	private static final String serverUriString = "http://192.168.1.146:3000/query";
	private static ScanMode scanMode = ScanMode.DISABLED;
	
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
		try {
			URI uri = new URI(serverUriString);
			GetDataTask gdt = new GetDataTask(this, uri, td.id, GetDataTask.RequestType.SHELF);
			new Thread(gdt).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void bookDataReceived(BookData bd, boolean error) {
		if(scanMode == ScanMode.SINGLE && !error && bd != null) {
			System.out.println("Received BookData: " + bd.toString());
		}
		else {
			System.out.println("Something went wront in NfcActivity.bookDataReceived()");
		}
	}

	@Override
	public void shelfDataReceived(BookData[] shelf, boolean error) {
		if(scanMode == ScanMode.SHELF && !error && shelf != null) {
			System.out.println("Received BookData array: ");
			
			for(BookData bd : shelf) {
				System.out.println(bd.toString());
			}
		}
		else {
			System.out.println("Something went wrong in NfcActivity.shelfDataReceived()");
		}
	}
}
