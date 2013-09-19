package it.torvergata.mp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDectailActivity extends Activity {
	ImageView ivImage;
	TextView tvTitle; 
	TextView tvDescription; 
	TextView tvPrice; 
	TextView tvQuantitative;
	TextView tvSimplePrice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);

		ivImage = (ImageView) findViewById(R.id.ivDetailImage);
		tvTitle = (TextView) findViewById(R.id.tvTitleDetail);
		tvDescription = (TextView) findViewById(R.id.tvDescriptionDetail);
		tvPrice = (TextView) findViewById(R.id.tvTotalPrice);
		tvQuantitative = (TextView) findViewById(R.id.tvQuantitativeDetail);
		tvSimplePrice = (TextView) findViewById(R.id.tvSimplePrice);

		Intent i = getIntent();
		Product tempProduct = (Product) i.getParcelableExtra("PRODUCT");

		String price =Double.toString(tempProduct.getPrezzoTotale());
		price=price.replace('.',',');
		
		ivImage.setImageDrawable(tempProduct.getImmagine());
		tvTitle.setText(tempProduct.getNome());
		tvDescription.setText(tempProduct.getDescrizione());
		tvPrice.setText(getString(R.string.tvTotal)+" "+price+"  "+getString(R.string.Euro));
		tvQuantitative.setText(getString(R.string.tQuantitative)+" "+tempProduct.getQuantita());
		tvSimplePrice.setText(getString(R.string.tPrice)+" "+tempProduct.getPrezzoUnitario()+" "+getString(R.string.Euro));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_dectail, menu);
		return true;
	}

}
