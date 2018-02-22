package com.common.Bean;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.adapter.teacher.Childbeans;
import com.cloudstream.cslink.teacher.MainActivity;
import com.cloudstream.cslink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by etech on 9/6/16.
 */
public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData(Context context) {
        String[] Drawer_child_item1=context.getResources().getStringArray(R.array.drawer_item_report);
        String[] Drawer_child_item2=context.getResources().getStringArray(R.array.drawer_item_Admin);
        String[] Drawerlist_item = context.getResources().getStringArray(R.array.drawer_item_teacher);
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        for(int i=0;i<Drawerlist_item.length;i++)
        {
            if(i==2)
            {
                List<String> Report = new ArrayList<String>();
                for(int j=0;j<Drawer_child_item1.length;j++) {
                    Report.add(Drawer_child_item1[j]);
                }
                expandableListDetail.put(Drawerlist_item[i], Report);

            }
            else if(i==3)
            {
                List<String> Admin = new ArrayList<String>();
                for(int j=0;j<Drawer_child_item2.length;j++) {
                    Admin.add(Drawer_child_item2[j]);
                }
                expandableListDetail.put(Drawerlist_item[i], Admin);
            }
            else
            {
                List<String> Admin = new ArrayList<String>();
                expandableListDetail.put(Drawerlist_item[i],Admin);
            }
        }

        return expandableListDetail;
    }

    public static HashMap<String, List<Integer>> getDataImage(Context context) {
        String[] Drawerlist_item = context.getResources().getStringArray(R.array.drawer_item_teacher);
        HashMap<String, List<Integer>> expandableListDetail = new HashMap<String, List<Integer>>();

        for(int i=0;i<Drawerlist_item.length;i++)
        {
            if(i==2)
            {
                List<Integer> Report = new ArrayList<Integer>();
                //for(int j=0;j<Drawer_child_item1.length;j++)
                {
                    Report.add(R.drawable.adres_img_mark);
                    Report.add(R.drawable.adres_img_absent);
                    Report.add(R.drawable.img_card);
                }
                expandableListDetail.put(Drawerlist_item[i], Report);
            }
            else if(i==3)
            {
                List<Integer> Admin = new ArrayList<Integer>();
               // for(int j=0;j<Drawer_item_img.length;j++)
              {
                    Admin.add(R.drawable.adres_img_mark);
                    Admin.add(R.drawable.img_card);
                }
                expandableListDetail.put(Drawerlist_item[i], Admin);
            }
            else
            {
                List<Integer> Admin = new ArrayList<Integer>();
                expandableListDetail.put(Drawerlist_item[i], Admin);
            }
        }

        return expandableListDetail;
    }

    public static List<String> getDatalist(Context context) {
        String[] Drawerlist_item = context.getResources().getStringArray(R.array.drawer_item_teacher);
         List<String> expandableListDetail = new ArrayList<String>();
        for(int i=0;i<Drawerlist_item.length;i++)
        {
            expandableListDetail.add(Drawerlist_item[i]);
        }
        return expandableListDetail;
    }

    public static HashMap<String, List<Childbeans>> getData_reason(Context context, ArrayList<Childbeans> arrayListTemplete, ArrayList<Childbeans> currArrayStudents) {
        HashMap<String, List<Childbeans>> expandableListDetail = new HashMap<String, List<Childbeans>>();

        for(int i=0;i<=currArrayStudents.size();i++)
        {
            if(i==0) {
                List<Childbeans> reason = new ArrayList<Childbeans>();
                for(int j=0;j<arrayListTemplete.size();j++) {
                    reason.add(arrayListTemplete.get(j));
                }
                expandableListDetail.put(context.getResources().getString(R.string.str_select_reason), reason);
            }
            else
            {
                List<Childbeans> Admin = new ArrayList<Childbeans>();
                expandableListDetail.put(currArrayStudents.get(i - 1).chat_time, Admin);
            }
        }

        return expandableListDetail;
    }

    public static List<Childbeans> getparentlist(Context context, ArrayList<Childbeans> periodsInfo) {
        List<Childbeans> expandableListDetail = new ArrayList<Childbeans>();
        for(int i=0;i<=periodsInfo.size();i++)
        {
            if(i==0)
            {
                Childbeans bean = new Childbeans();
                bean.lecture_no="0";
                bean.chat_time=context.getResources().getString(R.string.str_select_reason);
                expandableListDetail.add(bean);
            }
            else
                expandableListDetail.add(periodsInfo.get(i-1));
        }
        return expandableListDetail;
    }
}
