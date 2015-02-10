package com.apps4better.recycle4better.model;

import java.util.concurrent.CountDownLatch;

import android.content.Intent;

public class ProductEditorServiceWrapper extends ProductEditorService{
	private CountDownLatch latch;
	
	/* (non-Javadoc)
	 * @see com.apps4better.recycle4better.model.ProductEditorService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onHandleIntent(intent);
		latch.countDown();
	}

	/* (non-Javadoc)
	 * @see com.apps4better.recycle4better.model.ProductEditorService#getSuccess()
	 */
	@Override
	public int getSuccess() {
		// TODO Auto-generated method stub
		return super.getSuccess();
	}

	public void setLatch (CountDownLatch latch){
		this.latch = latch;
	}
	
}
