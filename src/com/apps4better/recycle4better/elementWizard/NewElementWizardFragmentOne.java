package com.apps4better.recycle4better.elementWizard;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.apps4better.recycle4better.R;

public class NewElementWizardFragmentOne extends Fragment{

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_element_wizard_one, container);
		return layout;
	}

}
