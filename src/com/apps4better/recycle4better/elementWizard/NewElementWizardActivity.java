package com.apps4better.recycle4better.elementWizard;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.camera.MyCameraActivity;
import com.apps4better.recycle4better.camera.PreviewFragment;
import com.apps4better.recycle4better.camera.PreviewFragmentObserver;
import com.apps4better.recycle4better.model.Element;
import com.apps4better.recycle4better.model.Product;
import com.commonsware.cwac.camera.CameraFragment;

public class NewElementWizardActivity extends MyCameraActivity implements PreviewFragmentObserver, 
			WizardFragmentObserver {
	private Product product;
	private Element element;
	
	//Fragment Tags
	public static final String FRAGMENT_ONE_TAG = "fragment_one";
	public static final String FRAGMENT_RECYCLABLE_TAG = "fragment_recyclable";

	//Intent Tag
	public static final String PRODUCT_TAG	= "product";
	/* (non-Javadoc)
	 * @see com.apps4better.recycle4better.camera.MyCameraActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		NewElementWizardFragmentOne frag = new NewElementWizardFragmentOne();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fragment_container, frag, FRAGMENT_ONE_TAG);
		transaction.commit();
		
		product = getIntent().getParcelableExtra(PRODUCT_TAG);
		element = new Element();
		element.setNumber(product.getNextElementNumber());
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
			element.setPhotoId(pictureFile.getAbsolutePath());
			
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


	@Override
	public void setElementRecyclable(int recyclable) {
		// TODO Auto-generated method stub
		switch (recyclable){
			
		}
	}
	

	
}
