package it.torvergata.mp.activity;
import it.torvergata.mp.Const;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.R.menu;
import it.torvergata.mp.R.string;
import it.torvergata.mp.activity.MainActivity.LoadData;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.DrawableManager;
import it.torvergata.mp.helper.ProductAdapter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends ListActivity {
	
	private ListProduct productList;
	private ProductAdapter adapter ;
	private DrawableManager drawab; 
	private TextView totalPrice;
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context ctx =this;
		setContentView(R.layout.activity_camera);
		
		totalPrice 			= (TextView) findViewById(R.id.tvTotalPrice);
		Button btnAdd 		= (Button) findViewById(R.id.btnAdd);
		Button btnContinue 	= (Button) findViewById(R.id.btnContinue);
		
		startQrCodeScan();

		final ListView list = (ListView) findViewById(id.list);
		productList = new ListProduct();
		adapter =new ProductAdapter(ctx,
				R.layout.new_list_item, productList);
		setListAdapter(adapter);
		
		/***
		 * Al click dell'item viene lanciata l'activity di dettaglio passandogli il prodotto 
		 * selezionato come oggetto impacchettato
		 */
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(ctx, ProductDectailActivity.class);
				intent.putExtra("PRODUCT", productList.get(arg2));
				startActivity(intent);
				
			}
		});
		
		/***
		 * Al click prolungato dell''item si accede al dialog per l'eliminazione del prodotto
		 */
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
						setTotalPrice(totalPrice);
						}catch (IndexOutOfBoundsException e){
							adapter.notifyDataSetChanged();
						}
						dialogBox.dismiss();	
					}
				});
				
				return false;
			}
		});
	
	/***
	 * Alla pressione del bottone "Aggiungi" riparte l'attività di scansione di ulteriori prodotti
	 */
	btnAdd.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			startQrCodeScan();
		}
	});
		

	}
	
	/***
	 * Metodo di instaziazione del Dialog di eliminazioen
	 * @param position
	 * @return AlertDialog
	 */
	private AlertDialog DeleteDialog(final int position) {
		Product prod = productList.get(position);
		AlertDialog alertDialog = new AlertDialog.Builder(this)
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

	
	/***
	 * Metodo di conversione in "Stringa" della risposta del Server
	 * fornita in Json e catturata nell inputStream is
	 * @param is
	 * @return answer
	 */
	private StringBuilder inputStreamToString(InputStream is) {
		String rLine = "";
		StringBuilder answer = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			while ((rLine = rd.readLine()) != null) {
				answer.append(rLine);
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return answer;
	}

	/**
	 * Metodo per il lancio della scansione dei QrCode
	 */
	public void startQrCodeScan() {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	/***
	 * Metodo di cattura dei risultati della Scansione QrCode,
	 * 
	 * Se è stato correttamente scansionato un QrCode, e si verifica che il prodotto non è gia nella lista,
	 * si procede al lancio del task che effettuerà il download delle informazioni attraverso un AsyncTask Thread.
	 * Qualora il prodotto risulti già presente nella lista, si provede semplicemente ad incrementare la sua quantità.
	 * 
	 * Se invece viene premuto il tasto back durante la scansione, si termina la fase di acquisizione dei Qrcode
	 * 
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Si gestisce la scansione corretta di un QrCode
				Log.i("Contenuto: ", contents);
		
				Product tProd =productList.searchById(Integer.parseInt(contents));
				if (tProd!=null){
					tProd.increment();
					productList.setIncrementTotalPrice(tProd.getPrezzoUnitario());
					startQrCodeScan();
				}
				else{
					LoadDataProduct task = new LoadDataProduct();
					task.execute(contents);
					startQrCodeScan();
				}
			}
				else if (resultCode == RESULT_CANCELED) {
				setTotalPrice(totalPrice);
				Toast.makeText(CameraActivity.this, "Fine Acquisizione",
						Toast.LENGTH_SHORT).show();
			

			}
		}
	}
	
	/***
	 * Metodo di Aggiornamento del prezzo totale
	 * @param totalPrice
	 */
	public void setTotalPrice(TextView totalPrice){
		String price =Double.toString(productList.getTotalPrice());
		price=price.replace('.',',');
		totalPrice.setText(getString(R.string.tvTotal)+" "+price+" "+getString(R.string.Euro));
		
	}
	
	/***
	 * Classe di gestione del Thread che effettua il download dei dati informativi del prodotto.
	 *
	 */
	public class LoadDataProduct extends AsyncTask<String, Void, Void> {
	    ProgressDialog progressDialog;
	    	    @Override
	    protected void onPreExecute()
	    {
	    };      
	 
	    @Override
	    protected void onPostExecute(Void result)
	    {
	    
	    }
		@Override
		protected Void doInBackground(String... params) {
			String productId = params[0];

			final Product tempProd = new Product(Integer.parseInt(productId));
			//Preparazione delle informazioni da inviare al server
			final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", productId));

			try {
				//Connessione al Server
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://" + Const.IPADDRESS
						+ "/info_download.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				//Ricezione della risposta
				HttpResponse response = httpclient.execute(httppost);
				
				//Conersione da inputString a JsonResult
				String jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
				Log.i("JsonResult", "[" + jsonResult + "]");
				JSONObject object = new JSONObject(jsonResult);

				//Lettura dell'oggetto Json
				String idProdotto = object.getString("idProdotto");
				String nome = object.getString("nome");
				double prezzo = object.getDouble("prezzo");
				String scadenza = object.getString("scadenza");
				String disponibilita = object.getString("disponibilita");
				String descrizione = object.getString("descrizione");
				String fileImmagine = object.getString("file_immagine");

				
				Log.i("idProdotto: ", idProdotto);
				Log.i("nome: ", nome);
				Log.i("prezzo: ", Double.toString(prezzo));
				Log.i("scadenza: ", scadenza);
				Log.i("descrizione: ", descrizione);
				Log.i("disponibilita: ", disponibilita);
				Log.i("file_immagine: ", fileImmagine);

				//Creazione del nuovo Prodotto
				tempProd.setId(Integer.parseInt(idProdotto));
				tempProd.setNome(nome);
				tempProd.setPrezzoUnitario(prezzo);
				tempProd.setScadenza(scadenza);
				tempProd.setDescrizione(descrizione);
				tempProd.setDisponibilita(Integer.parseInt(disponibilita));
				tempProd.setFileImmagine(fileImmagine);
				
				//Aggiunta del nuovo prodotto alla lista dei prodotti
				productList.add(tempProd);

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
			}

			return null;
		};
	 }
	
}
