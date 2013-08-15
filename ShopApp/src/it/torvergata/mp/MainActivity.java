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

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
	
	Button bAccesso;
	
	String result = "";
	InputStream is = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		edUsername = (EditText) findViewById(R.id.editTextUsername);
		edPassword = (EditText) findViewById(R.id.editTextPassword);
		
		tvRegistrazione = (TextView) findViewById(R.id.textViewRegistrazione);
		
		bAccesso = (Button) findViewById(R.id.buttonAccess);

		bAccesso.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if(edUsername.getText().length()==0 ||edPassword.getText().length()==0){
					Toast toast = Toast.makeText(MainActivity.this, "Username o Password Vuoti", Toast.LENGTH_LONG);
					toast.show();
				}
				else{
					String user = edUsername.getText().toString();
					String pass = edPassword.getText().toString();
					
					Log.i("USER", user);
					Log.i("PASS", pass);
					
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("u",user));
					nameValuePairs.add(new BasicNameValuePair("p",pass));
					//http post
					try{
					        HttpClient httpclient = new DefaultHttpClient();
//					        HttpPost httppost = new HttpPost("http://10.0.2.2/login.php?u="+user+"&p="+pass);
					        HttpPost httppost = new HttpPost("http://10.0.2.2/login.php");
					        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					        HttpResponse response = httpclient.execute(httppost); 
					        HttpEntity entity = response.getEntity();
					        is = entity.getContent();
					        Log.i("tag", is.toString());
					}catch(Exception e){
					        Log.e("log_tag", "Error in http connection: "+e.toString());
					}
					//convert response to string
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
					if(result.toString().equals("YES")){
						Toast toast = Toast.makeText(MainActivity.this, "CONNESSO", Toast.LENGTH_LONG);
						toast.show();
					}
					else {
						Toast toast = Toast.makeText(MainActivity.this, "USER e PASSWORD ERRATI", Toast.LENGTH_LONG);
						toast.show();
					}
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
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
