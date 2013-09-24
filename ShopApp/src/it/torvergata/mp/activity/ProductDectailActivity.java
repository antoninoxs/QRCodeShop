package it.torvergata.mp.activity;

import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.R.menu;
import it.torvergata.mp.R.string;
import it.torvergata.mp.entity.Product;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDectailActivity extends Activity {
	private ImageView ivImage;
	private TextView tvTitle,tvDescription,tvSimplePrice,tvQuantitative,tvPrice;
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
		
		//Inizializzazione
		ivImage = (ImageView) findViewById(R.id.ivDetailImage);
		tvTitle = (TextView) findViewById(R.id.tvTitleDetail);
		tvDescription = (TextView) findViewById(R.id.tvDescriptionDetail);
		tvPrice = (TextView) findViewById(R.id.tvTotalPrice);
		tvQuantitative = (TextView) findViewById(R.id.tvQuantitativeDetail);
		tvSimplePrice = (TextView) findViewById(R.id.tvSimplePrice);

		//Cattura del prodotto
		Intent i = getIntent();
		Product tempProduct = (Product) i.getParcelableExtra("PRODUCT");

		//Costruzione della View
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
