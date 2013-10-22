package it.torvergata.mp.activity.tab;


 
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
 

public class TabScanModeDetailItemFragment extends Fragment {
    
	private Product prod;
	private LinearLayout mLinearLayout;
	private TextView tvTitle,tvDescription,tvPrice,tvQuantitative,tvSimplePrice;
	private ImageView ivImage;
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
       
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.activity_product_detail,
				container, false);
        
        tvTitle 			= (TextView) mLinearLayout.findViewById(R.id.tvTitleDetail);
        
      //Inizializzazione
      		ivImage = (ImageView)  mLinearLayout.findViewById(R.id.ivDetailImage);
      		tvTitle = (TextView)  mLinearLayout.findViewById(R.id.tvTitleDetail);
      		tvDescription = (TextView)  mLinearLayout.findViewById(R.id.tvDescriptionDetail);
      		tvPrice = (TextView)  mLinearLayout.findViewById(R.id.tvTotalPrice);
      		tvQuantitative = (TextView)  mLinearLayout.findViewById(R.id.tvQuantitativeDetail);
      		tvSimplePrice = (TextView)  mLinearLayout.findViewById(R.id.tvSimplePrice);
        
        
        
		String price =Double.toString(prod.getPrezzoTotale());
		price=price.replace('.',',');
		ivImage.setImageDrawable(prod.getImmagine());
		tvTitle.setText(prod.getNome());
		tvDescription.setText(prod.getDescrizione());
		tvPrice.setText(getString(R.string.tvTotal)+" "+price+"  "+getString(R.string.Euro));
		tvQuantitative.setText(getString(R.string.tQuantitative)+" "+prod.getQuantita());
		tvSimplePrice.setText(getString(R.string.tPrice)+" "+prod.getPrezzoUnitario()+" "+getString(R.string.Euro));
        
        return mLinearLayout;
        
    }
    
	public void updateProduct(Product pr) {
		// TODO Auto-generated method stub
		prod=pr;
	}

}