package com.helwigdev.solarwindshelpdesk;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class JsonArrayReader extends AsyncTask<String, Void, JSONArray>{
	
	public JsonArrayReader(Context c){
		this.c = c;
	}
	
	public JsonArrayReader(){}
	

	public AsyncResponse delegate = null;
	static Context c;
	static public String cookieString = null;

	public static JSONArray readJsonFromUrl(String url) throws IOException,
			JSONException {
		
		SharedPreferences settings = c.getSharedPreferences(Login.PREFS_NAME, 0);

		DefaultHttpClient httpclient = new DefaultHttpClient();
		url = url.replace(" ", "");
		String targetString = settings.getString("httpHostAddress", "");
		Log.d("Target string", targetString);
		HttpHost target = new HttpHost(targetString, 443, "https");
		Log.d("URL Request", url);
		HttpGet get = new HttpGet(url);
		if (cookieString != null) {
			get.setHeader("Cookie", cookieString);
		}
		HttpResponse httpResponse = httpclient.execute(target, get);
		HttpEntity entity = httpResponse.getEntity();
		int code = httpResponse.getStatusLine().getStatusCode();
		Log.d("HTTP Response", code + " " + httpResponse.getStatusLine().getReasonPhrase());
		Header[] headers = httpResponse.getAllHeaders();

		for (Header h : headers) {
			Log.d("Header reponse", h.getName() + " " + h.getValue());
		}
		
		//TODO generate error json for various http response errors and no network connection
		
		try {
			
			if(code != 200){//if response error, we must send that back
				JSONArray o = new JSONArray();
				o.put(new JSONObject().put("Type", "Error").put("errType", code));
				
				return o;
			}

			String jsonText = EntityUtils.toString(entity);
			Log.d("JsonArrayReader", "Recieved data:" + jsonText);
			JSONArray json = new JSONArray(jsonText);
			
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		// TODO Auto-generated method stub

		try {
			return readJsonFromUrl(params[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(JSONArray result) {
		delegate.processFinish(result);
	}
}
