package com.apps4better.recycle4better.model;

import java.util.ArrayList;

public class Product {
	private ArrayList <Element> elementList = new ArrayList <Element>();
	private String name ;
	private int pId;
	private String photoId;
	
	public Product (){
		
	}
	
	public Product (ArrayList <Element> list){
		this.elementList = list;
	}
//////////////////////    GETTERS   ///////////////////
	public ArrayList<Element> getElementList() {
		return elementList;
	}

	public String getName() {
		return name;
	}

	public int getpId() {
		return pId;
	}
	
public String getPhotoId() {
		return photoId;
	}

/////////////////////   SETTERS  ////////////////////////

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}


	public void setElementList(ArrayList<Element> elementList) {
		this.elementList = elementList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}
	
	
}
