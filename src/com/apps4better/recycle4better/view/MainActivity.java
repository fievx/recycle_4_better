package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps4better.recycle4better.R;

public class MainActivity extends Activity {
	private Button scanButton;
	private TextView bcTypeView;
	private TextView bcResultView;
	private Button findProductButton;
	private EditText productIdTextField;
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		//We inflate the layout and get all relevant resources
		RelativeLayout layout = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_main, null);
		scanButton = (Button) layout.findViewById(R.id.buttonScan);
		bcTypeView = (TextView) layout.findViewById(R.id.codeTypeText);
		bcResultView = (TextView) layout.findViewById(R.id.codeResultText);
		findProductButton = (Button) layout.findViewById(R.id.findButton);
		productIdTextField = (EditText) layout.findViewById(R.id.productIdTextField);
		
		findProductButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("Main activity", "giving product id = "+productIdTextField.getText().toString());
				Intent intent = new Intent (context, ProductDetailActivity.class);
				intent.putExtra("product_id", productIdTextField.getText().toString());
				startActivity(intent);
			}
			
		});
		
		setContentView(layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
public boolean onOptionsItemSelected(MenuItem item) {
	// Handle action bar item clicks here. The action bar will
	// automatically handle clicks on the Home/Up button, so long
	// as you specify a parent activity in AndroidManifest.xml.
	int id = item.getItemId();
	if (id == R.id.action_settings) {
		return true;
	}
	return super.onOptionsItemSelected(item);
}
	
public void startScan (View view){
	Log.d("Main", "scan started");
	Intent i = new Intent (this,ScannerActivity.class);
	startActivity (i);
}
	

}
