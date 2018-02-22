package com.adapter.teacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.view.CircularImageView;

import java.util.ArrayList;

public class ChildrenListAdapter extends BaseAdapter {
    ArrayList<Childbeans> Date;

    ViewHolder holder;

    Context myc;

    public ChildrenListAdapter(Context c, ArrayList<Childbeans> messageList) {
        myc = c;
        this.Date = messageList;
    }

    public void updateReceiptsList( ArrayList<Childbeans> messageList) {
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
        TextView tachername, subject, txtBadge, txtClass;
        CircularImageView profile_pic;
    }

    @Override
    public View getView(final int pos,  View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.adres_children_list_item, null);

            holder = new ViewHolder();
            holder.tachername = (TextView) convertview.findViewById(R.id.textView_teachername);
            holder.subject = (TextView) convertview.findViewById(R.id.textView_subject);
            holder.txtClass = (TextView) convertview.findViewById(R.id.txtClass);
            holder.txtBadge = (TextView) convertview.findViewById(R.id.txtBadge);

            holder.profile_pic = (CircularImageView) convertview.findViewById(R.id.imageView1);
            holder.profile_pic.setBorderWidth(5);
//			holder.profile_pic.setBorderColor( getResources().getColor(R.color.white));


            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.tachername.setText( Date.get(pos).child_name );
        holder.subject.setText(myc.getResources().getString(R.string.str_parent) + (Date.get(pos).parent_name.equals("null") ? "" : Date.get(pos).parent_name) );
        if ( Date.get(pos).class_name != null && !Date.get(pos).class_name.isEmpty() ) {
            holder.txtClass.setVisibility(View.VISIBLE);
            holder.txtClass.setText(myc.getResources().getString(R.string.str_class)+ Date.get(pos).class_name);
        }
        ApplicationData.setProfileImg(holder.profile_pic, ApplicationData.web_server_url + "uploads/" + Date.get(pos).child_image, myc);

        holder.txtBadge.setVisibility(View.VISIBLE);
        if (Date.get(pos).badge == 0) {
            holder.txtBadge.setVisibility(View.GONE);
        } else if (Date.get(pos).badge < 10){
            holder.txtBadge.setText(Date.get(pos).badge + "");
        } else {
            holder.txtBadge.setText("N");
        }

        return convertview;
    }
}