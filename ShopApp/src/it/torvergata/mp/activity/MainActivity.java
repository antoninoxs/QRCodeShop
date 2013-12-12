package it.torvergata.mp.activity;


import it.torvergata.mp.Const;
import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.R.menu;
import it.torvergata.mp.activity.database.DatabaseManager;
import it.torvergata.mp.activity.tab.TabsFragmentActivity;
import it.torvergata.mp.crypto.CryptoSha256;
import it.torvergata.mp.helper.Dialogs;
import it.torvergata.mp.helper.HttpConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.R.string;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity iniziale di inserimento credenziali
 */
public class MainActivity extends Activity {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private EditText edUsername,edPassword;
	private	TextView tvRegistrazione;
	private	Button bAccesso,btnSalta;
	private	String res="",
			user="",
			password="",
			passwordCrypto="";
	private InputStream is = null;
	private	Handler handler;
	private CryptoSha256 crypto;
	private Dialogs dialogs;
	
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
   

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "Your-Sender-ID";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;

	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context ctx=this;
		
		if (checkPlayServices()) {
			 gcm = GoogleCloudMessaging.getInstance(this);
	            regid = getRegistrationId(context);

	            if (regid.isEmpty()) {
	                registerInBackground();
	            }
			final DatabaseManager db = new DatabaseManager(ctx);
			
			//Gestione della Sessione
			SharedPreferences settings = getSharedPreferences(Const.PREFS_NAME,0 );
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
			
			/*Inizializzazione dell'oggetto crypto per la crittografia 
			e dell'oggetto Dialogs per gli alert Dialog*/
			crypto= new CryptoSha256();
			dialogs= new Dialogs();
			
			
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
						
						try {
							Log.i("BEFORE CRYPTO", password);
							//Crittografia della password
							passwordCrypto = crypto.encrypt(password);
							Log.i("AFTER CRYPTO", passwordCrypto);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
								
						Log.i("USER", user);
						Log.i("PASS", passwordCrypto);
						
						//Handler per il messaggio di risposta del Server, proveniente dal Thread che effettua il login.
						handler = new Handler() {
				            @Override
				            public void handleMessage(Message message) {
				                res=(String) message.obj;
				                
				                /*
				                 * Tre Possibilità:
				                 * res=1 Connesso
				                 * res=2 Timeout
				                 * res=0 Non Connesso
				                 * 
				                 * */
				                if(Integer.parseInt(res)==1){
									Toast.makeText(MainActivity.this, "Connesso",
											Toast.LENGTH_SHORT).show();
									
									//Gestione della Sessione
									//L'utente ha effettuato il login con successo, salviamo questa informazione
									SharedPreferences settings = getSharedPreferences(Const.PREFS_NAME, 0);
									//Necessitiamo di un oggetto editor per effettuare dei cambiamenti alle preferences
									SharedPreferences.Editor editor = settings.edit();
									//Impostiamo LoggedIn a True
									editor.putBoolean("LoggedIn", true);
									
									//Impostiamo LoggedIn a True
									editor.putString("User", user);
									Log.i("USER SALVATO IN PREFERENCES", user);
									
									//Eseguiamo il Commit
									editor.commit();
									
									Intent intent = new Intent(getBaseContext(), TabsFragmentActivity.class);
									dialog.dismiss();
									startActivity(intent);
									//Si chiama il metodo finish per evitare che quando l'utente prema il tasto
									//back, l'applicazione torni alla schermata di login
									finish();
				                }
				                else if(Integer.parseInt(res)==Const.TIMEOUT){
				                	//Caso Timeout
				                	AlertDialog dialogBox = dialogs.ConnectionTimeout(ctx);
				    				dialogBox.show();
				                }
								else if(Integer.parseInt(res)==0){
									//Caso Connessione non riuscita
									dialog.dismiss();
									Toast.makeText(MainActivity.this, R.string.tUserPasswordWrong,
											Toast.LENGTH_SHORT).show();
								}
				                
				            }
						};
						//Determiniamo se c'è una connessione ad internet
						boolean isConnected = Const.verifyConnection(getBaseContext());
						if(isConnected){
							//Lancio dell'AsyncTask Thread che effettua il login al Server
							LoadData task = new LoadData();
							task.execute();
						}else{
							AlertDialog dialogBox = dialogs.ConnectionNotFound(ctx);
							dialogBox.show();
							
						}
									
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
				public void onClick(View view) {
				    if (true) {
				        new AsyncTask() {
				            protected String doInBackground(Void... params) {
				                String msg = "";
				                try {
				                    Bundle data = new Bundle();
				                        data.putString("my_message", "Hello World");
				                        data.putString("my_action",
				                                "com.google.android.gcm.demo.app.ECHO_NOW");
				                        String id = Integer.toString(msgId.incrementAndGet());
				                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
				                        msg = "Sent message";
				                } catch (IOException ex) {
				                    msg = "Error :" + ex.getMessage();
				                }
				                return msg;
				            }

				            protected void onPostExecute(String msg) {
				                mDisplay.append(msg + "\n");
				            }

							@Override
							protected Object doInBackground(Object... params) {
								// TODO Auto-generated method stub
								return null;
							}
				        }.execute(null, null, null);
				    } else if (false) {
				        mDisplay.setText("");
				    }
				}
			});
		}else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
	
	}
	
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i("TAG", "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
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
	   		String result = null;
			try {
			
				JSONObject json = new JSONObject();
				json.put("user", user);
				json.put("password", passwordCrypto);
				Log.i("PASSWORD NEL JSON", passwordCrypto);
			
				//La classe Http Connection provvede a gestire la connessione (timeout, handler etc etc)
				HttpConnection connection = new HttpConnection();
				JSONObject object = connection.connect("login", json, handler,Const.CONNECTION_TIMEOUT,Const.SOCKET_TIMEOUT);
				
				result = object.getString("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Comunicazione al Thread principale dell'esito dell'operazione di accesso
			Message message = handler.obtainMessage(1, result);
			handler.sendMessage(message);
	
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
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
	    new AsyncTask() {
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;

	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                sendRegistrationIdToBackend();

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
	        }

	        protected void onPostExecute(String msg) {
	            mDisplay.append(msg + "\n");
	        }

			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				return null;
			}
	    }.execute(null, null, null);
	   
	}
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
	    // Your implementation here.
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
}
