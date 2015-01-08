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
import android.widget.ScrollView;
import android.widget.Toast;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.camera.MyCameraActivity;
import com.apps4better.recycle4better.model.PictureUploaderService;
import com.apps4better.recycle4better.model.Product;
import com.apps4better.recycle4better.model.ProductEditorService;
import com.apps4better.recycle4better.view.ProductDetailActivity.GetProductDetailTask;
import com.squareup.picasso.Picasso;

public class NewProductActivity extends Activity{
	
	private Product product = new Product ();
	private ScrollView layout;
	
	private EditText pBrandEdit, pModelEdit;
	private Button addProductButton, saveButton;
	private View formView;
	private boolean formVisible = false;
	private ImageView photoView;
	private String extension;
	
	//Tags used for the camera implementation
	public static final String TAG_CAMERA_FRAGMENT = "camera_fragment";
	public static final String TAG_PREVIEW_FRAGMENT = "preview_fragment";
	private static final int CODE_IMAGE_PATH = 1;
	public static final String TAG_IMAGE_PATH = "image_path";
	private boolean productUploaded = false;
	private boolean pictureUploaded = false;
	
	
	//a string to store the path once the user has taken a photo of the product
	private String imagePath="";
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//We get the product Id from the Intent
		Bundle extra = getIntent().getExtras();
		product.setpId(extra.getInt("product_id"));
		extension = getResources().getString(R.string.image_extension);
		
		//We inflate the layout and get all the widgets
		layout = (ScrollView) ScrollView.inflate(this, R.layout.activity_add_product_layout, null);
		pBrandEdit = (EditText) layout.findViewById(R.id.product_brand_edit_text);
		pModelEdit = (EditText) layout.findViewById(R.id.product_model_edit_text);
		addProductButton = (Button) layout.findViewById(R.id.add_product_button);
		saveButton = (Button) layout.findViewById(R.id.save_product_button);
		formView = (View) layout.findViewById(R.id.formLayout);
		photoView = (ImageView) layout.findViewById(R.id.product_image_view);
		
		//We hide the new product form
		formView.setVisibility(View.GONE);
		
		//We set the layout as contentView
		setContentView(layout);
	}
	
	public void onResume (){
		super.onResume();
		
		//We set here all the listeners on the buttons
		addProductButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showForm();
			}
			
		});
		photoView.setOnClickListener (new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takePhoto ();
			}
			
		});
		saveButton.setOnClickListener (new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveProduct();
			}
			
		});
		
		//Listeners on the Broadcast receivers
		LocalBroadcastManager.getInstance(this).registerReceiver(productUploadReceiver, new IntentFilter(ProductEditorService.CODE_PRODUCT_UPLOAD));
		LocalBroadcastManager.getInstance(this).registerReceiver(pictureUploadReceiver, new IntentFilter(PictureUploaderService.CODE_IMAGE_UPLOAD));
	}
	
	protected void onPause (){
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(productUploadReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(pictureUploadReceiver);
	}
	
	private void showForm (){
		if (formVisible == false){
			formView.setVisibility(View.VISIBLE);
			formVisible = true;
			addProductButton.setEnabled(false);
		}
	}

/*
 * saveProduct makes sure all fields are filled and if yes, call the upload method.
 * checks are : brand is filled, model is filled, photo was taken.
 */
private void saveProduct (){
	if (pBrandEdit.getText().toString().isEmpty() || pModelEdit.getText().toString().isEmpty()){
		String a = getResources().getString(R.string.field_not_filled_text);
		Toast.makeText(this, a, Toast.LENGTH_LONG).show();
		if (imagePath.isEmpty()){
			String b = getResources().getString(R.string.no_product_photo_taken_text);
			Toast.makeText(this, b, Toast.LENGTH_LONG).show();
			return ;
		}
		return ;
	}
	if (imagePath.isEmpty()){
		String a = getResources().getString(R.string.no_product_photo_taken_text);
		Toast.makeText(this, a, Toast.LENGTH_LONG).show();
		return ;
	}
	
	//We store all the informations in the product
	product.setBrand(pBrandEdit.getText().toString());
	product.setModel(pModelEdit.getText().toString());
	product.setPhotoId("/product_"+String.valueOf(product.getpId())+"_product"+extension);
	
	//We upload the product using the ProductEditorService
	Intent intent = new Intent (this, ProductEditorService.class);
	intent.putExtra("product", product);
	startService(intent);
	
	//We upload the image using the PictureUploaderService
	intent = new Intent (this, PictureUploaderService.class);
	intent.putExtra(PictureUploaderService.TAG_IMAGE_NAME, "product_"+String.valueOf(product.getpId())+"_product");
	intent.putExtra(PictureUploaderService.TAG_IMAGE_PATH, imagePath);
	startService (intent);
	
	//We start the spinner fragment while the picture and the product info are being uploaded
	SpinnerFragment spin = new SpinnerFragment ();
	getFragmentManager().beginTransaction().add(R.id.fragment_frame, spin, "spinner_tag").commit();
}

/**
 * Start an intent of MyCameraActivity and waits for result.
 */
public void takePhoto (){
	Intent intent = new Intent (this, MyCameraActivity.class);
	intent.putExtra("photo_name", "new_product"+String.valueOf(product.getpId()));
	startActivityForResult(intent, CODE_IMAGE_PATH);
}


public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Handle the logic for the requestCode, resultCode and data returned...
	switch (requestCode){
	case   CODE_IMAGE_PATH :
		if (resultCode == Activity.RESULT_OK ){
			imagePath = data.getStringExtra(TAG_IMAGE_PATH);
			Log.d("main Activity result", "result is OK and image is loading with path : " + imagePath);
			File f = new File (imagePath);
			Picasso.with(this).load(f).centerInside().fit().into(photoView);
		}
		break;
	
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
	i.putExtra("product_id", product.getpId());
	i.putExtra("load_info", loadInfo);
	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//flag used to clear all activities started after ProductDetail Activity.
	startActivity(i);
}

/**
 * We need a receiver to handle the broadcast sent from the ProductEditorService
 * 
 */
	private BroadcastReceiver  productUploadReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			//If the element was successfully saved, display a succes toast and start ProductDetailActivity
			if (intent.getExtras().getBoolean("success")){
				productUploaded = true;
				if (pictureUploaded == true && productUploaded == true){
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
				if (pictureUploaded == true && productUploaded == true){
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
