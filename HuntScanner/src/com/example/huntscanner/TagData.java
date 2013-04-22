package com.example.huntscanner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;

import org.json.JSONObject;

public class TagData {

	public static final int BOOK = 0;
	public static final int SHELF = 1;
	
	public final int type;
	public final long id;

	public TagData(int type, long id) {
		this.type = type;
		this.id = id;
	}
	
	
	/**
	 * Converts this TagData object to a JSON string, or returns null if error.
	 * 
	 * @return
	 */
	public String toJSON() {
		String json = null;
		JSONObject jobject = new JSONObject();
		
		try {
		jobject.put("id", this.id);
		jobject.put("type", this.type);
		
		json = jobject.toString();
		} catch(Exception e) {
			json = null;
		}
		
		return json;
	}
	
	
	/**
	 * Converts a JSON string into a TagData object, or returns null if error.
	 * @param json
	 * @return
	 */
	public static TagData fromJSON(String json) {
		TagData td = null;
		
		try {
			JSONObject jobject = new JSONObject(json);
			
			long id = jobject.getLong("id");
			int type = jobject.getInt("type");
			
			td = new TagData(type, id);
		} catch(Exception e) {
			td = null;
		}
		
		return td;
	}
}
