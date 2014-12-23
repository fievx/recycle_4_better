package com.apps4better.recycle4better.camera;

import java.io.File;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;




import com.apps4better.recycle4better.R;
import com.squareup.picasso.Picasso;

public class PreviewFragment extends Fragment{
	private Button saveButton, retakePhotoButton;
	private ImageView photoView;
	private String imagePath;
	private View view;
	
	private Activity activity;
	
	public static PreviewFragment getInstance (String imagePath){
		PreviewFragment pFrag = new PreviewFragment();
		
		Bundle args = new Bundle ();
		args.putString("image_path", imagePath);
		pFrag.setArguments(args);
		
		return pFrag;		
	}
	
	
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.activity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		//We get the imagePath from the bundle
		Bundle bundle = this.getArguments();
		imagePath = bundle.getString("image_path");
		Log.d("PreviewFragment create", "image path retrieve from Bundle and is :" +imagePath);
		
		//We get all the widgets from the layout.
		view = (View) inflater.inflate(R.layout.fragment_preview_photo, container, false);
		photoView = (ImageView) view.findViewById(R.id.photo_view);
		saveButton = (Button) view.findViewById(R.id.save_button);
		retakePhotoButton = (Button) view.findViewById(R.id.retake_photo_button);	
		
		//We set the photo on the imageView
		File f = new File (imagePath);
		Picasso.with(activity).load(f).fit().into(photoView);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		/*
		 * Both buttons simply send a parameter to the main Activity through its update method
		 * as part of implementing the FragmentObserver interface.r
		 */
		saveButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//we send the action to the main activity through the FragmentObserver Interface
				 try{
			            ((FragmentObserver) activity).update("save_photo");
			        }catch (ClassCastException cce){
			 
			        }
			}
			
		});
		retakePhotoButton.setOnClickListener (new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((FragmentObserver) activity).update("retake_photo");
			}
			
		});
		
	}
	
	

		
	
}
