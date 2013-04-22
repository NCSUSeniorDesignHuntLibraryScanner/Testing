package com.example.huntscanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.os.AsyncTask;

public class GetDataTask implements Runnable {

	enum RequestType {
		SHELF,
		BOOK
	}
	
	private final GetDataTaskCallback callback;
	private final URI uri;
	private final long id;
	private final RequestType requestType;
	
	public GetDataTask(GetDataTaskCallback cb, URI uri, long id, RequestType requestType) {
		this.callback = cb;
		this.uri = uri;
		this.id = id;
		this.requestType = requestType;
	}
	
	@Override
	public void run() {
		boolean error = false;
		BasicNameValuePair postData = null;
		
		switch(requestType) {
		case SHELF:
			postData = new BasicNameValuePair("sid", Long.toString(id));
			break;
		case BOOK:
			postData = new BasicNameValuePair("bid", Long.toString(id));
			break;
		}
		
		ArrayList<BasicNameValuePair> postDataList = new ArrayList<BasicNameValuePair>(1);
		postDataList.add(postData);
		
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost httpPost = new HttpPost(uri);
		
		InputStream is = null;
		String json = null;
		HttpResponse httpResponse = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(postDataList));
			httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			
			br = new BufferedReader(new InputStreamReader(is));
			sb = new StringBuilder();
			String line = null;
			
			while((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			error = true;
		}
		
		json = sb.toString();
		System.out.println("Received json from server: " + json);
		
		switch(requestType) {
		case SHELF:
			callback.shelfDataReceived(BookData.fromJSONArray(json), error);
			break;
		case BOOK:
			callback.bookDataReceived(BookData.fromJSON(json), error);
			break;
		}
	}
}
