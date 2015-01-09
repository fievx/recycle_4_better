package com.apps4better.recycle4better.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.model.ElementDetailObserver;
import com.apps4better.recycle4better.model.ElementEditorService;
import com.apps4better.recycle4better.model.Product;
import com.apps4better.recycle4better.model.ProductDetailObserver;
import com.apps4better.recycle4better.model.ProductEditorService;
import com.squareup.picasso.Picasso;

public class ProductDetailFragment extends Fragment{
	private Product product;
	private Activity activity;
	private RelativeLayout layout;
	public static final String TAG_PRODUCT = "product";
	private static final String extansion = ".png";
	
	private ImageView ePictureView, eRecyclable;
	private TextView pBrandText, pModelText, pTrustText;
	private Button editButton; 
	private ImageButton upVoteButton, downVoteButton;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//Retrieve the Bundle from the argument and extract the product
		Bundle bundle = this.getArguments();
		this.product = bundle.getParcelable(TAG_PRODUCT);
		
		layout = (RelativeLayout) inflater.inflate(R.layout.fragment_product_detail, container, false);
		ePictureView = (ImageView) layout.findViewById(R.id.product_picture_view);
		pBrandText = (TextView) layout.findViewById(R.id.product_brand_text_view);
		pModelText = (TextView) layout.findViewById(R.id.product_model_text_view);
		pTrustText = (TextView) layout.findViewById(R.id.product_trust_score_text);
		editButton = (Button) layout.findViewById(R.id.product_edit_button);
		upVoteButton = (ImageButton) layout.findViewById(R.id.product_upvote_button);
		downVoteButton = (ImageButton) layout.findViewById(R.id.product_down_vote_button);
		
		//We create the view with all the variables from the Element
		// the product image loaded via Picasso
		String imageUrl = activity.getResources().getString(R.string.image_bucket_url)+product.getPhotoId();
		Drawable placeHolder = activity.getResources().getDrawable(R.drawable.placeholder_element_detail);
		Picasso.with(activity).load(imageUrl).placeholder(placeHolder).fit().into(ePictureView);
		
		this.pBrandText.setText(product.getBrand());//name
		this.pModelText.setText(product.getModel());//description
		
		this.pTrustText.setText(String.valueOf(product.getTrustScore()));//Trust score
		
		return layout;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	
	@Override
public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		editButton.setOnClickListener (new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edit ();
			}
			
		});
		upVoteButton.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				upVote();
			}
			
		});
		downVoteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downVote ();
			}
			
		});
	
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	/**
	 * Returns an intance of ElementDetailFragment with a Bundle as Argument. 
	 * The Element in param is passed in the bundle.
	 * @param 
	 * @return ElementDetailFragment
	 */
	public static ProductDetailFragment getInstance (Product product){
		ProductDetailFragment frag = new ProductDetailFragment ();
		Bundle bundle = new Bundle();
		bundle.putParcelable(TAG_PRODUCT, product);
		frag.setArguments(bundle);
		return frag;
	}

	private void upVote(){
		this.product.setTrustScore(this.product.getTrustScore()+1);
		Intent intent = new Intent (activity, ProductEditorService.class);
		intent.putExtra(ProductEditorService.TAG_PRODUCT, this.product);
		activity.startService(intent);
		
		//We update the score on the display
		this.pTrustText.setText(String.valueOf(product.getTrustScore()));
	}
	
	private void downVote (){
		this.product.setTrustScore(this.product.getTrustScore()-1);
		Intent intent = new Intent (activity, ProductEditorService.class);
		intent.putExtra(ProductEditorService.TAG_PRODUCT, this.product);
		activity.startService(intent);
		
		//We update the score on the display
		this.pTrustText.setText(String.valueOf(product.getTrustScore()));
	}


private void edit (){
	((ProductDetailObserver)activity).editProduct(product);
}
}
