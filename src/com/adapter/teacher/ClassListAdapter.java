package com.adapter.teacher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by etech on 22/6/16.
 */
public class ClassListAdapter extends BaseExpandableListAdapter {
    private final Activity context;
    private final String[] classes;
    private final int height;
    private final int relsendheight;
    private final CheckBox chkAll;
    private boolean b;
    private final ArrayList<Boolean> flag = new ArrayList<Boolean>();
    private final ArrayList<Childbeans> allStudentList;
    private final ExpandableListView lstclass;
    ArrayList<HashMap<String, HashMap<Integer, String>>> map;
    boolean shown_flag = true;
    private PopupWindow popUp;
    ArrayList<Childbeans> inner_list;
    ArrayList<Childbeans> alist = new ArrayList<Childbeans>();
    HashMap<String, ArrayList<Childbeans>> maphash = new HashMap<String, ArrayList<Childbeans>>();
    private int parentPosition = 0;

    public ClassListAdapter(Activity context, String[] classes, boolean b, ArrayList<Childbeans> allStudentList,
                            ExpandableListView lstclass, int height, int relsendheight, CheckBox chkAll) {
        this.context = context;
        this.classes = classes;
        this.b = b;
        this.allStudentList = allStudentList;
        this.lstclass = lstclass;
        this.height = height;
        this.relsendheight = relsendheight;
        this.chkAll = chkAll;

        for (int i = 0; i < classes.length; i++) {
            flag.add(false);
        }
        for (int i = 0; i < classes.length; i++) {
            if (b)
                flag.set(i, true);
            else
                flag.set(i, false);
        }
    }

    public void update(boolean b, HashMap<String, ArrayList<Childbeans>> maphash) {
        this.maphash = maphash;
        this.b = b;

        if (flag != null) {

            for (int i = 0; i < classes.length; i++) {
                if (b)  // if select all is selected then all class must be selected
                    flag.set(i, true);
                else {
                    flag.set(i, false);
                }
            }
        }
        if (this.maphash == null)
            this.maphash = new HashMap<String, ArrayList<Childbeans>>();
    }

    @Override
    public int getGroupCount() {
        return classes.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (maphash != null && maphash.get(classes[groupPosition]) != null) {
            if (maphash.get(classes[groupPosition]).size() > 0)
                return maphash.get(classes[groupPosition]).size();
            else
                return 0;
        } else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return classes[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return (maphash.get(classes[groupPosition]).get(childPosition));
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
    public View getGroupView(final int groupPosition, boolean isExpanded, final View convertView, final ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.adres_item_student_list, parent, false);
            holder = new ViewHolder();
            holder.txt_name = (TextView) view.findViewById(R.id.txt_name);
            holder.lin_class = (LinearLayout) view.findViewById(R.id.lin_class);
            holder.chk_item = (CheckBox) view.findViewById(R.id.chk_item);
            holder.imageView1 = (ImageView) view.findViewById(R.id.imageView1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (classes[groupPosition] != null && classes[groupPosition].length() > 0) {
            holder.txt_name.setText(classes[groupPosition]);
        }

        //Select all is checked then all checkbox is checked
        holder.chk_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flag.set(groupPosition, true);
                    if (lstclass.isGroupExpanded(groupPosition))
                        lstclass.collapseGroup(groupPosition);
                    //remove selected data from list if it class is selected
                    if (maphash != null && maphash.get(classes[groupPosition]) != null && maphash.get(classes[groupPosition]).size() > 0)
                        maphash.remove((classes[groupPosition]));//maphash.get

                    notifyDataSetChanged();
                } else {
                    flag.set(groupPosition, false);
                    chkAll.setChecked(false);
                    b = false;
                    notifyDataSetChanged();
                }

            }
        });

        holder.chk_item.setChecked(flag.get(groupPosition));

        final View finalView = view;
      /*  holder.imageView1.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (!b && !flag.get(groupPosition)) {


                    popUp = popupWindowsort(groupPosition, v);
                    //popUp.showAsDropDown(v, 0, 0);
                    int[] loc_int = new int[2];

                    Rect location = new Rect();
                    location.left = loc_int[0];
                    location.top = loc_int[1];
                    location.right = location.left + holder.imageView1.getWidth();
                    location.bottom = location.top + holder.imageView1.getHeight();



                    int bottomval=height-holder.lin_class.getHeight();
                    int newval=bottomval-lstclass.getHeight()-location.bottom;
                    int finalval=lstclass.getMeasuredHeight()-location.bottom;


                    if (groupPosition != 0) {
                        popUp.showAtLocation(v, Gravity.AXIS_Y_SHIFT,0, location.bottom);
                    } else
                        popUp.showAsDropDown(v, 0, 0);


                } else if (b) {
                    maphash = new HashMap<String, ArrayList<Childbeans>>();
                    inner_list = new ArrayList<Childbeans>();
                    try {
                        ApplicationData.showMessage(context,context.getResources().getString(R.string.gp),
                                context.getResources().getString(R.string.alert_gp_classes),context.getResources().getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(flag.get(groupPosition))
                {
                    try {
                        ApplicationData.showMessage(context,context.getResources().getString(R.string.gp),
                                context.getResources().getString(R.string.alert_gp_single_class),context.getResources().getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/

        holder.imageView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    parentPosition = groupPosition;

                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (!b && !flag.get(groupPosition)) {
                        popUp = popupWindowsort(groupPosition, v);
                        //popUp.showAsDropDown(v, 0, 0);
                        int[] loc_int = new int[2];
                        v.getLocationOnScreen(loc_int);

                        Rect location = new Rect();
                        location.left = loc_int[0];
                        location.top = loc_int[1];
                        location.right = location.left + v.getWidth();
                        location.bottom = location.top + v.getHeight();
                        int yPos = location.top - v.getMeasuredHeight();
                        int xPos = location.left - v.getMeasuredWidth();

                        int h3 = height - relsendheight;
                        int bottomval = height - h3;
                        //   int listheight = height - h3 - (int) lstclass.getY();
                        //   int selectedval = (listheight - (int) event.getY() - 10) + (int) lstclass.getY();

                        int finaheight = height / 3;
                        //set height of popup for location.
                        if (alist.size() > 0 && alist.size() < 4)
                            finaheight = alist.size() < height / 3 ? alist.size() : height / 3;

                        try {
                            if ((bottomval - yPos) < finaheight) { //event.getY()>listheight-v.getHeight()
                                if ((context.getResources().getConfiguration().screenLayout &
                                        Configuration.SCREENLAYOUT_SIZE_MASK) ==
                                        Configuration.SCREENLAYOUT_SIZE_SMALL) {

                                    popUp.showAtLocation(v, Gravity.NO_GRAVITY, 20, yPos - (finaheight));

                                } else if ((context.getResources().getConfiguration().screenLayout &
                                        Configuration.SCREENLAYOUT_SIZE_MASK) ==
                                        Configuration.SCREENLAYOUT_SIZE_NORMAL) {

                                    popUp.showAtLocation(v, Gravity.NO_GRAVITY, 20, yPos - (finaheight) + 20);

                                } else if ((context.getResources().getConfiguration().screenLayout &
                                        Configuration.SCREENLAYOUT_SIZE_MASK) ==
                                        Configuration.SCREENLAYOUT_SIZE_LARGE) {
                                    // on a large screen device ...
                                    popUp.showAtLocation(v, Gravity.NO_GRAVITY, 20, yPos - (finaheight) + 13);

                                } else if ((context.getResources().getConfiguration().screenLayout &
                                        Configuration.SCREENLAYOUT_SIZE_MASK) ==
                                        Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                                    // on a large screen device ...
                                    popUp.showAtLocation(v, Gravity.NO_GRAVITY, 20, yPos - (finaheight) + 25);

                                }
                            } else {
                                popUp.showAsDropDown(v, 0, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else if (b) {
                        maphash = new HashMap<String, ArrayList<Childbeans>>();
                        inner_list = new ArrayList<Childbeans>();
                        try {
                            ApplicationData.showMessage(context, "",
                                    context.getResources().getString(R.string.alert_gp_classes), context.getResources().getString(R.string.str_ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (flag.get(groupPosition)) {
                        try {
                            ApplicationData.showMessage(context, "",
                                    context.getResources().getString(R.string.alert_gp_single_class), context.getResources().getString(R.string.str_ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });

        holder.lin_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maphash != null && maphash.size() > 0 && !flag.get(groupPosition)) {
                    lstclass.expandGroup(groupPosition);
                }
            }
        });


        return view;
    }


    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolderChild holderchild;
        if (convertView == null) {
            holderchild = new ViewHolderChild();
            convertView = inflater.inflate(R.layout.adres_item_childlist_delete, parent, false);
            holderchild.txt_selected_name = (TextView) convertView.findViewById(R.id.txt_selected_name);
            holderchild.img_del = (ImageView) convertView.findViewById(R.id.img_del);
            convertView.setTag(holderchild);
        } else {
            holderchild = (ViewHolderChild) convertView.getTag();
        }

        Childbeans childname = ((Childbeans) getChild(groupPosition, childPosition));
        if (childname != null && childname.child_name != null && childname.child_name.length() > 0) {
            holderchild.txt_selected_name.setText(childname.child_name);
        }

        holderchild.img_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                maphash.get(classes[groupPosition]).remove(childPosition);
                if (maphash.get(classes[groupPosition]).size() == 0) {
                    maphash.remove(classes[groupPosition]);
                }
                notifyDataSetChanged();
                if (maphash == null) {
                    maphash = new HashMap<String, ArrayList<Childbeans>>();
                }
                //  update(b, maphash);

            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder {
        public TextView txt_name;
        public CheckBox chk_item;
        public LinearLayout lin_class;
        public ImageView imageView1;
    }

    class ViewHolderChild {
        public TextView txt_selected_name;
        public LinearLayout lin_class;
        public CheckBox chk_item;
        public ImageView img_del;
    }


    private PopupWindow popupWindowsort(final int position, View v) {

        String classnm = classes[position];

        if (alist != null && alist.size() > 0) {
            alist.clear();
            alist = new ArrayList<>();
        }

        inner_list = new ArrayList<Childbeans>();

        // name of student from all list of particular class and add in alist array
        for (int loop = 0; loop < allStudentList.size(); loop++) {
            if (allStudentList.get(loop).class_name.equals(classnm)) {
                Childbeans bean = new Childbeans();
                bean.child_name = allStudentList.get(loop).child_name;
                bean.user_id = allStudentList.get(loop).user_id;
                bean.class_id = allStudentList.get(loop).class_id;
                alist.add(bean);
            }
        }

        // if any student name is already selected for that particular class then add in
        //inner_list
        if (maphash != null && maphash.size() > 0) {
            Iterator<String> ite = maphash.keySet().iterator();
            //if hashmap size is not zero then check value is already added or not
            int counter = 0;
            while (ite.hasNext()) {
                String key = ite.next();
                if (key.equalsIgnoreCase(classes[position])) {

                    for (int add = 0; add < maphash.get(key).size(); add++) {
                        inner_list.add(maphash.get(key).get(add));
                    }
                    break;
                }
            }
        }

        PopupWindow popupWindow = new PopupWindow(context);

        ListView lst_student;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;

        view = inflater.inflate(R.layout.adres_item_listview, null);
        lst_student = (ListView) view.findViewById(R.id.lst_student);

        CustomArrayAdapter adapter = new CustomArrayAdapter(context, alist);
        lst_student.setAdapter(adapter);

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // popupWindow.setBackgroundDrawable(null);

        // popupWindow.setOutsideTouchable(true);
        // set the list view as pop up window content


        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        if (alist.size() > height / 3) {
            param.height = height / 3;
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            if (alist.size() > 0 && alist.size() < 4)
                param.height = alist.size() * 100;
            else
                param.height = height / 3;

            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        }
        lst_student.setLayoutParams(param);


        popupWindow.setContentView(view);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //  dismisswindow(position);
            }
        });
        return popupWindow;

    }

    private void dismisswindow(int groupPosition) {
        if (maphash != null && maphash.size() > 0) {
            if (inner_list.size() > 0) {
                Iterator<String> ite = maphash.keySet().iterator();
                int no_added = 0;
                //if hashmap size is not zero then check value is already added or not
                while (ite.hasNext()) {
                    if (((String) (ite.next())).equalsIgnoreCase(classes[groupPosition])) {
                        maphash.remove(classes[groupPosition]);
                        maphash.put(classes[groupPosition], inner_list);
                        no_added = 1;
                        break;
                    } else {
                        no_added = 0;
                    }

                }
                //value is not added then add value in Hashmap
                if (no_added == 0) {
                    maphash.put(classes[groupPosition], inner_list);
                }
            }
        } else {
            if (maphash != null)
                maphash.put(classes[groupPosition], inner_list);
        }

        if (maphash != null && maphash.size() > 0) {
            if (maphash.get(classes[groupPosition]) != null && maphash.get(classes[groupPosition]).size() > 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    lstclass.expandGroup(groupPosition, true);
                }
                //  else just expand the Group without animation
                else {
                    lstclass.expandGroup(groupPosition);
                }
        }
        notifyDataSetChanged();
    }


    //popup window adapter
    class CustomArrayAdapter extends BaseAdapter {
        private final ArrayList<Childbeans> list;
        Activity context;
        ArrayList<Boolean> check_flag;


        public CustomArrayAdapter(Activity context, ArrayList<Childbeans> list) {
            this.context = context;
            this.list = list;
            check_flag = new ArrayList<Boolean>();
            for (int i = 0; i < list.size(); i++) {
                check_flag.add(false);
            }

            //already add value to set checked
            if (inner_list.size() > 0) {
                for (int j = 0; j < inner_list.size(); j++) {
                    for (int i = 0; i < list.size(); i++) {
                        if (inner_list.get(j).user_id.equals(list.get(i).user_id)) {
                            check_flag.set(i, true);
                            break;
                        }
                    }
                }
            }

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
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
                convertView = inflater.inflate(R.layout.adres_item_groupmessage_spinner, parent, false);
                holder = new ViewHolderStudent();
                holder.txt_stname = (TextView) convertView.findViewById(R.id.txt_stname);
                //  holder.lin_class = (LinearLayout) view.findViewById(R.id.lin_class);
                holder.chk_stitem = (CheckBox) convertView.findViewById(R.id.text1);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolderStudent) convertView.getTag();
            }
            holder.txt_stname.setText(list.get(position).child_name);

            holder.chk_stitem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        check_flag.set(position, true);
                        addinlist(position);
                    } else {
                        check_flag.set(position, false);

                        for (int del = 0; del < inner_list.size(); del++) {
                            if (inner_list.get(del).user_id.equals(list.get(position).user_id)) {
                                inner_list.remove(del);
                            }
                        }

                        removeitem(position);
                    }
                }
            });

            holder.chk_stitem.setChecked(check_flag.get(position));

            return convertView;
        }

        private void addinlist(int position) {
            int no_added = 0;
            //check Hashmap size is zero or not
            if (inner_list.size() > 0) {

                //if hashmap size is not zero then check value is already added or not
                for (int del = 0; del < inner_list.size(); del++) {
                    if (inner_list.get(del).user_id.equals(list.get(position).user_id)) {
                        no_added = 1;
                        break;
                    } else
                        no_added = 0;
                }

                //value is not added then add value in Hashmap
                if (no_added == 0) {
                    Childbeans bean = new Childbeans();
                    bean.child_name = list.get(position).child_name;
                    bean.user_id = list.get(position).user_id;
                    bean.class_id = list.get(position).class_id;
                    inner_list.add(list.get(position));
                }

            }
            //put value in Hashmap
            else
                inner_list.add(list.get(position));

            removeitem(position);
        }
    }

    private void removeitem(int position) {
        if (maphash != null && maphash.size() > 0) {
            if (inner_list.size() > 0) {
                Iterator<String> ite = maphash.keySet().iterator();
                int no_added = 0;
                //if hashmap size is not zero then check value is already added or not
                while (ite.hasNext()) {
                    if (((String) (ite.next())).equalsIgnoreCase(classes[parentPosition])) {
                        maphash.remove(classes[parentPosition]);
                        maphash.put(classes[parentPosition], inner_list);
                        no_added = 1;
                        break;
                    } else {
                        no_added = 0;
                    }

                }
                //value is not added then add value in Hashmap
                if (no_added == 0) {
                    maphash.put(classes[parentPosition], inner_list);
                }
            }
        } else {
            if (maphash != null)
                maphash.put(classes[parentPosition], inner_list);
        }

        if (maphash != null && maphash.size() > 0) {
            if (maphash.get(classes[parentPosition]) != null && maphash.get(classes[parentPosition]).size() > 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    lstclass.expandGroup(parentPosition, true);
                }
                //  else just expand the Group without animation
                else {
                    lstclass.expandGroup(parentPosition);
                }
        }
        notifyDataSetChanged();
    }

    class ViewHolderStudent {

        public TextView txt_stname;
        public CheckBox chk_stitem;
    }

    public ArrayList<String> getstudentid(Activity contxt, String[] classes, ArrayList<Childbeans> arrayAllClasses) {
        String ids = "";
        String classid = "";
        ArrayList<String> al = new ArrayList<String>();
        if (maphash != null && maphash.size() > 0) {
            Iterator it = maphash.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();

                if (maphash.get(key).size() > 0) {
                    if (classid.equals("")) {
                        classid = maphash.get(key).get(0).class_id + "";
                        Log.e("while if loop", classid);
                    } else {
                        classid = classid + "," + maphash.get(key).get(0).class_id;
                        Log.e("while else loop", classid);
                    }
                    for (int j = 0; j < maphash.get(key).size(); j++) {
                        if (ids.equals(""))
                            ids = maphash.get(key).get(j).user_id;
                        else
                            ids = ids + "," + maphash.get(key).get(j).user_id;
                    }
                }
            }

            for (int i = 0; i < arrayAllClasses.size(); i++) {
                if (flag.get(i)) {
                    if (i == 0 && classid.equals(""))
                        classid = arrayAllClasses.get(i).class_id;
                    else
                        classid = classid + "," + arrayAllClasses.get(i).class_id;

                }
            }

            al.add(ids);
            al.add(classid);
            return al;

        } else
            for (int i = 0; i < arrayAllClasses.size(); i++) {
                Log.d("ClassListAdapter ", "arrayAllClasses data : " + arrayAllClasses.get(i).class_name);
                if (flag.get(i)) {
                    if (i == 0)
                        classid = arrayAllClasses.get(i).class_id;
                    else
                        classid = classid + "," + arrayAllClasses.get(i).class_id;

                }
            }
        al.add("0");
        al.add(classid);
        return al;
    }

}
