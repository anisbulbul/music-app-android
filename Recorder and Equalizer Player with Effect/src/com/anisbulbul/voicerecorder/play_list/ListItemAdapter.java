package com.anisbulbul.voicerecorder.play_list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anisbulbul.voicerecorder.R;

@SuppressLint("InflateParams")
public class ListItemAdapter extends BaseAdapter {
	private static List<ItemDetails> itemDetailsArrayList = null; // audio items
																// detail array
	private ArrayList<ItemDetails> arraylist;
	private Integer imgid = R.drawable.music_icon; // item audio icon

	private LayoutInflater listLayoutInflater;
	private Context mContext;

	public ListItemAdapter(Context context, ArrayList<ItemDetails> results) {
		itemDetailsArrayList = results;
		mContext = context;
		listLayoutInflater = LayoutInflater.from(mContext);
		
		arraylist = new ArrayList<ItemDetails>();
		arraylist.addAll(results);
	}

	// method to get audio item count
	public int getCount() {
		return itemDetailsArrayList.size();
	}

	// method to get audio object details in position
	public long getItemId(int position) {
		return position;
	}

	// method to set item details
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = listLayoutInflater.inflate(
					R.layout.audio_record_list_details, null);
			holder = new ViewHolder();
			holder.txtItemName = (TextView) convertView.findViewById(R.id.name);
			holder.txtItemDate = (TextView) convertView
					.findViewById(R.id.itemDescription);
			holder.txtItemDuration = (TextView) convertView
					.findViewById(R.id.size);
			holder.itemImage = (ImageView) convertView.findViewById(R.id.photo);
			holder.txtItemSize = (TextView) convertView
					.findViewById(R.id.duration);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// view audio item details in display
		holder.txtItemName.setText(itemDetailsArrayList.get(position)
				.getVoiceTitle());
		holder.txtItemDate.setText(itemDetailsArrayList.get(position)
				.getVoiceDate());
		holder.txtItemDuration.setText(itemDetailsArrayList.get(position)
				.getVoiceDuration());
		holder.txtItemSize.setText(itemDetailsArrayList.get(position)
				.getVoiceSize());
		holder.itemImage.setImageResource(imgid);

		return convertView;
	}

	// item details attributes of xml layout
	static class ViewHolder {
		TextView txtItemName;
		TextView txtItemDate;
		TextView txtItemDuration;
		TextView txtItemSize;
		ImageView itemImage;
	}

	// get item object
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void filter(String charText) {

		charText = charText.toLowerCase(Locale.getDefault());
		itemDetailsArrayList.clear();
		if (charText.length() == 0) {
			itemDetailsArrayList.addAll(arraylist);
		} 
		else 
		{
			for (ItemDetails wp : arraylist) 
			{
				if (wp.getVoiceName().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					itemDetailsArrayList.add(wp);
				}
			}
		}
		notifyDataSetChanged();
	
		
	}
}
