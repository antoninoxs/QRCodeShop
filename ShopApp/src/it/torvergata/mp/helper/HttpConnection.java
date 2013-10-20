package it.torvergata.mp.helper;

import it.torvergata.mp.Const;
import it.torvergata.mp.GenericFunctions;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpConnection {

	public HttpConnection(){
		
	}
	
	public JSONObject connect(String phpFile, String valueName, String value, Handler handler){
		// Preparazione delle informazioni da inviare al server
		final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(valueName, value));
		JSONObject object=null;
		try {

			// Connessione al Server

			//Creazione oggetto per parametri
			HttpParams httpParameters = new BasicHttpParams();
			
			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 3000;

			//Imposto il parametro di timeout connection
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 3000;
			
			//Imposto il timeout della socket
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
					
			HttpPost httppost = new HttpPost("http://" + Const.IPADDRESS
					+ "/"+phpFile+".php");
			
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Ricezione della risposta
			BasicHttpResponse httpResponse = (BasicHttpResponse)  httpClient.execute(httppost);
			
			// Conersione da inputString a JsonResult
			String jsonResult = GenericFunctions.inputStreamToString(
					httpResponse.getEntity().getContent()).toString();
			Log.i("JsonResult", "[" + jsonResult + "]");
			
			object = new JSONObject(jsonResult);
			
			return object;
			
		}catch (ConnectTimeoutException e) {
			Log.e("TIMEOUT", "Timeout connection: " + e.toString());
			Message message = handler.obtainMessage(1, Const.TIMEOUT, 0);
			handler.sendMessage(message);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection: " + e.toString());
		}
		return object;
	
	}
}
