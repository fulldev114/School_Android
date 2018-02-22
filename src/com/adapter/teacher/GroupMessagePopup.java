package com.adapter.teacher;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;

/**
 * Created by etech on 22/6/16.
 */
public class GroupMessagePopup extends PopupWindow {


    private final Activity context;

    public GroupMessagePopup(Activity context) {

        this.context=context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        ViewHolder holder;
       /* PopupWindow pw = new PopupWindow(
                inflater.inflate(R.layout.adres_item_groupmessage_spinner, null, false),
                100,
                100,
                true);*/

        if (view == null)
        {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.adres_item_groupmessage_spinner, null);
            holder.txt_name = (TextView) view.findViewById(R.id.txt_name);
            view.setTag(holder);
        }

        else
        {
            holder= (ViewHolder) view.getTag();
        }
        setContentView(view);

    }

    public PopupWindow popupWindowsort() {
        PopupWindow popupWindow = new PopupWindow(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        ViewHolder holder;
       /* PopupWindow pw = new PopupWindow(
                inflater.inflate(R.layout.adres_item_groupmessage_spinner, null, false),
                100,
                100,
                true);*/

        if (view == null)
        {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.adres_item_groupmessage_spinner, null);
            holder.txt_name = (TextView) view.findViewById(R.id.txt_name);
            view.setTag(holder);
        }

        else
        {
            holder= (ViewHolder) view.getTag();
        }
        popupWindow.setContentView(view);
        popupWindow.showAsDropDown(view);

        return popupWindow;
    }

    class ViewHolder {

        public TextView txt_name;
    }

}