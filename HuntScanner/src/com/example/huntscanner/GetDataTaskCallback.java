package com.example.huntscanner;

public interface GetDataTaskCallback {
	public void bookDataReceived(BookData bd, boolean error, int handle);
	public void shelfDataReceived(BookData[] shelf, boolean error, int handle);
}
