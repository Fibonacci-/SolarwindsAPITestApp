package com.helwigdev.solarwindshelpdesk;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchFragment extends Fragment{

	Button bSearch;
	EditText etTicketId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_search, null);

		bSearch = (Button) view.findViewById(R.id.bSearch);
		etTicketId = (EditText) view.findViewById(R.id.etTicketId);
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		etTicketId.requestFocus();
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		bSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String number = etTicketId.getText().toString();
				if (!number.equals("")) {

					
					int ticketNumber = Integer.parseInt(number);
					Log.d("Search", ticketNumber + "");
					Intent i = new Intent(getActivity().getBaseContext(),
							ShowTicket.class);
					i.putExtra("id", ticketNumber);
					getActivity().startActivity(i);

				} else {
					Toast.makeText(getActivity(), "Enter a number first",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		return view;

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etTicketId.getWindowToken(), 0);
	}


}
