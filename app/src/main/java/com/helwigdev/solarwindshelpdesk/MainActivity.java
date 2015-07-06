package com.helwigdev.solarwindshelpdesk;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
	private String[] mCategories;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle = "Solarwinds Help Desk";
	private ActionBarDrawerToggle mDrawerToggle;
	private Boolean killOnExit = false;
	private Boolean firstLaunch = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mCategories = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mCategories));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        if(firstLaunch){
        	selectItem(0);
        	firstLaunch = false;
        	Log.d("Main", "Initial launch detected - setting main screen"); 
        }
        
        mTitle = getActionBar().getTitle(); 
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerToggle.syncState();

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
	}
	
	 /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void selectItem(int position) {
		String title = mCategories[position];
		Fragment fragment;
		switch(position){
		case 0:
			fragment = new MyTicketsFragment();
			break;
		case 1:
			fragment = new SearchFragment();
			
			break;
		default:
			fragment = new Fragment();
			break;
		}
			
		
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();
	    
	    mDrawerList.setItemChecked(position, true);
	    setTitle(title);
	    mDrawerLayout.closeDrawer(mDrawerList);
		
	}
	
	@Override
	public void setTitle(CharSequence title) {
	    mTitle = title;
	    getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {//TODO this is actually to log out
			SharedPreferences.Editor editor = getSharedPreferences(Login.PREFS_NAME, 0).edit();
			editor.remove("sessionKey");
			editor.remove("Set-Cookie");
			JsonObjectReader.cookieString = null;//gotta clear all cookies
			editor.commit();
			killOnExit = true;
			Intent i = new Intent(getApplicationContext(), Login.class);
			startActivity(i);
			//TODO change animation to something more sane
			return true;
		}
		if (mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	        }
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(killOnExit){
			finish();
		}
	}

	
	
}
