package com.omneagate.erbc.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.erbc.Dto.ElectricityUsage;
import com.omneagate.erbc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Shanthakumar on 20-07-2016.
 */
public class UsageAdapter extends BaseAdapter {


    private Context mContext;
    private LayoutInflater mInflater;
    private List<ElectricityUsage> electricityUsages;
    private List<Integer> colors;

    public UsageAdapter(Context context, List<ElectricityUsage> electricityUsage,
                        List<Integer> colors) {
        this.mContext = context;
        this.electricityUsages = electricityUsage;
        this.colors = colors;
        this.mInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return electricityUsages.size();
    }

    @Override
    public Object getItem(int position) {
        return electricityUsages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.usage_row, null);
            holder = new ViewHolder();
            holder.mTvColor = (TextView) convertView
                    .findViewById(R.id.txt_usage_color);
            holder.mTvName = (TextView) convertView
                    .findViewById(R.id.txt_usage_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTvColor.setBackgroundColor(colors.get(position));

        holder.mTvName.setText(electricityUsages.get(position).getConsumerNumber()
                + " / " + electricityUsages.get(position).getConsumerName());

        return convertView;
    }

    public class ViewHolder {
        private TextView mTvColor;
        private TextView mTvName;
    }
}
