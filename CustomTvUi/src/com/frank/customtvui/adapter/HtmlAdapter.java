package com.frank.customtvui.adapter;

import java.util.List;

import android.R;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter class to use for the list
 * 
 * @param <T>
 */
public class HtmlAdapter extends ArrayAdapter<String> {
	public List<String> mDatas;
	
	/**
	 * Constructor
	 * 
	 * @param context
	 *            The context
	 * @param contacts
	 *            The list of contacts
	 */
	public HtmlAdapter(final Context context, List<String> datas) {
		super(context, 0, datas);
		mDatas = datas;
	}
	  
	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		if (position >= getCount()) {
			return null;
		}
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = new TextView(getContext());
			holder.mImageView = (TextView) convertView;
			holder.mImageView.setWidth(300);
			holder.mImageView.setHeight(150);
			holder.mImageView.setGravity(Gravity.CENTER);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		((TextView) holder.mImageView).setText("当前元素position = "+position);
		((TextView) holder.mImageView).setBackgroundResource(R.color.black);
		
		return convertView;
	}
	
	
	
	@Override
	public int getCount() {
		if(mDatas != null){
			return mDatas.size();
		}
		return 0;
	}



	/**
	 * holder1
	 * @author san
	 *
	 */
	class ViewHolder {
		public TextView mImageView;
	}
	
}