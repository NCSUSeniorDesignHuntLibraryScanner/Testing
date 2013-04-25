package com.example.huntscanner;

import com.example.huntscanner.NfcActivity.ScanMode;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

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
		NfcActivity.setScanMode(ScanMode.SINGLE);
	}
	
	@Override
	public void onStop()  {
		super.onStop();
	}
	
	public void newBook(BookData b) {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(b.title);
		
		TextView author = (TextView) findViewById(R.id.author);
		author.setText(b.author);
		
		TextView bookshelf = (TextView) findViewById(R.id.bookshelf);
		bookshelf.setText(b.bookshelf);
	}
	
}
