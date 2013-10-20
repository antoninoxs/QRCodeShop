package it.torvergata.mp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Const {
	
	//public static final String IPADDRESS = "tesi.dyndns.tv:88";
	//public static final String IMAGE_URL = "http://tesi.dyndns.tv:88/images/";
	public static final String IPADDRESS = "151.24.36.7:88";
	public static final String IMAGE_URL = "http://151.24.36.7:88/images/";
	public static final int OK = 1;
	public static final int KO = 0;
	public static final int TIMEOUT = 2;
	//Numero di cifre in un id di un prodotto.
	public static final int IDFORMAT = 6;
	//Nome del file per le Preferences
	public static final String PREFS_NAME = "FilePreferences";
	
	public static final Drawable resize(Drawable image) {
	    Bitmap b = ((BitmapDrawable)image).getBitmap();
	    Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 130, 130, false);
	    return new BitmapDrawable(bitmapResized);
	}

	public static boolean verifyConnection(Context ctx) {
		// TODO Auto-generated method stub
		ConnectivityManager cm =
		        (ConnectivityManager)ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}
}
