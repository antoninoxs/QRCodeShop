package it.torvergata.mp;

/**
 * This file is part of AdvancedListViewDemo.
 * You should have downloaded this file from www.intransitione.com, if not, 
 * please inform me by writing an e-mail at the address below:
 *
 * Copyright [2011] [Marco Dinacci <marco.dinacci@gmail.com>]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * The license text is available online and in the LICENSE file accompanying the distribution
 * of this program.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductAdapter extends ArrayAdapter<Product> {

	private LayoutInflater mInflater;
	
	private DrawableManager drawab; 
	private List<Product> productList =new ArrayList<Product>();
	private int mViewResourceId;
	
	public ProductAdapter(Context ctx, int viewResourceId,List<Product> pList) {
		super(ctx, viewResourceId, pList);
		
		
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		productList=pList;

		drawab = new DrawableManager();

		
		mViewResourceId = viewResourceId;
	}

	@Override
	public int getCount() {
		return productList.size();
	}

	@Override
	public Product getItem(int position) {
		return productList.get(position);
	}


	@Override
	public long getItemId(int position) {
		return productList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(mViewResourceId, null);
		
		TextView tv1 = (TextView)convertView.findViewById(R.id.title);
		TextView tv2 = (TextView)convertView.findViewById(R.id.description);
		ImageView iv = (ImageView)convertView.findViewById(R.id.list_image);
		TextView tvPrice = (TextView)convertView.findViewById(R.id.price);
		
		tv1.setText(productList.get(position).getNome());
		tv2.setText(productList.get(position).getDescrizione());
		tvPrice.setText(Double.toString(productList.get(position).getPrezzo())+" Euro");
		
		drawab.fetchDrawableOnThread(Const.IMAGE_URL+productList.get(position).getFileImmagine(), iv);
		//iv.setImageDrawable(drawab.fetchDrawable(Const.IMAGE_URL));

		return convertView;
	}
}
