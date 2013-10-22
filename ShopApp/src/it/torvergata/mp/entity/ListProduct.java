package it.torvergata.mp.entity;


import java.util.ArrayList;
import java.util.List;

public class ListProduct extends ArrayList<Product> {
	
	double totalPrice=0.00;
	
	public ListProduct(){
		super();
		
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public boolean add(Product prod){
		super.add(prod);
		
		totalPrice+=prod.getPrezzoTotale();
		totalPrice=round(totalPrice,2);
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
		totalPrice-=super.get(position).getPrezzoTotale();
		totalPrice=round(totalPrice,2);
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
		totalPrice+=prezzoUnitarioProdotto;
	}

	public void updateLast(int pos) {
		Product t=this.get(pos);
		this.remove(pos);
		this.add(t);
		
	}
	
}
