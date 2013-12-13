package it.torvergata.mp;
 
import android.content.Context;
import android.content.Intent;
  
public final class CommonUtilities {
    // indirizzo del server per registrare
    static final String SERVER_URL = "http://shopapp.dyndns.org:88/PUSH/register.php"; 
    // ID del progetto di google
    static final String SENDER_ID = "589713569951"; 
    static final String TAG = "Notifica per android";
    static final String DISPLAY_MESSAGE_ACTION = "it.dferrari.notifiche.DISPLAY_MESSAGE";
    static final String EXTRA_MESSAGE = "message";
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}