package com.apps4better.recycle4better.elementWizard;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.camera.MyCameraActivity;
import com.apps4better.recycle4better.camera.PreviewFragment;
import com.apps4better.recycle4better.camera.PreviewFragmentObserver;
import com.apps4better.recycle4better.model.Element;
import com.apps4better.recycle4better.model.ElementEditorService;
import com.apps4better.recycle4better.model.PictureUploaderService;
import com.apps4better.recycle4better.model.Product;
import com.apps4better.recycle4better.view.ProductDetailActivity;
import com.apps4better.recycle4better.view.SpinnerFragment;
import com.commonsware.cwac.camera.CameraFragment;

public class NewElementWizardActivity extends MyCameraActivity implements PreviewFragmentObserver, 
			WizardFragmentObserver {
	private Product product;
	private Element element;
	private String extension;
	private String imagePath;
	private Boolean continueWizard;
	private Boolean elementUploaded = false;
	private Boolean pictureUploaded = false;
	
	//Fragment Tags
	public static final String FRAGMENT_ONE_TAG = "fragment_one";
	public static final String FRAGMENT_RECYCLABLE_TAG = "fragment_recyclable";
	public static final String FRAGMENT_CONTINUE_TAG = "fragment_continue";

	//Intent Tag
	public static final String PRODUCT_TAG	= "product";
	/* (non-Javadoc)
	 * @see com.apps4better.recycle4better.camera.MyCameraActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//We add the first fragment on top of the camera
		NewElementWizardFragmentOne frag = new NewElementWizardFragmentOne();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fragment_container, frag, FRAGMENT_ONE_TAG);
		transaction.commit();
		
		product = getIntent().getParcelableExtra(PRODUCT_TAG);
		element = new Element();
		element.setNumber(product.getNextElementNumber());
		element.setProductId(product.getpId());
		extension = getResources().getString(R.string.image_extension);
	}
	
	
	/* (non-Javadoc)
	 * @see com.apps4better.recycle4better.camera.MyCameraActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//We register the broadcast recievers
		LocalBroadcastManager.getInstance(this).registerReceiver(elementUploadReceiver, new IntentFilter (ElementEditorService.CODE_ELEMENT_UPLOAD));
		LocalBroadcastManager.getInstance(this).registerReceiver(pictureUploadReceiver, new IntentFilter (PictureUploaderService.CODE_IMAGE_UPLOAD));
	}


	/* (non-Javadoc)
	 * @see com.apps4better.recycle4better.camera.MyCameraActivity#update(java.lang.String)
	 */
	@Override
	public void update(String param) {
		// TODO Auto-generated method stub
		FragmentTransaction transac = getFragmentManager().beginTransaction();
		
		switch (param){
		case "save_photo" :
			Log.d("MyCameraActivity", "Update method called with parameter : save_photo");
			//Set the photo path in the Element
			imagePath = pictureFile.getAbsolutePath();
			element.setPhotoId("/product_"+String.valueOf(element.getProductId())+"_element"+String.valueOf(element.getNumber())+extension);
			
			//We remove the fragmentOne
			NewElementWizardFragmentOne fragmentOne = (NewElementWizardFragmentOne) getFragmentManager().findFragmentByTag(FRAGMENT_ONE_TAG);
			if (fragmentOne!=null)
				transac.remove(fragmentOne);
			
			//We set the buttons invisible on the previewFragment
			PreviewFragment preview = (PreviewFragment) getFragmentManager().findFragmentByTag(MyCameraActivity.TAG_PREVIEW_FRAGMENT);
			preview.setButtonsInvisible();
			
			//We add the RecyclableWizardFragment
			RecyclableWizardFragment frag = new RecyclableWizardFragment();
			transac.add(R.id.fragment_container, frag, FRAGMENT_RECYCLABLE_TAG);
			transac.addToBackStack(null).commit();
			break;
		case "retake_photo" :
			Log.d("MyCameraActivity", "Update method called with parameter : retake_photo");
			delFile (pictureFile.getAbsolutePath());
			CameraFragment cF = (CameraFragment) getFragmentManager().findFragmentByTag(TAG_CAMERA_FRAGMENT);
			shutterButton.setVisibility(Button.VISIBLE);
			PreviewFragment f = (PreviewFragment) getFragmentManager().findFragmentByTag(TAG_PREVIEW_FRAGMENT);
			//transac.remove(cF);
			transac.remove(f).commit();
			cF.restartPreview();

			//initCamera();
			break;
		case "start_preview":
			runOnUiThread(new Runnable (){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					startPreview();
				}
				
			});
			Log.d("Camera Activity", "Start Preview");
			break;
		}
	}


	/**
	 * Used by the RecyclableWizardFragment
	 * Set the recyclable attribute and replace RecyclableFragment by the ContinueFragment
	 */
	@Override
	public void setElementRecyclable(int recyclable) {
		//We get the result for the RecyclableWizardFragment
		element.setRecyclable(recyclable);
		
		//Then we display the ContinueWizardFragment
		ContinueWizardFragment frag = new ContinueWizardFragment ();
		RecyclableWizardFragment rFrag = (RecyclableWizardFragment) getFragmentManager().findFragmentByTag(FRAGMENT_RECYCLABLE_TAG);
		FragmentTransaction transac = getFragmentManager().beginTransaction();
		transac.remove(rFrag).add(R.id.fragment_container, frag, FRAGMENT_CONTINUE_TAG);
		transac.addToBackStack(null);
		transac.commit();
	}


	/* (non-Javadoc)
	 * @see com.apps4better.recycle4better.elementWizard.WizardFragmentObserver#setContinueWizard(boolean)
	 */
	@Override
	public void setContinueWizard(boolean continueBoolean) {
		//first we set the continue Boolean which 
		this.continueWizard = continueBoolean;
		// Then we save and upload the Element that was created.
		uploadElement();
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
		intent = new Intent (this, PictureUploaderService.class);
		intent.putExtra(PictureUploaderService.TAG_IMAGE_NAME, "product_"+String.valueOf(element.getProductId())+"_element"+String.valueOf(element.getNumber()));
		intent.putExtra(PictureUploaderService.TAG_IMAGE_PATH, imagePath);
		startService (intent);
		
		//place the spinner fragment while the element and the picture are being uploaded
		SpinnerFragment spin = new SpinnerFragment();
		getFragmentManager().beginTransaction().add(R.id.fragment_container, spin).commit();
		
	}
	
	/**
	 * Method called once the element has been fully uploaded.
	 * Deals with the end of wizard or restart
	 */
	protected void elementUploadFinished(){
		if (continueWizard){
			//we remove the ContinueFragment
			FragmentManager manager = getFragmentManager();
			FragmentTransaction transac = manager.beginTransaction();
			ContinueWizardFragment continueFrag = (ContinueWizardFragment) manager.findFragmentByTag(FRAGMENT_CONTINUE_TAG);
			if (continueFrag != null) {
				transac.remove(continueFrag);
			}
			
			//We put the camera fragment back on
			CameraFragment cF = (CameraFragment) getFragmentManager().findFragmentByTag(TAG_CAMERA_FRAGMENT);
			if (cF!=null){
			shutterButton.setVisibility(Button.VISIBLE);
			}
			else {
				cF = new CameraFragment();
			}
			
			//we remove the preview Fragment
			PreviewFragment previewFrag = (PreviewFragment) manager.findFragmentByTag(TAG_PREVIEW_FRAGMENT);
			if (previewFrag!= null){
				transac.remove(previewFrag);
			}
			
			//We restart the camera
			transac.commit();
			cF.restartPreview();
		}
		else {
			Intent intent = new Intent (this, ProductDetailActivity.class);
			intent.putExtra(ProductDetailActivity.TAG_PRODUCT_ID, product.getpId());
			intent.putExtra(ProductDetailActivity.TAG_LOAD_INFO, true);
			startActivity(intent);
			
			//We resume the orientation to sensor
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
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
						elementUploadFinished();
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
					elementUploadFinished();
				}
			}
			else {
				String message = context.getResources().getString(R.string.picture_upload_failure);
				Toast.makeText(context,message , Toast.LENGTH_SHORT).show();
			}
		}
	
};
}
