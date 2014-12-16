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
	private static final int RADIO_NO = R.id.radio_not_recyclable;
	private static final int RADIO_YES = R.id.radio_recyclable;
	private static final int RADIO_NOT_SURE = R.id.radio_not_sure;
	private ScrollView layout;
	
	private Element element = new Element();
	
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//get the infos from the Intent.
		Bundle extras = getIntent().getExtras();
		element.setProductId(extras.getInt("pId"));
		element.setNumber(extras.getInt("elementNumber"));
		
		layout = (ScrollView) ScrollView.inflate(this,R.layout.activity_add_element_layout, null);
		
		//We get all the widgets from the layout
		this.addPhotoButton = (Button) layout.findViewById(R.id.add_element_picture_button);
		this.saveElementButton = (Button) layout.findViewById(R.id.save_element_button);
		this.eNameEdit = (EditText) layout.findViewById(R.id.element_name_edit_text);
		this.eDescEdit = (EditText) layout.findViewById(R.id.element_descr_edit_text);
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
		
	  }
	

	
	
	/*
	 * Method save element perfoms all the checks on the form and calls
	 * SaveElementTask if successfull
	 */
	private void saveElement (){
		element.setName(eNameEdit.getText().toString());
		element.setDescription(eDescEdit.getText().toString());
		if (eWeightEdit.getText().toString().equals("")==false)element.setWeight(Integer.valueOf(eWeightEdit.getText().toString()).intValue());
		switch (this.eRecyclableRadio.getCheckedRadioButtonId()){
			case RADIO_NO :
				element.setRecyclable(0);
				break;
			case RADIO_YES:
				element.setRecyclable(1);
				break;
			case RADIO_NOT_SURE:
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

	/*
	 * Start the ElementEditorService with the information saved
	 * and then returns to the productDetailActivity
	 */
	private void uploadElement(){
		Intent intent = new Intent (this, ElementEditorService.class);
		intent.putExtra("element", element);
		startService(intent);
		intent = new Intent (this, ProductDetailActivity.class);
		intent.putExtra("product_id", element.getProductId());
		startActivity(intent);
	}
	
	

}
