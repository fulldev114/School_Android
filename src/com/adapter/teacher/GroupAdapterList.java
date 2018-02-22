package com.adapter.teacher;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudstream.cslink.teacher.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.Bean.GroupBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by etech on 20/3/17.
 */

/*
public class GroupAdapterList extends BaseAdapter {
    private Context mContext;
    private ArrayList<GroupBean> list;
    private ArrayList<Boolean> flag;
    private CheckBox chkSelectall;


    public GroupAdapterList(Context mContext, ArrayList<GroupBean> list, CheckBox chkSelectall) {
        this.mContext = mContext;
        this.list = list;
        this.chkSelectall = chkSelectall;

    }

    public void updatedata(Context mContext, ArrayList<GroupBean> list, CheckBox chkSelectall) {
        this.mContext = mContext;
        this.list = list;
        this.chkSelectall = chkSelectall;
        if (list != null) {
            flag = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                flag.add(false);
            }
        }
        notifyDataSetChanged();
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
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolderChild holderchild;
        if (convertView == null) {
            holderchild = new ViewHolderChild();
            convertView = inflater.inflate(R.layout.adres_item_emeregnecygroup_checkbox, parent, false);
            holderchild.txtGroupName = (TextView) convertView.findViewById(R.id.txt_grp_nm);
            holderchild.chkGroup = (CheckBox) convertView.findViewById(R.id.chk_item);
            holderchild.linGroupCheck = (LinearLayout) convertView.findViewById(R.id.lin_grp_chk);
            convertView.setTag(holderchild);
        } else {
            holderchild = (ViewHolderChild) convertView.getTag();
        }


        if (list.get(position) != null && list.get(position).groupName != null && list.get(position).groupName.length() > 0) {
            holderchild.txtGroupName.setText(list.get(position).groupName);
        }

        //Select all is checked then all checkbox is checked
        holderchild.chkGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkSelectall.setChecked(false);
                    flag.set(position, true);
                    notifyDataSetChanged();
                } else {
                    flag.set(position, false);
                    notifyDataSetChanged();
                }

            }
        });

        holderchild.linGroupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.get(position) == true) {
                    holderchild.chkGroup.setChecked(false);
                    flag.set(position, false);
                    notifyDataSetChanged();
                } else {
                    holderchild.chkGroup.setChecked(true);
                    flag.set(position, true);
                    notifyDataSetChanged();
                }
            }
        });

        holderchild.chkGroup.setChecked(flag.get(position));

        return convertView;
    }

    public void setCheckedfalse(ArrayList<GroupBean> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                flag.set(i, false);
            }
            notifyDataSetChanged();
        }

    }


    class ViewHolderChild {
        public TextView txtGroupName;
        public CheckBox chkGroup;
        public LinearLayout linGroupCheck;
    }


    public GroupBean getSelectedSubId(Context context) {

        GroupBean bean = new GroupBean();
        String selectedGroupId = "";
        String selectedGroupName = "";
        int counter = 0;
        if(flag!=null) {
            for (int loop = 0; loop < flag.size(); loop++) {
                if (flag.get(loop)) {
                    if (counter == 0) {
                        selectedGroupId = list.get(loop).groupID;
                        selectedGroupName = list.get(loop).groupName;
                    } else {
                        selectedGroupId = selectedGroupId + "," + list.get(loop).groupID;
                        selectedGroupName = selectedGroupName + "," + list.get(loop).groupName;
                    }
                    counter++;
                }
            }
        }
        bean.groupID = selectedGroupId;
        bean.groupName = selectedGroupName;
        return bean;
    }
}
*/

public class GroupAdapterList extends BaseExpandableListAdapter {

    private Context mContext;
    private HashMap<String, ArrayList<GroupBean>> list;
    private CheckBox chkSelectall;
    private ArrayList<Boolean> flag;
    private HashMap<Integer, ArrayList<Boolean>> childFlag;
    private ExpandableListView expandableList;
    private ArrayList<GroupBean> groupList;

    public GroupAdapterList(Context mContext, HashMap<String, ArrayList<GroupBean>> list, CheckBox chkSelectall, ExpandableListView expandableList) {
        this.mContext = mContext;
        this.list = list;
        this.chkSelectall = chkSelectall;
        this.expandableList = expandableList;
    }

    public void updatedata(Context mContext, HashMap<String, ArrayList<GroupBean>> list, CheckBox chkSelectall,
                           ExpandableListView expandableList, ArrayList<GroupBean> groupList) {
        this.mContext = mContext;
        this.list = list;
        this.chkSelectall = chkSelectall;
        this.expandableList = expandableList;
        this.groupList = groupList;

        if (list != null) {
            flag = new ArrayList<>();
            childFlag = new HashMap<>();
            ArrayList<Boolean> subflag = null;
            for (int i = 0; i < list.size(); i++) {
                flag.add(false);
                String key = list.keySet().iterator().next();
                subflag = new ArrayList<>();
                for (int j = 0; j < list.get(key).size(); j++) {
                    subflag.add(false);
                }
                childFlag.put(i, subflag);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupList.get(groupPosition).groupName).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return (list.get(groupList.get(groupPosition).groupName).get(childPosition));
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.adres_item_emeregnecygroup_checkbox, parent, false);
            holder.txtGroupName = (TextView) convertView.findViewById(R.id.txt_grp_nm);
            holder.chkGroup = (CheckBox) convertView.findViewById(R.id.chk_item);
            holder.linGroupCheck = (LinearLayout) convertView.findViewById(R.id.lin_grp_chk);
            holder.img_downarrow = (ImageView) convertView.findViewById(R.id.img_downarrow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtGroupName.setText(groupList.get(groupPosition).groupName);
        holder.chkGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkSelectall.setChecked(false);
                    flag.set(groupPosition, true);
                    expandableList.collapseGroup(groupPosition);
                    removeChild(groupPosition);
                    notifyDataSetChanged();
                } else {
                    flag.set(groupPosition, false);
                    notifyDataSetChanged();
                }

            }
        });

        holder.img_downarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.get(groupPosition)) {
                    try {
                        ApplicationData.showMessage(mContext, "",
                                mContext.getResources().getString(R.string.alert_gp_select_member), mContext.getResources().getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (expandableList.isGroupExpanded(groupPosition))
                        expandableList.collapseGroup(groupPosition);
                    else
                        expandableList.expandGroup(groupPosition);
                }
            }
        });
        holder.linGroupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.get(groupPosition) == true) {
                    holder.chkGroup.setChecked(false);
                    flag.set(groupPosition, false);
                    notifyDataSetChanged();
                } else {
                    holder.chkGroup.setChecked(true);
                    flag.set(groupPosition, true);
                    expandableList.collapseGroup(groupPosition);
                    removeChild(groupPosition);
                    notifyDataSetChanged();
                }

            }
        });

        holder.chkGroup.setChecked(flag.get(groupPosition));
        return convertView;
    }

    private void removeChild(int groupPosition) {
        ArrayList<Boolean> childStatus = childFlag.get(groupPosition);
        for (int i = 0; i < childStatus.size(); i++) {
            childStatus.set(i, false);
        }
        childFlag.put(groupPosition, childStatus);
    }


    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewHolderChild holderchild;
        if (convertView == null) {
            holderchild = new ViewHolderChild();
            convertView = inflater.inflate(R.layout.adres_item_subgroup_emergency, parent, false);
            holderchild.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holderchild.txtNumber = (TextView) convertView.findViewById(R.id.txt_number);
            holderchild.chkMember = (CheckBox) convertView.findViewById(R.id.chk_member);
            holderchild.linSubGP = (LinearLayout) convertView.findViewById(R.id.lin_subgp);
            convertView.setTag(holderchild);
        } else {
            holderchild = (ViewHolderChild) convertView.getTag();
        }

        GroupBean subGroupBean = ((GroupBean) getChild(groupPosition, childPosition));
        if (subGroupBean != null && subGroupBean.groupMemberName != null && subGroupBean.groupMemberName.length() > 0) {
            holderchild.txtName.setText(subGroupBean.groupMemberName);

            if (subGroupBean.groupMemberNumber != null && subGroupBean.groupMemberNumber.length() > 0)
                holderchild.txtNumber.setText(subGroupBean.groupMemberNumber);
        }

        holderchild.chkMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkSelectall.setChecked(false);
                    ArrayList<Boolean> status = childFlag.get(groupPosition);
                    status.set(childPosition, true);
                    childFlag.put(groupPosition, status);
                    notifyDataSetChanged();
                } else {
                    ArrayList<Boolean> status = childFlag.get(groupPosition);
                    status.set(childPosition, false);
                    childFlag.put(groupPosition, status);
                    notifyDataSetChanged();
                }

            }
        });


        holderchild.linSubGP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childFlag.get(groupPosition).get(childPosition) == true) {
                    holderchild.chkMember.setChecked(false);
                    ArrayList<Boolean> status = childFlag.get(groupPosition);
                    status.set(childPosition, false);
                    childFlag.put(groupPosition, status);
                    notifyDataSetChanged();
                } else {
                    holderchild.chkMember.setChecked(true);
                    ArrayList<Boolean> status = childFlag.get(groupPosition);
                    status.set(childPosition, true);
                    childFlag.put(groupPosition, status);
                    notifyDataSetChanged();
                }

            }
        });

        holderchild.chkMember.setChecked(childFlag.get(groupPosition).get(childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder {
        public TextView txtGroupName;
        public CheckBox chkGroup;
        public LinearLayout linGroupCheck;
        public ImageView img_downarrow;
    }

    class ViewHolderChild {
        public TextView txtName, txtNumber;
        public CheckBox chkMember;
        public LinearLayout linSubGP;
    }


    public GroupBean getSelectedSubId(ArrayList<GroupBean> gpBean) {

        GroupBean bean = new GroupBean();
        String selectedGroupId = "";
        String selectMemberId = "";
        int counter = 0;
        if (flag != null) {
            for (int loop = 0; loop < flag.size(); loop++) {
                if (flag.get(loop)) {
                    if (selectedGroupId.equals("")) {
                        selectedGroupId = gpBean.get(loop).groupID;
                    } else {
                        selectedGroupId = selectedGroupId + "," + gpBean.get(loop).groupID;
                    }
                } else {
                    for (int j = 0; j < childFlag.get(loop).size(); j++) {

                        if (childFlag.get(loop).get(j)) {
                            if (selectMemberId.equals("")) {
                                selectMemberId = list.get(gpBean.get(loop).groupName).get(j).groupMemberId;
                            } else {
                                selectMemberId = selectMemberId + "," + list.get(gpBean.get(loop).groupName).get(j).groupMemberId;
                            }
                        }
                    }
                }
            }

        }
        bean.groupID = selectedGroupId;
        bean.groupMemberId = selectMemberId;
        return bean;
    }

    public void setCheckedfalse(HashMap<String, ArrayList<GroupBean>> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                flag.set(i, false);

                if (childFlag != null) {
                    if (childFlag.size() > 0) {
                        String key = list.keySet().iterator().next();
                        ArrayList<Boolean> subflag = new ArrayList<>();
                        for (int j = 0; j < list.get(key).size(); j++) {
                            subflag.add(false);
                        }
                        childFlag.put(i, subflag);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

}