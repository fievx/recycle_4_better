package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.ListView.MyAdapter;
import com.apps4better.recycle4better.ListView.MyAdapterListener;
import com.apps4better.recycle4better.model.Element;
import com.apps4better.recycle4better.model.ElementDetailObserver;
import com.apps4better.recycle4better.model.ElementsLoader;
import com.apps4better.recycle4better.model.Product;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends Activity implements MyAdapterListener, ElementDetailObserver{
	private Context context;
	private Product product;
	private int pId;
	
	/*
	 *  the loadInfo boolean is used to tell the activity if it must download product information 
	 *  if set to false, the Activity will not download the info
	 *  We use it so that the activity will wait to receive an answer from either the ElementEditorService or
	 *  ProductEditorService befor loading the product info
	 */
	private boolean loadInfo = true;
	
	private RelativeLayout layout;
	private ImageView pPhotoView;
	private TextView pBrandText;
	private TextView pModelText;
	private Button addElementButton;
	
	private static final String extansion = ".png";
	
	//Fragment tags
	private static final String TAG_NEW_ELEMENT_FRAGMENT = "new_element";
	private static final String TAG_ELEMENT_DETAIL = "element_detail";
	
	public void onCreate (Bundle bundle){
		super.onCreate(bundle);
		context = this;

		//We get the ProductId from the Intent
		pId = getIntent().getIntExtra("product_id", 0);
		loadInfo = getIntent().getBooleanExtra("load_info", true);

		
	}
	
	protected void onResume(){
		super.onResume();
		
		//We call the GetProductDetailTask
		if (loadInfo) new GetProductDetailTask(pId).execute();
		LocalBroadcastManager.getInstance(this).registerReceiver(saveElementReceiver, new IntentFilter("message"));
		
	}

  @Override
  	protected void onPause() {
    super.onPause();
    try{
    LocalBroadcastManager.getInstance(this).unregisterReceiver(saveElementReceiver);
    }catch (Exception e){
    	e.printStackTrace();
    }
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
				Log.d("debug recycler", "le produit contient "+String.valueOf(product.getElementCount()));
				
				//We build the layout, first with the product details, and then the elements using the RecyclerView
				layout = (RelativeLayout) RelativeLayout.inflate(context, R.layout.activity_product_detail, null);
				setContentView(layout);
				pPhotoView = (ImageView) layout.findViewById(R.id.product_photo_image_view);
				pBrandText = (TextView) layout.findViewById(R.id.product_name_text_view);
				pModelText =(TextView) layout.findViewById(R.id.product_desc_text_view);
				addElementButton = (Button) layout.findViewById(R.id.add_element_button);
				
				// First the product
				String imageUrl = context.getResources().getString(R.string.server_address)+product.getPhotoId()+extansion;
				Log.d("picasso", imageUrl);
				Picasso.with(context).load(imageUrl).fit().into(pPhotoView);
				pBrandText.setText(product.getBrand());
				pModelText.setText(product.getModel());
				
				//We set a listener on the button
				addElementButton.setOnClickListener(new OnClickListener (){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Element e = new Element ();
						e.setNumber(product.getElementList().size()+1);
						e.setProductId(product.getpId());
						displayNewElementFragment (e);
					}
					
				});
				
				//We set the recyclerView with the elements from the product
				RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.my_recycler_view);
				recyclerView.setHasFixedSize(true);
				recyclerView.setAdapter(new MyAdapter(product.getElementList(), ProductDetailActivity.this));
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
				recyclerView.setItemAnimator(new DefaultItemAnimator());
				Log.d("getProductDetailClass", "layout for recycler View loaded");
			}
			//we start the product not found activity
			else {
				Intent intent = new Intent (context, NewProductActivity.class);
				intent.putExtra("product_id", pId);
				startActivity(intent);
			}
		}
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

				//We reaload the product
				new GetProductDetailTask(pId).execute();
			}
			else {
				String message = context.getResources().getString(R.string.element_edit_failure);
				Toast.makeText(context,message , Toast.LENGTH_SHORT).show();
			}
				
			
		}
		
	};

	
	@Override
	public void displayElementFragment(int elementId) {
		// TODO Auto-generated method stub
		ElementDetailFragment frag = ElementDetailFragment.getInstance(product.getElementById(elementId));
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.addToBackStack(null);
		transaction.add(R.id.product_detail_fragment_container, frag, ProductDetailActivity.TAG_ELEMENT_DETAIL).commit();
	}

	public void displayNewElementFragment (Element e){
		NewElementFragment fragment = NewElementFragment.getInstance(e);
		FragmentTransaction transac = getFragmentManager().beginTransaction();
		//if there is an ElementDetailFramgent, we remove it
		ElementDetailFragment f = (ElementDetailFragment) getFragmentManager().findFragmentByTag(TAG_NEW_ELEMENT_FRAGMENT);
		if ( f != null)
			transac.remove(f);
		transac.add(R.id.product_detail_fragment_container, fragment, ProductDetailActivity.TAG_NEW_ELEMENT_FRAGMENT);
		transac.addToBackStack(null).commit();
	}
	
	@Override
	public void editElement(Element element) {
		// TODO Auto-generated method stub
		displayNewElementFragment(element);
	}	
}
