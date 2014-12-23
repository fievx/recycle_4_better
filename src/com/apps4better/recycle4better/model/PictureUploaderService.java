package com.apps4better.recycle4better.model;

import java.io.ByteArrayOutputStream;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.apps4better.recycle4better.R;

public class PictureUploaderService extends IntentService{
	 // URL to upload image
    private String serverAddress = "";
    private String url = "/add_element.php";
    
    private String imagePath;
    public static final String TAG_IMAGE_PATH = "image_path";
    
    private ByteArrayOutputStream bStream;
	
	
	public PictureUploaderService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		serverAddress = this.getResources().getString(R.string.server_address);
		url = serverAddress+url;
		
		//the image path retrieved from the Bundle
		imagePath = intent.getStringExtra(TAG_IMAGE_PATH);
		
		//compress the image
		if (compress(imagePath)){
			upload ();
		}
		
		//upload it to the server
	}

	/**
	 * Convert the BMP image to PNG to save space before uploading
	 * @param path
	 */
	private boolean compress (String path){
		bStream = new ByteArrayOutputStream();
		Bitmap b = BitmapFactory.decodeFile(path);
		if (b.compress(Bitmap.CompressFormat.PNG, 100, bStream)) return true;
		else return false;
	}
	
	private void upload (){
		
	}
}
