package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.model.Element;
import com.apps4better.recycle4better.model.ElementEditorService;

public class NewElementActivity extends Activity{
	private Button addPhotoButton, saveElementButton;
	private EditText eNameEdit, eDescEdit, eWeightEdit, eMaterialCommonEdit, eMaterialScientEdit;
	private RadioGroup eRecyclableRadio;
	private ScrollView layout;
	
	private int pId;
	private int elementNumber;
	
	private Element element = new Element();
	
	
	public void onCreate (Bundle savedInstanceState){
		//get the infos from the Intent.
		Bundle extras = getIntent().getExtras();
		pId = extras.getInt("pId");
		elementNumber = extras.getInt("elementNumber");
		
		layout = (ScrollView) ScrollView.inflate(this,R.layout.activity_add_element_layout, null);
		
		//We get all the widgets from the layout
		this.addPhotoButton = (Button) layout.findViewById(R.id.add_element_picture_button);
		this.saveElementButton = (Button) layout.findViewById(R.id.save_element_button);
		this.eNameEdit = (EditText) layout.findViewById(R.id.element_name_edit_text);
		this.eDescEdit = (EditText) layout.findViewById(R.id.element_descr_edit_text);
		this.eWeightEdit = (EditText) layout.findViewById(R.id.element_weight_edit_text);
		this.eMaterialCommonEdit = (EditText) layout.findViewById(R.id.element_material_common_edit_text);
		this.eMaterialScientEdit = (EditText) layout.findViewById(R.id.element_material_scientific_edit_text);
		this.eRecyclableRadio = (RadioGroup) layout.findViewById(R.id.element_recyclable_radio_group);
		
		//We add listeners to the two buttons
		addPhotoButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takePhoto();
			}
			
		});
		saveElementButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveElement();
			}
			
		});
		
		setContentView(layout);
	}
	
	public void takePhoto (){
		
	}
	
	
	@Override
	  protected void onResume() {
	    super.onResume();
	    LocalBroadcastManager.getInstance(this).registerReceiver(saveElementReceiver, new IntentFilter("messageSave"));
	  }
	  @Override
	  protected void onPause() {
	    super.onPause();
	    unregisterReceiver(saveElementReceiver);
	  }
	
	
	/*
	 * Method save element perfoms all the checks on the form and calls
	 * SaveElementTask if successfull
	 */
	private void saveElement (){
		element.setName(eNameEdit.getText().toString());
		element.setDescription(eDescEdit.getText().toString());
		element.setWeight(Integer.valueOf(eWeightEdit.getText().toString()).intValue());
		switch (this.eRecyclableRadio.getCheckedRadioButtonId()){
			case 1 :
				element.setRecyclable(0);
				break;
			case 2:
				element.setRecyclable(1);
				break;
			case 3:
				element.setRecyclable(2);
				break;
			default:
				element.setRecyclable(2);
				break;
		}
		element.setMaterialCommon(this.eMaterialCommonEdit.getText().toString());
		element.setMaterialScientific(this.eMaterialScientEdit.getText().toString());
		
		if (eNameEdit.getText().toString() != null){
			uploadElement();		
	}
		else {
			String a = getResources().getString(R.string.no_name_entered_toast);
			Toast.makeText(this,a , Toast.LENGTH_SHORT).show();
		}
	}

	private void uploadElement(){
		Intent intent = new Intent (this, ElementEditorService.class);
		intent.putExtra("element", element);
		startService(intent);
	}
	
	
	//We need a receiver to handle the broadcast sent from the ElementEditorService
	private BroadcastReceiver  saveElementReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			//If the element was successfully saved, display a succes toast and start ProductDetailActivity
			if (intent.getExtras().getBoolean("success")){
				String message = context.getResources().getString(R.string.element_edit_success);
				Toast.makeText(context,message , Toast.LENGTH_SHORT).show();
				Intent i = new Intent();
				i.putExtra("product_id", pId);
			}
			else {
				String message = context.getResources().getString(R.string.element_edit_failure);
				Toast.makeText(context,message , Toast.LENGTH_SHORT).show();
			}
				
			
		}
		
	};	
}
