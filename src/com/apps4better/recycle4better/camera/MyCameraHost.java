package com.apps4better.recycle4better.camera;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

public class MyCameraHost extends SimpleCameraHost {
	private String photoName;
	private String extension = ".jpg";
	private Activity activity;
	
	public MyCameraHost(Context _ctxt, Activity activity) {
		super(_ctxt);
		this.activity = activity;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected String getPhotoFilename() {
		// TODO Auto-generated method stub
		return this.photoName + this.extension;
	}

	public void setPhotoName (String name){
		this.photoName = name;
	}
	
	 
	 @Override
	 public boolean useSingleShotMode() {
	     return true;
	 }


	@Override
	protected File getPhotoPath() {
		// TODO Auto-generated method stub
		return super.getPhotoPath();
	}






	@Override
	public void saveImage(PictureTransaction xact, Bitmap bitmap) {
		// TODO Auto-generated method stub
		super.saveImage(xact, bitmap);
		
		//Once the photo is saved, we notify the CameraActivity to start the preview fragment
		((PreviewFragmentObserver) activity).update("start_preview");
	}

	@Override
	public void saveImage(PictureTransaction arg0, byte[] arg1) {
		// TODO Auto-generated method stub
		super.saveImage(arg0, arg1);
		//Once the photo is saved, we notify the CameraActivity to start the preview fragment
		((PreviewFragmentObserver) activity).update("start_preview");
	}



}
