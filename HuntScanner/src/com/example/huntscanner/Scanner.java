package com.example.huntscanner;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Scanner extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        
        Intent intent = getIntent();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	NfcActivity.setScanMode(NfcActivity.ScanMode.DISABLED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scanner, menu);
        return true;
    }
    
    public void shelfScan(View button2){
    	Intent shelf = new Intent(this, ShelfScan.class);
    	startActivity(shelf);
    	
    }
    
    public void bookScan(View button1){
    	Intent book = new Intent(this, BookScan.class);
    	startActivity(book);
    }
 
}


