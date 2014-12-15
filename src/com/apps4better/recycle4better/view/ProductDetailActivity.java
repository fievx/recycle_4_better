package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.ListView.MyAdapter;
import com.apps4better.recycle4better.model.ElementsLoader;
import com.apps4better.recycle4better.model.Product;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends Activity {
	private Context context;
	private Product product;
	private int pId;
	
	private RelativeLayout layout;
	private ImageView pPhotoView;
	private TextView pNameText;
	private TextView pDescText;
	private Button addElementButton;
	
	private static final String extansion = ".bmp";
	
	public void onCreate (Bundle bundle){
		super.onCreate(bundle);
		context = this;


		
	}
	
	protected void onResume(){
		super.onResume();
		
		//We get the ProductId from the Intent
		pId = getIntent().getIntExtra("product_id", 0);	
		
		//We call the GetProductDetailTask
		new GetProductDetailTask(pId).execute();
		
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
				//We build the layout, first with the product details, and then the elements using the RecyclerView
				layout = (RelativeLayout) RelativeLayout.inflate(context, R.layout.activity_product_detail, null);
				setContentView(layout);
				pPhotoView = (ImageView) layout.findViewById(R.id.product_photo_image_view);
				pNameText = (TextView) layout.findViewById(R.id.product_name_text_view);
				pDescText =(TextView) layout.findViewById(R.id.product_desc_text_view);
				
				
				// First the product
				String imageUrl = context.getResources().getString(R.string.server_address)+product.getPhotoId()+extansion;
				Log.d("picasso", imageUrl);
				Picasso.with(context).load(imageUrl).fit().into(pPhotoView);
				pNameText.setText(product.getName());
				pDescText.setText(product.getDescription());
				
				RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
				recyclerView.setHasFixedSize(true);
				recyclerView.setAdapter(new MyAdapter(product.getElementList()));
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
				recyclerView.setItemAnimator(new DefaultItemAnimator());
				Log.d("getProductDetailClass", "layout for recycler View loaded");
			}
			//we start the product not found activity
			else {
				
			}
		}
	}
	
}
