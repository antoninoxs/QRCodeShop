package it.torvergata.mp;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registrazione extends Activity {

	EditText edNome;
	EditText edCognome;
	EditText edEmail;
	EditText edUsername;
	EditText edPassword;
	EditText edPassword1;
	Button   bRegistrati;
	InputStream is = null;
	InputStream ins = null;
	
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
		
		bRegistrati.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(edNome.length()==0 || edCognome.length()==0 || edEmail.length()== 0 || edUsername.length()==0 || edPassword.length() == 0){
					Toast toast = Toast.makeText(Registrazione.this, "Uno dei campi necessari per la registrazione è vuoto", Toast.LENGTH_LONG);
					toast.show();
				}else if(!((edPassword.getText().toString()).equals(edPassword1.getText().toString()))){
					Toast toast = Toast.makeText(Registrazione.this, "Le password non coincidono", Toast.LENGTH_LONG);
					toast.show();
				}else{
					String nome     = edNome.getText().toString();
					String cognome  = edCognome.getText().toString();
					String email    = edEmail.getText().toString();
					String username = edUsername.getText().toString();
					String password = edPassword.getText().toString();
					insertToDB(nome,cognome,email,username,password);
				}
			}
		});
	}
	
	public void insertToDB(String nome, String cognome, String email, String username, String password){
		
		 
		String result = "";
		
		if(true){
			final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("n",nome));
			nameValuePairs.add(new BasicNameValuePair("c",cognome));
			nameValuePairs.add(new BasicNameValuePair("e",email));
			nameValuePairs.add(new BasicNameValuePair("u",username));
			nameValuePairs.add(new BasicNameValuePair("p",password));

			
			Thread threadConnectionForRegister = new Thread(new Runnable(){
			    @Override
			    public void run() {
			        try {
			        	 HttpClient httpclient = new DefaultHttpClient();
//					        HttpPost httppost = new HttpPost("http://10.0.2.2/registrazione.php?n="+nome+"&c="+cognome+"&e="+email+"&u="+username+"&p="+password);
					        HttpPost httppost = new HttpPost("http://"+Const.IPADDRESS+"/registrazione.php");
					        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					        HttpResponse response = httpclient.execute(httppost); 
					        HttpEntity entity = response.getEntity();
					        is = entity.getContent();
					        Log.i("tag", is.toString());
			        } catch (Exception e) {
			        	 Log.e("log_tag", "Error in http connection: "+e.toString());
			        }
			    }
			});

			threadConnectionForRegister.start(); 
			//http post
		
			
			try{
		        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		        StringBuilder sb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		                sb.append(line);
		        }
		        Log.i("tag1", is.toString());
		        is.close();
		        result=sb.toString();
		        Log.i("result", result);
			}catch(Exception e){
			        Log.e("log_tag", "Error converting result "+e.toString());
			}
		}
		else {
			Toast toast = Toast.makeText(Registrazione.this, "Username già utilizzato", Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
//	Ritorna true se l'username è già stao utilizzato
	public boolean controlloUserName(String usr){
		
		
		String result = "";
		
		final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("u",usr));
		
		Thread threadConnectionForCheckUser = new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		        	 HttpClient httpclient = new DefaultHttpClient();
				        HttpPost httppost = new HttpPost("http://"+Const.IPADDRESS+"/controlloUserName.php");
				        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				        HttpResponse response = httpclient.execute(httppost); 
				        HttpEntity entity = response.getEntity();
				        ins = entity.getContent();
				        Log.i("controlloUSR", ins.toString());
		        	} catch (Exception e) {
		        	 Log.e("log_tag", "Error in http connection: "+e.toString());
		        }
		    }
		});

		threadConnectionForCheckUser.start(); 
		
		//http post
		try{
		       
		}catch(Exception e){
		        Log.e("log_tag", "Error in http connection: "+e.toString());
		}
		
//		Conversione risposta in Stringa
		try{
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	                sb.append(line);
	        }
	        Log.i("controlloUSR1", is.toString());
	        is.close();
	        result=sb.toString();
	        Log.i("resultControlloUSR", result);
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
		}
		Log.i("size", ""+result.toString().length());
		if(result.toString().equals("YES")){
			return true;
		}
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registrazione, menu);
		return true;
	}

}
