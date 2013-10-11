package it.torvergata.mp.activity.tab;

import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.tab.TabScanModeScanningFragment.OnTermAcquisitionListener;
import it.torvergata.mp.entity.ListProduct;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author mwho
 * 
 */
public class TabScanModeMainFragment extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	
	OnStartAcquisitionListener mCallback;
	// Container Activity must implement this interface
    public interface OnStartAcquisitionListener {
        public void ViewScanningFragment();
    }

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
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
		LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_frag1_layout,
				container, false);

		// note that we're looking for a button with id="@+id/myButton" in your
		// inflated layout
		// Naturally, this can be any View; it doesn't have to be a button
		Button mButton = (Button) mLinearLayout.findViewById(R.id.btnQrCode);
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				mCallback.ViewScanningFragment();

			}
		});
		return mLinearLayout;

	}

	public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnStartAcquisitionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStartAcquisitionListener");
        }
    }
}