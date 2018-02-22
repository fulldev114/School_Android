package com.cloudstream.cslink.teacher;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudstream.cslink.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AdapterHomeScreen extends BaseExpandableListAdapter {

    private HashMap<String, List<Integer>> img_list = new HashMap<String, List<Integer>>();
    private List<String> title_list = new ArrayList<String>();

    private HashMap<String, List<String>> child_list = new HashMap<String, List<String>>();
    String[] tag;
    Integer[] image;
    Integer[] badge;


    Context myc;
    private int flag_report = 0, flag_admin = 0;


    public AdapterHomeScreen(Context context, List<String> title_list, HashMap<String, List<String>> child_list, Integer[] drawerlist_image, Integer[] drawerlist_badge, HashMap<String,
            List<Integer>> img_list) {
        myc = context;
        this.title_list = title_list;
        this.child_list = child_list;
        image = drawerlist_image;
        badge = drawerlist_badge;
        this.img_list = img_list;

    }

    public void updatedata(List<String> title_list, HashMap<String, List<String>> child_list,
                           HashMap<String, List<Integer>> img_list) {
        this.title_list = title_list;
        this.child_list = child_list;
        this.img_list = img_list;
    }

    @Override
    public int getGroupCount() {
        return title_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child_list.get(title_list.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return title_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child_list.get(title_list.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int pos, boolean isExpanded, View convertview, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;

        if (convertview == null) {
            convertview = li.inflate(R.layout.adres_homescreenlisttile, null);

            holder = new ViewHolder();
            holder.date = (TextView) convertview.findViewById(R.id.textView1);
            holder.img = (ImageView) convertview.findViewById(R.id.imageView1);
            holder.txtBadge = (TextView) convertview.findViewById(R.id.txtBadge);
            holder.dropdown = (ImageView) convertview.findViewById(R.id.dropdown);
            convertview.setTag(holder);

        } else
            holder = (ViewHolder) convertview.getTag();

        if (pos == 2 || pos == 3) {
            holder.dropdown.setVisibility(View.VISIBLE);
            //holder.date.setTextColor(myc.getResources().getColor(R.color.btn_cmd_focus));
        } else {
            holder.dropdown.setVisibility(View.GONE);
        }
        holder.date.setTextColor(myc.getResources().getColorStateList(R.color.btn_cmd_color));

        holder.date.setText(title_list.get(pos));

        int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.img.setBackgroundDrawable(myc.getResources()
                    .getDrawable(image[pos]));
        } else {
            holder.img.setBackground(myc.getResources().getDrawable(
                    image[pos]));
        }

        holder.txtBadge.setVisibility(View.VISIBLE);
        if (badge[pos] == 0) {
            holder.txtBadge.setVisibility(View.GONE);
        } else if (badge[pos] < 10) {
            holder.txtBadge.setText(badge[pos] + "");
        } else {
            holder.txtBadge.setText("N");
        }

        return convertview;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String expandedListText = (String) getChild(groupPosition, childPosition);
        ViewHolderChild holderchild;

        LayoutInflater li = (LayoutInflater) myc.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (expandedListText != null && expandedListText.length() > 0) {
            if (convertView == null) {
                convertView = li.inflate(R.layout.adres_child_menu_list, null);
                holderchild = new ViewHolderChild();

                holderchild.text_child = (TextView) convertView.findViewById(R.id.text_child);
                holderchild.image_child = (ImageView) convertView.findViewById(R.id.image_child);
                convertView.setTag(holderchild);
            } else {
                holderchild = (ViewHolderChild) convertView.getTag();
            }


            holderchild.text_child.setTextColor(myc.getResources().getColorStateList(R.color.btn_cmd_color));
            int img = img_list.get(title_list.get(groupPosition)).get(childPosition);

            holderchild.text_child.setText(expandedListText);
            holderchild.image_child.setImageResource(img);
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
        public ImageView dropdown;
    }

    static class ViewHolderChild {
        TextView text_child;
        ImageView image_child;
    }

}