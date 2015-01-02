package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.model.Element;
import com.apps4better.recycle4better.model.ElementDetailObserver;
import com.apps4better.recycle4better.model.ElementEditorService;
import com.squareup.picasso.Picasso;

public class ElementDetailFragment extends Fragment {
	private Element element;
	private Activity activity;
	private RelativeLayout layout;
	public static final String TAG_ELEMENT = "element";
	private static final String extansion = ".png";
	
	private ImageView ePictureView, eRecyclable;
	private TextView eNameText, eDescText, eTrustText;
	private Button editButton; 
	private ImageButton upVoteButton, downVoteButton;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//Retrieve the Bundle from the argument and extract the Element
		Bundle bundle = this.getArguments();
		this.element = bundle.getParcelable(TAG_ELEMENT);
		
		layout = (RelativeLayout) inflater.inflate(R.layout.fragment_element_detail, container, false);
		ePictureView = (ImageView) layout.findViewById(R.id.element_picture_view);
		eRecyclable = (ImageView) layout.findViewById(R.id.element_recyclable_view);
		eNameText = (TextView) layout.findViewById(R.id.element_name_text_view);
		eDescText = (TextView) layout.findViewById(R.id.element_description_text_view);
		eTrustText = (TextView) layout.findViewById(R.id.element_trust_score_text);
		editButton = (Button) layout.findViewById(R.id.element_edit_button);
		upVoteButton = (ImageButton) layout.findViewById(R.id.element_upvote_button);
		downVoteButton = (ImageButton) layout.findViewById(R.id.element_down_vote_button);
		
		//We create the view with all the variables from the Element
		// the element image loaded via Picasso
		String imageUrl = activity.getResources().getString(R.string.server_address)+element.getPhotoId();
		Drawable placeHolder = activity.getResources().getDrawable(R.drawable.placeholder_element_detail);
		Picasso.with(activity).load(imageUrl).placeholder(placeHolder).fit().into(ePictureView);
		
		this.eNameText.setText(element.getName());//name
		this.eDescText.setText(element.getDescription());//description
		
		//the recyclable symbol
		switch (element.getRecyclable()){
		case 0 :
			eRecyclable.setImageDrawable(activity.getResources().getDrawable(R.drawable.tick_no));
			break;
		case 1:
			eRecyclable.setImageDrawable(activity.getResources().getDrawable(R.drawable.tick_yes));
			break;
		case 2:
			eRecyclable.setImageDrawable(activity.getResources().getDrawable(R.drawable.tick_maybe));
			break;
		}
		
		this.eTrustText.setText(String.valueOf(element.getTrustScore()));//Trust score
		
		return layout;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		editButton.setOnClickListener (new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edit ();
			}
			
		});
		upVoteButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				upVote();
			}
			
		});
		downVoteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downVote ();
			}
			
		});
	
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	/**
	 * Returns an intance of ElementDetailFragment with a Bundle as Argument. 
	 * The Element in param is passed in the bundle.
	 * @param element
	 * @return ElementDetailFragment
	 */
	public static ElementDetailFragment getInstance (Element element){
		ElementDetailFragment frag = new ElementDetailFragment ();
		Bundle bundle = new Bundle();
		bundle.putParcelable(TAG_ELEMENT, element);
		frag.setArguments(bundle);
		return frag;
	}

	private void upVote(){
		this.element.setTrustScore(this.element.getTrustScore()+1);
		Intent intent = new Intent (activity, ElementEditorService.class);
		intent.putExtra("element", this.element);
		activity.startService(intent);
		
		//We update the score on the display
		this.eTrustText.setText(String.valueOf(element.getTrustScore()));
	}
	
	private void downVote (){
		this.element.setTrustScore(this.element.getTrustScore()-1);
		Intent intent = new Intent (activity, ElementEditorService.class);
		intent.putExtra("element", this.element);
		activity.startService(intent);
		
		//We update the score on the display
		this.eTrustText.setText(String.valueOf(element.getTrustScore()));
	}


private void edit (){
	((ElementDetailObserver)activity).editElement(element);
}

}
