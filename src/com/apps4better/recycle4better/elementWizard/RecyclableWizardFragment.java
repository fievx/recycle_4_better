package com.apps4better.recycle4better.elementWizard;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.model.Element;

public class RecyclableWizardFragment extends Fragment{
	private RelativeLayout layout;
	private Button yesButton, noButton, maybeButton;
	private Activity activity;
		
	
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
		layout = (RelativeLayout) inflater.inflate(R.layout.fragment_element_wizard_recycle, container, false);
		
		//get all the buttons
		yesButton = (Button) layout.findViewById(R.id.button_yes);
		noButton = (Button) layout.findViewById(R.id.button_no);
		maybeButton = (Button) layout.findViewById(R.id.button_maybe);
		
		return layout;
	}


	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//set actions on buttons
		yesButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((WizardFragmentObserver)activity).setElementRecyclable(Element.TAG_ELEMENT_RECYCLABE);
			}
			
		});
		noButton.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((WizardFragmentObserver)activity).setElementRecyclable(Element.TAG_ELEMENT_NOT_RECYCLABE);
			}
			
		});
		maybeButton.setOnClickListener(new OnClickListener (){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((WizardFragmentObserver)activity).setElementRecyclable(Element.TAG_ELEMENT_MAYBE_RECYCLABE);
			}
			
		});
	}

	
	
}
