package com.cloudstream.cslink.teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.TeacherListAdapter_message;
import com.cloudstream.cslink.R;
import com.common.utils.ConstantApi;
import com.db.teacher.DatabaseHelper;
import com.request.ETechAsyncTask;
import com.widget.textstyle.MyTextView_Signika_Regular;
import com.xmpp.teacher.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SFOHomeActivity extends Activity implements View.OnClickListener {

    Activity mActivity;
    protected LinearLayout mRelWrapp;
    private LayoutInflater mInflater;
    private View mRootView;

    private View mRelSFOViewAll;
    private View mRelNotArrivedViewAll;
    private View mRelCheckedOutViewAll;
    private View mRelActivitiesViewAll;

    private View mRelCheckedIn;
    private View mRelCheckedOut;
    private View mRelMessage;
    private View mRelGroupMessage;
    private View mRelActivities;

    private MyTextView_Signika_Regular mNotiBadgeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.header_sfo_home);
        mRelWrapp=(LinearLayout)findViewById(R.id.relwrapp);

        mNotiBadgeTextView = (MyTextView_Signika_Regular)findViewById(R.id.txtBadge_notification);

        mInflater = getLayoutInflater();
        mRootView = mInflater.inflate(R.layout.adres_fragment_sfohome, null);

        mRelSFOViewAll = mRootView.findViewById(R.id.sfoViewAllGroup);
        mRelSFOViewAll.setOnClickListener(this);
        mRelNotArrivedViewAll = mRootView.findViewById(R.id.notarrivedViewAllGroup);
        mRelNotArrivedViewAll.setOnClickListener(this);
        mRelCheckedOutViewAll = mRootView.findViewById(R.id.checkedOutViewAllGroup);
        mRelCheckedOutViewAll.setOnClickListener(this);
        mRelActivitiesViewAll = mRootView.findViewById(R.id.activitiesViewAllGroup);
        mRelActivitiesViewAll.setOnClickListener(this);

        mRelCheckedIn = mRootView.findViewById(R.id.checkedInGroup);
        mRelCheckedIn.setOnClickListener(this);

        mRelCheckedOut = mRootView.findViewById(R.id.checkedOutGroup);
        mRelCheckedOut.setOnClickListener(this);

        mRelMessage = mRootView.findViewById(R.id.messageGroup);
        mRelMessage.setOnClickListener(this);

        mRelGroupMessage = mRootView.findViewById(R.id.groupMessageGroup);
        mRelGroupMessage.setOnClickListener(this);

        mRelActivities = mRootView.findViewById(R.id.activitiesGroup);
        mRelActivities.setOnClickListener(this);
        mRelWrapp.addView(mRootView);
        callapi();

    }


    public void callapi() {
        try {
//            HashMap<String, Object> params = new HashMap<String, Object>();
//            params.put("teacher_id", teacher_id);
//
//            ETechAsyncTask task = new ETechAsyncTask(FragmentTeacher.this, this, ConstantApi.GET_TEACHER_DETAIL, params);
//            task.execute(ApplicationData.main_url + ConstantApi.GET_TEACHER_DETAIL + ".php?");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkedInGroup) {
//            Intent i = new Intent(getActivity(), FragmentChat.class);
//            startActivity(i);

        } else if (v.getId() == R.id.checkedOutGroup) {
//            Intent i = new Intent(getActivity(), FragmentGroupMessage.class);
//            startActivity(i);

        } else if (v.getId() == R.id.messageGroup) {
            Intent i = new Intent(mActivity, FragmentChat.class);
            startActivity(i);

        } else if (v.getId() == R.id.groupMessageGroup) {
            Intent i = new Intent(mActivity, FragmentGroupMessage.class);
            startActivity(i);

        } else if (v.getId() == R.id.activitiesGroup) {
            Intent i = new Intent(mActivity, ActivitiesActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.sfoViewAllGroup) {
//            Intent i = new Intent(getActivity(), FragmentGroupMessage.class);
//            startActivity(i);

        } else if (v.getId() == R.id.notarrivedViewAllGroup) {
//            Intent i = new Intent(getActivity(), FragmentAttendance.class);
//            startActivity(i);
        } else if (v.getId() == R.id.checkedOutViewAllGroup) {
//            Intent i = new Intent(getActivity(), FragmentTeacher.class);
//            startActivity(i);
//            DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
//            db.clearallchatbadge(teacher_id, "teacher-teacher");
//            SharedPreferences.Editor edit = sharedpref.edit();
//            edit.putString("internal_badge", "0");
//            edit.commit();

        } else if (v.getId() == R.id.activitiesViewAllGroup) {
//            Intent i = new Intent(mActivity, FragmentStudentSetting.class);
//            startActivity(i);
        } else if (v.getId() == R.id.rel_sfo) {
//            Intent i = new Intent(mActivity, FragmentSFOHome.class);
//            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setmessagebadge();
        ApplicationData.setMainActivity(mActivity);
        //ApplicationData.setInternalActivity(null);
        View view = getCurrentFocus();
        if (view!=null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void onSearchClicked() {

    }
    private void onCalendarClicked() {

    }

    private void onNotificationClicked() {

    }


}
