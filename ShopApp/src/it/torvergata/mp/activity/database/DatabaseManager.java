package it.torvergata.mp.activity.database;


import it.torvergata.mp.entity.ListProduct;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseManager {
	DatabaseInterface dbInterface;
	SQLiteDatabase db;

	//Costruttore
	public DatabaseManager(Context context) {
	    dbInterface = new DatabaseInterface(context);
	}
	
		
	public void open() {
		db = dbInterface.getWritableDatabase();
	}
	
	public void close() {
		db.close();
	}
	
	//Metodo per aggiungere una Riga alla tabella
	public void addRow()
	{
		ContentValues values = new ContentValues();
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_ID, "1212121");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_NAME, "Gocciole");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_DESCRIPTION, "Gocciole Pavesi");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_AVAILABILITY, "25");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_EXPIRE_DATE, "2013-06-12");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_PRICE, "23.00");
		values.put(dbInterface.TABLE_PRODOTTO_COLUMN_IMAGE_FILE, "gocc.png");

		try{
			db.insert(dbInterface.TABLE_PRODOTTO, null, values);
		}
		catch (Exception e){
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}
	
	//Metodo per cancellare una Riga della tabella
	public void deleteRow()
	{
	}

	//Metodo per aggiornare una riga della tabella
	public void updateRow()
	{
	}


	public void insertOrder(int res, ListProduct list) {
		// TODO Auto-generated method stub
		ContentValues OrderValues =new ContentValues();
		OrderValues.put(dbInterface.TABLE_ORDINE_COLUMN_ID, res);
		//OrderValues.put(dbInterface.TABLE_ORDINE_COLUMN_TIMEE, "16:55:33");
	
		
		try{
			
			db.insert(dbInterface.TABLE_ORDINE, null, OrderValues);
		}
		catch (Exception e){
			Log.e("DB Error VALORI TABELLA ORDINE", e.toString());
			e.printStackTrace();
		}
		
		for (int i=0;i<list.size();i++){
			ContentValues PoductValues = new ContentValues();
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_ID, 				list.get(i).getId());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_NAME, 			list.get(i).getNome());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_DESCRIPTION, 	list.get(i).getDescrizione());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_AVAILABILITY,	list.get(i).getDisponibilita());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_EXPIRE_DATE, 	list.get(i).getScadenza());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_PRICE,			list.get(i).getPrezzoUnitario());
			PoductValues.put(dbInterface.TABLE_PRODOTTO_COLUMN_IMAGE_FILE, 		list.get(i).getFileImmagine());
		
			try{
				String field = dbInterface.TABLE_PRODOTTO_COLUMN_ID+" = ?";
				String [] filter = {""+list.get(i).getId()};
				Cursor cursor= db.query(dbInterface.TABLE_PRODOTTO,
						null ,
						field,
						filter,null, null,null);
				cursor.moveToLast();
				if(cursor.getCount() == 1) {
					continue;
				}else{
					db.insert(dbInterface.TABLE_PRODOTTO, null, PoductValues);
				}
			}
			catch (SQLiteConstraintException e){
				Log.e("DB Exception Constrait", e.toString());
				continue;
			}
			catch (Exception e){
				Log.e("DB Error VALORI TABELLA PRODOTTO", e.toString());
				e.printStackTrace();
			}
			ContentValues ContainsValues = new ContentValues();
			ContainsValues.put(dbInterface.TABLE_CONTIENE_COLUMN_ID_ORDINE, res);
			ContainsValues.put(dbInterface.TABLE_CONTIENE_COLUMN_ID_PRODOTTO,list.get(i).getId());
			ContainsValues.put(dbInterface.TABLE_CONTIENE_COLUMN_QUANTITATIVE, list.get(i).getQuantita());
			try{
				db.insert(dbInterface.TABLE_CONTIENE,null, ContainsValues);
			}
			catch (Exception e){
				Log.e("DB Error VALORI TABELLA CONTIENE", e.toString());
				e.printStackTrace();
			}
		
		}
		
		

	}
	
	
}