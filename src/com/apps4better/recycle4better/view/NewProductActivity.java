package com.apps4better.recycle4better.view;

import java.io.File;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.squareup.picasso.Picasso;

public class NewProductActivity extends Activity{
	
	private Product product = new Product ();
	private ScrollView layout;
	
	private EditText pBrandEdit, pModelEdit;
	private Button addProductButton;
	private boolean formVisible = false;
	private ImageView photoView;
	private String extension;
	
	//Tags used for the camera implementation
	public static final String TAG_CAMERA_FRAGMENT = "camera_fragment";
	public static final String TAG_PREVIEW_FRAGMENT = "preview_fragment";
	public static final String TAG_NEW_PRODUCT_FRAGMENT = "new_product_fragment";
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
		addProductButton = (Button) layout.findViewById(R.id.add_product_button);
//		formView = (View) layout.findViewById(R.id.formLayout);
		
		//We check if the savedInstanceState bundle if empty, if not we inspect it
		if (savedInstanceState!=null){
			if (savedInstanceState.getBoolean("formVisible")){
				formVisible = true;
				addProductButton.setEnabled(false);
				NewProductFragment frag = (NewProductFragment ) getFragmentManager().findFragmentByTag(TAG_NEW_PRODUCT_FRAGMENT);
				if (frag!=null) getFragmentManager().beginTransaction().show(frag).commit();
			}
		}
		
		//We set the layout as contentView
		setContentView(layout);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.product_detail_action, menu);
		return super.onCreateOptionsMenu(menu);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()){
		case R.id.action_main_activity :
			Intent i = new Intent (this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}

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

	}
	
	protected void onPause (){
		super.onPause();
	}
	
		
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("formVisible", formVisible);
	}

	private void showForm (){
		if (formVisible == false){
			NewProductFragment frag = NewProductFragment.getInstance(product);
			FragmentTransaction transac = getFragmentManager ().beginTransaction();
			transac.add(R.id.formLayout, frag, TAG_NEW_PRODUCT_FRAGMENT);
			transac.addToBackStack(null).commit(); 
			formVisible = true;
			addProductButton.setEnabled(false);
		}
	}

public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
}

}
