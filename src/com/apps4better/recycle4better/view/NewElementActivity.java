package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.model.Element;

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
	
	/*
	 * Method save element perfoms all the checks on the form and calls
	 * SaveElementTask if successfull
	 */
	public void saveElement (){
		String name = eNameEdit.getText().toString();
		String desc = eDescEdit.getText().toString();
		int weight = Integer.valueOf(eWeightEdit.getText().toString()).intValue();
		int recyclable;
		switch (this.eRecyclableRadio.getCheckedRadioButtonId()){
			case 1 :
				recyclable = 0;
				break;
			case 2:
				recyclable = 1;
				break;
			case 3:
				recyclable = 2;
				break;
			default:
				recyclable = 2;
				break;
		}
		String materialCommon = this.eMaterialCommonEdit.getText().toString();
		String materialScient = this.eMaterialScientEdit.getText().toString();
		
		if (eNameEdit.getText().toString() != null){
			new SaveElementTask()execute();			
	}
		else {
			String a = getResources().getString(R.string.no_name_entered_toast);
			Toast.makeText(this,a , Toast.LENGTH_SHORT).show();
		}
	}
}
