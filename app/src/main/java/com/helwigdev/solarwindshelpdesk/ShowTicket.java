package com.helwigdev.solarwindshelpdesk;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ShowTicket extends Activity implements AsyncResponse{
	
	TextView tvShowTicket;
	int id;
	Ticket ticket = null;
	SharedPreferences settings;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		settings = getSharedPreferences(Login.PREFS_NAME, 0);
		setContentView(R.layout.activity_show_ticket);
		tvShowTicket = (TextView) findViewById(R.id.tvShowTicket);
		Intent i = getIntent();
		id = i.getIntExtra("id", 0);
		pDialog = ProgressDialog.show(this, "Working...", "Finding ticket details");
		//get ticket object from server
		String sessionKey = settings.getString("sessionKey", "0");
		String request = Login.getRequestAddress + "/ra/Tickets/" + id + "?sessionKey=" + sessionKey;
		String cookie = settings.getString("Set-Cookie", "NO COOKIE SAVED");
		JsonObjectReader.cookieString = cookie;
		JsonObjectReader j = new JsonObjectReader(this);
		j.delegate = this;
		j.execute(request);
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_ticket, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} 
		return super.onOptionsItemSelected(item);
	}



	@Override
	public void processFinish(Object output) {
		// TODO Auto-generated method stub
		pDialog.dismiss();
		try {
			ticket = new Ticket((JSONObject) output);
			tvShowTicket.setText(Html.fromHtml(ticket.toString()));
		} catch (JSONException e) {
			Toast.makeText(this, "Invalid ticket number", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			finish();
		}
	}
}
