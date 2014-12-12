package com.apps4better.recycle4better.model;

import java.io.Serializable;

/*
 * An element object represents a real part of a product to be recycled
 */
public class Element implements Serializable{
	private int number;
	private String description;
	private float weight;
	private boolean recyclable;
	private String photoId;
	private int trustScore;
	
	public Element (){
		
	}

	public int getNumber() {
		return number;
	}

	public String getDescription() {
		return description;
	}

	public float getWeight() {
		return weight;
	}

	public boolean isRecyclable() {
		return recyclable;
	}

	public String getPhotoId() {
		return photoId;
	}

	public int getTrustScore() {
		return trustScore;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public void setRecyclable(boolean recyclable) {
		this.recyclable = recyclable;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public void setTrustScore(int trustScore) {
		this.trustScore = trustScore;
	}
	
	
}
