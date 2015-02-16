package com.apps4better.recycle4better.test;

import com.apps4better.recycle4better.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * This Activity is only used to test fragments in the JUnit framework.
 * From the JUnit framework, we use this Activity to open any fragment and run
 * tests
 * @author jeremy
 *
 */
public class TestFragmentActivity extends Activity{

	 @Override
	   protected void onCreate(Bundle arg0) {
	     super.onCreate(arg0);
	     setContentView(R.layout.activity_fortests);
	   }
}
