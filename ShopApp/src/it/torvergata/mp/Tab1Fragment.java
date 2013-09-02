package it.torvergata.mp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class Tab1Fragment extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
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
				Log.i("SCAN MODE", "Lancio Activity Camera");
				Intent intent = new Intent(getActivity(), CameraActivity.class);
				startActivity(intent);
			}
		});
		return mLinearLayout;

	}
}