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
import android.widget.ScrollView;
import android.widget.Toast;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.camera.MyCameraActivity;
import com.apps4better.recycle4better.model.ElementEditorService;
import com.apps4better.recycle4better.model.NetworkInspector;
import com.apps4better.recycle4better.model.PictureUploaderService;
import com.apps4better.recycle4better.model.Product;
import com.apps4better.recycle4better.model.ProductEditorService;
import com.squareup.picasso.Picasso;

public class NewProductFragment extends Fragment{
	Product product;
	Activity activity;
	//All elements from the UI
	ScrollView layout;
	EditText brandEdit, modelEdit;
	ImageView pImageView;
	Button saveButton;
	private boolean productUploaded = false;
	private boolean pictureUploaded = false;
	private boolean pictureChanged = false;
	
	//Tags used for the camera implementation
	public static final String TAG_CAMERA_FRAGMENT = "camera_fragment";
	public static final String TAG_PREVIEW_FRAGMENT = "preview_fragment";
	public static final int CODE_IMAGE_PATH = 2;
	public static final String TAG_IMAGE_PATH = "image_path";
	private String imagePath="";
	private String extension = ".jpg";
	
	//Tag for Bundle
	public static final String TAG_PRODUCT = "product";
	public static final String TAG_PRODUCT_BRAND = "product_brand";
	public static final String TAG_PRODUCT_MODEL = "product_model";

	/* (non-Javadoc)
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}
	
	/**
	 * Use this method to get an instance of NewProductFragment and give it a Product before dynamically 
	 * adding the fragment.
	 * @param product
	 * @return Returns an instance of NewProductFragment with a Product as Argument
	 */
	public static NewProductFragment getInstance (Product product){
		NewProductFragment frag = new NewProductFragment ();
		Bundle b = new Bundle();
		b.putParcelable(TAG_PRODUCT, product);
		frag.setArguments(b);
		return frag;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle b = getArguments();
		this.product = b.getParcelable(TAG_PRODUCT);
		Log.d("Debug product fragment", "product id = "+product.getpId());
		
		layout = (ScrollView) inflater.inflate(R.layout.fragment_add_product, container, false);
		brandEdit = (EditText) layout.findViewById(R.id.product_brand_edit_text);
		modelEdit = (EditText) layout.findViewById(R.id.product_model_edit_text);
		pImageView = (ImageView) layout.findViewById(R.id.product_image_view);
		saveButton = (Button) layout.findViewById(R.id.save_product_button);
		
		//If no info is stored in the saveInstance Bundle, we use info from the Product object
		if (savedInstanceState == null){
			if (product.getBrand()!=null)brandEdit.setText(product.getBrand());
			if (product.getModel()!=null)modelEdit.setText(product.getModel());
			// the product  image loaded via Picasso
			if (product.getPhotoId()!=null){
			String imageUrl = activity.getResources().getString(R.string.image_bucket_url)+product.getPhotoId();
			Drawable placeHolder = activity.getResources().getDrawable(R.drawable.placeholder_element_detail);
			Picasso.with(activity).load(imageUrl).placeholder(placeHolder).fit().into(pImageView);
		}
		}
			else {
				if (savedInstanceState.getString(TAG_PRODUCT_BRAND)!=null)brandEdit.setText(savedInstanceState.getString(TAG_PRODUCT_BRAND));
				if (savedInstanceState.getString(TAG_PRODUCT_MODEL)!=null)modelEdit.setText(savedInstanceState.getString(TAG_PRODUCT_MODEL));
				if (savedInstanceState.getString(TAG_IMAGE_PATH)!=null){
					String imageUrl = savedInstanceState.getString(TAG_IMAGE_PATH);
					imagePath = imageUrl;
					Drawable placeHolder = activity.getResources().getDrawable(R.drawable.placeholder_element_detail);
					Picasso.with(activity).load(imageUrl).placeholder(placeHolder).into(pImageView);
				}
			}
			
		
		return layout;
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//We add listeners to the two buttons
		pImageView.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takePhoto();
			}
			
		});
		saveButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveProduct();
			}
			
		});
		
		//We register the broadcast recievers
		LocalBroadcastManager.getInstance(activity).registerReceiver(productUploadReceiver, new IntentFilter (ProductEditorService.CODE_PRODUCT_UPLOAD));
		LocalBroadcastManager.getInstance(activity).registerReceiver(pictureUploadReceiver, new IntentFilter (PictureUploaderService.CODE_IMAGE_UPLOAD));
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString(TAG_PRODUCT_BRAND, brandEdit.getText().toString());
		outState.putString(TAG_PRODUCT_MODEL, modelEdit.getText().toString());
		if(!imagePath.equals(""))outState.putString(TAG_IMAGE_PATH, imagePath);
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(productUploadReceiver);
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(pictureUploadReceiver);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Handle the logic for the requestCode, resultCode and data returned...
		switch (requestCode){
		case   CODE_IMAGE_PATH :
			if (resultCode == Activity.RESULT_OK ){
				//we set the pictureChanged boolean to true so that it gets uploaded on save
				pictureChanged = true;
				imagePath = data.getStringExtra(TAG_IMAGE_PATH);
				Log.d("main Activity result", "result is OK and image is loading with path : " + imagePath);
				File f = new File (imagePath);
				Picasso.with(activity).load(f).into(pImageView);
			}
			break;
		
		}
	}
	
	public void takePhoto (){
		Intent intent = new Intent (activity, MyCameraActivity.class);
		intent.putExtra("photo_name", "new_product");
		startActivityForResult(intent, NewProductFragment.CODE_IMAGE_PATH);
	}
	
	private void saveProduct (){
		if (brandEdit.getText().toString().isEmpty() || modelEdit.getText().toString().isEmpty()){
			String a = getResources().getString(R.string.field_not_filled_text);
			Toast.makeText(activity, a, Toast.LENGTH_LONG).show();

			if (imagePath.isEmpty()&& product.getPhotoId()== null){

				String b = getResources().getString(R.string.no_product_photo_taken_text);
				Toast.makeText(activity, b, Toast.LENGTH_LONG).show();
				return ;
			}
			return ;
		}
		if (imagePath.isEmpty() && product.getPhotoId()== null){
			String a = getResources().getString(R.string.no_product_photo_taken_text);
			Toast.makeText(activity, a, Toast.LENGTH_LONG).show();
			return ;
		}
		
		//We store all the informations in the product
		product.setBrand(brandEdit.getText().toString());
		product.setModel(modelEdit.getText().toString());
		product.setPhotoId("/product_"+String.valueOf(product.getpId())+"_product"+extension);
		
		//We check for internet connect and then call uploadProduct method
		if (NetworkInspector.haveNetworkConnection(activity))
			uploadProduct();
		else {
			String text = getResources().getString(R.string.no_network_connection);
			Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
		}
	}

	private void uploadProduct(){
		//We upload the product using the ProductEditorService
				Intent intent = new Intent (activity, ProductEditorService.class);
				intent.putExtra("product", product.getCloneWithoutElements());
				activity.startService(intent);
				
				//We upload the image using the PictureUploaderService
				if (pictureChanged == true){
				intent = new Intent (activity, PictureUploaderService.class);
				//When setting the image name, we dont set the extension as the PictureUploaderService does it
				intent.putExtra(PictureUploaderService.TAG_IMAGE_NAME, "product_"+String.valueOf(product.getpId())+"_product");
				intent.putExtra(PictureUploaderService.TAG_IMAGE_PATH, imagePath);
				activity.startService (intent);
				}
				else pictureUploaded = true;
					
				//We start the spinner fragment while the picture and the product info are being uploaded
				SpinnerFragment spin = new SpinnerFragment ();
				getFragmentManager().beginTransaction().add(R.id.fragment_container, spin, "spinner_tag").commit();
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

	/**
	 * Start the ProductDetailActivity and specify wether it should update the product detail 
	 * at start of the activity
	 * @param loadInfo
	 */
	private void startProductDetailActivity (boolean loadInfo){
		// We go back to the ProductDetailActivity and tell the activity to wait for the Receiver to receive succes from the service
		Intent i = new Intent (activity, ProductDetailActivity.class);
		i.putExtra("product_id", product.getpId());
		i.putExtra("load_info", loadInfo);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//flag used to clear all activities started after ProductDetail Activity.
		startActivity(i);
	}

}
