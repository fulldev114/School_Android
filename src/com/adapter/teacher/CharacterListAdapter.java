package com.adapter.teacher;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by etech on 22/6/16.
 */
public class CharacterListAdapter extends BaseExpandableListAdapter {
    private final ArrayList<Childbeans> characterarray;
    private ArrayList<Childbeans> arraylist;
    private Context context;
    private ExpandableListView expan_report;
    private ArrayList<String> semester_name= new ArrayList<>();
    private boolean b;
    private final ArrayList<Boolean> flag = new ArrayList<Boolean>();
    ArrayList<HashMap<String, HashMap<Integer, String>>> map;
    boolean shown_flag = true;
    private PopupWindow popUp;


    public CharacterListAdapter(Context context, ArrayList<Childbeans> arraylist,
                                ArrayList<String> semester_name, ExpandableListView expand_report,
                                ArrayList<Childbeans> characterarray) {
        this.context = context;
        this.arraylist = arraylist;
        this.semester_name=semester_name;
        this.expan_report=expand_report;
        this.characterarray=characterarray;
    }

    public void updatenotify(Context context, ArrayList<Childbeans> arraylist,
                             ArrayList<String> semester_name, ExpandableListView expand_report) {

        this.context = context;
        this.arraylist = arraylist;
        this.semester_name=semester_name;
        this.expan_report=expand_report;
    }

    @Override
    public int getGroupCount() {
        return semester_name.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
            return characterarray.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return semester_name.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return characterarray.get(childPosition);
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

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.adres_item_character_semester_header, parent, false);
            holder = new ViewHolder();
            holder.txt_sem_nm = (TextView) view.findViewById(R.id.txt_sem_nm);
            holder.img_comment=(ImageView)view.findViewById(R.id.img_comment);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String comment = "";

        holder.txt_sem_nm.setText(semester_name.get(groupPosition).toUpperCase());
        if(!arraylist.get(groupPosition).comment.equalsIgnoreCase("") && arraylist.get(groupPosition).comment!=null)
        {
            holder.img_comment.setVisibility(View.VISIBLE);
            comment=arraylist.get(groupPosition).comment;
        }
        else
        {
            holder.img_comment.setVisibility(View.GONE);
        }

        final String finalComment = comment;
        holder.img_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalComment != null && finalComment.length() > 0) {
                    try {
                        ApplicationData.showMessage(context, context.getString(R.string.comment), finalComment, context.getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        expan_report.expandGroup(groupPosition);

        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolderChild holderchild;
        if (convertView == null) {
            holderchild = new ViewHolderChild();
            convertView = inflater.inflate(R.layout.adres_item_character_mark, parent, false);
            holderchild.txt_first = (TextView) convertView.findViewById(R.id.txt_first);
            holderchild.view_line=(View)convertView.findViewById(R.id.view_line);
            holderchild.lyt_info1 = (LinearLayout) convertView.findViewById(R.id.lyt_info1);

            convertView.setTag(holderchild);
        } else {
            holderchild = (ViewHolderChild) convertView.getTag();
        }

        final Childbeans childname = characterarray.get(childPosition);
        if (childname != null && childname.character_name != null && childname.character_name.length() > 0) {
            holderchild.txt_first.setText(childname.character_name);
        }


        //show selected character
       // for(int i=0;i<characterarray.size();i++) {
            if (arraylist.get(groupPosition).character_name.equalsIgnoreCase(childname.character_name)) {
                holderchild.txt_first.setTextColor(context.getResources().getColor(R.color.light_green));
                holderchild.lyt_info1.setVisibility(View.VISIBLE);
            } else {
                holderchild.txt_first.setTextColor(context.getResources().getColor(R.color.light_neavy_blue));
                holderchild.lyt_info1.setVisibility(View.GONE);
            }


      //  }


        if(childPosition==characterarray.size()-1)
        {
            holderchild.view_line.setVisibility(View.GONE);
        }
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    class ViewHolder {
        public TextView txt_sem_nm;
        public CheckBox chk_item;
        public LinearLayout lin_class;
        public ImageView imageView1;
        public ImageView img_comment;
    }

    class ViewHolderChild {
        public TextView txt_sub_nm;
        public LinearLayout lyt_info;
        public TextView txt_first,txt_second,txt_third;
        public LinearLayout lyt_info1,lyt_info2,lyt_info3;
        public View view_line;
    }


    class ViewHolderStudent {

        public TextView txt_number;
        public LinearLayout lyt_info;
    }


   /* public String getclassid(Activity contxt, ArrayList<Childbeans> classes) {
        String position = "";
        if (flag != null && flag.size() > 0) {
            for (int i = 0; i < flag.size(); i++) {
                if (flag.get(i)) {
                    if (position.equals(""))
                        position = classes.get(i).class_id;
                    else
                        position = position + "," + classes.get(i).class_id;
                }
            }
        }
        return position;
    }*/
}
