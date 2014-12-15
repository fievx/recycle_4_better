package com.apps4better.recycle4better.model;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * An element object represents a real part of a product to be recycled
 */
public class Element implements Parcelable{
	private int number;
	private String name;
	private String description;
	private int weight;
	private int recyclable;
	private String photoId;
	private String materialCommon;
	private String materialScientific;
	private int trustScore;
	
	
	
	public Element (){
		
	}

	public int getNumber() {
		return number;
	}

	public String getDescription() {
		return description;
	}

	public int getWeight() {
		return weight;
	}

	public int getRecyclable() {
		return recyclable;
	}

	public String getPhotoId() {
		return photoId;
	}

	public int getTrustScore() {
		return trustScore;
	}

	public String getName() {
		return name;
	}

	public String getMaterialCommon() {
		return materialCommon;
	}

	public String getMaterialScientific() {
		return materialScientific;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMaterialCommon(String materialCommon) {
		this.materialCommon = materialCommon;
	}

	public void setMaterialScientific(String materialScientific) {
		this.materialScientific = materialScientific;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public void setRecyclable(int recyclable) {
		this.recyclable = recyclable;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public void setTrustScore(int trustScore) {
		this.trustScore = trustScore;
	}

	
	
	/////////  Parcelable Implementation ////////
	//constructor for parcel
	public Element (Parcel in){
		this.description = in.readString();
		this.materialCommon = in.readString();
		this.materialScientific = in.readString();
		this.name = in.readString();
		this.number = in.readInt();
		this.photoId = in.readString();
		this.recyclable = in.readInt();
		this.trustScore = in.readInt();
		this.weight = in.readInt();
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(description);
		dest.writeString(materialCommon);
		dest.writeString(materialScientific);
		dest.writeString(name);
		dest.writeInt(number);
		dest.writeString(photoId);
		dest.writeInt(recyclable);
		dest.writeInt(trustScore);
		dest.writeInt(weight);
	}
	
	public static final Parcelable.Creator<Element> CREATOR = new Parcelable.Creator<Element>() {
		public Element createFromParcel (Parcel source){
			return new Element (source);
		}
		public Element [] newArray (int size){
			return new Element [size];
		}
	};
	
	
}
