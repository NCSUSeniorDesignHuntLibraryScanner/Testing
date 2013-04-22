package com.example.huntscanner;

import com.example.huntscanner.NfcActivity.ScanMode;

import android.app.Activity;
import android.os.Bundle;

public class BookScan extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_page);
	}
	
	@Override
	protected void onStart()  {
		super.onStart();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("BookScan onResume");
		NfcActivity.setScanMode(ScanMode.SINGLE);
	}
	
//	@Override
//	public void onPause(){
//		super.onPause();
//		System.out.println("BookScan onPause");
//		NfcActivity.setScanMode(ScanMode.DISABLED);
//	}
	
	@Override
	public void onStop()  {
		super.onStop();
		
	}
}
