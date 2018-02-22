package com.adapter.teacher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StudnetAttendanceAdpater extends BaseExpandableListAdapter {

    private String reason="";
    private HashMap<String, List<Integer>> img_list = new HashMap<String, List<Integer>>();
    private List<Childbeans> title_list = new ArrayList<Childbeans>();

    private HashMap<String, List<Childbeans>> child_list = new HashMap<String, List<Childbeans>>();
    String[] tag;
    Integer[] image;
    Integer[] badge;


    Context myc;
    private int flag_report = 0, flag_admin = 0;
    int absentStatus = 0;


    public StudnetAttendanceAdpater(Activity context, HashMap<String, List<Childbeans>> child_list,
                                    List<Childbeans> title_list, String reason) {

        myc = context;
        this.title_list = title_list;
        this.child_list = child_list;
        this.reason = reason;
    }

    public void updateAdapter(int postion, List<Childbeans> tilt_list) {
        this.title_list = tilt_list;
        notifyDataSetChanged();
    }
    public void setreason(int postion, String reason, ArrayList<Childbeans> tilt_list) {
        this.title_list = tilt_list;
        this.reason=reason;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return title_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child_list.get(title_list.get(groupPosition).chat_time).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return title_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child_list.get(title_list.get(groupPosition).chat_time).get(childPosition).child_template_title;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getGroupView(int position, boolean isExpanded, View convertview, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder = null;
        if (convertview == null) {

            holder = new ViewHolder();
            convertview = li.inflate(R.layout.adres_attendance_detail_list_item, null);
            holder.txtPeriod = (TextView) convertview.findViewById(R.id.txtPeriod);
            holder.imgStatus = (TextView) convertview.findViewById(R.id.imgStatus);
            holder.txtStatus = (TextView) convertview.findViewById(R.id.txtStatus);
            holder.rel_top_reason = (RelativeLayout) convertview.findViewById(R.id.rel_top_reason);
            holder.rel_second = (RelativeLayout) convertview.findViewById(R.id.rel_second);
            holder.textView_ans = (TextView) convertview.findViewById(R.id.textView_ans);
            convertview.setTag(holder);
         
        } else
            holder = (ViewHolder) convertview.getTag();


        if (position == 0) {

            holder.rel_top_reason.setVisibility(View.VISIBLE);
            holder.rel_second.setVisibility(View.GONE);
            if(reason!=null && reason.length()>0){
                holder.textView_ans.setText(reason);
                holder.textView_ans.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.textView_ans.setVisibility(View.GONE);
            }

        } else {
            holder.rel_top_reason.setVisibility(View.GONE);
            holder.rel_second.setVisibility(View.VISIBLE);
            int pos = position;
            holder.txtPeriod.setText(title_list.get(pos).lecture_no + "   " + title_list.get(pos).chat_time);
            if (title_list.get(pos).attend1 != null && title_list.get(pos).attend1.length() > 0)
                absentStatus = Integer.valueOf(title_list.get(pos).attend1);

            if (absentStatus == 0) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.imgStatus.setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.absent_red));
                } else {
                    holder.imgStatus.setBackground(myc.getResources().getDrawable(R.drawable.absent_red));
                }
                holder.txtStatus.setText(myc.getResources().getString(R.string.str_absent));
            } else if (absentStatus == 2) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.imgStatus.setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.adres_absent_yellow));
                } else {
                    holder.imgStatus.setBackground(myc.getResources().getDrawable(R.drawable.adres_absent_yellow));
                }
                holder.txtStatus.setText(myc.getResources().getString(R.string.str_absent));
            } else {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.imgStatus.setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.adres_present_green));
                } else {
                    holder.imgStatus.setBackground(myc.getResources().getDrawable(R.drawable.adres_present_green));
                }
                holder.txtStatus.setText(myc.getResources().getString(R.string.str_present));
            }


        }

    return convertview;
}

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(groupPosition, childPosition);
        ViewHolderChild holderchild;

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (expandedListText != null && expandedListText.length() > 0) {
            if (convertView == null) {
                convertView = li.inflate(R.layout.adres_item_spinner, null);
                holderchild = new ViewHolderChild();

                holderchild.lin_spinner = (LinearLayout) convertView.findViewById(R.id.lin_spinner);
                holderchild.txt_spin = (TextView) convertView.findViewById(R.id.txt_spin);

                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holderchild.lin_spinner.setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.adres_change_list_bg_registerabsent));
                } else {
                    holderchild.lin_spinner.setBackground(myc.getResources().getDrawable(R.drawable.adres_change_list_bg_registerabsent));

                }

                holderchild.txt_spin.setTextColor(myc.getResources().getColorStateList(R.color.btn_cmd_focus));
                convertView.setTag(holderchild);

            } else {
                holderchild = (ViewHolderChild) convertView.getTag();
            }
            holderchild.txt_spin.setText(expandedListText);
            holderchild.txt_spin.setTextColor(myc.getResources().getColorStateList(R.color.btn_cmd_focus));
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


static class ViewHolder {
    TextView date;
    ImageView img;
    TextView txtBadge;
    TextView txtPeriod, txtStatus;
    TextView imgStatus;
    public ImageView dropdown;
    public RelativeLayout rel_top_reason;
    public RelativeLayout rel_second;
    public TextView txtnew;
    public TextView textView_ans;
}

static class ViewHolderChild {
    TextView txt_spin;
    ImageView image_child;
    public LinearLayout lin_spinner;
}


}