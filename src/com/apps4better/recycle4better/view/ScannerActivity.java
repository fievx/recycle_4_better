package com.apps4better.recycle4better.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.apps4better.recycle4better.R;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;

public class ScannerActivity extends CaptureActivity {
	
	public void onCreate (Bundle bundle){
		super.onCreate(bundle);
		
		this.setContentView(R.layout.scanner_layout);
	}

    public void handleDecode(Result rawResult, Bitmap barcode)

    {

       Toast.makeText(this.getApplicationContext(), "Scanned code "+ rawResult.getText(), Toast.LENGTH_LONG).show();

    }
}
