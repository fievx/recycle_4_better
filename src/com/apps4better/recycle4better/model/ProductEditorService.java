package com.apps4better.recycle4better.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.view.NewProductActivity;

public class ProductEditorService extends IntentService{
	private Product product;
	 // URL that edits the element
    private String serverAddress = "";
    private String url = "/add_product.php";
    
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser ();
    
    // JSON Node name
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT_ID = "product_id";   
    private static final String TAG_PRODUCT_BRAND = "product_brand";
    private static final String TAG_PRODUCT_MODEL = "product_model";
    private static final String TAG_PHOTO_ID = "product_photo_id";
    
    public static final String  CODE_PRODUCT_UPLOAD = "product_upload";


	public ProductEditorService() {
		super("ElementEditorService");
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		serverAddress = this.getResources().getString(R.string.server_address);
		url = serverAddress+url;
		
		//We get the element from the Intent
		Bundle extra = intent.getExtras();
		product = extra.getParcelable("product");
		
		uploadProduct();		
	}
	
	private void uploadProduct(){
		int success; 
		try {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	        	params.add(new BasicNameValuePair(TAG_PRODUCT_ID, String.valueOf(product.getpId())));
	        	params.add(new BasicNameValuePair(TAG_PRODUCT_BRAND, product.getBrand()));
	        	params.add(new BasicNameValuePair(TAG_PRODUCT_MODEL, product.getModel()));
	        	params.add(new BasicNameValuePair(TAG_PHOTO_ID, product.getPhotoId()));

	            // getting product details by making HTTP request
	            // Note that product details url will use GET request
	            JSONObject json = jParser.makeHttpRequest(url, "GET", params);
	              
	         // json success tag
	            success = json.getInt(TAG_SUCCESS);
	            if (success == 1 ) {
	            	sendBroadcast(true);
	            	
	            }else{
             sendBroadcast(false);
         }
         } catch (JSONException e) {
         	e.printStackTrace();
         } catch (Exception e){
         	e.printStackTrace();
         }
		finally {
			
		}
	}

	/*
	 * Uses a LocalBroadcastManager to send the status of the upload
	 */
	private void sendBroadcast (boolean success){
		Intent intent = new Intent (CODE_PRODUCT_UPLOAD);
		intent.putExtra("success", success);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
}
