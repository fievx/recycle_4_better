package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.apps4better.recycle4better.model.ElementsLoader;
import com.apps4better.recycle4better.model.Product;

public class MainActivity extends Activity {
	private Button scanButton;
	private TextView bcTypeView;
	private TextView bcResultView;
	private Button findProductButton;
	private EditText productIdTextField;
	private Product product;
	
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
				String a = productIdTextField.getText().toString();
				int id = Integer.valueOf(a).intValue();
				new GetProductDetailTask(id).execute();
				
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
	
protected class GetProductDetailTask extends AsyncTask <Void, Void, Void> {
	ElementsLoader loader;
	int pId;
	public GetProductDetailTask (int pId){
		this.pId = pId;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		loader = new ElementsLoader (context, pId);
		loader.load();
		return null;
	}
	
	protected void onPostExecute (Void param){
		product = loader.getProduct();
		
		//If the loader returns a non null product, we start the ProductDetailActivity and give it the 
		//product as extra.
		if (product != null){
			Intent intent = new Intent (context, ProductDetailActivity.class);
			intent.putExtra("product", product);
			startActivity(intent);
		}
		//we start the product not found activity
		else {
			
		}
	}
}

}
