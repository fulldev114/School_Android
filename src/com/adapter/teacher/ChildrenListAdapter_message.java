package com.adapter.teacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.view.CircularImageView;

import java.util.ArrayList;

public class ChildrenListAdapter_message extends BaseAdapter {
    ArrayList<Childbeans> Date;

    ViewHolder holder;

    Context myc;

    public ChildrenListAdapter_message(Context c, ArrayList<Childbeans> messageList) {
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
        public LinearLayout inc_gp_bdg;
        public TextView txtBadge_gp;
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
			holder.profile_pic.setBorderColor( myc.getResources().getColor(R.color.color_blue_p));
            holder.inc_gp_bdg = (LinearLayout) convertview.findViewById(R.id.inc_gp_bdg);
            holder.txtBadge_gp = (TextView) convertview.findViewById(R.id.txtBadge_gp);


            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.tachername.setText(Date.get(pos).child_name);
        holder.tachername.setTextColor(myc.getResources().getColor(R.color.blue_chat_list));

        holder.subject.setVisibility(View.GONE);
        if ( Date.get(pos).class_name != null && !Date.get(pos).class_name.isEmpty() ) {
            holder.txtClass.setVisibility(View.VISIBLE);
            holder.txtClass.setText(Date.get(pos).class_name);//myc.getResources().getString(R.string.str_class)+" "+
        }
        ApplicationData.setProfileImg(holder.profile_pic, ApplicationData.web_server_url + "uploads/" + Date.get(pos).child_image, myc);

        //set badge
        if (Date.get(pos).badge == 0) {
            holder.inc_gp_bdg.setVisibility(View.GONE);
        } else if (Date.get(pos).badge < 100)
        {
            holder.inc_gp_bdg.setVisibility(View.VISIBLE);
            holder.txtBadge_gp.setText(String.valueOf(Date.get(pos).badge));
        } else {
            holder.txtBadge_gp.setText("N");
        }

        return convertview;
    }
}