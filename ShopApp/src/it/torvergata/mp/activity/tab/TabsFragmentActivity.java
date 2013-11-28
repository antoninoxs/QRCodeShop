package it.torvergata.mp.activity.tab;


import it.torvergata.mp.R;
import it.torvergata.mp.R.drawable;
import it.torvergata.mp.R.id;
import it.torvergata.mp.R.layout;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Product;

import java.util.HashMap;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
 

 
/**
 * Activity Container di gestione del meccanismo tabHost,in questa classe provvediamo alla creazione 
 * iniziale del tabHost, all'aggiunta dei tre tab principali, nonchè alla loro instaziazione 
 * questa classe inoltre ha il compito di gestire le transazioni dei fragment,
 * per adempiere a questo compito, e per passare i parametri durante le transizioni
 * si serve delle interfacce dei varie classi di TabFragment.
 */

public class TabsFragmentActivity extends FragmentActivity implements TabHost.OnTabChangeListener, 
TabScanModeScanningFragment.OnTermAcquisitionListener,
TabScanModeListFragment.OnAddQrCodeListener,
TabScanModeMainFragment.OnStartAcquisitionListener,
TabScanModeDetailItemFragment.OnReturnListListener,
TabScanModeSendOrderFragment.OnFinishOrderListener{
 
    private TabHost mTabHost;
    private HashMap mapTabInfo = new HashMap();
    private TabInfo mLastTab = null;
    private Button btnQrCode;
    
    /***
     * Classe di informazioni del tab
     */
    private class TabInfo {
    	private String tag;
    	private Class clss;
    	private Bundle args;
    	private Fragment fragment;
    
    	TabInfo(String tag, Class clazz, Bundle args) {
    		this.tag = tag;
    		this.clss = clazz;
    		this.args = args;
    	}
 
    }
 
    /***
     * Classe di creazione del Tab
     */
    class TabFactory implements TabContentFactory {
 
        private final Context mContext;
 
    
        public TabFactory(Context context) {
            mContext = context;
        }
 
     
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
 
    }
  
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        // Step 1: Inflate layout
        setContentView(R.layout.tabs_layout);
        
        // Step 2: Setup TabHost
        initialiseTabHost(savedInstanceState);
       
        
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
        
        
    }


    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }
 
    /**
     * Metodo di inizializzazione del Tabhost 
     * @param args
     */
    private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        
        //Aggiunta dei tre Tab
        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Scan Mode"), ( tabInfo = new TabInfo("Tab1", TabScanModeMainFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Catalogo"), ( tabInfo = new TabInfo("Tab2", TabCatalogMainFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator("Ordini"), ( tabInfo = new TabInfo("Tab3", TabOrdersMainFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        
        // Default to first tab
        this.onTabChanged("Tab1");
        //
        setTabsBackground();
        mTabHost.setOnTabChangedListener(this);
        
     
    }
 	/***
 	 * Metodo per il settaggio delle immagini dei pulsanti del Tab
 	 */
    private void setTabsBackground() {
 		
 		//Impostazione di tutte le immaggini non selezionate
 		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
 			switch (i) {
			case 0:
				mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.tab_qr_not_selected); // Non selezionato
				break;
			case 1:
				mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.tab_bs_not_selected); // Non selezionato
				break;
			case 2:
				mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.tab_or_not_selected); // Non selezionato
				break;
			default:
				mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.tab_not_selected); // Non selezionato
				break;
			}
 			
 		}
 		int current= mTabHost.getCurrentTab();
 		//Impostazione dell'immagine del tab Selezionato
 		switch (current) {
		case 0:
			mTabHost.getTabWidget().getChildAt(current)
				.setBackgroundResource(R.drawable.tab_qr_selected); //selezionato
			break;
		case 1:
			mTabHost.getTabWidget().getChildAt(current)
				.setBackgroundResource(R.drawable.tab_bs_selected); //selezionato
			break;
		case 2:
			mTabHost.getTabWidget().getChildAt(current)
				.setBackgroundResource(R.drawable.tab_or_selected); //selezionato
			break;
		default:
			mTabHost.getTabWidget().getChildAt(current)
				.setBackgroundResource(R.drawable.tab_selected); //selezionato
			break;
		}
 		
 	}
 	
 
 	/**
 	 * Metodo per l'aggiunta di un Tab ad un TabHost
 	 * @param activity Principale
 	 * @param tabHost su cui aggiungere il tab
 	 * @param tabSpec Identificativo ed etichetta del Tab
 	 * @param tabInfo per l'associazione del tab ad una classe
 	 */
 	private static void addTab(TabsFragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        
 		// Instanziazione di una Tab Factory al tabSpec
        tabSpec.setContent(activity.new TabFactory(activity));
        
        String tag = tabSpec.getTag();
        /*
         * Bisogna controllare se abbiamo già un fragment per questo tab, 
         * probabilmente da uno stato salvato in precedenza. Se presente
         * dobbiamo disabilitarlo.
         * */
        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }
        //Procediamo all'aggiunta del tab
        tabHost.addTab(tabSpec);
    }
 
 	/**
 	 * Metodo per il passaggio (transizione) da un Tab all'altro
 	 */
    public void onTabChanged(String tag) {
        TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
        if (mLastTab != newTab) {
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(this,
                            newTab.clss.getName(), newTab.args);
                    ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }
 
            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
        }
        setTabsBackground();
        
    }

    
    /**
     * Implementazione dell'interfaccia che permette di
     * eseguire la transazione al Fragment della lista 
     * dei prodotti passandogli tale lista
     */
	@Override
	public void ViewListFragment(ListProduct list) {
		// TODO Auto-generated method stub

		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("Scanning");
        TabScanModeListFragment fragment = new TabScanModeListFragment();
        
        //Si richiama il metodo per impostare la lista prodotti
        fragment.updateProductList(list);
        
        fragmentTransaction.replace(R.id.realtabcontent, fragment);
        fragmentTransaction.commit();

	}


    /**
     * Implementazione dell'interfaccia che permette di
     * eseguire la transazione dal Fragment lista profotti
     * al Fragment di Scansione dei prodotti al fine di aggiungere eventuali prodotti
     * a tale scopo si passa la lista dei prodotti precedente per eventuale aggiornamento
     */
	@Override
	public void ViewScanningFragment(ListProduct list) {
		// TODO Auto-generated method stub
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TabScanModeScanningFragment fragmentScann = new TabScanModeScanningFragment();
        
        //Si richiama il metodo per impostare la lista prodotti
        fragmentScann.updateProductList(list);
        
        fragmentTransaction.replace(R.id.realtabcontent, fragmentScann);
        fragmentTransaction.commit();
	}
	
	  /**
     * Implementazione dell'interfaccia che permette di
     * eseguire la transazione dal Main Fragment 
     * al Fragment di Scansione dei prodotti
     */
	public void ViewScanningFragment() {
		// TODO Auto-generated method stub
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.addToBackStack("Main");
        TabScanModeScanningFragment fragmentScann = new TabScanModeScanningFragment();
        
        fragmentTransaction.replace(R.id.realtabcontent, fragmentScann);
        fragmentTransaction.commit();
	}


	  /**
     * Implementazione dell'interfaccia che permette di
     * eseguire la transazione dal Fragment della lista prodotti 
     * al Fragment di invio ordine. A tale scopo si passa la lista dei  prodotti
     */
	@Override
	public void ViewOrderFragment(ListProduct product) {
		// TODO Auto-generated method stub
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TabScanModeSendOrderFragment fragmentScann = new TabScanModeSendOrderFragment();
        fragmentTransaction.addToBackStack("Order");
        
        //Si richiama il metodo per impostare la lista prodotti
        fragmentScann.updateProduct(product);
        
        fragmentTransaction.replace(R.id.realtabcontent, fragmentScann);
        fragmentTransaction.commit();
	}

	  /**
     * Implementazione dell'interfaccia che permette di
     * eseguire la transazione dal Fragment della lista prodotti 
     * al Fragment di dettaglio del singolo prodotto.
     * Ricordando che in questo fragment sarà possibile modificare la
     * quantità del singolo prodotto e quindi il prezzo complessivo dei prodotti,
     * si provvede a passare la lista dei  prodotti e la posizione del prodotto di
     * cui si vuole accedere al dettaglio
     */
	@Override
	public void ViewProductDetailFragment(ListProduct list, int pos) {
		// TODO Auto-generated method stub
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TabScanModeDetailItemFragment fragmentScann = new TabScanModeDetailItemFragment();
        fragmentTransaction.addToBackStack("Scanning");
        
        fragmentScann.updateProduct(list,pos);
        
        fragmentTransaction.replace(R.id.realtabcontent, fragmentScann);
        fragmentTransaction.commit();
	}


	public void FinishOrder(ListProduct list) {
		// TODO Auto-generated method stub

		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
     
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        TabScanModeMainFragment fragmentMain = new TabScanModeMainFragment();
        
        fragmentTransaction.replace(R.id.realtabcontent, fragmentMain);
        fragmentTransaction.commit();
	}
	
	

	
 
 
}