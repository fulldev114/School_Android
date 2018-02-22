package com.cloudstream.cslink.teacher;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CursorJoiner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.EmergencyAdapter;
import com.cloudstream.cslink.R;
import com.xmpp.teacher.Constant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by etech on 15/7/16.
 */
public class FragmentSystem extends Fragment {

    private static final int MESSAGE_SEND_SUCCESS = 101;
    private static final int RESULT_OK = -1;
    private ListView lst_emergency;
    private LinearLayout send;
    private TextView txt_cancel;
    private String teacher_id, teacher_name, teacher_email, school_id;

    Integer[] img_draw = {R.drawable.fire, R.drawable.alert, R.drawable.water_flood};
    ArrayList<Childbeans> alist = new ArrayList<Childbeans>();
    private String[] text_title;
    private EmergencyAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adres_fragment_system, container, false);

        lst_emergency = (ListView) view.findViewById(R.id.lst_emergency);
        send = (LinearLayout) view.findViewById(R.id.send);
        txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);

        text_title = getResources().getStringArray(R.array.emergency_message);

        //sharedpreference
        definesharedpreference();

        adapter = new EmergencyAdapter(getActivity(), img_draw, text_title);
        lst_emergency.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess_age = adapter.sendmessage(getActivity());
                if (mess_age.length() > 0) {
                    try {
                        mess_age = URLEncoder.encode(mess_age, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("sender_id", teacher_id);
                    map.put("message", mess_age);
                    map.put("school_id", school_id);
                    map.put("language", ApplicationData.getlanguage(getActivity()));

                    Intent i = new Intent(getActivity(), ActivityGroupSelection.class);
                    i.putExtra("map", map);
                    startActivityForResult(i, MESSAGE_SEND_SUCCESS);
                } else {
                    ApplicationData.calldialog(getActivity(), "", getString(R.string.select_msg), getString(R.string.str_ok), "", null, 1);
                }

/*
                ApplicationData.calldialog(getActivity(), "", getString(R.string.alert_emeregency), getString(R.string.str_yes),
                        getString(R.string.str_no), new ApplicationData.DialogListener() {
                            @Override
                            public void diaBtnClick(int diaID, int btnIndex) {
                                if (btnIndex == 2) {
                                    String mess_age = adapter.sendmessage(getActivity());
                                    if (mess_age.length() > 0) {
                                        try {
                                            mess_age = URLEncoder.encode(mess_age, "UTF-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }

                                        HashMap<String, Object> map = new HashMap<String, Object>();
                                        map.put("sender_id", teacher_id);
                                        map.put("message", mess_age);
                                        map.put("school_id", school_id);
                                        map.put("language", ApplicationData.getlanguage(getActivity()));
                                        if (!GlobalConstrants.isWifiConnected(getActivity())) {
                                            return;
                                        } else {
                                            ETechAsyncTask task = new ETechAsyncTask(getActivity(), new AsyncTaskCompleteListener<String>() {
                                                @Override
                                                public void onTaskComplete(int statusCode, String result, String webserviceCb, Object tag) {
                                                    try {
                                                        if (statusCode == ETechAsyncTask.COMPLETED) {
                                                            JSONObject jObject = new JSONObject(result);

                                                            try {
                                                                if (webserviceCb.equalsIgnoreCase(ConstantApi.SEND_CUSTOM_MESSAGE)) {
                                                                    String flag = jObject.getString("flag");


                                                                    if (flag.equalsIgnoreCase("1")) {
                                                                        ApplicationData.showMessage(getActivity(), getString(R.string.app_name), getString(R.string.successmsg), getString(R.string.str_ok));
                                                                        adapter.updatedata(getActivity());
                                                                    } else {
                                                                        String message = jObject.getString("msg");
                                                                        ApplicationData.showMessage(getActivity(), getString(R.string.app_name), message, getString(R.string.str_ok));
                                                                    }
                                                                } else {
                                                                    ApplicationData.showToast(getActivity(), R.string.server_error, false);
                                                                }

                                                                //setAdapter();
                                                            } catch (Exception e) {
                                                                Log.e("FragmentSystem", "onTaskComplete() " + e, e);
                                                            }
                                                        } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
                                                            try {
                                                                ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, ConstantApi.SEND_CUSTOM_MESSAGE, map);
                                            task.execute(ApplicationData.main_url + ConstantApi.SEND_CUSTOM_MESSAGE + ".php?");
                                        }
                                    } else {
                                        ApplicationData.showToast(getActivity(), getResources().getString(R.string.select_msg), true);
                                    }
                                }
                            }
                        }, 1);
*/

            }
        });
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EmergencyAlertActivity) getActivity()).removeactivity();

            }
        });

        return view;
    }

    private void definesharedpreference() {
        SharedPreferences sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);
        teacher_id = sharedpref.getString("teacher_id", "");
        teacher_name = sharedpref.getString("teacher_name", "");
        teacher_email = sharedpref.getString("email", "");
        school_id = sharedpref.getString("school_id", "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MESSAGE_SEND_SUCCESS) {
                adapter.updatedata(getActivity());
            }
        }
    }
}
