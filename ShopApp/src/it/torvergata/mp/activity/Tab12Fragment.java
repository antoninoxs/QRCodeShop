package it.torvergata.mp.activity;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author mwho
 * 
 */
public class Tab12Fragment extends Fragment {

	private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private LinearLayout mLinearLayout;
    
    TextView scanText;
    Button scanButton;
    Button encodeButton;
    
    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    } 

	    

	    public void onResume(){
	    	super.onResume();
	    	
	    	mCamera = getCameraInstance();

	        mPreview = new CameraPreview(getActivity(), mCamera, previewCb, autoFocusCB);
	        FrameLayout preview = (FrameLayout)mLinearLayout.findViewById(R.id.cameraPreview);
	        
	        if (preview.getChildCount() > 0){
	        	preview.removeAllViews();
	        }
	        
	        preview.addView(mPreview);
	        
	    }
	    
	    public void onPause() {
	        super.onPause();
	        releaseCamera();
	    }

	    /** A safe way to get an instance of the Camera object. */
	    public static Camera getCameraInstance(){
	        Camera c = null;
	        try {
	            c = Camera.open();
	        } catch (Exception e){
	        }
	        return c;
	    }

	    private void releaseCamera() {
	        if (mCamera != null) {
	            previewing = false;
	            mCamera.setPreviewCallback(null);
	            mCamera.release();
	            mCamera = null;
	        }
	    }

	    private Runnable doAutoFocus = new Runnable() {
	            public void run() {
	                if (previewing)
	                    mCamera.autoFocus(autoFocusCB);
	            }
	        };

	    PreviewCallback previewCb = new PreviewCallback() {
	            public void onPreviewFrame(byte[] data, Camera camera) {
	                Camera.Parameters parameters = camera.getParameters();
	                Size size = parameters.getPreviewSize();

	                Image barcode = new Image(size.width, size.height, "Y800");
	                barcode.setData(data);

	                int result = scanner.scanImage(barcode);
	                
	                if (result != 0) {
	                    previewing = false;
	                    mCamera.setPreviewCallback(null);
	                    mCamera.stopPreview();
	                    
	                    SymbolSet syms = scanner.getResults();
	                    for (Symbol sym : syms) {
	                        scanText.setText("barcode result " + sym.getData());
	                        barcodeScanned = true;
	                    }
	                }
	            }
	        };

	    // Mimic continuous auto-focusing
	    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
	            public void onAutoFocus(boolean success, Camera camera) {
	                autoFocusHandler.postDelayed(doAutoFocus, 1000);
	            }
	        };

		
	
	
	
	/***
	 * 
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
			mLinearLayout = (LinearLayout) inflater.inflate(R.layout.activity_zbar,
				container, false);
	        autoFocusHandler = new Handler();
	        
	        /* Instance barcode scanner */
	        scanner = new ImageScanner();
	        scanner.setConfig(0, Config.X_DENSITY, 3);
	        scanner.setConfig(0, Config.Y_DENSITY, 3);
	        
	        scanText = (TextView)mLinearLayout.findViewById(R.id.scanText);

	        scanButton = (Button)mLinearLayout.findViewById(R.id.ScanButton);
	        encodeButton = (Button)mLinearLayout.findViewById(R.id.EncodeButton);
	        
	        scanButton.setOnClickListener(new OnClickListener() {
	                public void onClick(View v) {
	                    if (barcodeScanned) {
	                        barcodeScanned = false;
	                        scanText.setText("Scanning...");
	                        mCamera.setPreviewCallback(previewCb);
	                        mCamera.startPreview();
	                        previewing = true;
	                        mCamera.autoFocus(autoFocusCB);
	                    }
	                }
	            });
	        
	        
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.

			return null;
		}
		

		
		return mLinearLayout;

	}
}