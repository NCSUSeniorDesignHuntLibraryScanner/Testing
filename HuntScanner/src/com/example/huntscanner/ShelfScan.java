package com.example.huntscanner;

import com.example.huntscanner.NfcActivity.ScanMode;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShelfScan extends Activity {
	
	private TextView prevTitle;
	private TextView prevAuthor;
	private TextView status;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shelf_page);
		
		prevTitle = (TextView) findViewById(R.id.title_shelf);
		prevAuthor = (TextView) findViewById(R.id.author_shelf);
		status = (TextView) findViewById(R.id.status);
	}
	
	protected void onStart()  {
		super.onStart();
		System.out.println("onStart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		NfcActivity.setScanMode(ScanMode.SHELF);
		System.out.println("onResume");
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		System.out.println("onNewIntent");

//		Intent intent = getIntent();
		String title = intent.getStringExtra("PREV_TITLE");
		String author = intent.getStringExtra("PREV_AUTHOR");
		String status = intent.getStringExtra("SCAN_STATUS");
		boolean colorIsRed = intent.getBooleanExtra("COLOR_RED", false);
		
		System.out.println(title + " " + author);
		
		if(title != null){
			prevTitle.setText(title);
		}
		
		if(author != null){
			prevAuthor.setText(author);
		}
		
		if(status != null && !status.equals("")){
			this.status.setText(status);
		}
		
//		if(colorIsRed){
//			this.status.setBackgroundColor(0xff0000);
//		}
//		else{
//			this.status.setBackgroundColor(0x000000);
//		}
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
