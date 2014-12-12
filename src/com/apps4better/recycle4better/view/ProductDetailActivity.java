package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.content.Context;
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

		//We get the parcelable Product from the Intent
		product = getIntent().getParcelableExtra("product");
		Log.d("product detail", "received id is "+pId);
		
		//We build the layout, first with the product details, and then the elements using the RecyclerView
		layout = (RelativeLayout) RelativeLayout.inflate(context, R.layout.activity_product_detail, null);
		setContentView(layout);
		pPhotoView = (ImageView) layout.findViewById(R.id.product_photo_image_view);
		pNameText = (TextView) layout.findViewById(R.id.product_name_text_view);
		pDescText =(TextView) layout.findViewById(R.id.product_desc_text_view);
		
		
		// First the product
		Picasso.with(context).load(context.getResources().getString(R.string.server_address)+product.getPhotoId()+extansion).into(pPhotoView);
		pNameText.setText(product.getName());
		pDescText.setText(product.getDescription());
		
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(new MyAdapter(product.getElementList()));
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		Log.d("getProductDetailClass", "layout for recycler View loaded");
	}
	
	
}
