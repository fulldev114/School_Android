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

public class GroupMessageHistoryAdapter extends BaseAdapter {
    ArrayList<Childbeans> arrayList;

    ViewHolder holder;

    Context myc;

    public GroupMessageHistoryAdapter(Context c, ArrayList<Childbeans> messageList) {
        myc = c;
        this.arrayList = messageList;
    }

    public void updateReceiptsList( ArrayList<Childbeans> messageList) {
        this.arrayList.clear();
        this.arrayList.addAll(messageList);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return arrayList.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txtDate, txtMessage, txtClass;
        CircularImageView profile_pic;
    }

    @Override
    public View getView(final int pos,  View convertview, ViewGroup arg2) {

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null) {
            convertview = li.inflate(R.layout.adres_groupmessage_history_list_item, null);

            holder = new ViewHolder();
            holder.txtClass = (TextView) convertview.findViewById(R.id.txtClass);
            holder.txtDate = (TextView) convertview.findViewById(R.id.txtDate);
            holder.txtMessage = (TextView) convertview.findViewById(R.id.txtMessage);

            holder.profile_pic = (CircularImageView) convertview.findViewById(R.id.imgProfile);
            holder.profile_pic.setBorderWidth(5);
//			holder.profile_pic.setBorderColor( getResources().getColor(R.color.white));

            convertview.setTag(holder);
        } else {
            holder = (ViewHolder) convertview.getTag();
        }

        String dt = ApplicationData.convertToNorweiDateWithTime(arrayList.get(pos).created_at, myc);
        holder.txtDate.setText( dt );
        holder.txtClass.setText(myc.getString(R.string.str_class_to)+arrayList.get(pos).class_name);
        holder.txtMessage.setText(arrayList.get(pos).message_desc);

        return convertview;
    }
}