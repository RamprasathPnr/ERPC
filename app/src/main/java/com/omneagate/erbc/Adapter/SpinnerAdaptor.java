package com.omneagate.erbc.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.erbc.R;

import java.util.List;

/**
 * Created by Shanthakumar on 10-08-2016.
 */
public class SpinnerAdaptor extends BaseAdapter {
    Context ct;
    String language;

    List<String> menuList;
    private LayoutInflater mInflater;


    public SpinnerAdaptor(Context context, List<String> orders, String selectedLanguage) {
        this.ct = context;
        this.menuList = orders;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.language = selectedLanguage;

    }

    public int getCount() {
        return menuList.size();
    }

    public String getItem(int position) {
        return menuList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.spinner_text, null);
            holder = new ViewHolder();
            holder.number = (TextView) view.findViewById(R.id.spinner_text);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.number.setText(menuList.get(position));
        /*if (language.equalsIgnoreCase("en")) {
            holder.number.setText(menuList.get(position));
        } else if (language.equalsIgnoreCase("ta")) {
            holder.number.setText(menuList.get(position));
        }*/
        return view;
    }

    class ViewHolder {
        TextView number;
    }
}