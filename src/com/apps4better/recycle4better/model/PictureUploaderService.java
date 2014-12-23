package com.apps4better.recycle4better.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.apps4better.recycle4better.R;

public class PictureUploaderService extends IntentService{
	 // URL to upload image
    private String serverAddress = "";
    private String serverUrl = "";
    
	String imageName;
	String imageFileName ;
	private static final String extension = ".png";
	public static final String TAG_IMAGE_NAME = "image_name";
    
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
		serverUrl = serverAddress+serverUrl;
		
		//the image path and file name are retrieved from the Bundle
		imagePath = intent.getStringExtra(TAG_IMAGE_PATH);
		imageName = intent.getStringExtra (TAG_IMAGE_NAME);
		imageFileName = imageName + extension;
		
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
		//static information needed to wrap the file
		String crlf = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";
		
		//Setup the request
		HttpURLConnection httpUrlConnection = null;
		URL url;
		try {
			url = new URL(serverUrl);
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setDoOutput(true);

			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
			httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			
			//start content wrapper
			DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());

			request.writeBytes(twoHyphens + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"" + this.imageName + "\";filename=\"" + this.imageFileName + "\"" + crlf);
			request.writeBytes(crlf);
			
			//we write the ByteArrayOutputStream in the request
			request.write(bStream.toByteArray());
			
			//end content wrapper
			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e){
			
		} catch (IOException e){
			
		} finally {
			
		}

	}
}
