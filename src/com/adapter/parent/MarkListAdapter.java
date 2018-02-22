package com.adapter.parent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.Bean.MarkBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by etech on 22/6/16.
 */
public class MarkListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private HashMap<String, ArrayList<Childbeans>> arraylist;
    private ExpandableListView expan_report;
    private ArrayList<String> semester_name = new ArrayList<>();
    private boolean b;
    private final ArrayList<Boolean> flag = new ArrayList<Boolean>();
    ArrayList<HashMap<String, HashMap<Integer, String>>> map;
    boolean shown_flag = true;
    private PopupWindow popUp;


    public MarkListAdapter(Context context, HashMap<String, ArrayList<Childbeans>> arraylist,
                           ArrayList<String> semester_name, ExpandableListView expand_report) {
        this.context = context;
        this.arraylist = arraylist;
        this.semester_name = semester_name;
        this.expan_report = expand_report;
    }

    public void updatenotify(Context context, HashMap<String, ArrayList<Childbeans>> arraylist,
                             ArrayList<String> semester_name, ExpandableListView expand_report) {

        this.context = context;
        this.arraylist = arraylist;
        this.semester_name = semester_name;
        this.expan_report = expand_report;
    }

    /* public void update(boolean b, HashMap<String, ArrayList<Childbeans>> maphash) {
         this.maphash = maphash;
         this.b = b;

         if (flag != null) {

             for (int i = 0; i < classes.length; i++) {
                 if (b)
                     flag.set(i, true);
                 else
                     flag.set(i, false);
             }
         }
         if (this.maphash == null)
             this.maphash = new HashMap<String, ArrayList<Childbeans>>();
     }
 */
    @Override
    public int getGroupCount() {
        return semester_name.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return arraylist.get(semester_name.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return semester_name.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return (arraylist.get(semester_name.get(groupPosition)).get(childPosition));
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
            view = inflater.inflate(R.layout.item_semester_header, parent, false);
            holder = new ViewHolder();
            holder.txt_sem_nm = (TextView) view.findViewById(R.id.txt_sem_nm);
            holder.lin_note = (LinearLayout) view.findViewById(R.id.lin_note);
            holder.title = (LinearLayout) view.findViewById(R.id.title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.txt_sem_nm.setText(semester_name.get(groupPosition).toUpperCase());

        if (arraylist.get(semester_name.get(groupPosition)) != null &&
                arraylist.get(semester_name.get(groupPosition)).size() > 0) {
            holder.title.setVisibility(View.VISIBLE);
            holder.lin_note.setVisibility(View.GONE);
        }
        else {
            holder.title.setVisibility(View.GONE);
            holder.lin_note.setVisibility(View.VISIBLE);
        }

        expan_report.expandGroup(groupPosition);

        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolderChild holderchild;
        if (convertView == null) {
            holderchild = new ViewHolderChild();
            convertView = inflater.inflate(R.layout.item_mark, parent, false);
            holderchild.txt_sub_nm = (TextView) convertView.findViewById(R.id.txt_sub_nm);
            holderchild.grid_mark = (GridView) convertView.findViewById(R.id.grid_mark);
            convertView.setTag(holderchild);
        } else {
            holderchild = (ViewHolderChild) convertView.getTag();
        }

        Childbeans childname = ((Childbeans) getChild(groupPosition, childPosition));
        if (childname != null && childname.subject_name != null && childname.subject_name.length() > 0) {
            holderchild.txt_sub_nm.setText(childname.subject_name);
        }

        GridViewadpater adpater = new GridViewadpater(context, childname, childPosition);
        holderchild.grid_mark.setAdapter(adpater);

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
        //public TextView txt_note;
        public LinearLayout title;
        public LinearLayout lin_note;
    }

    class ViewHolderChild {
        public TextView txt_sub_nm;
        public GridView grid_mark;
    }


    //popup window adapter
    class GridViewadpater extends BaseAdapter {
        private final Childbeans data;
        Context context;
        ArrayList<Boolean> check_flag;

        public GridViewadpater(Context context, Childbeans data, int childPosition) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.markarray.size();
        }

        @Override
        public Object getItem(int position) {
            return data.markarray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewHolderStudent holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_grid_mark, parent, false);
                holder = new ViewHolderStudent();
                holder.txt_number = (TextView) convertView.findViewById(R.id.txt_number);
                //  holder.lin_class = (LinearLayout) view.findViewById(R.id.lin_class);
                holder.lyt_info = (LinearLayout) convertView.findViewById(R.id.lyt_info);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolderStudent) convertView.getTag();
            }

            final MarkBean bean = data.markarray.get(position);

            holder.txt_number.setText(bean.mark);

            holder.lyt_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.comment != null && bean.comment.length() > 0) {
                        try {
                            ApplicationData.showMessage(context, context.getString(R.string.comment), bean.comment, context.getString(R.string.str_ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return convertView;
        }
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
