package it.torvergata.mp.activity.tab;


 
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.database.DatabaseManager;
import it.torvergata.mp.activity.tab.TabScanModeListFragment.OnAddQrCodeListener;
import it.torvergata.mp.entity.ListOrders;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.helper.Dialogs;
import it.torvergata.mp.helper.OrdersAdapter;
import it.torvergata.mp.helper.ProductAdapter;
import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
 
/**
 * @author mwho
 *
 */
public class TabOrdersMainFragment extends Fragment {

	    /** (non-Javadoc)
	     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	     */
		private ListProduct productList;
		private TextView totalPrice;
		private Button btnAdd,BtnContinue;
		private LinearLayout mLinearLayout;
		private OrdersAdapter adapter;
		private Dialogs dialogs;
		
		OnAddQrCodeListener mCallback;

		// Container Activity must implement this interface
	    public interface OnAddQrCodeListener {
	        public void ViewScanningFragment(ListProduct list);
	        public void ViewProductDetailFragment(ListProduct list,int pos);
	        public void ViewOrderFragment(ListProduct list);
			
	 }
	  
		
		
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	    	
	    	final DatabaseManager db = new DatabaseManager(getActivity());
	    	ListOrders listOrders= new ListOrders();
	    	db.open();
	    	listOrders=db.returnListOrder();
	    	//productList=db.returnProductListOrder(236);
	    	db.close();
	    	
	    	
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
	   		final ListView list = (ListView) mLinearLayout.findViewById(id.list);
	        
			adapter =new OrdersAdapter(getActivity(),
					R.layout.new_list_item, listOrders);
			list.setAdapter(adapter);
		
	        return mLinearLayout;
	        
	    }
	  
	    
		
	
		

	}