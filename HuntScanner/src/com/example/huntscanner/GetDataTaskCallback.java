package com.example.huntscanner;

public interface GetDataTaskCallback {
	public void bookDataReceived(BookData bd, boolean error);
	public void shelfDataReceived(BookData[] shelf, boolean error);
}
