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

public class AttendanceListAdapter extends BaseAdapter {
    ArrayList<Childbeans> arrayAllPeriods;
    ArrayList<StudentAttendanceInfo> arrayStudents;

    ViewHolder holder;

    Context myc;

    /*  Integer[] drawableAttend = { R.drawable.attend1,
              R.drawable.attend2,
              R.drawable.attend3,
              R.drawable.attend4,
              R.drawable.attend5,
              R.drawable.attend6,
              R.drawable.attend7,
              R.drawable.attend8};

      Integer[] drawableAbsent = { R.drawable.absent1,
              R.drawable.absent2,
              R.drawable.absent3,
              R.drawable.absent4,
              R.drawable.absent5,
              R.drawable.absent6,
              R.drawable.absent7,
              R.drawable.absent8};

      Integer[] drawableNotice = { R.drawable.notice1,
              R.drawable.notice2,
              R.drawable.notice3,
              R.drawable.notice4,
              R.drawable.notice5,
              R.drawable.notice6,
              R.drawable.notice7,
              R.drawable.notice8};*/
    public AttendanceListAdapter(Context c, ArrayList<StudentAttendanceInfo> arrayAllStudents) {
        myc = c;
        this.arrayStudents = arrayAllStudents;
    }

    public void updateReceiptsList(ArrayList<StudentAttendanceInfo> messageList) {
        this.arrayStudents.clear();
        this.arrayStudents.addAll(messageList);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return arrayStudents.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView name;
        CircularImageView profile_pic;
        TextView[] img = new TextView[8];
        public TextView txt_reason;
        public LinearLayout lin_periods;
    }

    @Override
    public View getView(final int pos, View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.adres_attendance_list_item, null);

            holder = new ViewHolder();
            holder.profile_pic = (CircularImageView) convertview.findViewById(R.id.imgProfile);
            holder.profile_pic.setBorderWidth(5);

            holder.name = (TextView) convertview.findViewById(R.id.txtName);
            holder.txt_reason = (TextView) convertview.findViewById(R.id.txt_reason);

            /*for(int i = 0; i < 8; i++) {
                holder.img[i] = new CircularImageView(myc);
            }*/
            holder.lin_periods = (LinearLayout) convertview.findViewById(R.id.lin_periods);
            holder.img[0] = (TextView) convertview.findViewById(R.id.img1);
            holder.img[1] = (TextView) convertview.findViewById(R.id.img2);
            holder.img[2] = (TextView) convertview.findViewById(R.id.img3);
            holder.img[3] = (TextView) convertview.findViewById(R.id.img4);
            holder.img[4] = (TextView) convertview.findViewById(R.id.img5);
            holder.img[5] = (TextView) convertview.findViewById(R.id.img6);
            holder.img[6] = (TextView) convertview.findViewById(R.id.img7);
            holder.img[7] = (TextView) convertview.findViewById(R.id.img8);

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        holder.name.setText(arrayStudents.get(pos).name);
        ApplicationData.setProfileImg(holder.profile_pic, ApplicationData.web_server_url + "uploads/" + arrayStudents.get(pos).image, myc);

        if (arrayStudents.get(pos).periodsInfo.size() > 0) {
            for (int i = 0; i < arrayStudents.get(pos).periodsInfo.size(); i++) {
                holder.lin_periods.setVisibility(View.VISIBLE);
                holder.img[i].setVisibility(View.VISIBLE);
                holder.img[i].setText(arrayStudents.get(pos).periodsInfo.get(i).lecture_no);
            }
            for (int i = arrayStudents.get(pos).periodsInfo.size(); i < 8; i++) {
                holder.img[i].setVisibility(View.GONE);
            }
        } else {
            holder.lin_periods.setVisibility(View.GONE);
        }

        arrayAllPeriods = arrayStudents.get(pos).periodsInfo;

        if (arrayAllPeriods != null && arrayAllPeriods.size() > 0) {
            int period_no = 1;
            holder.txt_reason.setVisibility(View.GONE);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {

                for (period_no = 0; period_no < arrayAllPeriods.size(); period_no++) {
                    String attend = arrayAllPeriods.get(period_no - 1).attend;
                    if (attend.equals("2"))
                        holder.img[period_no].setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.adres_circle_yellow_full));
                    else if (attend.equals("0"))
                        holder.img[period_no].setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.adres_circle_red_full));
                    else
                        holder.img[period_no].setBackgroundDrawable(myc.getResources().getDrawable(R.drawable.circle_green_full));
                }
            } else {
                for (period_no = 0; period_no < arrayAllPeriods.size(); period_no++) {
                    String attend = arrayAllPeriods.get(period_no).attend;
                    if (attend.equals("2"))
                        holder.img[period_no].setBackground(myc.getResources().getDrawable(R.drawable.adres_circle_yellow_full));
                    else if (attend.equals("0"))
                        holder.img[period_no].setBackground(myc.getResources().getDrawable(R.drawable.adres_circle_red_full));
                    else
                        holder.img[period_no].setBackground(myc.getResources().getDrawable(R.drawable.circle_green_full));

                    if (arrayAllPeriods.get(period_no).reason != null && arrayAllPeriods.get(period_no).reason.length() > 0) {
                        holder.txt_reason.setText("(" + arrayAllPeriods.get(period_no).reason + ")");
                        holder.txt_reason.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            holder.txt_reason.setVisibility(View.VISIBLE);
            holder.txt_reason.setText(myc.getString(R.string.no_lecture));
        }
        return convertview;
    }
}