package it.torvergata.mp;


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


	EditText edUsername;
	EditText edPassword;
	
	TextView tvRegistrazione;
	
	Button bAccesso,btnSalta;
	
	String res="";
	InputStream is = null;
	
	String user="";
	String password="";

	Handler handler;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		edUsername = (EditText) findViewById(R.id.editTextUsername);
		edPassword = (EditText) findViewById(R.id.editTextPassword);
		
		tvRegistrazione = (TextView) findViewById(R.id.textViewRegistrazione);
		
		bAccesso = (Button) findViewById(R.id.buttonAccess);
		btnSalta = (Button) findViewById(R.id.btnSalta);
		

		final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		
		
		bAccesso.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				
				if(edUsername.getText().length()==0 ||edPassword.getText().length()==0){
					Toast toast = Toast.makeText(MainActivity.this, "Username o Password Vuoti", Toast.LENGTH_LONG);
					toast.show();
				}
				
				
				else{

					user = edUsername.getText().toString();
					password = edPassword.getText().toString();
					
					Log.i("USER", user);
					Log.i("PASS", password);
					
				
					
					handler = new Handler() {
			            @Override
			            public void handleMessage(Message message) {
			                res=(String) message.obj;
			                if(res.toString().equals("YES")){
								Toast.makeText(MainActivity.this, "Connesso",
										Toast.LENGTH_SHORT).show();
								Intent intent = new Intent(getBaseContext(), TabsFragmentActivity.class);
								dialog.dismiss();
								startActivity(intent);
			                }
							else if(res.toString().equals("FALSE")){
								dialog.dismiss();
								Toast.makeText(MainActivity.this, "Username e Password Errati",
										Toast.LENGTH_SHORT).show();
							}
			                
			            }
					};
					
					/*
					Thread threadConnectionForLogin = new Thread(new Runnable(){
					    @Override
					    public void run() {
					        try {
					        	 HttpClient httpclient = new DefaultHttpClient();
//							   	 HttpPost httppost = new HttpPost("http://10.0.2.2/login.php?u="+user+"&p="+pass);
							     HttpPost httppost = new HttpPost("http://"+Const.IPADDRESS+"/login.php");
							     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							     HttpResponse response = httpclient.execute(httppost); 
							     HttpEntity entity = response.getEntity();
							     is = entity.getContent();
							     Log.i("tag", is.toString());
					        	
							     BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
							     StringBuilder sb = new StringBuilder();
							     String line = null;
							     while ((line = reader.readLine()) != null) {
							             sb.append(line);
							        }
							     Log.i("tag1", is.toString());
							     is.close();
							     String result=sb.toString();
							     Log.i("result", result);
							     Message message = handler.obtainMessage(1, result);
					             handler.sendMessage(message);
							     
					        } catch (Exception e) {
					        	 Log.e("log_tag", "Error in http connection: "+e.toString());
					        }
					    }
					});*/
					
				
					LoadData task = new LoadData();
					task.execute();
					
					
				
					//threadConnectionForLogin.start(); 
					
				}
			}
		});
		
		tvRegistrazione.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), Registrazione.class);
				startActivity(intent);
				finish();
			}
		});
	
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

	
	public class LoadData extends AsyncTask<Void, Void, Void> {
	    ProgressDialog progressDialog;
	    //declare other objects as per your need
	    @Override
	    protected void onPreExecute()
	    {
	        progressDialog= ProgressDialog.show(MainActivity.this, "ShopApp","Accesso in corso...", true);

	        //do initialization of required objects objects here                
	    };      
	    @Override
	    protected Void doInBackground(Void... params)
	    {   
	    	 final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			 nameValuePairs.add(new BasicNameValuePair("u",user));
			 nameValuePairs.add(new BasicNameValuePair("p",password));
	    	 HttpClient httpclient = new DefaultHttpClient();
//		   	 HttpPost httppost = new HttpPost("http://10.0.2.2/login.php?u="+user+"&p="+pass);
		     HttpPost httppost = new HttpPost("http://"+Const.IPADDRESS+"/login.php");
		     try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
		     HttpResponse response = httpclient.execute(httppost); 
		     HttpEntity entity = response.getEntity();
		     is = entity.getContent();
		     Log.i("tag", is.toString());
	   	
		     BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		     StringBuilder sb = new StringBuilder();
		     String line = null;
		     while ((line = reader.readLine()) != null) {
		             sb.append(line);
		        }
		     Log.i("tag1", is.toString());
		     is.close();
		     String result=sb.toString();
		     Log.i("result", result);
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
	         
	         //do loading operation here  
	        return null;
	    }       
	    @Override
	    protected void onPostExecute(Void result)
	    {
	        super.onPostExecute(result);
	        progressDialog.dismiss();
	    };
	 }
	
	
}
