package com.example.huntscanner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class BookData {

	public final long id;
	public final String title;
	public final String author;
	public final int bookshelf;
	
	public BookData(long id, String title, String author, int bookshelf) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.bookshelf = bookshelf;
	}
	
	
	/**
	 * Converts this BookData object to a JSON string, or returns null if error.
	 * 
	 * @return
	 */
	public String toJSON() {
		String json = null;
		JSONObject jobject = new JSONObject();
		
		try {
		jobject.put("id", this.id);
		
		json = jobject.toString();
		} catch(Exception e) {
			json = null;
		}
		
		return json;
	}
	
	
	/**
	 * Converts a JSON string into a BookData object, or returns null if error.
	 * @param json
	 * @return
	 */
	public static BookData fromJSON(String json, boolean encapsulatedInArray) {
		BookData bd = null;
		
		try {
			JSONObject jobject = null;
			if(encapsulatedInArray) {
				JSONObject jobject = new JSONObject(json).getJSONArray("1").getJSONObject(0);
			}
			else {
				JSONObject
			}
			
			long id = jobject.getLong("bid");
			String title = jobject.getString("tit");
			String author = jobject.getString("auth");
			int bookshelf = jobject.getInt("bid");
			
			bd = new BookData(id, title, author, bookshelf);
		} catch(Exception e) {
			e.printStackTrace();
			bd = null;
		}
		
		return bd;
	}
	
	
	public static BookData[] fromJSONArray(String json) {
		List<BookData> bookDataList = new LinkedList<BookData>();
		
		try {
			JSONArray jarray = new JSONObject(json).getJSONArray("1");
			
			for(int i=0; i<jarray.length(); i++) {
				bookDataList.add(BookData.fromJSON(jarray.getJSONObject(i).toString()));
			}
		} catch(Exception e) {
			return null;
		}
		
		return (BookData[]) bookDataList.toArray();
	}
	
	@Override
	public String toString() {
		return "id=" + this.id
		   + ", title=" + this.title
		   + ", author=" + this.author
		   + ", bookshelf=" + this.bookshelf;
	}
}
