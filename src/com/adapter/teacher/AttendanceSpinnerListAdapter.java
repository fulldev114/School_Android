package com.adapter.teacher;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;

public class AttendanceSpinnerListAdapter extends BaseAdapter {
    String[] Date;

    ViewHolder holder;

    Context myc;

    public AttendanceSpinnerListAdapter(Context c, String[] messageList) {
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
            convertview = li.inflate(R.layout.adres_item_spinner, parent,false);

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
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.adres_item_spinner, parent,false);

            TextView txt_spin = (TextView) convertView.findViewById(R.id.txt_spin);

        txt_spin.setText(Date[position]);

        return convertView;
    }

}