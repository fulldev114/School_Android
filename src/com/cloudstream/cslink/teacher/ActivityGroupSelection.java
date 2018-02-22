package com.cloudstream.cslink.teacher;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.adapter.teacher.GroupAdapterList;
import com.cloudstream.cslink.R;
import com.cloudstream.cslink.teacher.ActivityHeader;
import com.common.Bean.GroupBean;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by etech on 20/3/17.
 */

public class ActivityGroupSelection extends ActivityHeader implements AsyncTaskCompleteListener<String> {
    private LinearLayout linSelectAll, linSend, linCancel;
    private ListView listViewGroup;
    private HashMap<String, Object> map;
    private LayoutInflater inflater;
    private RelativeLayout screenview;
    private GroupAdapterList groupAdapter;
    private HashMap<String, ArrayList<GroupBean>> list = new HashMap<String, ArrayList<GroupBean>>();
    private String teacher_id, teacher_name, teacher_email, school_id;
    private String TAG = "ActivityGroupSelection";
    private CheckBox chkSelectall;
    private LinearLayout linNote;
    private ExpandableListView expandableList;
    private ArrayList<GroupBean> gpBean = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        screenview = (RelativeLayout) inflater.inflate(R.layout.adres_screen_emergencygroup_list, null);
        relwrapp.addView(screenview);

        init();

        showheadermenu(ActivityGroupSelection.this, getString(R.string.emergency_msg), R.color.color_blue_p, false);

        if (getIntent().hasExtra("map")) {
            map = (HashMap<String, Object>) getIntent().getSerializableExtra("map");
        }

        HashMap<String, Object> getListMap = new HashMap<>();
        getListMap.put("school_id", school_id);
        if (GlobalConstrants.isWifiConnected(ActivityGroupSelection.this)) {
            ETechAsyncTask task = new ETechAsyncTask(this, this, ConstantApi.GET_EMERGENCY_GROUPLIST, getListMap);
            task.execute(ApplicationData.main_url + ConstantApi.GET_EMERGENCY_GROUPLIST + ".php?");
        } else {
            try {
                ApplicationData.showToast(ActivityGroupSelection.this, R.string.msg_operation_error, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void init() {
        linSelectAll = (LinearLayout) findViewById(R.id.lin_selectall);
        listViewGroup = (ListView) findViewById(R.id.emergencylist_grp);
        linSend = (LinearLayout) findViewById(R.id.lin_emesend);
        linCancel = (LinearLayout) findViewById(R.id.lin_emecancel);
        chkSelectall = (CheckBox) findViewById(R.id.chk_selectall);
        linNote = (LinearLayout) findViewById(R.id.lin_note);
        expandableList = (ExpandableListView) findViewById(R.id.emergency_expandlist);
        linSelectAll.setOnClickListener(clickListener);
        linSend.setOnClickListener(sendClickListener);
        linCancel.setOnClickListener(clickListener);
        imgback.setOnClickListener(clickListener);

        SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        teacher_id = sharedpref.getString("teacher_id", "");
        teacher_name = sharedpref.getString("teacher_name", "");
        teacher_email = sharedpref.getString("email", "");
        school_id = sharedpref.getString("school_id", "");

        groupAdapter = new GroupAdapterList(ActivityGroupSelection.this, list, chkSelectall, expandableList);
        expandableList.setAdapter(groupAdapter);

    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.lin_selectall) {
                if (chkSelectall.isChecked()) {
                    chkSelectall.setChecked(false);
                } else {
                    chkSelectall.setChecked(true);
                    groupAdapter.setCheckedfalse(list);

                }

            } else if (v.getId() == R.id.lin_emecancel) {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            } else if (v.getId() == R.id.imgback) {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }

        }
    };

    View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.lin_emesend) {
                GroupBean bean = groupAdapter.getSelectedSubId(gpBean);
                String message = "";
                if (chkSelectall.isChecked()) {
                    message = getString(R.string.alert_emeregency);
                    sendMessage(message, bean);
                } else if ((bean.groupID != null && bean.groupID.length() > 0) || (bean.groupMemberId != null && bean.groupMemberId.length() > 0)) {
                    message = getResources().getString(R.string.alert_send_grp);
                    sendMessage(message, bean);
                } else if (!chkSelectall.isChecked() && (bean.groupID == null || bean.groupID == "" || bean.groupID.length() < 0)) {
                    ApplicationData.calldialog(ActivityGroupSelection.this, "", getString(R.string.select_one_option),
                            getString(R.string.str_ok), null, null, 1);
                }
            }
        }

        private void sendMessage(String message, final GroupBean groupId) {
            ApplicationData.calldialog(ActivityGroupSelection.this, "", message, getString(R.string.str_yes),
                    getString(R.string.str_no), new ApplicationData.DialogListener() {
                        @Override
                        public void diaBtnClick(int diaID, int btnIndex) {
                            if (btnIndex == 2) {
                                map.put("group_id", groupId.groupID);
                                map.put("member_id", groupId.groupMemberId);
                                map.put("language", ApplicationData.getlanguage(ActivityGroupSelection.this));
                                if (!GlobalConstrants.isWifiConnected(ActivityGroupSelection.this)) {
                                    return;
                                } else {
                                    ETechAsyncTask task = new ETechAsyncTask(ActivityGroupSelection.this, ActivityGroupSelection.this,
                                            ConstantApi.SEND_EMEREGENCY_MESSAGE, map);
                                    task.execute(ApplicationData.main_url + ConstantApi.SEND_EMEREGENCY_MESSAGE + ".php?");
                                }
                            }
                        }
                    }, 2);
        }
    };

    @Override
    public void onTaskComplete(int statusCode, String result, String webserviceCb, Object tag) {
        try {
            if (statusCode == ETechAsyncTask.COMPLETED) {
                JSONObject jObject = new JSONObject(result);

                try {
                    if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_EMERGENCY_GROUPLIST)) {
                        String flag = jObject.getString("flag");

                        if (flag.equalsIgnoreCase("1")) {
                            JSONArray jarray = jObject.getJSONArray("groups");
                            if (jarray.length() > 0) {
                                gpBean.clear();
                                linNote.setVisibility(View.GONE);
                                listViewGroup.setVisibility(View.VISIBLE);
                                ArrayList<GroupBean> alBean = null;
                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject jGroup = jarray.getJSONObject(i);
                                    GroupBean gb = null;
                                    if (!jGroup.isNull("group_id")) {
                                        if (!jGroup.isNull("group_name")) {
                                            gb = new GroupBean();
                                            gb.groupID = String.valueOf(jGroup.get("group_id"));
                                            gb.groupName = String.valueOf(jGroup.get("group_name"));
                                        }
                                        JSONArray subArray = jGroup.getJSONArray("group_mobile");
                                        alBean = new ArrayList<>();
                                        for (int j = 0; j < subArray.length(); j++) {
                                            JSONObject jSubGroup = subArray.getJSONObject(j);
                                            GroupBean gbSub = new GroupBean();
                                            gbSub.groupMemberId = String.valueOf(jSubGroup.get("gm_id"));
                                            gbSub.groupMemberName = String.valueOf(jSubGroup.get("gm_name"));
                                            gbSub.groupMemberNumber = String.valueOf(jSubGroup.get("mobile_no"));
                                            gbSub.groupID = String.valueOf(jSubGroup.get("group_id"));
                                            gbSub.groupMemberStatus = String.valueOf(jSubGroup.get("status"));
                                            gbSub.groupMemberCreatedAt = String.valueOf(jSubGroup.get("created_at"));

                                            alBean.add(gbSub);
                                        }
                                        gpBean.add(gb);
                                        list.put(gb.groupName, alBean);
                                    }
                                }
                                groupAdapter.updatedata(ActivityGroupSelection.this, list, chkSelectall, expandableList, gpBean);
                            } else {
                                linNote.setVisibility(View.VISIBLE);
                                listViewGroup.setVisibility(View.GONE);
                            }
                        } else {
                            linNote.setVisibility(View.VISIBLE);
                            listViewGroup.setVisibility(View.GONE);
                            String message = jObject.getString("msg");
                            ApplicationData.showMessage(ActivityGroupSelection.this, getString(R.string.app_name), message, getString(R.string.str_ok));
                        }
                    } else if (webserviceCb.equalsIgnoreCase(ConstantApi.SEND_EMEREGENCY_MESSAGE)) {
                        String flag = jObject.getString("flag");
                        if (flag.equalsIgnoreCase("1")) {
                            ApplicationData.calldialog(ActivityGroupSelection.this, "", getString(R.string.successmsg),
                                    getString(R.string.str_ok), null, new ApplicationData.DialogListener() {
                                        @Override
                                        public void diaBtnClick(int diaID, int btnIndex) {
                                            Intent intent = new Intent();
                                            setResult(-1, intent);
                                            finish();
                                        }
                                    }, 2);

                        } else {
                            String message = jObject.getString("msg");
                            ApplicationData.showMessage(ActivityGroupSelection.this, getString(R.string.app_name), message, getString(R.string.str_ok));
                        }
                    } else {
                        ApplicationData.showToast(ActivityGroupSelection.this, R.string.server_error, false);
                    }

                    //setAdapter();
                } catch (Exception e) {
                    Log.e(TAG, "onTaskComplete() " + e, e);
                }
            } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
                try {
                    ApplicationData.showToast(ActivityGroupSelection.this, R.string.msg_operation_error, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
