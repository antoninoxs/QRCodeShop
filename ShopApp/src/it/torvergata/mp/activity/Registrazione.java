package it.torvergata.mp.activity;

import it.torvergata.mp.Const;
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.R.menu;
import it.torvergata.mp.activity.CameraActivity.LoadDataProduct;
import it.torvergata.mp.entity.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registrazione extends Activity {

	private EditText edNome, edCognome, edEmail, edUsername, edPassword,
			edPassword1;
	private Button bRegistrati;
	private InputStream is = null, ins = null;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrazione);

		edNome = (EditText) findViewById(R.id.ETregistrazioneNome);
		edCognome = (EditText) findViewById(R.id.ETregistrazioneCognome);
		edEmail = (EditText) findViewById(R.id.ETregistrazioneEmail);
		edUsername = (EditText) findViewById(R.id.ETregistrazioneUsername);
		edPassword = (EditText) findViewById(R.id.ETregistrazionePassword);
		edPassword1 = (EditText) findViewById(R.id.ETregistrazionePassword1);
		bRegistrati = (Button) findViewById(R.id.BregistrazioneRegistrati);

		edNome.requestFocus();
		
		bRegistrati.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (edNome.length() == 0 || edCognome.length() == 0
						|| edEmail.length() == 0 || edUsername.length() == 0
						|| edPassword.length() == 0) {
					Toast toast = Toast.makeText(Registrazione.this,
							R.string.tBlankField, Toast.LENGTH_LONG);
					toast.show();
				} else if (!((edPassword.getText().toString())
						.equals(edPassword1.getText().toString()))) {
					Toast toast = Toast.makeText(Registrazione.this,
							R.string.tPasswordMismatch, Toast.LENGTH_LONG);
					toast.show();
				} else {
					String nome = edNome.getText().toString();
					String cognome = edCognome.getText().toString();
					String email = edEmail.getText().toString();
					String username = edUsername.getText().toString();
					String password = edPassword.getText().toString();
					insertToDB(nome, cognome, email, username, password);
				}
			}
		});
	}

	public void insertToDB(String nome, String cognome, String email,
			String username, String password) {

		Registration task = new Registration();
		task.execute(nome, cognome, email, username, password);

		//Handler per il messaggio di risposta del Server, proveniente dal Thread che effettua il login.
		handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                
            	Bundle b = message.getData();
            	String mess = b.getString("Message");
            	String res = b.getString("Result");
            	String errQ = b.getString("ErrorQuery");
            	
            	//KOU==Ko User, è l'identificativo del messaggio che segnala un Username già presente
            	if(res.equals("KOU")){
            		Toast toast = Toast.makeText(Registrazione.this,
            				mess, Toast.LENGTH_LONG);
            		toast.show();
            	}
            	//KO== Gestioamo l'errore Generico, viene stampato l'errore in un Toast
            	else if(res.equals("KO")){
                Toast toast = Toast.makeText(Registrazione.this,
        				errQ, Toast.LENGTH_LONG);
        		toast.show();
            	}
            	//Ok Operazione di registrazione eseguita con successo, torniamo alla schermata di Login
            	else if(res.equals("OK")){
            		Toast toast = Toast.makeText(Registrazione.this,
            				mess, Toast.LENGTH_LONG);
            		toast.show();
            		finish();
            	}
                
            }
		};
		
		
		// Toast toast = Toast.makeText(Registrazione.this,
		// "Username già utilizzato", Toast.LENGTH_LONG);
		// toast.show();

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registrazione, menu);
		return true;
	}

	/***
	 * Classe AsyncTask di gestione per l'inserimento del nuovo utente nel DB.
	 */
	public class Registration extends AsyncTask<String, Void, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			// Creazione di un Dialog di attesa
			progressDialog = ProgressDialog.show(Registrazione.this, "ShopApp",
					"Registrazione in corso...", true);
		};

		@Override
	    protected Void doInBackground(String... params)
	    {   
		
	    	try {
	    		
	    		URL url = new URL("http://" + Const.IPADDRESS+ "/registrazione.php");

				JSONObject json = new JSONObject();
				json.put("Name", params[0]);
				json.put("Surname", params[1]);
				json.put("Email", params[2]);
				json.put("User", params[3]);
				json.put("Password", params[4]);	

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url.toURI());

				// Prepare JSON to send by setting the entity
				httpPost.setEntity(new StringEntity(json.toString(), "UTF-8"));

				// Set up the header types needed to properly transfer JSON
				httpPost.setHeader("Content-Type", "application/json");
				httpPost.setHeader("Accept-Encoding", "application/json");
			//	httpPost.setHeader("Accept-Language", "en-US");

				// Execute POST
				HttpResponse response = httpClient.execute(httpPost);
				
				
				
//				String res = "";
//				HttpClient httpclient = new DefaultHttpClient();
//				HttpPost httppost = new HttpPost("http://" + Const.IPADDRESS
//						+ "/registrazione.php");
//				
//				//Impacchettamento delle informazioni
//				JSONObject json = new JSONObject();
//				json.put("Name", params[0]);
//				json.put("Surname", params[1]);
//				json.put("Email", params[2]);
//				json.put("User", params[3]);
//				json.put("Password", params[4]);	
//				StringEntity se = new StringEntity(json.toString());
//				
//				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
//				"application/json"));
//				httppost.setEntity(se);
//			
//				HttpResponse response = httpclient.execute(httppost);
//				
			
				
				//Conversione da inputString a JsonResult
				String jsonResult = GenericFunctions.inputStreamToString(
						response.getEntity().getContent()).toString();
				Log.i("JsonResult", "[" + jsonResult + "]");
				JSONObject object = new JSONObject(jsonResult);

				//Lettura dell'oggetto Json
				String mess = object.getString("Message");
				String result = object.getString("Result");
				String errorQuery = object.getString("QueryErr");
				
				
				Log.i("Message",mess);
				Log.i("Result", result);
				Log.i("errorQuery", errorQuery);
				
				
				//Comunicazione al Thread principale dell'esito dell'operazione di Registrazione
				Message message = handler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("Message",mess);
				b.putString("Result", result);
				b.putString("errorQuery",mess);
				message.setData(b);
				handler.sendMessage(message);
				
			
				
				
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
			}
			return null;	    	
	        
	    }		@Override
		protected void onPostExecute(Void result) {
			// Chiusura del Dialog di attesa
			super.onPostExecute(result);
			progressDialog.dismiss();
		};
	}

}
