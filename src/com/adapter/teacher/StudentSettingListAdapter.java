package com.adapter.teacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.view.CircularImageView;

import java.util.ArrayList;

public class StudentSettingListAdapter extends BaseAdapter {
    ArrayList<Childbeans> Date;

    ViewHolder holder;

    Context myc;

    public StudentSettingListAdapter(Context c, ArrayList<Childbeans> messageList) {
        myc = c;
        this.Date = messageList;
    }

    public void updateReceiptsList(ArrayList<Childbeans> messageList) {
        this.Date.clear();
        this.Date.addAll(messageList);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return Date.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView tachername, subject, txtClass;
        CircularImageView profile_pic;
        RelativeLayout lytAll;
        public LinearLayout lin_status_approve;
    }

    @Override
    public View getView(final int pos, View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.adres_student_setting_list, null);

            holder = new ViewHolder();
            // holder.lytAll = (RelativeLayout) convertview.findViewById(R.id.lytAll);
            holder.tachername = (TextView) convertview.findViewById(R.id.textView_teachername);
            holder.subject = (TextView) convertview.findViewById(R.id.textView_subject);
            holder.txtClass = (TextView) convertview.findViewById(R.id.txtClass);

            holder.lin_status_approve = (LinearLayout) convertview.findViewById(R.id.lin_status_approve);
            holder.profile_pic = (CircularImageView) convertview.findViewById(R.id.imageView1);
            holder.profile_pic.setBorderWidth(5);


            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }
        Childbeans chbean = Date.get(pos);

        holder.tachername.setText(Date.get(pos).child_name);
        String parentName = Date.get(pos).nc_parent_name.equals("null") ? "" : Date.get(pos).nc_parent_name;

        if (parentName != null && parentName.length() > 0 && !parentName.equalsIgnoreCase("null")) {
            holder.lin_status_approve.setVisibility(View.GONE);
            holder.subject.setVisibility(View.VISIBLE);
            holder.subject.setText(myc.getString(R.string.pending_status));
            holder.subject.setTextColor(myc.getResources().getColor(R.color.orange_pending));
        } else if (chbean.parent_name != null && !chbean.parent_name.equalsIgnoreCase("null") && chbean.parent_name.length() > 0) {
            holder.lin_status_approve.setVisibility(View.VISIBLE);
            holder.subject.setVisibility(View.GONE);
        } else {
            holder.lin_status_approve.setVisibility(View.GONE);
            holder.subject.setVisibility(View.VISIBLE);
            holder.subject.setText(myc.getString(R.string.not_assigned));
            holder.subject.setTextColor(myc.getResources().getColor(R.color.color_blue_p));
        }

        ApplicationData.setProfileImg(holder.profile_pic, ApplicationData.web_server_url + "uploads/" + Date.get(pos).image, myc);

        return convertview;
    }
}