package it.torvergata.mp.activity.tab;


 
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.tab.TabScanModeScanningFragment.OnTermAcquisitionListener;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.helper.ProductAdapter;
import android.R.id;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
 

public class TabScanModeListFragment extends Fragment {
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	private ListProduct productList;
	private TextView totalPrice;
	private Button btnAdd,BtnContinue;
	private LinearLayout mLinearLayout;
	private ProductAdapter adapter;
	
	
	OnAddQrCodeListener mCallback;
	// Container Activity must implement this interface
    public interface OnAddQrCodeListener {
        public void ViewScanningFragment(ListProduct list);
    }
	
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
       
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_frag_scan_mode_list_layout,
				container, false);
        
    	totalPrice 			= (TextView) mLinearLayout.findViewById(R.id.tvTotalPrice);
		Button btnAdd 		= (Button) mLinearLayout.findViewById(R.id.btnAdd);
		Button btnContinue 	= (Button) mLinearLayout.findViewById(R.id.btnContinue);
		final ListView list = (ListView) mLinearLayout.findViewById(id.list);
        
		adapter =new ProductAdapter(getActivity(),
				R.layout.new_list_item, productList);
		list.setAdapter(adapter);
		
		btnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCallback.ViewScanningFragment(productList);
			}
		});
		
        return mLinearLayout;
        
    }
    
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnAddQrCodeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTermAcquisitionListener");
        }
    }
	public void updateProductList(ListProduct list) {
		// TODO Auto-generated method stub
		productList=list;
	}
}