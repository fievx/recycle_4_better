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

public class ElementEditorService extends IntentService{
	private Element element;
	 // URL that edits the element
    private String serverAddress = "";
    private String url = "/add_element.php";
    
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser ();
    
    // JSON Node name
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT_ID = "product_id";   
    private static final String TAG_ELEMENT_NUMBER = "element_number";
    private static final String TAG_ELEMENT_RECYCLABLE = "element_recyclable";
    private static final String TAG_ELEMENT_DESCRIPTION = "element_description";
    private static final String TAG_ELEMENT_NAME = "element_name";
    private static final String TAG_PHOTO_ID = "element_photo_id";
    private static final String TAG_ELEMENT_MATERIAL_COMMON = "element_material_common";
    private static final String TAG_ELEMENT_MATERIAL_SCIENTIFIC = "element_material_scientific";
    private static final String TAG_ELEMENT_WEIGHT = "element_weight";
    private static final String TAG_ELEMENT_TRUST = "element_trust_score";
    
	public static final String  CODE_ELEMENT_UPLOAD = "element_upload";

	public ElementEditorService() {
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
		element = extra.getParcelable("element");
		
		uploadElement();		
	}
	
	private void uploadElement(){
		int success; 
		try {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	        	params.add(new BasicNameValuePair(TAG_PRODUCT_ID, String.valueOf(element.getProductId())));
	        	params.add(new BasicNameValuePair(TAG_ELEMENT_NAME, element.getName()));
	        	params.add(new BasicNameValuePair(TAG_ELEMENT_DESCRIPTION, element.getDescription()));
	        	params.add(new BasicNameValuePair(TAG_PHOTO_ID, element.getPhotoId()));
	        	params.add(new BasicNameValuePair(TAG_ELEMENT_NUMBER, String.valueOf(element.getNumber())));
	        	params.add(new BasicNameValuePair(TAG_ELEMENT_RECYCLABLE, String.valueOf(element.getRecyclable())));
	        	params.add(new BasicNameValuePair(TAG_ELEMENT_MATERIAL_COMMON, element.getMaterialCommon()));
	        	params.add(new BasicNameValuePair(TAG_ELEMENT_MATERIAL_SCIENTIFIC, element.getMaterialScientific()));
	        	params.add(new BasicNameValuePair(TAG_ELEMENT_MATERIAL_COMMON, element.getMaterialCommon()));
	        	params.add(new BasicNameValuePair(TAG_ELEMENT_WEIGHT, String.valueOf(element.getWeight())));
	        	params.add(new BasicNameValuePair(TAG_ELEMENT_TRUST, String.valueOf(element.getTrustScore())));

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
		Intent intent = new Intent (CODE_ELEMENT_UPLOAD);
		intent.putExtra("success", success);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
}
