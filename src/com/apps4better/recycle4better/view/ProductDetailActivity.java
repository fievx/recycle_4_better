package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.ListView.MyAdapter;
import com.apps4better.recycle4better.model.ElementsLoader;
import com.apps4better.recycle4better.model.Product;

public class ProductDetailActivity extends Activity {
	private Context context;
	private Product product;
	private int pId;
	
	public void onCreate (Bundle bundle){
		super.onCreate(bundle);
		context = this;

		
		Bundle extras = getIntent().getExtras();
		String a = extras.getString("product_id");
		pId = Integer.valueOf(a).intValue();
		Log.d("product detail", "received id is "+pId);
		
		//We start the getProductDetailTask which will run the DB request, 
		//return the product details and generate the RecyclerView
		new getProductDetailTask (pId).execute();
		
	}

	
	protected class getProductDetailTask extends AsyncTask <Void, Void, Void> {
		ElementsLoader loader;
		int pId;
		public getProductDetailTask (int pId){
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
			if (product != null){
				setContentView(R.layout.activity_product_detail);
				RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
				recyclerView.setHasFixedSize(true);
				recyclerView.setAdapter(new MyAdapter(product.getElementList()));
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
				recyclerView.setItemAnimator(new DefaultItemAnimator());
				Log.d("getProductDetailClass", "layout for recycler View loaded");
			}
		}
	}
}
