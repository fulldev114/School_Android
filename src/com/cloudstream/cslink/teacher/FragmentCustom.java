package com.cloudstream.cslink.teacher;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudstream.cslink.R;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.xmpp.teacher.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by etech on 15/7/16.
 */
public class FragmentCustom extends Fragment {

    private ListView lst_emergency;
    private EditText edit_msg;
    private LinearLayout send;
    private TextView txt_cancel;
    private String mess_age;
    private String teacher_id, teacher_name, teacher_email, school_id;
    private static final int MESSAGE_SEND_SUCCESS = 101;
    private static final int RESULT_OK = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adres_fragment_custom, container, false);

        edit_msg = (EditText) view.findViewById(R.id.edit_msg);
        send = (LinearLayout) view.findViewById(R.id.send);
        txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);

        SharedPreferences sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);
        teacher_id = sharedpref.getString("teacher_id", "");
        teacher_name = sharedpref.getString("teacher_name", "");
        teacher_email = sharedpref.getString("email", "");
        school_id = sharedpref.getString("school_id", "");

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (edit_msg.getText().length() == 0) {
                    ApplicationData.calldialog(getActivity(), "",getActivity().getString(R.string.error_msg), getActivity().getString(R.string.str_ok),
                            null, null, 1);
                    return;
                } else {
                    teacher_name = teacher_name.replace(" ", "%20");

                    try {
                        mess_age = URLEncoder.encode(edit_msg.getText().toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("sender_id", teacher_id);
                    map.put("message", mess_age);
                    map.put("school_id", school_id);

                    Intent i = new Intent(getActivity(), ActivityGroupSelection.class);
                    i.putExtra("map", map);
                    startActivityForResult(i, MESSAGE_SEND_SUCCESS);

/*
                ApplicationData.calldialog(getActivity(), "", getString(R.string.alert_emeregency), getString(R.string.str_yes),
                        getString(R.string.str_no), new ApplicationData.DialogListener() {
                            @Override
                            public void diaBtnClick(int diaID, int btnIndex) {
                                if (btnIndex == 2) {
                                    teacher_name = teacher_name.replace(" ", "%20");

                                    try {
                                        mess_age = URLEncoder.encode(edit_msg.getText().toString(), "UTF-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("sender_id", teacher_id);
                                    map.put("message", mess_age);
                                    map.put("school_id", school_id);
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
                                                                    edit_msg.setText("");
                                                                    ApplicationData.showMessage(getActivity(), getString(R.string.app_name), getString(R.string.successmsg), getString(R.string.str_ok));
                                                                } else {
                                                                    String message = jObject.getString("msg");
                                                                    ApplicationData.showToast(getActivity(), message, false);
                                                                }
                                                            } else {
                                                                ApplicationData.showToast(getActivity(), R.string.server_error, false);
                                                            }

                                                            //setAdapter();
                                                        } catch (Exception e) {
                                                            Log.e("ReportCardActivity", "onTaskComplete() " + e, e);
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
                                }
                            }
                        }, 1);
*/

                }
            }
        });


        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_msg.setText("");
                ((EmergencyAlertActivity) getActivity()).removeactivity();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MESSAGE_SEND_SUCCESS) {
                edit_msg.setText("");
            }
        }
    }

}
