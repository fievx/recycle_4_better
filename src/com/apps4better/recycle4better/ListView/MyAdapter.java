package com.apps4better.recycle4better.ListView;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps4better.recycle4better.R;
import com.apps4better.recycle4better.model.Element;
import com.squareup.picasso.Picasso;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
  private ArrayList<Element> mDataset;
  private Activity activity;

  // Provide a reference to the views for each data item
  // Complex data items may need more than one view per item, and
  // you provide access to all the views for a data item in a view holder
  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
    // each data item is just a string in this case
    public TextView txtElementName;
    public ImageView elementIcon;
    public ImageView recyclableTick;
    public TextView elementTrustText;
    public TextView elementNameText1;
    public TextView elementRecyclable1;
    public TextView elementTrust1;
    
    public ViewHolder(View v) {
      super(v);
      txtElementName = (TextView) v.findViewById(R.id.element_name_textview);
      elementIcon = (ImageView) v.findViewById(R.id.icon);
      recyclableTick = (ImageView) v.findViewById(R.id.recyclable_imageview);
      elementTrustText = (TextView) v.findViewById(R.id.element_trust_score_text_view);
      elementNameText1 = (TextView) v.findViewById(R.id.firstLine);
      elementRecyclable1 = (TextView) v.findViewById(R.id.secondLine);
      elementTrust1 = (TextView) v.findViewById(R.id.no_element_text);
      v.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Element e = mDataset.get(getPosition());
		((MyAdapterListener)activity).displayElementFragment(e.getNumber());
	}
    
  }

  public void add(int position, Element item) {
    mDataset.add(position, item);
    notifyItemInserted(position);
  }

  public void remove(String item) {
    int position = mDataset.indexOf(item);
    mDataset.remove(position);
    notifyItemRemoved(position);
  }

  // Provide a suitable constructor (depends on the kind of dataset)
  public MyAdapter(ArrayList<Element> myDataset, Activity activity) {
    mDataset = myDataset;
    this.activity = activity;
  }

  // Create new views (invoked by the layout manager)
  @Override
  public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
    // create a new view
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
    // set the view's size, margins, paddings and layout parameters
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  // Replace the contents of a view (invoked by the layout manager)
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    final Element element = mDataset.get(position);
    holder.txtElementName.setText(element.getName());
    
    //We construct the image Url based on the server address from the resources and the image id.
    //Then we use picasso to load and display the image.
    String extension = activity.getResources().getString(R.string.image_extension);
    String imageUrl = activity.getResources().getString(R.string.image_bucket_url) + element.getPhotoId();
    Drawable placeHolder = activity.getResources().getDrawable(R.drawable.no_photo_placeholder);
    Picasso.with(activity).load(imageUrl).placeholder(placeHolder).into(holder.elementIcon);
    
    //we test to see if the element is recyclable and display the correct logo
    if (element.getRecyclable()==1){
    	holder.recyclableTick.setImageDrawable(holder.recyclableTick.getContext().getResources().getDrawable(R.drawable.tick_yes));
    }
    else if (element.getRecyclable()==0)
    	holder.recyclableTick.setImageDrawable(holder.recyclableTick.getContext().getResources().getDrawable(R.drawable.tick_no));
    else holder.recyclableTick.setImageDrawable(holder.recyclableTick.getContext().getResources().getDrawable(R.drawable.tick_maybe));
    
    //We display the trust score
    holder.elementTrustText.setText(String.valueOf(element.getTrustScore()));
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override
  public int getItemCount() {
    return mDataset.size();
  }

} 