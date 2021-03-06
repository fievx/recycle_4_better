package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.View;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.model.FragmentObserver;

public class SettingsFragment extends PreferenceFragment{
	public static final String TAG_WIZARD = "wizard";
	private Activity activity;	
	private Preference wizardPref;
	
	
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
		
		wizardPref = findPreference("element_wizard_preference");
		wizardPref.setOnPreferenceClickListener(new OnPreferenceClickListener (){

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				try{
				((FragmentObserver)getActivity()).update(TAG_WIZARD);
				}
				catch (Exception e){
					e.getStackTrace();
				}
				return true;
			}
			
		});
	}

	
		
}
