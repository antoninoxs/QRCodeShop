package it.torvergata.mp.activity.tab;


 
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.torvergata.mp.Const;
import it.torvergata.mp.R;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.tab.TabScanModeScanningFragment.LoadDataProduct;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.HttpConnection;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
 
/**
 * @author mwho
 *
 */
public class TabSendOrderFragment extends Fragment {
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	
	private LinearLayout mLinearLayout;
	private ListProduct productList;
	private ImageButton ibSendOrder;
	private Handler handler;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	//Handler per il messaggio di risposta del Server, proveniente dal Thread.
    			handler = new Handler() {
    	            @Override
    	            public void handleMessage(Message mess) {
    	            	
    	            	int res = mess.arg1;
    	               	
    	            	if(res==Const.KO){
    	                	AlertDialog dialogBox = ProductNotFound();
    						dialogBox.show();
    	                }
    	            	
    	                else if(res==Const.TIMEOUT){
    	                	AlertDialog dialogBox = ConnectionTimeout();
    	    				dialogBox.show();
    	                }
    	                else Log.i("Ordine", "OK");
    	                }
    	                
    	            
    			};
    			
    	
    	if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
        mLinearLayout = (LinearLayout) inflater.inflate(R.layout.tab_order_layout,
				container, false);
        
        ibSendOrder= (ImageButton) mLinearLayout.findViewById(R.id.ibSendOrder);
        
      //Gestione della Sessione
		SharedPreferences settings = getActivity().getSharedPreferences(Const.PREFS_NAME, 0);
	
		String user = settings.getString("User","*");
		Log.i("USER RECUPERATO DA PREFERENCES", user);
		
        final JSONObject listIdForOrder =productList.getListIdForOrder(user);
        		
        
        ibSendOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Determiniamo se c'è una connessione ad internet
				boolean isConnected = Const.verifyConnection(getActivity());
				if(isConnected){
					//Lancio dell'AsyncTask Thread che effettua il download delle informazioni dal Server
					SendOrder task = new SendOrder();
					task.execute(listIdForOrder);
				}else{
					AlertDialog dialogBox = ConnectionNotFound();
					dialogBox.show();
				}
			}
		});
		
        return mLinearLayout;}

	public void updateProduct(ListProduct productl) {
		// TODO Auto-generated method stub
		productList=productl;
	}
	
	public class SendOrder extends AsyncTask<JSONObject, Void, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
		};

		@Override
		protected void onPostExecute(Void result) {
		}

		@Override
		protected Void doInBackground(JSONObject... params) {
			JSONObject json = params[0];
	
				try {
				HttpConnection connection = new HttpConnection();
				
				String jsonStr = json.toString();

				Log.i("Json Inviato: ", json.toString(4));
							
				JSONObject object = connection.connect("ordernew", json, handler);
								
				String result = object.getString("result");
				if (Integer.parseInt(result)==Const.OK){
				
					
					String idCliente = object.getString("idCliente");
					Log.i("idCliente: ", idCliente);
					
					
				
					//Comunicazione al Thread principale del nome del prodotto
					Message message = handler.obtainMessage(1, Const.OK, 0);
				
					handler.sendMessage(message);
				}
				else{
					//Comunicazione al Thread principale del nome del prodotto
					//Message message = handler.obtainMessage(1, Const.KO);
					Message message = handler.obtainMessage(1, Const.KO, 0);
					handler.sendMessage(message);
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("log_tag", "Generic Error:" + e.toString());
			}

			return null;
		}

	
	}
	private AlertDialog ProductNotFound() {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.tWarning)
				.setMessage(R.string.tProductNotFound)
				.setIcon(android.R.drawable.ic_dialog_alert)//.setIcon(R.drawable.img_delete)
				.setPositiveButton(R.string.tContinueScan,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss(); 
								
							}
						})
				.create();
		return alertDialog;
	}
	
	private AlertDialog ConnectionNotFound() {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.tWarning)
				.setMessage(R.string.tActivateConnection)
				.setIcon(android.R.drawable.ic_dialog_alert)//.setIcon(R.drawable.img_delete)
				.setPositiveButton(R.string.tOk,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss(); 
								
							}
						})
				.create();
		return alertDialog;
	}
	
	private AlertDialog ConnectionTimeout() {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.tWarning)
				.setMessage(R.string.tTimeout)
				.setIcon(android.R.drawable.ic_dialog_alert)//.setIcon(R.drawable.img_delete)
				.setPositiveButton(R.string.tOk,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss(); 
								
							}
						})
				.create();
		return alertDialog;
	}

}