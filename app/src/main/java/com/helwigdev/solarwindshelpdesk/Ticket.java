package com.helwigdev.solarwindshelpdesk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Ticket {
	public Ticket(JSONObject o) throws JSONException {
		jsonTicket = o;
		Log.d("JsonOut",o.toString());
		id = o.getInt("id");
		client = o.getString("displayClient");
		requestType = o.getJSONObject("problemtype").getString(
				"detailDisplayName");
		
		location = o.getJSONObject("location").getString("locationName");
		assignedTech = o.getJSONObject("clientTech").getString("displayName");
		subject = o.getString("subject");
		detail = o.getString("detail");

		JSONArray jNotes = o.getJSONArray("notes");
		notes = new JSONObject[jNotes.length()];
		for (int i = 0; i < jNotes.length(); i++) {
			notes[i] = jNotes.getJSONObject(i);
		}
	}

	int id;
	
	JSONObject jsonTicket;

	String client;// displayClient in JSON
	String requestType;
	String location;
	String assignedTech;
	String subject;
	String detail;
	JSONObject[] notes;
	String[] statusChangeOptions;

	public String toString() {
		String string = id + "\n\r" + client + "\n\r" + requestType + "\n\r"
				+ location + "\n\r" + assignedTech + "\n\r" + subject + "\n\r"
				+ detail;
		for (JSONObject object : notes) {
			try {
				string += object.getString("prettyUpdatedString");
				string += object.getString("mobileNoteText");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return (string);
	}

}
