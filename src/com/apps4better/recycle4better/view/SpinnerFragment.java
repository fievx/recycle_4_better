package com.apps4better.recycle4better.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.apps4better.recycle4better.R;

public class SpinnerFragment extends Fragment{

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = (View) inflater.inflate(R.layout.fragment_spinner, container, false);
		ProgressBar spinner = (ProgressBar) layout.findViewById(R.id.progressBar1);
		spinner.setVisibility(View.VISIBLE);
		
		return layout;
	}
	
	public void onResume (){
		super.onResume();
	}
	
}
