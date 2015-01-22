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
import android.widget.ScrollView;
import android.widget.Toast;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.model.NetworkInspector;
import com.apps4better.recycle4better.model.Product;
import com.apps4better.recycle4better.scanner.IntentIntegrator;
import com.apps4better.recycle4better.scanner.IntentResult;

public class MainActivity extends Activity {
	private Button scanButton;
	private Button findProductButton;
	private EditText productIdTextField;
	private Product product;
	private long pId;
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		//We inflate the layout and get all relevant resources
		ScrollView layout = (ScrollView) ScrollView.inflate(this, R.layout.activity_main, null);
		scanButton = (Button) layout.findViewById(R.id.buttonScan);
		findProductButton = (Button) layout.findViewById(R.id.findButton);
		productIdTextField = (EditText) layout.findViewById(R.id.productIdTextField);
		
		//We restore the values from the Bundle in case the Activity had to be recreated
		if (savedInstanceState != null){
			if (savedInstanceState.getString("pIdTextFieldValue")!= null)
			productIdTextField.setText(savedInstanceState.getString("pIdTextFieldValue"));
			if (Long.valueOf(savedInstanceState.getLong("product_id"))!=0){
				pId = savedInstanceState.getLong("product_id");
				Intent i = new Intent(context, ProductDetailActivity.class);
				i.putExtra("product_id", pId);
				i.putExtra("load_info", true);
				startActivity(i);
			}
		}
		
		setContentView(layout);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("pIdTextFieldValue", productIdTextField.getText().toString());
		//we save the product id
		if (Long.valueOf(pId) != 0) outState.putLong("product_id", pId);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		//We restore the values from the Bundle in case the Activity had to be recreated
		if (savedInstanceState.getString("pIdTextFieldValue")!= null)
		productIdTextField.setText(savedInstanceState.getString("pIdTextFieldValue"));
	}

	public void onResume(){
		super.onResume();
		
		findProductButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//check for network connection before anything
				if (NetworkInspector.haveNetworkConnection(MainActivity.this)){
					Log.d("Main activity", "giving product id = "+productIdTextField.getText().toString());
					String a = productIdTextField.getText().toString();
					long id = Long.valueOf(a).longValue();
					Intent intent = new Intent(context, ProductDetailActivity.class);
					intent.putExtra("product_id", id);
					intent.putExtra("load_info", true);
					startActivity(intent);
				}
				else {
					String text = getResources().getString(R.string.no_network_connection);
					Toast.makeText(context, text, Toast.LENGTH_LONG).show();
				}
			}
			
		});
		scanButton.setOnClickListener(new OnClickListener (){
			public void onClick (View v){
				if (NetworkInspector.haveNetworkConnection(MainActivity.this))
				startScan();
				else {
					String text = getResources().getString(R.string.no_network_connection);
					Toast.makeText(context, text, Toast.LENGTH_LONG).show();
				}
			}
		});
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
	return super.onOptionsItemSelected(item);
}
	
public void startScan (){
	Log.d("Main", "scan started");
//	Intent i = new Intent (this,ScannerActivity.class);
//	startActivity (i);
	
	IntentIntegrator integrator = new IntentIntegrator(this);
	integrator.initiateScan();
}



public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	  
	  if (scanResult.getContents() != null) {
		  		pId= Long.valueOf(scanResult.getContents()).longValue();
				Intent i = new Intent(context, ProductDetailActivity.class);
				i.putExtra("product_id", pId);
				i.putExtra("load_info", true);
				startActivity(i);
	  }	  
	}


}
