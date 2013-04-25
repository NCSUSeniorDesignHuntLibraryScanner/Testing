package com.example.huntscanner;

public class OrderChecker {
	
	enum ScanResult {
		CORRECT,
		OUT_OF_ORDER,
		END_OF_SHELF,
		SAME_BOOK,
	}

	private BookData[] shelf;
	private int index = 0;
	
	public OrderChecker(BookData[] shelf) {
		this.shelf = shelf;
	}
	
	public ScanResult nextTag(TagData tag) {
		ScanResult ret;
		
		if(index >= shelf.length) {
			ret = ScanResult.END_OF_SHELF;
		}
		else if(shelf[index].id == tag.id) {
			ret = ScanResult.CORRECT;
		}
		else if(index > 0 && shelf[index-1].id == tag.id) {
			ret = ScanResult.SAME_BOOK;
		}
		else {
			ret = ScanResult.OUT_OF_ORDER;
		}
		
		if(ret == ScanResult.CORRECT) {
			index++;
		}
		
		return ret;
	}
	
	public BookData leftBook() {
		if(index > 0)
			return shelf[index-1];
		
		return null;
	}
	
	public BookData rightBook() {
		if(index < shelf.length-2)
			return shelf[index+1];
		
		return null;
	}
}
