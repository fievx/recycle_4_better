package com.apps4better.recycle4better.camera;

import java.io.File;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.view.NewElementFragment;
import com.commonsware.cwac.camera.CameraFragment;



public class MyCameraActivity extends Activity implements PreviewFragmentObserver{
	private final String TAG_CAMERA_FRAGMENT = "camera_fragment";
	private final String TAG_PREVIEW_FRAGMENT = "preview_fragment";
	private String photoName;
	private RelativeLayout layout;
	private Button shutterButton;
	private File pictureFile;
	private MyCameraHost cameraHost;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//We lock the orientation to portrait to have all pictures in portrait mode
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		photoName = getIntent().getStringExtra("photo_name");
		layout = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_camera ,null);
		shutterButton = (Button) layout.findViewById(R.id.shutter_button);
		setContentView(layout);
		initCamera();
	}
	
	
	 /**
     * Checks that the CameraFragment exists and is visible to the user,
     * then takes a picture.
     */
    private void takePicture() {
        CameraFragment f = (CameraFragment) getFragmentManager().findFragmentByTag(TAG_CAMERA_FRAGMENT);
        if (f != null && f.isVisible()) {
        	Time timeText = new Time();
        	timeText.setToNow();
            cameraHost.setPhotoName(photoName+timeText.second);
        	f.takePicture();
            pictureFile = cameraHost.getPhotoPath();
            Log.d("camera activity", "photo path is : "+pictureFile.getAbsolutePath());
        }
    }
    
    /**
     * start the preview fragment.
     */
    private void startPreview (){
        shutterButton.setVisibility(Button.GONE);
    	PreviewFragment pF = PreviewFragment.getInstance(pictureFile.getAbsolutePath());
    	
    	FragmentTransaction transaction = getFragmentManager().beginTransaction();
    	transaction.add(R.id.camera_container, pF, TAG_PREVIEW_FRAGMENT).commit();
    }


	@Override
	public void update(String param) {
		// TODO Auto-generated method stub
		switch (param){
		case "save_photo" :
			Log.d("MyCameraActivity", "Update method called with parameter : save_photo");
			Intent intent = new Intent ();
			intent.putExtra(NewElementFragment.TAG_IMAGE_PATH, pictureFile.getAbsolutePath());
			setResult(Activity.RESULT_OK, intent);
			
			//We resume the orientation to sensor
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			finish();
			break;
		case "retake_photo" :
			Log.d("MyCameraActivity", "Update method called with parameter : retake_photo");
			delFile (pictureFile.getAbsolutePath());
			CameraFragment cF = (CameraFragment) getFragmentManager().findFragmentByTag(TAG_CAMERA_FRAGMENT);
			shutterButton.setVisibility(Button.VISIBLE);
			FragmentTransaction transac = getFragmentManager().beginTransaction();
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

	public void initCamera (){
		//Create the CameraFragment and add it to the layout
        CameraFragment f = new CameraFragment();
        getFragmentManager().beginTransaction().add(R.id.camera_container, f, TAG_CAMERA_FRAGMENT).commit();
 
        //Set the CameraHost
        cameraHost = new MyCameraHost(this, this);
        f.setHost(cameraHost);
		
      //Set an onClickListener for a shutter button
        shutterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
               
            }
        });
	}

	
	private void delFile(String filePath) {
	    try {
	        // delete the original file
	    	if(new File(filePath).delete())
	        Log.d("File Delete", "dele file success");
	    	else Log.d("File Delete", "dele file failure");
	    	}
	   catch (Exception e) {
	        Log.e("tag", e.getMessage());
	        Log.d("File Delete", "dele file failure");
	    }
	}


	

}
