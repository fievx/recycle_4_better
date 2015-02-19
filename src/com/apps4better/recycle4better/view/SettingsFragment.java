package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import com.apps4better.recycle4better.R;

public class SettingsFragment extends PreferenceFragment{
	private Activity activity;
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.my_preferences);
	}


	/* (non-Javadoc)
	 * @see android.preference.PreferenceFragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		//Need to programatically set the background to a color so it does not appear as transparent
		View view = getView();
		view.setBackgroundColor(Color.WHITE);
		view.setClickable(true);
	}

	
		
}
