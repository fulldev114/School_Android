package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;
import com.cloudstream.cslink.common.calendar.CalendarActivity;
import com.cloudstream.cslink.common.activities.ActivitiesActivity;
import com.widget.textstyle.MyTextView_Signika_Regular;

public class TeacherSFOHomeActivity extends Activity implements View.OnClickListener {

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

    private Dialog mCheckedDlg;

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

    public void showCheckedDialog(Context context, final Boolean isCheckedIn) throws Exception {
        //custom dialog
        if (mCheckedDlg != null && mCheckedDlg.isShowing())
            mCheckedDlg.dismiss();

        mCheckedDlg = new Dialog(context);
        mCheckedDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCheckedDlg.setContentView(R.layout.checkedmsgdialog);
        mCheckedDlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvTitle = (TextView) mCheckedDlg.findViewById(R.id.msgtitle);
        if (isCheckedIn)
            tvTitle.setText(R.string.checked_in_title_low);
        else
            tvTitle.setText(R.string.checked_out_title_low);

        ImageView btnClose = (ImageView) mCheckedDlg.findViewById(R.id.close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCheckedDlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnSFO = (Button) mCheckedDlg.findViewById(R.id.btn_sfo);
        if (isCheckedIn)
            btnSFO.setBackgroundResource(R.drawable.txt_cmd_blue);
        else
            btnSFO.setBackgroundResource(R.drawable.txt_cmd_yellow);

        btnSFO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckedDlg.dismiss();
                try {
                    Intent i = new Intent(mActivity, StudentSFOActivity.class);
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnActivity = (Button) mCheckedDlg.findViewById(R.id.btn_activity);
        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckedDlg.dismiss();
                try {
                    if (isCheckedIn) {
                        Intent i = new Intent(mActivity, SetCheckedInActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(mActivity, SetCheckedOutActivity.class);
                        startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mCheckedDlg.setCanceledOnTouchOutside(false);
        mCheckedDlg.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkedInGroup) {
            try {
                showCheckedDialog(mActivity, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (v.getId() == R.id.checkedOutGroup) {
            try {
                showCheckedDialog(mActivity, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
            Intent i = new Intent(mActivity, StudentSFOActivity.class);
            startActivity(i);

        } else if (v.getId() == R.id.notarrivedViewAllGroup) {
            Intent i = new Intent(mActivity, StudentNotArrivedActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.checkedOutViewAllGroup) {
            Intent i = new Intent(mActivity, StudentCheckedOutActivity.class);
            startActivity(i);
//            DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
//            db.clearallchatbadge(teacher_id, "teacher-teacher");
//            SharedPreferences.Editor edit = sharedpref.edit();
//            edit.putString("internal_badge", "0");
//            edit.commit();

        } else if (v.getId() == R.id.activitiesViewAllGroup) {
            Intent i = new Intent(mActivity, StudentActivitiesActivity.class);
            startActivity(i);
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

    public void back(View v) {
        finish();
    }

    public void onSearchClicked(View v) {

    }

    public void onCalendarClicked(View v) {
        Intent i = new Intent(mActivity, CalendarActivity.class);
        startActivity(i);
    }

    public void onNotificationClicked(View v) {
        Intent i = new Intent(mActivity, NotificationsActivity.class);
        startActivity(i);
    }
}
