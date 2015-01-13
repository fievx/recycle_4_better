package com.apps4better.recycle4better.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
	private ArrayList <Element> elementList = new ArrayList <Element>();
	private String brand ;
	private String model;
	private long pId;
	private String photoId;
	private int trustScore;
	
	public Product (){
		
	}
	
	public Product (ArrayList <Element> list){
		this.elementList = list;
	}

	
	public Product(Parcel source) {
		// TODO Auto-generated constructor stub
		source.readList(elementList, null);
		this.model = source.readString();
		this.brand = source.readString();
		this.photoId= source.readString();
		this.pId = source.readLong();
	}

//////////////////////    GETTERS   ///////////////////
	public ArrayList<Element> getElementList() {
		return elementList;
	}

	public String getBrand() {
		return brand;
	}

	/**
	 * 
	 * @return long pId
	 */
	public long getpId() {
		return pId;
	}
	
public String getPhotoId() {
		return photoId;
	}

public String getModel(){
	return model;
}

public int getElementCount(){
	return elementList.size();
}

/**
 * Browse the ElementList and return the first element which has a matching element number
 * return null if no matching element is found.
 * @param elementNumber
 * @return
 */
public Element getElementById (int elementNumber){
	Element e;
	for (int i = 0; i<elementList.size(); i++){
		e = elementList.get(i);
		if (e.getNumber() == elementNumber) return e;
	}
	return null;
}


/////////////////////   SETTERS  ////////////////////////


	/**
 * @return the trustScore
 */
public int getTrustScore() {
	return trustScore;
}

/**
 * @param trustScore the trustScore to set
 */
public void setTrustScore(int trustScore) {
	this.trustScore = trustScore;
}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}


	public void setElementList(ArrayList<Element> elementList) {
		this.elementList = elementList;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public void setpId(long pId) {
		this.pId = pId;
	}

	public void setModel (String model){
		this.model = model;
	}
	
	//////////////// Parcel Methods //////////////
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeList(elementList);
		dest.writeString(model);
		dest.writeString(brand);
		dest.writeString(photoId);
		dest.writeLong(pId);
	}
	
	public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>(){

		@Override
		public Product createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Product (source);
		}

		@Override
		public Product[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Product [size];
		}
		
	};
	
	/**
	 * Conveniance method which return a Product object identical to the one calling the method, but 
	 * with a null list of Elements. This method is meant to be called before a product is passed to the 
	 * ProductEditorService to avoid bugs.
	 * @return Product
	 */
	public Product getCloneWithoutElements (){
		Product p = this;
		p.setElementList(null);
		return p;
	}
}
