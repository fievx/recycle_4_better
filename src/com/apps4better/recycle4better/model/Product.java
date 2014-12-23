package com.apps4better.recycle4better.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
	private ArrayList <Element> elementList = new ArrayList <Element>();
	private String brand ;
	private String model;
	private int pId;
	private String photoId;
	
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
		this.pId = source.readInt();
	}

//////////////////////    GETTERS   ///////////////////
	public ArrayList<Element> getElementList() {
		return elementList;
	}

	public String getBrand() {
		return brand;
	}

	public int getpId() {
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


/////////////////////   SETTERS  ////////////////////////


	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}


	public void setElementList(ArrayList<Element> elementList) {
		this.elementList = elementList;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public void setpId(int pId) {
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
		dest.writeInt(pId);
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
	
	
}
