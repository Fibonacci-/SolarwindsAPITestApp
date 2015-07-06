package com.helwigdev.solarwindshelpdesk;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyTicketsFragment extends ListFragment implements AsyncResponse {
	// /ra/tickets/mine?sessionKey=...
	public MyTicketsFragment() {

	}

	SharedPreferences settings;
	JsonArrayReader jReader;
	ProgressDialog pDialog;
	private ListView lv;
	ArrayList<Spanned> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		list = new ArrayList<Spanned>();
		jReader = new JsonArrayReader(getActivity());
		jReader.delegate = this;
		settings = getActivity().getSharedPreferences(Login.PREFS_NAME, 0);
		String cookie = settings.getString("Set-Cookie", "NO COOKIE SAVED");
		
		String request = Login.getRequestAddress
				+ "/ra/Tickets/mine?sessionKey="
				+ settings.getString("sessionKey", "");
		JsonArrayReader.cookieString = cookie;

		// pDialog = ProgressDialog.show(getActivity(), "Working...",
		// "Finding your tickets");
		jReader.execute(request);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String username = settings.getString("username", "No user saved");
		getActivity().setTitle("My Tickets : " + username);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		int ticketId = Integer.parseInt(list.get(position).toString()
				.substring(0, 4));
		Log.d("Ticket clicked", String.valueOf(ticketId));
		Intent i = new Intent(getActivity(), ShowTicket.class);
		i.putExtra("id", ticketId);
		startActivity(i);

	}

	@Override
	public void processFinish(Object output) {
		// TODO Auto-generated method stub
		JSONArray array = (JSONArray) output;
		Log.d("MyTickets got data", "Data: " + array.toString());
		// pDialog.dismiss();
		try {
			ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(
					getActivity(), android.R.layout.simple_list_item_1, list);

			setListAdapter(adapter);
			parseAndDisplay(array);
		} catch (NullPointerException e) {
			Log.w("My Tickets", "WARNING: MyTicketsFragment was killed before AsyncTask get ticket list finished");
		}

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		lv = (ListView) getListView();
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(lv.getWindowToken(), 0);
	}

	protected ArrayList<Spanned> parseAndDisplay(JSONArray array) {

		for (int i = 0; i < array.length(); i++) {
			try {
				list.add(Html.fromHtml("<b>"
						+ String.format("%04d",
								array.getJSONObject(i).getInt("id")) + ": </b>"
						+ array.getJSONObject(i).getString("shortDetail")));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (list.size() == 0) {
			list.add(Html.fromHtml("No tickets!"));
		}

		return list;
	}

}
