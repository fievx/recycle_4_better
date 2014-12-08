package com.apps4better.recycle4better.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.apps4better.recycle4better.R;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/*
 * The CampaignUpdater returns a JSON containing all information relating the current campaign (Sponsor name, 
 * recipient name, Goal, current donation...)
 */
public class ElementsLoader {
	
	private Context context = null;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser ();
    
    // URL that returns the updated donation
    private String serverAddress = "";
    private String url = "/i_doo_good/get_all_campaigns.php";
    
 // JSON Node name
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PRODUCT_ID = "product_id";
    private static final String TAG_ELEMENT_NUMBER = "element_number";
    private static final String TAG_ELEMENT_RECYCLABLE = "element_recyclable";
    private static final String TAG_ELEMENT_DESCRIPTION = "element_description";
    private static final String TAG_PHOTO_ID = "element_photo_id";
    private static final String TAG_ELEMENT_TRUST = "element_trust_score";

    
    //A table with all the Node names to easily iterate through them
    private static String [] nodeTable = {TAG_SUCCESS, TAG_PRODUCT, TAG_PRODUCT_ID,TAG_ELEMENT_NUMBER, TAG_ELEMENT_RECYCLABLE,
    	TAG_ELEMENT_DESCRIPTION, TAG_PHOTO_ID, TAG_ELEMENT_TRUST};
    
    private int success;
    
    private int pId;
    
    //An ArrayList which we will to store all different campaigns which will themselves be stored in Hashtables
    private ArrayList <Hashtable> elementList = new ArrayList <Hashtable> ();
    
    // products JSONArray
    JSONArray campaigns = null;
    
    //	Public constructor which takes an int as parameter. The int is the campaign Id and is passed on
    // to the JSONParser
    public ElementsLoader (Context context){
    	this.context = context;
    	this.serverAddress=context.getResources().getString(R.string.server_address);
    	this.url = this.serverAddress + this.url;
    }
    
    /*
     * Load gets all values from Db for the given Id without creating a background thread.
     * Use this method if you are creating an instance of CampaignUpdater inside a backgroung thread.
     * If not, use the loadInBackground() method. 
     */
    public void load (){

        try {
            // Building empty Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // getting product details by making HTTP request
            // Note that product details url will use GET request
            JSONObject json = jParser.makeHttpRequest(url, "GET", params);
              
         // json success tag
            success = json.getInt(TAG_SUCCESS);
            if (success == 1 ) {
            	Hashtable hashTable = new Hashtable();
                // successfully received product details
                JSONArray productObj = json.getJSONArray(TAG_PRODUCT); // JSON Array

                // get each product object from JSON Array and store them in the Hashtable
                for (int i = 0 ; i<productObj.length(); i++){
	                JSONObject product = productObj.getJSONObject(i);
	                for (String node : nodeTable){
	                	hashTable.put(node, product.getString(node));
	                }
	                this.elementList.add(hashTable);
                }

            }else{
                // Campaign was not found
            }
            } catch (JSONException e) {
            	e.printStackTrace();
            } catch (Exception e){
            	e.printStackTrace();
            }
        	
        
    }

public ArrayList getElementList (){
	return elementList;
}

}
