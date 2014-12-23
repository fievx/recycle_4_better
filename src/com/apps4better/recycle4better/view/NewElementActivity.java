package com.apps4better.recycle4better.view;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	
	private Element element = new Element();
	
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//get the infos from the Intent.
		Bundle extras = getIntent().getExtras();
		element.setProductId(extras.getInt("pId"));
		element.setNumber(extras.getInt("elementNumber"));
		
		layout = (ScrollView) ScrollView.inflate(this,R.layout.activity_add_element_layout, null);
		
		//We get all the widgets from the layout
		this.saveElementButton = (Button) layout.findViewById(R.id.save_element_button);
		this.eNameEdit = (EditText) layout.findViewById(R.id.element_name_edit_text);
		this.eDescEdit = (EditText) layout.findViewById(R.id.element_descr_edit_text);
		this.eRecyclableRadio = (RadioGroup) layout.findViewById(R.id.element_recyclable_radio_group);
		this.photoView = (ImageView) layout.findViewById(R.id.element_image_view);
		
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
		element.setPhotoId("/image/"+String.valueOf(element.getProductId())+"/element"+String.valueOf(element.getNumber()));
		
		if (eNameEdit.getText().toString() != null){
			uploadElement();		
	}
		else {
			String a = getResources().getString(R.string.no_name_entered_toast);
			Toast.makeText(this,a , Toast.LENGTH_SHORT).show();
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
		intent = new Intent (this, ProductDetailActivity.class);
		intent.putExtra("product_id", element.getProductId());
		intent.putExtra("load_info", false);
		startActivity(intent);
		
		//Upload the photo on the server using the PictureUploaderService
		if (!imagePath.isEmpty()){
		intent = new Intent (this, PictureUploaderService.class);
		intent.putExtra(PictureUploaderService.TAG_IMAGE_NAME, element.getPhotoId());
		intent.putExtra(PictureUploaderService.TAG_IMAGE_PATH, imagePath);
		startActivity (intent);
		}
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

}
