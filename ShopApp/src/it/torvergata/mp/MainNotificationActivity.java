package it.torvergata.mp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


import com.google.android.gcm.GCMRegistrar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import static it.torvergata.mp.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static it.torvergata.mp.CommonUtilities.EXTRA_MESSAGE;
import static it.torvergata.mp.CommonUtilities.SENDER_ID;

public class MainNotificationActivity extends Activity {
   ProgressDialog progressBar;
   AsyncTask<Void, Void, Void> mRegisterTask;
   @Override
   public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_notification);
       // Il telefono risulta pronto
       GCMRegistrar.checkDevice(this);
       // Il manifesto risulta pronto
       GCMRegistrar.checkManifest(this);
       registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
       // Ottieni il Registration ID
       final String regId = GCMRegistrar.getRegistrationId(this);
       // Controllo se sono registrato
       if (regId.equals("")) {
           // Mi registro
           GCMRegistrar.register(this, SENDER_ID);
       } else {
           // Sono registrato
           if (!GCMRegistrar.isRegisteredOnServer(this)) {
               // Provo a registrarmi ancora
           	Log.d("PROVA", "Non Sono REgIStrato");
               final Context context = this;
               mRegisterTask = new AsyncTask<Void, Void, Void>() {
                   @Override
                   protected Void doInBackground(Void... params) {
                       ServerUtilities.register(context, regId);
                       return null;
                   }
                   @Override
                   protected void onPostExecute(Void result) {
                       mRegisterTask = null;
                   }
               };
               mRegisterTask.execute(null, null, null);
           }
       }
       // vostro codice personale
   }
   /**
    * Ricevo notifica push
    * */
   private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
           // Sveglia il telefono se è in stand-by
           WakeLocker.acquire(getApplicationContext());
           // Visualizza il messaggio
           Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
           // Rilascia il wavelocker
           WakeLocker.release();
       }
   };
   @Override
   protected void onDestroy() {
       if (mRegisterTask != null) {
           mRegisterTask.cancel(true);
       }
       try {
           unregisterReceiver(mHandleMessageReceiver);
           GCMRegistrar.onDestroy(this);
       } catch (Exception e) {
       }
       super.onDestroy();
   }
}