package com.apps4better.recycle4better.model;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.apps4better.recycle4better.R;

public class ElementEditor extends IntentService{
	private Element element;
	 // URL that edits the element
    private String serverAddress = "";
    private String url = "/add_element.php";
    
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser ();

	public ElementEditor(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		serverAddress = this.getResources().getString(R.string.server_address);
		url = serverAddress+url;
		
		//We get the element from the Intent
		Bundle extra = intent.getExtras();
		
	}

}
