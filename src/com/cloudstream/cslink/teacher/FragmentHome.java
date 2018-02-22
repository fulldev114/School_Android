package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.teacher.Childbeans;
import com.cloudstream.cslink.R;
import com.common.dialog.MainProgress;
import com.db.teacher.DatabaseHelper;
import com.service.teacher.UpdateReceiverInternet;
import com.xmpp.teacher.Constant;
import com.xmpp.teacher.XMPPMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private MainProgress pDialog;
    private LinearLayout lin_message, lin_absent, lin_gpmsg;
    private ViewPager viewpager;
    private int selPos = 0;
    private String child_array, Response = "", teacher_id, parent_name, school_id;
    ArrayList<Childbeans> arrayList = null;
    String[] videoNames;
    private RelativeLayout rel_int_msg, rel_student, rel_sfo;
    private String psbadge, chat_badge, internal_badge, register_badge;
    private LinearLayout lin_badgestudent, linbadge_inter, inc_msg_bg, linc_reg_bg;
    TextView txtBadge_students;
    private SharedPreferences sharedpref;
    private TextView txtBadge, txtBadge_inter, txtBadgereg;
    private Activity mActivity;

    public FragmentHome() {

    }

    String url, _parent_id, child_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.adres_homescreen, container, false);

        sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);
        teacher_id = sharedpref.getString("teacher_id", "");
        school_id = sharedpref.getString("school_id", "");

        mActivity = getActivity();
        lin_message = (LinearLayout) rootView.findViewById(R.id.lin_msg);
        lin_gpmsg = (LinearLayout) rootView.findViewById(R.id.lin_gp);
        lin_absent = (LinearLayout) rootView.findViewById(R.id.lin_abs);
        rel_int_msg = (RelativeLayout) rootView.findViewById(R.id.rel_int_msg);
        rel_student = (RelativeLayout) rootView.findViewById(R.id.rel_student);
        rel_sfo = (RelativeLayout) rootView.findViewById(R.id.rel_sfo);
        lin_badgestudent = (LinearLayout) rootView.findViewById(R.id.lin_badgestudent);
        txtBadge_students = (TextView) rootView.findViewById(R.id.txtBadge_students);
        inc_msg_bg = (LinearLayout) rootView.findViewById(R.id.inc_msg_bg);
        txtBadge = (TextView) rootView.findViewById(R.id.txtBadge);
        linbadge_inter = (LinearLayout) rootView.findViewById(R.id.linbadge_inter);
        txtBadge_inter = (TextView) rootView.findViewById(R.id.txtBadge_inter);
        linc_reg_bg = (LinearLayout) rootView.findViewById(R.id.linc_reg_bg);
        txtBadgereg = (TextView) rootView.findViewById(R.id.txtBadgereg);
        lin_message.setOnClickListener(this);
        lin_gpmsg.setOnClickListener(this);
        lin_absent.setOnClickListener(this);
        rel_int_msg.setOnClickListener(this);
        rel_student.setOnClickListener(this);
        rel_sfo.setOnClickListener(this);

        //to check push notification is received or not in app..
//        boolean isDebuggable = 0 != (getActivity().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE);
//        boolean isBeingDebugged = android.os.Debug.isDebuggerConnected();
//
//        if (isDebuggable || isBeingDebugged) {
//            Button btnLog = (Button) rootView.findViewById(R.id.btnLog);
//            btnLog.setVisibility(View.VISIBLE);
//            btnLog.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(getActivity(), ActivityLog.class);
//                    startActivity(i);
//                }
//            });
//        }

        //emergency alert click
        ((MainActivity) getActivity()).lin_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), EmergencyAlertActivity.class);
                startActivity(i);
            }
        });

       /* if (psbadge != null && psbadge.length() > 0 && !psbadge.equalsIgnoreCase("0")) {
            lin_badgestudent.setVisibility(View.VISIBLE);
            txtBadge_students.setText(psbadge);
        }*/

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                ApplicationData.setMainActivity(mActivity);
            }
        }, 500);


        setbadge();

        Intent intent = new Intent(getActivity(), RegistrationIntentService.class);
        intent.putExtra("teacher_id", teacher_id);
        getActivity().startService(intent);

        return rootView;
    }

    private void loadChildrenList() {
        if (arrayList != null)
            arrayList.clear();
        arrayList = new ArrayList<Childbeans>();
        int noti_position = 0;
        if (child_array.equalsIgnoreCase("")) {

        } else {
            try {
                JSONArray jsonObj = new JSONArray(child_array);
                int adtuallength = jsonObj.length();
                videoNames = new String[adtuallength];
                for (int i = 0; i < adtuallength; i++) {
                    Childbeans childbeans = new Childbeans();
                    JSONObject c = jsonObj.getJSONObject(i);
                    childbeans.child_image = c.getString("child_image");
                    childbeans.child_name = c.getString("child_name");
                    videoNames[i] = new String(c.getString("child_name"));
                    childbeans.school_name = c.getString("school_name");
                    childbeans.child_moblie = c.getString("child_moblie");
                    childbeans.school_class_id = c.getString("school_class_id");
                    childbeans.user_id = c.getString("user_id");
                   /* if (String.valueOf(noti_kidid).equals(childbeans.user_id))
                        noti_position = i;*/
                    childbeans.child_gender = c.getString("child_gender");
                    childbeans.child_age = c.getString("child_age");
                    childbeans.school_id = c.getString("school_id");
                    childbeans.name = parent_name;
                    childbeans.badge = c.getInt("badge") + c.getInt("abi_badge") + c.getInt("abn_badge");
                    arrayList.add(childbeans);
                }
                // looping through All Contacts
            } catch (JSONException e) {
                System.out.println("Andy Error " + e);
                e.printStackTrace();
            }
        }


       /* if (!noti_type.equals("")) {
            goMainActivity(noti_position);
        }*/
    }

    public void onClick(View v) {
        Bundle bundle = new Bundle();
        // ((MainActivity)getActivity()).lin_emergency.setVisibility(View.GONE);
        ApplicationData.ishomebuttonview = true;

        if (v.getId() == R.id.lin_msg) {
            Intent i = new Intent(getActivity(), FragmentChat.class);
            startActivity(i);
            DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
            db.clearallchatbadge(teacher_id, "teacher-parent");
            SharedPreferences.Editor edit = sharedpref.edit();
            edit.putString("chat_badge", "0");
            edit.commit();

            // bundle.putString("noti_teacher_id", noti_from_id + "");
            // fragment.setArguments(bundle);
        } else if (v.getId() == R.id.lin_gp) {
            Intent i = new Intent(getActivity(), FragmentGroupMessage.class);
            startActivity(i);

        } else if (v.getId() == R.id.lin_abs) {
            Intent i = new Intent(getActivity(), FragmentAttendance.class);
            startActivity(i);
        } else if (v.getId() == R.id.rel_int_msg) {
            Intent i = new Intent(getActivity(), FragmentTeacher.class);
            startActivity(i);
            DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
            db.clearallchatbadge(teacher_id, "teacher-teacher");
            SharedPreferences.Editor edit = sharedpref.edit();
            edit.putString("internal_badge", "0");
            edit.commit();

        } else if (v.getId() == R.id.rel_student) {
            Intent i = new Intent(getActivity(), FragmentStudentSetting.class);
            startActivity(i);
        } else if (v.getId() == R.id.rel_sfo) {
            Intent i = new Intent(getActivity(), SFOHomeActivity.class);
            startActivity(i);
        }

    }


    protected void setlist_toadapter() {
        // TODO Auto-generated method stub
        /*adapter = new TeacherListAdapter(getActivity(), parentList);
        _teacherlist.setAdapter(adapter);*/

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void setbadge() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);
                    psbadge = sharedpref.getString("psbadge", "");
                    register_badge = sharedpref.getString("register_badge", "");
                    chat_badge = sharedpref.getString("chat_badge", "");
                    internal_badge = sharedpref.getString("internal_badge", "");

                    lin_badgestudent.setVisibility(View.GONE);
                    linc_reg_bg.setVisibility(View.GONE);

                    if (psbadge != null && psbadge.length() > 0 && !psbadge.equalsIgnoreCase("0")) {
                        if (Integer.parseInt(psbadge) <= 0)
                            lin_badgestudent.setVisibility(View.GONE);
                        else {
                            lin_badgestudent.setVisibility(View.VISIBLE);
                            if (Integer.parseInt(psbadge) > 99)
                                txtBadge_students.setText("N");
                            else
                                txtBadge_students.setText(psbadge);
                        }
                    }
        /*if (register_badge != null && register_badge.length() > 0 && !register_badge.equalsIgnoreCase("0")) {
            if (Integer.parseInt(register_badge) <= 0)
                linc_reg_bg.setVisibility(View.GONE);
            else {
                linc_reg_bg.setVisibility(View.VISIBLE);
                if (Integer.parseInt(register_badge) > 99)
                    txtBadgereg.setText("N");
                else
                    txtBadgereg.setText(register_badge);
            }
        }*/

      /*  if (chat_badge != null && chat_badge.length() > 0 && !chat_badge.equalsIgnoreCase("0")) {
            if (Integer.parseInt(chat_badge) <= 0)
                inc_msg_bg.setVisibility(View.GONE);
            else {
                inc_msg_bg.setVisibility(View.VISIBLE);
                txtBadge.setText(chat_badge);
            }
        }

        if (internal_badge != null && internal_badge.length() > 0 && !internal_badge.equalsIgnoreCase("0")) {
            if (Integer.parseInt(internal_badge) <= 0)
                linbadge_inter.setVisibility(View.GONE);
            else {
                linbadge_inter.setVisibility(View.VISIBLE);
                txtBadge_inter.setText(internal_badge);
            }
        }*/

                    Handler handl = new Handler();
                    handl.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ApplicationData.ignorbadge = false;
                        }
                    }, 4000);

                    setmessagebadge();
                }
            });
        }
    }

    public void setmessagebadge() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    inc_msg_bg.setVisibility(View.GONE);
                    linbadge_inter.setVisibility(View.GONE);

                    int chatbadge = 0, teacherchatbadge = 0, allchatbadge = 0, allteacherchatbadge = 0;

                    DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
                    String query = "select * from chat_msg_badge where User_id= " + '"' + teacher_id + '"';
                    ArrayList<HashMap<String, String>> messagelist = db.selectRecordsFromDBList(query, null);
                    if (messagelist != null) {
                        for (HashMap<String, String> badgemap : messagelist) {
                            if (badgemap.get("Message_type").equalsIgnoreCase("teacher-parent")) {
                                chatbadge = chatbadge + Integer.parseInt(badgemap.get("Badge"));
                                allchatbadge = Integer.parseInt(badgemap.get("AllBadge"));
                                if (allchatbadge > 0 || !chat_badge.equals("0")) {
                                    inc_msg_bg.setVisibility(View.VISIBLE);
                                    if (allchatbadge > 99 || Integer.parseInt(chat_badge) > 99)
                                        txtBadge.setText("N");
                                    else
                                        txtBadge.setText(String.valueOf(allchatbadge + Integer.parseInt(chat_badge)));
                                } else
                                    inc_msg_bg.setVisibility(View.GONE);
                            } else if (badgemap.get("Message_type").equalsIgnoreCase("teacher-teacher")) {
                                teacherchatbadge = teacherchatbadge + Integer.parseInt(badgemap.get("Badge"));
                                allteacherchatbadge = Integer.parseInt(badgemap.get("AllBadge"));
                                if (allteacherchatbadge > 0 || !internal_badge.equals("0")) {
                                    linbadge_inter.setVisibility(View.VISIBLE);
                                    if (allteacherchatbadge > 99 || Integer.parseInt(internal_badge) > 99)
                                        txtBadge_inter.setText("N");
                                    else
                                        txtBadge_inter.setText(String.valueOf(allteacherchatbadge + Integer.parseInt(internal_badge)));
                                } else
                                    linbadge_inter.setVisibility(View.GONE);
                            }
                        }

                        if (!txtBadge.getText().toString().equalsIgnoreCase("N")) {
                            txtBadge.setText(String.valueOf(allchatbadge + Integer.parseInt(chat_badge)));
                        }

                        if (!txtBadge_inter.getText().toString().equalsIgnoreCase("N")) {
                            txtBadge_inter.setText(String.valueOf(allteacherchatbadge + Integer.parseInt(internal_badge)));
                        }

                    }
                }
            });
        }
    }

}

