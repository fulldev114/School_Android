package com.adapter.parent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudstream.cslink.R;
import com.common.view.CircularImageView;

import java.util.ArrayList;

public class SpinnerListAdapter extends BaseAdapter {
    String[] Date;

    ViewHolder holder;

    Context myc;

    public SpinnerListAdapter(Context c, String[] messageList) {
        myc = c;
        this.Date = messageList;
    }


    public int getCount() {
        return Date.length;
    }

    public Object getItem(int arg0) {
        return Date[arg0];
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txt_spin, subject, txtBadge;

    }

    @Override
    public View getView(final int pos,  View convertview, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.item_spinner, parent,false);

            holder = new ViewHolder();
            holder.txt_spin = (TextView) convertview.findViewById(R.id.txt_spin);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.txt_spin.setText(Date[pos]);
        holder.txt_spin.setTextColor(myc.getResources().getColor(R.color.white_light));

        return convertview;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = li.inflate(R.layout.item_spinner, parent,false);

        TextView txt_spin = (TextView) convertView.findViewById(R.id.txt_spin);

        txt_spin.setText(Date[position]);
        txt_spin.setTextColor(myc.getResources().getColor(R.color.dark_blue));

        return convertView;
    }
}