package it.torvergata.mp;

import java.util.ArrayList;
import java.util.List;

public class ListProduct extends ArrayList<Product> {
	ArrayList<Product> productList;
	double totalPrice=0;
	
	public ListProduct(){
		productList=new ArrayList<Product>();
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public boolean add(Product prod){
		super.add(prod);
		totalPrice+=prod.getPrezzo();
		totalPrice=round(totalPrice,2);
		return true;
		
	}
	public Product remove(int position){
		totalPrice-=super.get(position).getPrezzo();
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
}
