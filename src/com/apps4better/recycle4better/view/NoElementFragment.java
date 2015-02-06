package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.model.NoElementObserver;

public class NoElementFragment extends Fragment{
	private Activity activity;
	private Button addElementButton;
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ScrollView layout = (ScrollView) inflater.inflate(R.layout.fragment_no_element, container, false);
		addElementButton = (Button) layout.findViewById(R.id.add_element_2);
		
		return layout;
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		addElementButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addElement();
			}
		});
	}
	
	private void addElement (){
		((NoElementObserver)activity).update(ProductDetailActivity.TAG_ADD_ELEMENT);
	}
	
}
