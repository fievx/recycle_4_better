package com.apps4better.recycle4better.view;

import java.io.File;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.camera.MyCameraActivity;
import com.apps4better.recycle4better.model.Element;
import com.apps4better.recycle4better.model.ElementEditorService;
import com.apps4better.recycle4better.model.NetworkInspector;
import com.apps4better.recycle4better.model.PictureUploaderService;
import com.squareup.picasso.Picasso;

public class NewElementFragment extends Fragment {
	private Button saveElementButton;
	private EditText eNameEdit, eDescEdit, eWeightEdit, eMaterialCommonEdit, eMaterialScientEdit;
	private RadioGroup eRecyclableRadio;
	private ImageView photoView;
	private static final int RADIO_NO = R.id.radio_not_recyclable;
	private static final int RADIO_YES = R.id.radio_recyclable;
	private static final int RADIO_NOT_SURE = R.id.radio_not_sure;
	private ScrollView layout;
	
	//Tags used for the camera implementation
	public static final String TAG_CAMERA_FRAGMENT = "camera_fragment";
	public static final String TAG_PREVIEW_FRAGMENT = "preview_fragment";
	public static final int CODE_IMAGE_PATH = 1;
	public static final String TAG_IMAGE_PATH = "image_path";
	private String imagePath="";
	private String extension;
	
	//Tags used for the Bundle
	public static final String TAG_ELEMENT = "element";
	public static final String TAG_PRODUCT_ID = "pId";
	public static final String TAG_ELEMENT_NUMBER = "elementNumber";
	public static final String TAG_ELEMENT_NAME = "element_name";
	public static final String TAG_ELEMENT_DESCR = "element_description";
	public static final String TAG_ELEMENT_RECYC = "element_recyclable";
	
	private boolean pictureChanged = false;
	private boolean elementUploaded = false;
	private boolean pictureUploaded = false;

	
	private Element element = new Element();
	
	private Activity activity;	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
		extension = activity.getResources().getString(R.string.image_extension);
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		//We check if the savedInstanceState bundle is not null and recreate the view if needed
		if (savedInstanceState != null){
			eNameEdit.setText(savedInstanceState.getString(TAG_ELEMENT_NAME));
			eDescEdit.setText(savedInstanceState.getString(TAG_ELEMENT_DESCR));
			switch (savedInstanceState.getInt(TAG_ELEMENT_RECYC)){ //A switch for the radio group of recyclable options
			case 0:
				this.eRecyclableRadio.check(RADIO_NO);
				break;
			case 1:
				this.eRecyclableRadio.check(RADIO_YES);
				break;
			case 2:
				this.eRecyclableRadio.check(RADIO_NOT_SURE);
				break;
			default:
				this.eRecyclableRadio.check(RADIO_NO);
			}
			Drawable placeholder1 = activity.getResources().getDrawable(R.drawable.placeholder_element_detail);
			if (element.getPhotoId()!=null){
				String imageUrl = savedInstanceState.getString(TAG_IMAGE_PATH);
				imagePath = imageUrl;
				Picasso.with(activity).load(imageUrl).placeholder(placeholder1).into(photoView);			
			
		}
		}
		
	}



	/**
	 * Static method used to create an instance of NewElementFragment
	 * @param e
	 * @return
	 */
	public static NewElementFragment getInstance (Element e){
		NewElementFragment frag = new NewElementFragment ();
		Bundle b = new Bundle ();
		b.putParcelable(TAG_ELEMENT, e);
		frag.setArguments(b);
		return frag;
	}

	public View onCreateView (LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState){
		//get the infos from the Intent.
		Bundle extras = this.getArguments();
		element = extras.getParcelable(TAG_ELEMENT);
		
		layout = (ScrollView) inflater.inflate(R.layout.activity_add_element_layout, root, false);
		
		//We get all the widgets from the layout
		this.saveElementButton = (Button) layout.findViewById(R.id.save_element_button);
		this.eNameEdit = (EditText) layout.findViewById(R.id.element_name_edit_text);
		this.eDescEdit = (EditText) layout.findViewById(R.id.element_descr_edit_text);
		this.eRecyclableRadio = (RadioGroup) layout.findViewById(R.id.element_recyclable_radio_group);
		this.photoView = (ImageView) layout.findViewById(R.id.element_image_view);
		
		
		//For each component of the element, if it is not null, we pre-fill the form.
		if (element.getName()!=null)this.eNameEdit.setText(element.getName());
		if (element.getDescription()!= null) this.eDescEdit.setText(element.getDescription());
		if (Integer.valueOf(element.getRecyclable())!= null){
		switch (element.getRecyclable()){ //A switch for the radio group of recyclable options
		case 0:
			this.eRecyclableRadio.check(RADIO_NO);
			break;
		case 1:
			this.eRecyclableRadio.check(RADIO_YES);
			break;
		case 2:
			this.eRecyclableRadio.check(RADIO_NOT_SURE);
			break;
		default:
			this.eRecyclableRadio.check(RADIO_NO);
		}
		}
		//If the element photo is stored in cache we take it, if not we try to load from the server
		Drawable placeholder = activity.getResources().getDrawable(R.drawable.placeholder_element_detail);
		if (element.getPhotoId()!=null){
			 String imageUrl = activity.getResources().getString(R.string.image_bucket_url)+ element.getPhotoId();
			Picasso.with(activity).load(imageUrl).placeholder(placeholder).into(photoView);			
		}		
		return(layout);
	}
	
	
	public void takePhoto (){
		Intent intent = new Intent (activity, MyCameraActivity.class);
		intent.putExtra("photo_name", "new_element"+String.valueOf(element.getNumber()));
		startActivityForResult(intent, NewElementFragment.CODE_IMAGE_PATH);
	}
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    
		//We add listeners to the two buttons
	    photoView.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takePhoto();
			}
			
		});
		saveElementButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveElement();
			}
			
		});
		
		//We register the broadcast recievers
		LocalBroadcastManager.getInstance(activity).registerReceiver(elementUploadReceiver, new IntentFilter (ElementEditorService.CODE_ELEMENT_UPLOAD));
		LocalBroadcastManager.getInstance(activity).registerReceiver(pictureUploadReceiver, new IntentFilter (PictureUploaderService.CODE_IMAGE_UPLOAD));
	  }

	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(elementUploadReceiver);
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(pictureUploadReceiver);
	}


	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString(TAG_ELEMENT_NAME, eNameEdit.getText().toString());
		outState.putString(TAG_ELEMENT_DESCR, eDescEdit.getText().toString());
		outState.putString(TAG_IMAGE_PATH, imagePath);
		switch (this.eRecyclableRadio.getCheckedRadioButtonId()){
		case RADIO_NO :
			outState.putInt(TAG_ELEMENT_RECYC,0);
			break;
		case RADIO_YES:
			outState.putInt(TAG_ELEMENT_RECYC,1);
			break;
		case RADIO_NOT_SURE:
			outState.putInt(TAG_ELEMENT_RECYC,2);
	}
	}
	

	/*
	 * Method save element perfoms all the checks on the form and calls
	 * SaveElementTask if successfull
	 */
	private void saveElement (){
		if (!eNameEdit.getText().toString().equals(""))element.setName(eNameEdit.getText().toString());
		if (!eDescEdit.getText().toString().equals(""))element.setDescription(eDescEdit.getText().toString());
		//if (eWeightEdit.getText().toString().equals("")==false)element.setWeight(Integer.valueOf(eWeightEdit.getText().toString()).intValue());
		switch (this.eRecyclableRadio.getCheckedRadioButtonId()){
			case RADIO_NO :
				element.setRecyclable(0);
				break;
			case RADIO_YES:
				element.setRecyclable(1);
				break;
			case RADIO_NOT_SURE:
				element.setRecyclable(2);
				break;
			default:
				element.setRecyclable(2);
				break;
		}
//		element.setMaterialCommon(this.eMaterialCommonEdit.getText().toString());
//		element.setMaterialScientific(this.eMaterialScientEdit.getText().toString());
		
		//We check that there is an imagePath (which indicates that the user just took a picture or chose from library
		//and we asign this image path to element.photoId
		if (!imagePath.equals(""))
		element.setPhotoId("/product_"+String.valueOf(element.getProductId())+"_element"+String.valueOf(element.getNumber())+extension);
		
		//Check that a name is entered and a photo is added. If not, displays Toasts.
		if (element.getName()!=null && element.getPhotoId()!=null){
			if (NetworkInspector.haveNetworkConnection(activity))
			uploadElement();		
			else {
				String text = getResources().getString(R.string.no_network_connection);
				Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
			}
		}
		else if (element.getName()==null){
			String a = getResources().getString(R.string.no_name_entered_toast);
			Toast.makeText(activity,a , Toast.LENGTH_SHORT).show();
			if (element.getPhotoId()==null){
				String b = getResources().getString(R.string.no_photo_taken_toast);
				Toast.makeText(activity,b , Toast.LENGTH_SHORT).show();
			}
		}
		else if (element.getPhotoId()==null){
			String b = getResources().getString(R.string.no_photo_taken_toast);
			Toast.makeText(activity,b , Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Start the ElementEditorService with the information saved
	 * and then returns to the productDetailActivity
	 */
	private void uploadElement(){
		//Upload the element in the DB using the ElementEditorService
		Intent intent = new Intent (activity, ElementEditorService.class);
		intent.putExtra("element", element);
		activity.startService(intent);

		//Upload the photo on the server using the PictureUploaderService
		if (!imagePath.isEmpty()&& pictureChanged== true){
		intent = new Intent (activity, PictureUploaderService.class);
		intent.putExtra(PictureUploaderService.TAG_IMAGE_NAME, "product_"+String.valueOf(element.getProductId())+"_element"+String.valueOf(element.getNumber()));
		intent.putExtra(PictureUploaderService.TAG_IMAGE_PATH, imagePath);
		activity.startService (intent);
		}
		//We set pictureUploaded to true otherwise the fragment will consider that the picture is never uploaded 
		// and the spinner will spin forever and ever and ever...
		else pictureUploaded= true;
		
		//place the spinner fragment while the element and the picture are being uploaded
		SpinnerFragment spin = new SpinnerFragment();
		getFragmentManager().beginTransaction().add(R.id.fragment_container, spin).commit();
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Handle the logic for the requestCode, resultCode and data returned...
		if (requestCode == CODE_IMAGE_PATH){
		switch (resultCode) {
		case Activity.RESULT_OK :
			//We set the pictureChanged boolean to true so that the picture gets uploaded
			pictureChanged = true;
			imagePath = data.getStringExtra(TAG_IMAGE_PATH);
			Log.d("New Element Fragment", "OnresultActivity is OK and image is loading with path : " + imagePath);
			File f = new File (imagePath);
			Picasso.with(activity).load(f).fit().into(photoView);
			break;
		}
		
		}
	}

	/**
	 * Start the ProductDetailActivity and specify wether it should update the product detail 
	 * at start of the activity
	 * @param loadInfo
	 */
	private void startProductDetailActivity (boolean loadInfo){
		// We go back to the ProductDetailActivity and tell the activity to wait for the Receiver to receive succes from the service
		Intent i = new Intent (activity, ProductDetailActivity.class);
		i.putExtra("product_id", element.getProductId());
		i.putExtra("load_info", loadInfo);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//flag used to clear all activities started after ProductDetail Activity.
		startActivity(i);
	}
	
	/**
	 * We need a receiver to handle the broadcast sent from the ProductEditorService
	 * 
	 */
		private BroadcastReceiver  elementUploadReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				
				//If the element was successfully saved, display a succes toast and start ProductDetailActivity
				if (intent.getExtras().getBoolean("success")){
					elementUploaded = true;
					if (pictureUploaded == true && elementUploaded == true){
						startProductDetailActivity (true);
					}
				}
				else {
					String message = context.getResources().getString(R.string.element_edit_failure);
					Toast.makeText(context,message , Toast.LENGTH_SHORT).show();
				}
					
				
			}
			
		};	
		
	private BroadcastReceiver pictureUploadReceiver = new BroadcastReceiver (){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//If the element was successfully saved, display a succes toast and start ProductDetailActivity
			if (intent.getExtras().getBoolean("success")){
				pictureUploaded = true;
				if (pictureUploaded == true && elementUploaded == true){
					startProductDetailActivity (true);
				}
			}
			else {
				String message = context.getResources().getString(R.string.picture_upload_failure);
				Toast.makeText(context,message , Toast.LENGTH_SHORT).show();
			}
		}
	
};

	
public void setImagePath(String path) {
this.imagePath = path;
	}

/**
 * is called by ProductDetailActivity once MyCameraActivity has returned the imagePath
 * puts the image on the imageView using Picasso.
 */
public void displaySavedImage (){
	File f = new File (imagePath);
	Picasso.with(activity).load(f).centerInside().fit().into(photoView);	
}

}
