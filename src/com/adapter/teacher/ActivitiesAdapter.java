package com.adapter.teacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.adapter.ActivitiesBeans;
import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.view.CircularImageView;
import com.widget.textstyle.MyTextView_Signika_Bold;

import java.util.ArrayList;

public class ActivitiesAdapter extends BaseAdapter {
    ArrayList<ActivitiesBeans> mActivitiesList;

    ViewHolder holder;

    Context myc;

    public ActivitiesAdapter(Context c, ArrayList<ActivitiesBeans> activityList) {
        myc = c;
        this.mActivitiesList = activityList;
    }

    public void updateReceiptsList(ArrayList<ActivitiesBeans> activityList) {
        this.mActivitiesList.clear();
        this.mActivitiesList.addAll(activityList);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        //return mActivitiesList.size();
        return 10;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView mTxtAcitivityName, mTxtPlace;
        MyTextView_Signika_Bold mTxtDate, mTxtMonth;
    }

    @Override
    public View getView(final int pos,  View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.activities_list_item, null);

            holder = new ViewHolder();
            holder.mTxtAcitivityName = (TextView) convertview.findViewById(R.id.textView_activityname);
            holder.mTxtPlace = (TextView) convertview.findViewById(R.id.textView_place);
            holder.mTxtDate = (MyTextView_Signika_Bold) convertview.findViewById(R.id.date_textview);
            holder.mTxtMonth = (MyTextView_Signika_Bold) convertview.findViewById(R.id.month_textview);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

//        holder.tachername.setText(mActivitiesList.get(pos).child_name );
//        holder.subject.setText(myc.getResources().getString(R.string.str_parent) + (Date.get(pos).parent_name.equals("null") ? "" : Date.get(pos).parent_name) );
//        if ( Date.get(pos).class_name != null && !Date.get(pos).class_name.isEmpty() ) {
//            holder.txtClass.setVisibility(View.VISIBLE);
//            holder.txtClass.setText(myc.getResources().getString(R.string.str_class)+ Date.get(pos).class_name);
//        }
//        ApplicationData.setProfileImg(holder.profile_pic, ApplicationData.web_server_url + "uploads/" + Date.get(pos).child_image, myc);
//
//        holder.txtBadge.setVisibility(View.VISIBLE);
//        if (Date.get(pos).badge == 0) {
//            holder.txtBadge.setVisibility(View.GONE);
//        } else if (Date.get(pos).badge < 10){
//            holder.txtBadge.setText(Date.get(pos).badge + "");
//        } else {
//            holder.txtBadge.setText("N");
//        }

        return convertview;
    }
}