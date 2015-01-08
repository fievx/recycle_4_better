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
import android.graphics.Matrix;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.cloud.CloudStorage;
import com.apps4better.recycle4better.cloud.CloudStorageListener;

public class PictureUploaderService extends IntentService implements CloudStorageListener{
	 // URL to upload image
    private String serverAddress = "";
    private String serverUrl = "/upload_photo.php";
    
	String imageName;
	String imageFileName ;
	private static final String extension = ".jpg";
	public static final String TAG_IMAGE_NAME = "image_name";
    
    private String imagePath;
    public static final String TAG_IMAGE_PATH = "image_path";
    
    private ByteArrayOutputStream bStream;
    private int serverResponseCode = 0;
    
    public static final String CODE_IMAGE_UPLOAD = "image_upload";
	
	
	public PictureUploaderService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public PictureUploaderService (){
		super("PictureUploaderService");
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
		Log.d("PictureUploader", "picture full name is :" +imageFileName);
		
		//compress the image
		if (compress(imagePath)){
			Log.d("PictureUploader", "conversion success,starting upload now");
			//upload it to the server
			//upload ();
			this.uploadToCloudStorage();
		}
		else Log.d("PictureUploader", "Conversion failed");
	}

	/**
	 * Convert the BMP image to PNG to save space before uploading
	 * @param path
	 */
	private boolean compress (String path){
		bStream = new ByteArrayOutputStream();
		Bitmap b = BitmapFactory.decodeFile(path);
		
		float actualHeight = b.getHeight();
		float actualWidth = b.getWidth();
		Log.d("Picture uploader", "Actual Height is : "+actualHeight+" and actual width is : "+actualWidth);
		float newHeight, newWidth;
		float maxHeight = 600;
		float maxWidth = 800;
		float imgRatio = actualWidth/actualHeight;
		float maxRatio = maxWidth/maxHeight;
	    int compressionQuality = 80;//50 percent compression
		
	    //if the original image is bigger than 600x800, we rescale it
	    if (actualHeight > maxHeight || actualWidth > maxWidth){
	        if(imgRatio < maxRatio){
	            //adjust width according to maxHeight
	            imgRatio = maxHeight / actualHeight;
	            newWidth = imgRatio * actualWidth;
	            newHeight = maxHeight;
	        }
	        else if(imgRatio > maxRatio){
	            //adjust height according to maxWidth
	            imgRatio = maxWidth / actualWidth;
	            newHeight = imgRatio * actualHeight;
	            newWidth = maxWidth;
	        }
	        else{
	        	newHeight = maxHeight;
	        	newWidth = maxWidth;
	        }
	        Log.d ("Picture uploader", "New Height is : "+(int)newHeight+" and new width is : "+(int)newWidth);
	        
		    //We rescale the image to have it match our new new Height and Width
//		    //We use a Matrix approach 
//		    Matrix matrix = new Matrix ();
//		    matrix.postScale(newWidth, newHeight);
//		    Bitmap resizedBitmap = Bitmap.createBitmap(b, 0, 0, actualWidth, actualHeight, matrix, false);
	        
	        Bitmap resizedBitmap = Bitmap.createScaledBitmap(b, (int)newWidth, (int)newHeight, false);
		    
		    //We compress it using the chosen compression quality
			if (resizedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, bStream)) return true;
			else return false;
	    }
	    
	    else {
	    //We compress it using the chosen compression quality
		if (b.compress(Bitmap.CompressFormat.JPEG, compressionQuality, bStream)) return true;
		else return false;
	    }
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
			request.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + this.imageFileName + "\"" + crlf);
			request.writeBytes(crlf);
			
			//we write the ByteArrayOutputStream in the request
			request.write(bStream.toByteArray());
			
			//end content wrapper
			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
			
			 // Response code from the server
            serverResponseCode = httpUrlConnection.getResponseCode();
            if(serverResponseCode == 200){
                Log.d("PictureUploader","Server responded OK after upload");
                sendBroadcast (true);
            }   
            
          //close the streams //
            bStream.close();
            request.flush();
            request.close();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendBroadcast (false);
		} catch (ProtocolException e){
			sendBroadcast (false);
		} catch (IOException e){
			sendBroadcast (false);
		} finally {
			
		}

	}

	/*
	 * Uses a LocalBroadcastManager to send the status of the upload
	 */
	private void sendBroadcast (boolean success){
		Intent intent = new Intent (CODE_IMAGE_UPLOAD);
		intent.putExtra("success", success);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void uploadToCloudStorage (){
		try {
			CloudStorage storage = new CloudStorage (this);
			storage.uploadImageFromStream("recycle4better_images", bStream,this.imageFileName, this.getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(String param) {
		// TODO Auto-generated method stub
		switch (param){
		case CloudStorageListener.TAG_SUCCESS :
			sendBroadcast (true);
			break;			
		}
	}
}
