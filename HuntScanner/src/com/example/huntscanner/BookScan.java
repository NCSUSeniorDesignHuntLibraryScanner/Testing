package com.example.huntscanner;

import com.example.huntscanner.NfcActivity.ScanMode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class BookScan extends Activity {
	
	private TextView title;
	private TextView author;
	private TextView bookshelf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_page);
		
		title = (TextView) findViewById(R.id.title);
		author = (TextView) findViewById(R.id.author);
		bookshelf = (TextView) findViewById(R.id.bookshelf);
	}
	
	@Override
	protected void onStart()  {
		super.onStart();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		NfcActivity.setScanMode(ScanMode.SINGLE);
		
		Intent intent = getIntent();
		String title = intent.getStringExtra("EXTRA_TITLE");
		String author = intent.getStringExtra("EXTRA_AUTHOR");
		String bookshelf = intent.getStringExtra("EXTRA_BOOKSHELF");
		
		if(title != null)
			this.title.setText(title);
		if(author != null)
			this.author.setText(author);
		if(bookshelf != null)
			this.bookshelf.setText(bookshelf);
	}
	
	@Override
	public void onStop()  {
		super.onStop();
	}	
}
