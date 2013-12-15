package it.torvergata.mp;

import static it.torvergata.mp.CommonUtilities.SERVER_URL;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
  
public final class ServerUtilities {
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    static void register(final Context context, final String regId) {
//        String serverUrl = SERVER_URL;
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("regId", regId);
//        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
//        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
//            try {
//                post(serverUrl, params);
//                GCMRegistrar.setRegisteredOnServer(context, true);
//                return;
//            } catch (IOException e) {
//                if (i == MAX_ATTEMPTS) {
//                    break;
//                }
//                try {
//                    Thread.sleep(backoff);
//                } catch (InterruptedException e1) {
//                    Thread.currentThread().interrupt();
//                    return;
//                }
//                backoff *= 2;
//            }
//        }
        
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("i",regId));
		//http post
		try{
		        HttpClient httpclient = new DefaultHttpClient();
//		        HttpPost httppost = new HttpPost("http://10.0.2.2/login.php?u="+user+"&p="+pass);
		        HttpPost httppost = new HttpPost(SERVER_URL);
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        HttpResponse response = httpclient.execute(httppost); 
		        Log.i("RESPONSE", response.toString());
		        HttpEntity entity = response.getEntity();
		        Log.i("ENTITY", entity.toString());
		}catch(Exception e){
		        Log.e("log_tag", "Error in http connection: "+e.toString());
		}
    }
    static void unregister(final Context context, final String regId) {
        String serverUrl = SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
        } catch (IOException e) {
        }
    }
    private static void post(String endpoint, Map<String, String> params) throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Errore " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
}