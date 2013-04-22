package com.example.huntscanner;

import com.example.huntscanner.NfcActivity.ScanMode;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;

public class ShelfScan extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf_page);
	}
	
	protected void onStart()  {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		NfcActivity.setScanMode(ScanMode.SHELF);
	}
	
//	public void onPause(){
//		super.onPause();
//		System.out.println("ShelfScan onPause");
//		NfcActivity.setScanMode(ScanMode.DISABLED);
//	}
	
	public void onStop()  {
		super.onStop();
	}
}
