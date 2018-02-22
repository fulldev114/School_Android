package com.adapter.teacher;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudstream.cslink.R;

import java.util.ArrayList;

public class YearSpinnerListAdapter extends BaseAdapter {
    ArrayList<String> Date;

    ViewHolder holder;

    Context myc;

    public YearSpinnerListAdapter(Context c, ArrayList messageList) {
        myc = c;
        this.Date = messageList;
    }


    public int getCount() {
        return Date.size();
    }

    public Object getItem(int arg0) {
        return Date.get(arg0);
    }

    public long getItemId(int position) {
        return position;
    }


    static class ViewHolder {
        TextView txt_spin, subject, txtBadge;

    }

    @Override
    public View getView(final int pos, View convertview, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.adres_item_spinner_mark, parent, false);

            holder = new ViewHolder();
            holder.txt_spin = (TextView) convertview.findViewById(R.id.txt_spin);
            convertview.setTag(holder);

        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.txt_spin.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        holder.txt_spin.setText(Date.get(pos));
        holder.txt_spin.setTextColor(myc.getResources().getColor(R.color.white_light));


        return convertview;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = li.inflate(R.layout.adres_item_spinner_mark, parent, false);

        TextView txt_spin = (TextView) convertView.findViewById(R.id.txt_spin);
        txt_spin.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        txt_spin.setText(Date.get(position));
        return convertView;
    }

}