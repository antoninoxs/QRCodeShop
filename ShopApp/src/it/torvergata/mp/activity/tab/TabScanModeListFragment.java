package it.torvergata.mp.activity.tab;


 
import it.torvergata.mp.Const;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.tab.TabScanModeScanningFragment.OnTermAcquisitionListener;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.ProductAdapter;
import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
        public void ViewProductDetailFragment(Product product);
		
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
		
		list.setOnItemLongClickListener(new OnItemLongClickListener() {


			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				
				final AlertDialog dialogBox = DeleteDialog(arg2);
				dialogBox.show();
				Button deleteButton = dialogBox
						.getButton(DialogInterface.BUTTON_POSITIVE);
				deleteButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try{
						productList.remove(arg2);
						adapter.notifyDataSetChanged();
						//setTotalPrice(totalPrice);
						}catch (IndexOutOfBoundsException e){
							adapter.notifyDataSetChanged();
						}
						dialogBox.dismiss();	
					}
				});
				
				return false;
			}
		});
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				mCallback.ViewProductDetailFragment(productList.get(arg2));
			}
		});
		
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
	private AlertDialog DeleteDialog(final int position) {
		Product prod = productList.get(position);
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(prod.getNome())
				.setMessage(R.string.tMessageDelete)
				.setIcon(Const.resize(prod.getImmagine()))
				.setPositiveButton(R.string.tDeleteProduct,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {							}
						})
				.setNegativeButton(R.string.tCancelDelete,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								
							}
						}).create();
		return alertDialog;
	}

}