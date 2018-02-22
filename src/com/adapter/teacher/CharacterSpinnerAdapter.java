package com.adapter.teacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudstream.cslink.R;

import java.util.ArrayList;

public class CharacterSpinnerAdapter extends BaseAdapter {
    ArrayList<Childbeans> Date;

    ViewHolder holder;

    Context myc;

    public CharacterSpinnerAdapter(Context c, ArrayList<Childbeans> messageList) {
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


        holder.txt_spin.setText(Date.get(pos).character_name);
        holder.txt_spin.setTextColor(myc.getResources().getColor(R.color.white_light));


        return convertview;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.adres_item_spinner, parent,false);

            TextView txt_spin = (TextView) convertView.findViewById(R.id.txt_spin);

        txt_spin.setText(Date.get(position).character_name);
        return convertView;
    }

}