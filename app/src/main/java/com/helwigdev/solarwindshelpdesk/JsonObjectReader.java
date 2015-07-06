package com.helwigdev.solarwindshelpdesk;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

//this class copied from http://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java

public class JsonObjectReader extends AsyncTask<String, Void, JSONObject> {
	
	public JsonObjectReader(Context c){
		JsonObjectReader.c = c;
	}
	
	public JsonObjectReader(){}

	public AsyncResponse delegate = null;
	static Context c;
	static public String cookieString = null;

	public static JSONObject readJsonFromUrl(String url) throws IOException,
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
				JSONObject o = new JSONObject();
				o.put("type","Error");
				o.put("errType", code);
				return o;
			}

			String jsonText = EntityUtils.toString(entity);
			Log.d("JsonReader", "Recieved data:" + jsonText);
			JSONObject json = new JSONObject(jsonText);
			try {
				json.put("Set-Cookie", httpResponse
						.getFirstHeader("Set-Cookie").getValue());
			} catch (NullPointerException e) {
				Log.e("Header response", "No cookie data to put");
			}
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		// TODO Auto-generated method stub

		try {
			return readJsonFromUrl(params[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JSONObject o = new JSONObject();
			
			try {
				o.put("type","Error");
				o.put("errType", Login.SERVER_SETTING_ERROR);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return o;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		delegate.processFinish(result);
	}

	// public static void main(String[] args) throws IOException, JSONException
	// {
	// JSONObject json =
	// readJsonFromUrl("https://graph.facebook.com/19292868552");
	// System.out.println(json.toString());
	// System.out.println(json.get("id"));
	// }
}