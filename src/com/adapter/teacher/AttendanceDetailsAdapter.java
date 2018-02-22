package com.adapter.teacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudstream.cslink.R;

import java.util.ArrayList;

public class AttendanceDetailsAdapter extends BaseAdapter {
    ArrayList<Childbeans> currArrayPeriods;

    ViewHolder holder;
    int absentStatus = 0;

    Context myc;

    public AttendanceDetailsAdapter(Context c, ArrayList<Childbeans> messageList) {
        myc = c;
        this.currArrayPeriods = messageList;
    }

    public int getCount() {
        return currArrayPeriods.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txtPeriod, txtStatus;
        TextView imgStatus;
    }

    @Override
    public View getView(final int pos,  View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.adres_attendance_detail_list_item, null);

            holder = new ViewHolder();
            holder.txtPeriod = (TextView) convertview.findViewById(R.id.txtPeriod);
            holder.imgStatus = (TextView) convertview.findViewById(R.id.imgStatus);
            holder.txtStatus = (TextView) convertview.findViewById(R.id.txtStatus);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.txtPeriod.setText( currArrayPeriods.get(pos).lecture_no + "   " + currArrayPeriods.get(pos).chat_time );
        absentStatus = Integer.valueOf(currArrayPeriods.get(pos).attend);
        drawImg(absentStatus);
        return convertview;
    }

    public void drawImg(int absentStatus) {
        if (absentStatus == 0) {
            if (android.os.Build.VERSION.SDK_INT  < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.imgStatus.setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.absent_red));
            } else {
                holder.imgStatus.setBackground(myc.getResources().getDrawable(R.drawable.absent_red));
            }
            holder.txtStatus.setText(myc.getResources().getString(R.string.str_absent));
        } else if (absentStatus == 2) {
            if (android.os.Build.VERSION.SDK_INT  < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.imgStatus.setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.adres_absent_yellow));
            } else {
                holder.imgStatus.setBackground(myc.getResources().getDrawable(R.drawable.adres_absent_yellow));
            }
            holder.txtStatus.setText(myc.getResources().getString(R.string.str_absent));
        } else {
            if (android.os.Build.VERSION.SDK_INT  < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.imgStatus.setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.adres_present_green));
            } else {
                holder.imgStatus.setBackground(myc.getResources().getDrawable(R.drawable.adres_present_green));
            }
            holder.txtStatus.setText(myc.getResources().getString(R.string.str_present));
        }
    }
}