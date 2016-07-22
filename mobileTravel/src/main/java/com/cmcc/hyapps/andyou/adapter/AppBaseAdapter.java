package com.cmcc.hyapps.andyou.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 定义一个基本的抽象类适配器-----用于适配listview， gridview的数据
 * 
 * @param <T>
 */
public abstract class AppBaseAdapter<T> extends BaseAdapter {
	public List<T> list;
	public Context context;
	public LayoutInflater inflater;

	public AppBaseAdapter(List<T> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
		notifyDataSetChanged();
	}
	public void  setDatasChange(List<T> list){
		this.list = list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return list != null && !list.isEmpty() ? list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convetView, ViewGroup parent) {
		return createView(position, convetView, parent);
	}

	public abstract View createView(int position, View convertView,
			ViewGroup parent);

}
