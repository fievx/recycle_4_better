package com.apps4better.recycle4better.view;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.apps4better.recycle4better.model.PictureUploaderService;
import com.squareup.picasso.Picasso;


/**
 * This class is note used anymore
 * @author jeremy
 *
 */
public class NewElementActivity extends Activity {
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
	private static final int CODE_IMAGE_PATH = 1;
	public static final String TAG_IMAGE_PATH = "image_path";
	private String imagePath="";
	
	//Tags used for the Bundle
	public static final String TAG_ELEMENT = "element";
	public static final String TAG_PRODUCT_ID = "pId";
	public static final String TAG_ELEMENT_NUMBER = "elementNumber";
	
	private boolean elementUploaded = false;
	private boolean pictureUploaded = false;

	
	private Element element = new Element();
	
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//get the infos from the Intent.
		Bundle extras = getIntent().getExtras();
		element = extras.getParcelable(TAG_ELEMENT);
		
		layout = (ScrollView) ScrollView.inflate(this,R.layout.activity_add_element_layout, null);
		
		//We get all the widgets from the layout
		this.saveElementButton = (Button) layout.findViewById(R.id.save_element_button);
		this.eNameEdit = (EditText) layout.findViewById(R.id.element_name_edit_text);
		this.eDescEdit = (EditText) layout.findViewById(R.id.element_descr_edit_text);
		this.eRecyclableRadio = (RadioGroup) layout.findViewById(R.id.element_recyclable_radio_group);
		this.photoView = (ImageView) layout.findViewById(R.id.element_image_view);
		
		//For each component of the element, if it is not null, we pre-fill the form.
		if (element.getName()!=null)this.eNameEdit.setText(element.getName());
		if (element.getDescription()!= null) this.eDescEdit.setText(element.getDescription());
		switch (element.getRecyclable()){ //A switch for the radio group of recyclable options
		case 0:
			this.eRecyclableRadio.getChildAt(RADIO_NO).setSelected(true);
			break;
		case 1:
			this.eRecyclableRadio.getChildAt(RADIO_YES).setSelected(true);
			break;
		case 2:
			this.eRecyclableRadio.getChildAt(RADIO_NOT_SURE).setSelected(true);
			break;
		default:
			this.eRecyclableRadio.getChildAt(RADIO_NO).setSelected(true);
		}
		//If the element photo is stored in cache we take it, if not we try to load from the server
		if (element.getCacheImagePath()!= null){ 
			File file = new File(element.getCacheImagePath());
			Picasso.with(this).load(file).fit().into(photoView);
		}
		else if (element.getPhotoId()!=null){
			File file = new File(element.getPhotoId());
			Picasso.with(this).load(file).fit().into(photoView);			
		}
		
		setContentView(layout);
	}
	
	
	public void takePhoto (){
		Intent intent = new Intent (this, MyCameraActivity.class);
		intent.putExtra("photo_name", "new_element"+String.valueOf(element.getNumber()));
		startActivityForResult(intent, CODE_IMAGE_PATH);
	}
	
	
	@Override
	  protected void onResume() {
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
		LocalBroadcastManager.getInstance(this).registerReceiver(elementUploadReceiver, new IntentFilter (ElementEditorService.CODE_ELEMENT_UPLOAD));
		LocalBroadcastManager.getInstance(this).registerReceiver(pictureUploadReceiver, new IntentFilter (PictureUploaderService.CODE_IMAGE_UPLOAD));
	  }

	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(elementUploadReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(pictureUploadReceiver);
	}
	
	


	/*
	 * Method save element perfoms all the checks on the form and calls
	 * SaveElementTask if successfull
	 */
	private void saveElement (){
		element.setName(eNameEdit.getText().toString());
		element.setDescription(eDescEdit.getText().toString());
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
		element.setPhotoId("/images/product_"+String.valueOf(element.getProductId())+"_element"+String.valueOf(element.getNumber()));
		
		//Check that a name is entered and a photo is added. If not, displays Toasts.
		if (eNameEdit.getText().toString() != null && !imagePath.equals("")){
			uploadElement();		
	}
		else if (eNameEdit.getText().toString() == null){
			String a = getResources().getString(R.string.no_name_entered_toast);
			Toast.makeText(this,a , Toast.LENGTH_SHORT).show();
			if (imagePath.equals("")){
				String b = getResources().getString(R.string.no_photo_taken_toast);
				Toast.makeText(this,b , Toast.LENGTH_SHORT).show();
			}
		}
		else if (imagePath.equals("")){
			String b = getResources().getString(R.string.no_photo_taken_toast);
			Toast.makeText(this,b , Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Start the ElementEditorService with the information saved
	 * and then returns to the productDetailActivity
	 */
	private void uploadElement(){
		//Upload the element in the DB using the ElementEditorService
		Intent intent = new Intent (this, ElementEditorService.class);
		intent.putExtra("element", element);
		startService(intent);

		//Upload the photo on the server using the PictureUploaderService
		if (!imagePath.isEmpty()){
		intent = new Intent (this, PictureUploaderService.class);
		intent.putExtra(PictureUploaderService.TAG_IMAGE_NAME, "product_"+String.valueOf(element.getProductId())+"_element"+String.valueOf(element.getNumber()));
		intent.putExtra(PictureUploaderService.TAG_IMAGE_PATH, imagePath);
		startService (intent);
		}
		
		//place the spinner fragment while the element and the picture are being uploaded
		SpinnerFragment spin = new SpinnerFragment();
		getFragmentManager().beginTransaction().add(R.id.fragment_container, spin).commit();
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Handle the logic for the requestCode, resultCode and data returned...
		if (requestCode == CODE_IMAGE_PATH){
		switch (resultCode) {
		case Activity.RESULT_OK :
			imagePath = data.getStringExtra(TAG_IMAGE_PATH);
			Log.d("main Activity result", "result is OK and image is loading with path : " + imagePath);
			File f = new File (imagePath);
			Picasso.with(this).load(f).centerInside().fit().into(photoView);
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
		Intent i = new Intent (this, ProductDetailActivity.class);
		i.putExtra("product_id", element.getProductId());
		i.putExtra("load_info", loadInfo);
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
}
