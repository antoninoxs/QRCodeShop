package it.torvergata.mp.activity.tab;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import it.torvergata.mp.Const;
import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.activity.CameraActivity;
import it.torvergata.mp.activity.CameraPreview;
import it.torvergata.mp.activity.MainActivity;
import it.torvergata.mp.activity.ProductDectailActivity;
import it.torvergata.mp.activity.CameraActivity.LoadDataProduct;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;
import it.torvergata.mp.helper.DrawableManager;
import it.torvergata.mp.helper.ProductAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @author mwho
 * 
 */
public class TabScanModeScanningFragment extends Fragment {

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private LinearLayout mLinearLayout;

	TextView scanText;
	Button FinishScanButton;
	Button encodeButton;

	ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
		System.loadLibrary("iconv");
	}

	/***
	 * *******************
	 */
	private ListProduct productList;
	private Handler handler;
	/***
	 * *****************
	 * 
	 */

	/***
	 * 
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mLinearLayout = (LinearLayout) inflater.inflate(R.layout.activity_zbar,
				container, false);
		autoFocusHandler = new Handler();

		/* Instance barcode scanner */
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);

		productList = new ListProduct();
		
		scanText = (TextView) mLinearLayout.findViewById(R.id.scanText);

		FinishScanButton = (Button) mLinearLayout.findViewById(R.id.FinishScanButton);

		FinishScanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				if (barcodeScanned) {
//					barcodeScanned = false;
//					scanText.setText("Scanning...");
//					mCamera.setPreviewCallback(previewCb);
//					mCamera.startPreview();
//					previewing = true;
//					mCamera.autoFocus(autoFocusCB);
//				}
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				FrameLayout preview = (FrameLayout) mLinearLayout
						.findViewById(R.id.cameraPreview);

				if (preview.getChildCount() > 0) {
					preview.removeAllViews();
				}
				FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = new TabScanModeListFragment();
                fragmentTransaction.replace(R.id.realtabcontent, fragment);
                fragmentTransaction.commit();
			}
		});

		//Handler per il messaggio di risposta del Server, proveniente dal Thread.
		handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                String res=(String) message.obj;
                scanText.setText("Ultimo prodotto Scansionato: " +res);
					
                
            }
		};
		
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.

			return null;
		}

		return mLinearLayout;

	}

	public void onResume() {
		super.onResume();

		mCamera = getCameraInstance();

		mPreview = new CameraPreview(getActivity(), mCamera, previewCb,
				autoFocusCB);
		FrameLayout preview = (FrameLayout) mLinearLayout
				.findViewById(R.id.cameraPreview);

		if (preview.getChildCount() > 0) {
			preview.removeAllViews();
		}

		preview.addView(mPreview);

	}

	public void onPause() {
		super.onPause();
		releaseCamera();
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
//				previewing = false;
//				mCamera.setPreviewCallback(null);
//				mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					String contents = sym.getData();
					scanText.setText("Ultimo prodotto Scansionato: Loading...");
					barcodeScanned = true;

					Product tProd = productList.searchById(Integer
							.parseInt(contents));
					if (tProd != null) {
						tProd.increment();
						productList.setIncrementTotalPrice(tProd
								.getPrezzoUnitario());
						scanText.setText("Ultimo prodotto Scansionato: " + tProd.getNome());
							

					} else {
						LoadDataProduct task = new LoadDataProduct();
						task.execute(contents);
							
					}
					

				}
			}

		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	/***
	 * Classe di gestione del Thread che effettua il download dei dati
	 * informativi del prodotto.
	 * 
	 */
	public class LoadDataProduct extends AsyncTask<String, Void, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
		};

		@Override
		protected void onPostExecute(Void result) {

		}

		@Override
		protected Void doInBackground(String... params) {
			String productId = params[0];

			final Product tempProd = new Product(Integer.parseInt(productId));
			// Preparazione delle informazioni da inviare al server
			final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", productId));

			try {
				// Connessione al Server
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://" + Const.IPADDRESS
						+ "/info_download.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				// Ricezione della risposta
				HttpResponse response = httpclient.execute(httppost);

				// Conersione da inputString a JsonResult
				String jsonResult = GenericFunctions.inputStreamToString(
						response.getEntity().getContent()).toString();
				Log.i("JsonResult", "[" + jsonResult + "]");
				JSONObject object = new JSONObject(jsonResult);

				// Lettura dell'oggetto Json
				String idProdotto = object.getString("idProdotto");
				String nome = object.getString("nome");
				double prezzo = object.getDouble("prezzo");
				String scadenza = object.getString("scadenza");
				String disponibilita = object.getString("disponibilita");
				String descrizione = object.getString("descrizione");
				String fileImmagine = object.getString("file_immagine");

				Log.i("idProdotto: ", idProdotto);
				Log.i("nome: ", nome);
				Log.i("prezzo: ", Double.toString(prezzo));
				Log.i("scadenza: ", scadenza);
				Log.i("descrizione: ", descrizione);
				Log.i("disponibilita: ", disponibilita);
				Log.i("file_immagine: ", fileImmagine);

				// Creazione del nuovo Prodotto
				tempProd.setId(Integer.parseInt(idProdotto));
				tempProd.setNome(nome);
				tempProd.setPrezzoUnitario(prezzo);
				tempProd.setScadenza(scadenza);
				tempProd.setDescrizione(descrizione);
				tempProd.setDisponibilita(Integer.parseInt(disponibilita));
				tempProd.setFileImmagine(fileImmagine);

				// Aggiunta del nuovo prodotto alla lista dei prodotti
				productList.add(tempProd);
				
				//Comunicazione al Thread principale del nome del prodotto
				Message message = handler.obtainMessage(1, nome);
				handler.sendMessage(message);
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection: " + e.toString());
			}

			return null;
		};
	}

}
