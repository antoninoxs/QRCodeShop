package it.torvergata.mp;

import it.torvergata.mp.MainActivity.LoadData;

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
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class CameraActivity extends ListActivity {
	List<Product> productList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		startQrCodeScan();

		final ListView list = (ListView) findViewById(id.list);
		productList = new ArrayList<Product>();
		setListAdapter(new ProductAdapter(getApplicationContext(),
				R.layout.new_list_item, productList));

	}

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

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				Log.i("Contenuto: ", contents);
				
				LoadDataProduct task = new LoadDataProduct();
				task.execute(contents);
				startQrCodeScan();
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(CameraActivity.this, "Fine Acquisizione",
						Toast.LENGTH_SHORT).show();
			

			}
		}
	}
	
	
	
	
	public class LoadDataProduct extends AsyncTask<String, Void, Void> {
	    ProgressDialog progressDialog;
	    
	   

		//declare other objects as per your need
	    @Override
	    protected void onPreExecute()
	    {
	      
	        //do initialization of required objects objects here                
	    };      
	 
	    @Override
	    protected void onPostExecute(Void result)
	    {
	    
	    }
		@Override
		protected Void doInBackground(String... params) {
			String productId = params[0];
			
			final Product tempProd = new Product(Integer.parseInt(productId));
			
			final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id",productId));
			
			
			try {
	        	HttpClient httpclient = new DefaultHttpClient();
	    		HttpPost httppost = new HttpPost("http://" + Const.IPADDRESS
	    				+ "/info_download.php");
	    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    		
				HttpResponse response = httpclient.execute(httppost);
				String jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
				
				Log.i("JsonResult", "["+jsonResult+"]");
				
				JSONObject object = new JSONObject(jsonResult);

				String idProdotto = 	object.getString("idProdotto");
				String nome = 			object.getString("nome");
				double prezzo =			object.getDouble("prezzo");
				String scadenza = 		object.getString("scadenza");
				String disponibilita = 	object.getString("disponibilita");
				String descrizione = 	object.getString("descrizione");
				String fileImmagine = 	object.getString("file_immagine");
				
				
				Log.i("idProdotto: ", idProdotto );
				Log.i("nome: ", nome );
				Log.i("prezzo: ", Double.toString(prezzo));
				Log.i("scadenza: ", scadenza );
				Log.i("descrizione: ", descrizione );
				Log.i("disponibilita: ", disponibilita );
				Log.i("file_immagine: ", fileImmagine );

				tempProd.setId(Integer.parseInt(idProdotto));
				tempProd.setNome(nome);
				tempProd.setPrezzo(prezzo);
				tempProd.setScadenza(scadenza);
				tempProd.setDescrizione(descrizione);
				tempProd.setDisponibilita(Integer.parseInt(disponibilita));
				tempProd.setFileImmagine(fileImmagine);
				
				productList.add(tempProd);
	    		
	        } catch (JSONException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
	        	 Log.e("log_tag", "Error in http connection: "+e.toString());
	        }
			// TODO Auto-generated method stub
			return null;
		};
	 }
	
}
