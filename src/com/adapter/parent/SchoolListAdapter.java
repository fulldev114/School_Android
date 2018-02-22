package com.adapter.parent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.view.CircularImageView;

import java.util.ArrayList;

public class SchoolListAdapter extends BaseAdapter {
    private final int icon;
    ArrayList<Childbeans> Date;

    ViewHolder holder;

    Context myc;

    public SchoolListAdapter(Context c, ArrayList<Childbeans> messageList, int icon) {
        myc = c;
        this.Date = messageList;
        this.icon=icon;
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
        TextView tachername, subject, txtBadge;
        ImageView profile_pic;
        public CircularImageView circle;
        public ImageView img_school;
        public RelativeLayout rel_image;
        public RelativeLayout rel_image_school;
    }

    @Override
    public View getView(final int pos,  View convertview, ViewGroup parent) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.school_list_item, parent,false);

            holder = new ViewHolder();
            holder.tachername = (TextView) convertview.findViewById(R.id.textView_teachername);
            holder.subject = (TextView) convertview.findViewById(R.id.textView_subject);
            holder.txtBadge = (TextView) convertview.findViewById(R.id.txtBadge);
            holder.circle=(CircularImageView) convertview.findViewById(R.id.circle);
            holder.profile_pic = (ImageView) convertview.findViewById(R.id.imageView1);
            holder.img_school = (ImageView) convertview.findViewById(R.id.img_school);
            holder.rel_image=(RelativeLayout)convertview.findViewById(R.id.rel_image);
            holder.rel_image_school=(RelativeLayout)convertview.findViewById(R.id.rel_image_school);
//			holder.profile_pic.setBorderColor( getResources().getColor(R.color.white));

            holder.rel_image.setVisibility(View.GONE);
            holder.rel_image_school.setVisibility(View.VISIBLE);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.img_school.setImageResource(icon);
        holder.tachername.setText(Date.get(pos).school_name);
        holder.subject.setVisibility(View.GONE);
        holder.txtBadge.setVisibility(View.GONE);

        return convertview;
    }
}