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
import com.apps4better.recycle4better.model.ProductDetailObserver;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends Activity implements MyAdapterListener, ElementDetailObserver, ProductDetailObserver{
	private Context context;
	private Product product;
	private int pId;
	
	/*
	 *  the loadInfo boolean is used to tell the activity if it must download product information 
	 *  if set to false, the Activity will not download the info
	 *  We use it so that the activity will wait to receive an answer from either the ElementEditorService or
	 *  ProductEditorService befor loading the product info
	 */
	private boolean loadInfo = false;
	
	private RelativeLayout layout, headerLayout;
	private ImageView pPhotoView;
	private TextView pBrandText;
	private TextView pModelText;
	private Button addElementButton;
	
	//tags used for the saved instance state
	private static final String TAG_SAVED_PRODUCT = "saved_product";
	private static final String TAG_LOAD_INFO = "load_info";
	
	String extension; 
	
	//Fragment tags
	private static final String TAG_NEW_ELEMENT_FRAGMENT = "new_element";
	private static final String TAG_NEW_PRODUCT_FRAGMENT = "new_product";
	private static final String TAG_ELEMENT_DETAIL_FRAGMENT = "element_detail";
	private static final String TAG_PRODUCT_DETAIL_FRAGMENT = "product_detail";
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		/*
		 * The view is not build in the onCreate method but in the 
		 * GetProductDetailTask.
		 */
		
		context = this;
		extension = getResources().getString(R.string.image_extension);

		//We get the ProductId from the Intent
		pId = getIntent().getIntExtra("product_id", 0);
		loadInfo = getIntent().getBooleanExtra("load_info", false);
		
		//If the savedInstanceBundle is not null, we rebuild the view from here without using the GetProductDetailTask
		if (savedInstanceState != null){
			product = savedInstanceState.getParcelable(TAG_SAVED_PRODUCT);
			loadInfo = savedInstanceState.getBoolean (TAG_LOAD_INFO);
			
			//We build the layout, first with the product details, and then the elements using the RecyclerView
			layout = (RelativeLayout) RelativeLayout.inflate(context, R.layout.activity_product_detail, null);
			setContentView(layout);
			headerLayout = (RelativeLayout) layout.findViewById(R.id.product_view_header);
			pPhotoView = (ImageView) layout.findViewById(R.id.product_photo_image_view);
			pBrandText = (TextView) layout.findViewById(R.id.product_name_text_view);
			pModelText =(TextView) layout.findViewById(R.id.product_desc_text_view);
			addElementButton = (Button) layout.findViewById(R.id.add_element_button);
			
			// First the product
			String imageUrl = context.getResources().getString(R.string.image_bucket_url)+product.getPhotoId();
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
			
			//We set a listener on the header to display the ProductDetailFragment
			headerLayout.setOnClickListener(new OnClickListener (){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					displayProductDetailFragment(product);				
				}
				
			});
			
			//we set the fragments from the Fragment Manager
			FragmentTransaction transac = getFragmentManager().beginTransaction();
			ElementDetailFragment detailFrag = (ElementDetailFragment) getFragmentManager().findFragmentByTag(TAG_ELEMENT_DETAIL_FRAGMENT);
			NewElementFragment newElementFrag = (NewElementFragment) getFragmentManager ().findFragmentByTag(TAG_NEW_ELEMENT_FRAGMENT);
			
			if (detailFrag != null){
				transac.add(detailFrag, TAG_ELEMENT_DETAIL_FRAGMENT).commit();
			}
			if (newElementFrag != null){
				transac.add(newElementFrag, TAG_NEW_ELEMENT_FRAGMENT);
			}
		}

		
	}
	
	protected void onResume(){
		super.onResume();
		/*
		 * We check if there is a fragment built that should be visible. If yes, we don't build the view
		 */
		NewElementFragment f = (NewElementFragment) getFragmentManager().findFragmentByTag(TAG_NEW_ELEMENT_FRAGMENT);
		NewProductFragment fr = (NewProductFragment) getFragmentManager().findFragmentByTag(TAG_NEW_PRODUCT_FRAGMENT);
		if (f!=null||fr!=null){
			loadInfo = false;
		}

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
	
	
  
	/* (non-Javadoc)
 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
 */
@Override
protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onRestoreInstanceState(savedInstanceState);
}

/* (non-Javadoc)
 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
 */
@Override
protected void onSaveInstanceState(Bundle outState) {
	// TODO Auto-generated method stub
	super.onSaveInstanceState(outState);
	
	// We save the product
	outState.putParcelable(TAG_SAVED_PRODUCT, product);
	outState.putBoolean(TAG_LOAD_INFO, false);//set load info to false
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
				headerLayout = (RelativeLayout) layout.findViewById(R.id.product_view_header);
				pPhotoView = (ImageView) layout.findViewById(R.id.product_photo_image_view);
				pBrandText = (TextView) layout.findViewById(R.id.product_name_text_view);
				pModelText =(TextView) layout.findViewById(R.id.product_desc_text_view);
				addElementButton = (Button) layout.findViewById(R.id.add_element_button);
				
				// First the product
				String imageUrl = context.getResources().getString(R.string.image_bucket_url)+product.getPhotoId();
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
				
				//We set a listener on the header to display the ProductDetailFragment
				headerLayout.setOnClickListener(new OnClickListener (){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						displayProductDetailFragment(product);
					}
					
				});
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
		transaction.add(R.id.product_detail_fragment_container, frag, ProductDetailActivity.TAG_ELEMENT_DETAIL_FRAGMENT);
		transaction.addToBackStack(null).commit();
	}

	public void displayNewElementFragment (Element e){
		NewElementFragment fragment = NewElementFragment.getInstance(e);
		FragmentTransaction transac = getFragmentManager().beginTransaction();
		//if there is an ElementDetailFramgent, we remove it
		ElementDetailFragment f = (ElementDetailFragment) getFragmentManager().findFragmentByTag(this.TAG_ELEMENT_DETAIL_FRAGMENT);
		if ( f != null)
			transac.remove(f);
		transac.add(R.id.product_detail_fragment_container, fragment, ProductDetailActivity.TAG_NEW_ELEMENT_FRAGMENT);
		transac.addToBackStack(null).commit();
	}
	
	public void displayProductDetailFragment( Product product){
		Log.d("product detail", "click detected on Header");
		ProductDetailFragment frag = ProductDetailFragment.getInstance(product);
		FragmentTransaction transac = getFragmentManager().beginTransaction();
		transac.add(R.id.product_detail_fragment_container, frag, TAG_PRODUCT_DETAIL_FRAGMENT);
		transac.addToBackStack(null);
		transac.commit();
	}
	
	/**
	 * Removes the ProductDetailFragment if it exists and places the 
	 * NewProductFragment instead
	 * @param Product
	 */
	public void displayNewProductFragment (Product p){
		NewProductFragment fragment = NewProductFragment.getInstance(p);
		FragmentTransaction transac = getFragmentManager().beginTransaction();
		//if there is a ProductDetailFramgent, we remove it
		ProductDetailFragment f = (ProductDetailFragment) getFragmentManager().findFragmentByTag(ProductDetailActivity.TAG_PRODUCT_DETAIL_FRAGMENT);
		if ( f != null)
			transac.remove(f);
		transac.add(R.id.product_detail_fragment_container, fragment, ProductDetailActivity.TAG_NEW_PRODUCT_FRAGMENT);
		transac.addToBackStack(null).commit();
	}
	
	@Override
	public void editElement(Element element) {
		// TODO Auto-generated method stub
		displayNewElementFragment(element);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//put the NewElementFragment back on top
		if (requestCode == NewElementFragment.CODE_IMAGE_PATH){
			//we set loadinfo to false so that the activity view won't get built 
			this.loadInfo = false;
		}
		//Else put the NewPRoductFragment back on top
		else if (requestCode == NewProductFragment.CODE_IMAGE_PATH){
			this.loadInfo = false;
		}
		//Do default action on result (nothing) so that the result is passed on the the fragments
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	@Override
public void editProduct(Product product) {
		// TODO Auto-generated method stub
		displayNewProductFragment(product);
	}

	
}
