package it.torvergata.mp.entity;


import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;

public class ListProduct extends ArrayList<Product> {
	
	double totalPrice=0.00;
	int count=0;
	
	public ListProduct(){
		super();
		
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}

	public int getCount() {
		return  this.count;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public boolean add(Product prod){
		super.add(prod);
		
		totalPrice+=prod.getPrezzoTotale();
		totalPrice=round(totalPrice,2);
		count++;
		return true;
		
	}
	
	public Product searchByIdAndUpdateLast(int id){
		
		for(int i=0;i<super.size();i++){
			if(super.get(i).getId()==id){
				Product t=super.get(i);
				updateLast(i);
				return t;	
			}
		}
		
		return null;
		
	}
	
	public Product remove(int position){
		int q=super.get(position).getQuantita();
		totalPrice-=super.get(position).getPrezzoTotale();
		totalPrice=round(totalPrice,2);
		for (int i=0;i<q;i++){
			count--;
		}
		super.remove(position);
		return null;
	}
	private double round(double d, int numbersAfterDecimalPoint) {
	    double n = Math.pow(10, numbersAfterDecimalPoint);
	    double d2 = d * n;
	    long l = (long) d2;
	    return ((double) l) / n;
	}

	public void setIncrementTotalPrice(double prezzoUnitarioProdotto) {
		// TODO Auto-generated method stub
		count++;
		totalPrice+=prezzoUnitarioProdotto;
	}

	public void updateLast(int pos) {
		Product t=this.get(pos);
		this.remove(pos);
		this.add(t);
		
	}

	public JSONObject getListIdForOrder(String user) {
		JSONArray jsonArray= new JSONArray();
		JSONObject jsonObjH = new JSONObject();
		
		JSONObject jsonObjU = new JSONObject();
		JSONObject json = new JSONObject();
		
		
		try {
			jsonObjH.put("hashOrder", "4389324798dsewrtewwetwetewtewtewtewrdsarujdw9ddisfh4289");
			jsonObjU.put("user", user);
		
		
		for(int i=0;i<this.size();i++){
			JSONObject temp = new JSONObject();
			temp.put("Q", this.get(i).getQuantita());
			temp.put("id", this.get(i).getId());
			jsonArray.put(temp);
		}

		json.put("HashID"	, jsonObjH);
		json.put("User"		, jsonObjU);
		json.put("Products"	, jsonArray);
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
		// TODO Auto-generated method stub
		
	}

	public void setDecrementTotalPrice(double prezzoUnitarioProdotto) {
		// TODO Auto-generated method stub
		count--;
		totalPrice-=prezzoUnitarioProdotto;
	}

	
}
