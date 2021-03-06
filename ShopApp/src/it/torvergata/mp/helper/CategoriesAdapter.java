package it.torvergata.mp.helper;

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

import it.torvergata.mp.GenericFunctions;
import it.torvergata.mp.R;
import it.torvergata.mp.R.id;
import it.torvergata.mp.entity.Category;
import it.torvergata.mp.entity.ListCategories;
import it.torvergata.mp.entity.ListOrders;
import it.torvergata.mp.entity.ListProduct;
import it.torvergata.mp.entity.Category;
import it.torvergata.mp.entity.Product;

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

public class CategoriesAdapter extends ArrayAdapter<Category> {

	private LayoutInflater mInflater;
	private Context context;
	
	
	private ListCategories listCategories =new ListCategories();
	private int mViewResourceId;
	
	public CategoriesAdapter(Context ctx, int viewResourceId,ListCategories cList) {
		super(ctx, viewResourceId,cList);
		context=ctx;
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listCategories=cList;
		
		mViewResourceId = viewResourceId;
	}

	@Override
	public int getCount() {
		return listCategories.size();
	}

	@Override
	public Category getItem(int position) {
		return listCategories.get(position);
	}


	@Override
	public long getItemId(int position) {
		return listCategories.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(mViewResourceId, null);
		
		TextView tvTitle = (TextView)convertView.findViewById(R.id.title);
		TextView tvDescription = (TextView)convertView.findViewById(R.id.description);
		ImageView iv = (ImageView)convertView.findViewById(R.id.list_image);
		TextView tvQuantitative = (TextView)convertView.findViewById(R.id.tvQuantitative);
		TextView tvStateoOrder = (TextView)convertView.findViewById(R.id.tvStateOrder);
		tvTitle.setText(listCategories.get(position).getName());
		
		return convertView;
	}
}
