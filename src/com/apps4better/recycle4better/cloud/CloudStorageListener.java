package com.apps4better.recycle4better.cloud;

/**
 * used to send result to service or activity once the cloud storage has finished 
 * its operation.
 * @author jeremy
 *
 */
public interface CloudStorageListener {

public static final String TAG_SUCCESS = "success";
public static final String TAG_FAILURE = "failure";
	public void update (String param);
}
