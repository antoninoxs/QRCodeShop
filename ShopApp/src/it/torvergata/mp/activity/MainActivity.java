package it.torvergata.mp.activity;


import it.torvergata.mp.Const;
import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.R.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText edUsername,edPassword;
	private	TextView tvRegistrazione;
	private	Button bAccesso,btnSalta;
	private	String res="",
			user="",
			password="";
	private InputStream is = null;
	private	Handler handler;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Gestione della Sessione
		SharedPreferences settings = getSharedPreferences(Const.PREFS_NAME, 0);
		//Si prende il valore LoggedIn, se questo non esiste, ritorna falso
		boolean hasLoggedIn = settings.getBoolean("LoggedIn", false);

		if(hasLoggedIn)
		{
		    //Se l'utente ha effettuato il login in precedenza si salta la schermata di Login
			Intent intent = new Intent(getBaseContext(), TabsFragmentActivity.class);
			startActivity(intent);
			finish();
		}
				
		setContentView(R.layout.activity_main);

		edUsername 			= (EditText) findViewById(R.id.editTextUsername);
		edPassword 			= (EditText) findViewById(R.id.editTextPassword);
		tvRegistrazione 	= (TextView) findViewById(R.id.textViewRegistrazione);
		bAccesso			= (Button) findViewById(R.id.buttonAccess);
		btnSalta 			= (Button) findViewById(R.id.btnSalta);
	
		final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
			
		bAccesso.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				
				//Check dei campi User e Password
				if(edUsername.getText().length()==0 ||edPassword.getText().length()==0){
					Toast toast = Toast.makeText(MainActivity.this, R.string.tBlankUserOrPassword, Toast.LENGTH_LONG);
					toast.show();
				}				
				
				else{
					user = edUsername.getText().toString();
					password = edPassword.getText().toString();
					
					Log.i("USER", user);
					Log.i("PASS", password);
					
					//Handler per il messaggio di risposta del Server, proveniente dal Thread che effettua il login.
					handler = new Handler() {
			            @Override
			            public void handleMessage(Message message) {
			                res=(String) message.obj;
			                if(res.toString().equals("YES")){
								Toast.makeText(MainActivity.this, "Connesso",
										Toast.LENGTH_SHORT).show();
								
								//Gestione della Sessione
								//L'utente ha effettuato il login con successo, salviamo questa informazione
								SharedPreferences settings = getSharedPreferences(Const.PREFS_NAME, 0);
								//Necessitiamo di un oggetto editor per effettuare dei cambiamenti alle preferences
								SharedPreferences.Editor editor = settings.edit();
								//Impostiamo LoggedIn a True
								editor.putBoolean("LoggedIn", true);
								//Eseguiamo il Commit
								editor.commit();
								
								Intent intent = new Intent(getBaseContext(), TabsFragmentActivity.class);
								dialog.dismiss();
								startActivity(intent);
								//Si chiama il metodo finish per evitare che quando l'utente prema il tasto
								//back, l'applicazione torni alla schermata di login
								finish();
			                }
							else if(res.toString().equals("FALSE")){
								dialog.dismiss();
								Toast.makeText(MainActivity.this, R.string.tUserPasswordWrong,
										Toast.LENGTH_SHORT).show();
							}
			                
			            }
					};
				
					//Lancio dell'AsyncTask Thread che effettua il login al Server
					LoadData task = new LoadData();
					task.execute();
								
				}
			}
		});
		
		// Quando si preme sulla TextView di Registrazione, viene lanciata l'activity di Registrazione
		tvRegistrazione.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), Registrazione.class);
				startActivity(intent);
			}
		});
	
		//Tasto per il Debug
		btnSalta.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
//				
				
				Intent intent = new Intent(getBaseContext(), TabsFragmentActivity.class);
				startActivity(intent);
				
			}
		});
	
	}
	

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	/***
	 * Classe AsyncTask di gestione dell'accesso, il thread provvede a 
	 * controllare la validità delle credenziali di accesso.
	 */
	public class LoadData extends AsyncTask<Void, Void, Void> {
	    ProgressDialog progressDialog;
	    
	    @Override
	    protected void onPreExecute()
	    {
	    	//Creazione di un Dialog di attesa per il login
	        progressDialog= ProgressDialog.show(MainActivity.this, "ShopApp","Accesso in corso...", true);
	    };      
	    @Override
	    protected Void doInBackground(Void... params)
	    {   
	    	//Preparazione delle informazioni di login da inviare al server
	    	final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			 nameValuePairs.add(new BasicNameValuePair("u",user));
			 nameValuePairs.add(new BasicNameValuePair("p",password));
	    	 
			 //Connessione al Server
			 HttpClient httpclient = new DefaultHttpClient();
		     HttpPost httppost = new HttpPost("http://"+Const.IPADDRESS+"/login.php");
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				//Gestione della risposta, InputStram->BufferReader->String
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				Log.i("tag", is.toString());

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				Log.i("tag1", is.toString());
				is.close();
				String result = sb.toString();
				Log.i("result", result);
				
				//Comunicazione al Thread principale dell'esito dell'operazione di accesso
				Message message = handler.obtainMessage(1, result);
				handler.sendMessage(message);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        return null;
	    }       
	    @Override
	    protected void onPostExecute(Void result)
	    {	
	    	//Chiusura del Dialog di attesa
	        super.onPostExecute(result);
	        progressDialog.dismiss();
	    };
	 }
	
	
}
