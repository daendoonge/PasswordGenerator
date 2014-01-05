package com.example.passwordgenerator;
//http://www.vogella.com/articles/AndroidSQLite/article.html
import java.text.ParseException;
import java.util.Random;
import com.example.passwordgenerator.util.SystemUiHider;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends ListActivity {
	private PasswordDataSource datasource;
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 60000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	public void OnClickBtn(View v) throws ParseException
    {
		ArrayAdapter<Password> adapter = (ArrayAdapter<Password>) getListAdapter();
	    Password password = null;
	    switch (v.getId()) {
	    case R.id.AddButton:
	    	Log.w("FullscreenActivity", "Add button clicked");
	    	Random r = new Random();
			String pw = new String();
			char c = (char) (r.nextInt(26) + 'A');
			pw = pw + c;
			for (int i = 0; i < 7; i++)
			{
				c = (char) (r.nextInt(26) + 'a');
				pw = pw + c;
			}
			for (int i = 0; i < 2; i++)
			{
				int j = r.nextInt(9);
				pw = pw + j;
			}	
	      password = datasource.createPassword(pw);
	      adapter.add(password);
	      break;
	    case R.id.DeleteButton:
	    	Log.w("FullscreenActivity", "Delete button clicked");
	      if (getListAdapter().getCount() > 0) {
	        password = (Password) getListAdapter().getItem(0);
	        datasource.deletePassword(password);
	        adapter.remove(password);
	      }
	      break;
	    }
	    adapter.notifyDataSetChanged();
	    
    } 	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final ListView contentView = (ListView) findViewById(android.R.id.list);
		datasource = new PasswordDataSource(this);
	    datasource.open();
	    List<Password> values = datasource.getAllPasswords();
	    ArrayAdapter<Password> adapter = new ArrayAdapter<Password>(this,
	            android.R.layout.simple_list_item_1, values);
	    setListAdapter(adapter);
	    
		// Set up an instance of SystemUiHider to control the system UI for
	 		// this activity.
	 		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
	 				HIDER_FLAGS);
	 		mSystemUiHider.setup();
	 		mSystemUiHider
	 				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
	 					// Cached values.
	 					int mControlsHeight;
	 					int mShortAnimTime;

	 					@Override
	 					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	 					public void onVisibilityChange(boolean visible) {
	 						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
	 							// If the ViewPropertyAnimator API is available
	 							// (Honeycomb MR2 and later), use it to animate the
	 							// in-layout UI controls at the bottom of the
	 							// screen.
	 							if (mControlsHeight == 0) {
	 								mControlsHeight = controlsView.getHeight();
	 							}
	 							if (mShortAnimTime == 0) {
	 								mShortAnimTime = getResources().getInteger(
	 										android.R.integer.config_shortAnimTime);
	 							}
	 							controlsView
	 									.animate()
	 									.translationY(visible ? 0 : mControlsHeight)
	 									.setDuration(mShortAnimTime);
	 						} else {
	 							// If the ViewPropertyAnimator APIs aren't
	 							// available, simply show or hide the in-layout UI
	 							// controls.
	 							controlsView.setVisibility(visible ? View.VISIBLE
	 									: View.GONE);
	 						}

	 						if (visible && AUTO_HIDE) {
	 							// Schedule a hide().
	 							delayedHide(AUTO_HIDE_DELAY_MILLIS);
	 						}
	 					}
	 				});

	 		// Set up the user interaction to manually show or hide the system UI.
	 		contentView.setOnItemClickListener(new OnItemClickListener() {
	 			public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
	 				if (TOGGLE_ON_CLICK) {
	 					mSystemUiHider.toggle();
	 				} else {
	 					mSystemUiHider.show();
	 				}
	 			}

	 		});

	 		mSystemUiHider.show();
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	@Override
	  protected void onResume() {
	    datasource.open();
	    super.onResume();
	  }

	  @Override
	  protected void onPause() {
	    datasource.close();
	    super.onPause();
	  }


}
