package com.example.huntscanner;

import com.example.huntscanner.NfcActivity.ScanMode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

public class BookScan extends Activity {
	
	private TextView title;
	private TextView author;
	private TextView bookshelf;
	private TextView leftTitle;
	private TextView rightTitle;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_page);
		
		title = (TextView) findViewById(R.id.title);
		author = (TextView) findViewById(R.id.author);
		bookshelf = (TextView) findViewById(R.id.bookshelf);
		leftTitle = (TextView) findViewById(R.id.left_title);
		rightTitle = (TextView) findViewById(R.id.right_title);
		
	}
	
	@Override
	protected void onStart()  {
		super.onStart();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		NfcActivity.setScanMode(ScanMode.SINGLE);
	}
	
	@Override
	public void onNewIntent(Intent intent) {

		String title = intent.getStringExtra("EXTRA_TITLE");
		String author = intent.getStringExtra("EXTRA_AUTHOR");
		String bookshelf = intent.getStringExtra("EXTRA_BOOKSHELF");
		String leftTitle = intent.getStringExtra("LEFT_TITLE");
		String rightTitle = intent.getStringExtra("RIGHT_TITLE");
		
		if(title != null)
			this.title.setText(title);
		if(author != null)
			this.author.setText(author);
		if(bookshelf != null)
			this.bookshelf.setText(bookshelf);
		if(leftTitle != null)
			this.leftTitle.setText(leftTitle);
		if(rightTitle != null)
			this.rightTitle.setText(rightTitle);
	}
	
	@Override
	public void onStop()  {
		super.onStop();
	}	
}
