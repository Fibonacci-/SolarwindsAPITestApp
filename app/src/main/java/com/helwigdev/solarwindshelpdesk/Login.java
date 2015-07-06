package com.helwigdev.solarwindshelpdesk;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements AsyncResponse {

	public static final String PREFS_NAME = "SWPrefs";
	public static final int SERVER_SETTING_ERROR = 999999;
	TextView tvLogin;
	EditText etUsername;
	EditText etPassword;
	Button bLogin;
	ProgressDialog pDialog;
	SharedPreferences settings;
	boolean killOnExit = false;

	Context appContext = this;
	JsonObjectReader jReader = new JsonObjectReader(appContext);
	String helpdeskAddress;
	public static final String getRequestAddress = "/helpdesk/WebObjects/Helpdesk.woa";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// find session key (if it exists)
		settings = getSharedPreferences(PREFS_NAME, 0);

		String loginServer = settings.getString("login_server_key", "");

		helpdeskAddress = "https://" + loginServer
				+ "/helpdesk/WebObjects/Helpdesk.woa";

		if (!settings.contains("httpHostAddress")) {
			Editor editor = settings.edit();
			editor.putString("httpHostAddress", loginServer);
			editor.commit();
		}

		if (settings.contains("sessionKey")) {
			// attempt to validate session

			// open pDialog
			pDialog = ProgressDialog.show(appContext, "Working...",
					"Validating session", true, false);
			// request server validation of session key
			String sessionKey = settings.getString("sessionKey", "0");
			Log.d("Session Check", "Stored session key " + sessionKey);

			String validation = getRequestAddress
					+ "/ra/Tickets/8986?sessionKey=" + sessionKey;
			String cookie = settings.getString("Set-Cookie", "NO COOKIE SAVED");
			JsonObjectReader.cookieString = cookie;
			Log.d("Verification", "Testing server with URL " + validation
					+ " and cookie string " + cookie);
			jReader.execute(validation);
			// if fail, continue to login as normal
		}

		jReader.delegate = this;

		tvLogin = (TextView) findViewById(R.id.tvLogin);
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		Button bLogin = (Button) findViewById(R.id.bLogin);

		bLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startLogin();

			}
		});
	}

	private void startLogin() {
		pDialog = ProgressDialog.show(appContext, "Working...", "Logging in",
				true, false);
		String username = etUsername.getText().toString();
		String password = etPassword.getText().toString();
		SharedPreferences.Editor edit = settings.edit();
		edit.putString("username", username);
		edit.commit();
		String loginString = getRequestAddress + "/ra/Session?username="
				+ username + "&password=" + password;

		jReader.execute(loginString);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, LoginSettings.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void processFinish(Object output1) {
		// TODO Auto-generated method stub
		jReader = new JsonObjectReader(appContext);
		jReader.delegate = this;
		JSONObject output = (JSONObject) output1;
		if (output == null) {
			Toast.makeText(getApplicationContext(),
					"Incorrect username or password", Toast.LENGTH_SHORT)
					.show();
			pDialog.dismiss();
			return;
		}
		pDialog.dismiss();

		try {
			if (output.getString("type").toString().equals("Error")) {
				// this is for if the session key authentication fails
				Log.e("Login", "Can't login: error " + output.getInt("errType"));

				if (output.getInt("errType") == SERVER_SETTING_ERROR) {
					Toast.makeText(getApplicationContext(),
							"The server is incorrect or unparseable",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Incorrect username or password: HTTP response "
									+ output.getInt("errType"),
							Toast.LENGTH_SHORT).show();
				}
			} else if (output.getString("type").toString().equals("Session")) {

				String sessionKey = output.getString("sessionKey");
				int instanceId = output.getInt("instanceId");

				SharedPreferences.Editor editor = settings.edit();
				editor.putString("sessionKey", sessionKey);
				editor.putInt("instanceId", instanceId);
				try {

					String setCookie = output.getString("Set-Cookie");
					editor.putString("Set-Cookie", setCookie);
				} catch (Exception e) {
					e.printStackTrace();
				}
				editor.commit();
				Log.d("Login", "Got session key " + sessionKey
						+ "with instance " + instanceId);
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				killOnExit = true;
				startActivity(i);
			} else {
				// must be verification check
				Log.d("Login", "Verification detected, continuing");
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(i);
			}
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(),
					"Unspecified error reading server response",
					Toast.LENGTH_SHORT).show();

			e.printStackTrace();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Take no action
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (killOnExit) {
			finish();
		}
	}

}
