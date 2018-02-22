package com.cloudstream.cslink.teacher;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.adapter.teacher.Childbeans;
import com.adapter.teacher.EmergencyMessageAdapter;
import com.cloudstream.cslink.R;
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
 * Created by etech on 15/7/16.
 */
public class FragmentEmegencyMessage extends Fragment {

    private ListView lst_emergency;
    private LinearLayout lin_semes;
    private String teacher_id, teacher_name, teacher_email,school_id;
    private ArrayList<Childbeans> listmessage;
    private EmergencyMessageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.adres_fragment_system,container,false);

        initcomponent(view);
        definesharedpreference();
        callapimessage();

        return view;
    }



    private void initcomponent(View view) {
        lst_emergency=(ListView)view.findViewById(R.id.lst_emergency);
        lin_semes=(LinearLayout)view.findViewById(R.id.lin_semes);

        lin_semes.setVisibility(View.GONE);

    }

    private void definesharedpreference() {
        SharedPreferences sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);
        teacher_id = sharedpref.getString("teacher_id", "");
        teacher_name = sharedpref.getString("teacher_name", "");
        teacher_email = sharedpref.getString("email", "");
        school_id = sharedpref.getString("school_id", "");
    }

    private void callapimessage() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", teacher_id);
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
                                if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_EMERGENCY_MESSAGE)) {
                                    String flag = jObject.getString("flag");

                                    if (flag.equalsIgnoreCase("1")) {
                                        listmessage= new ArrayList<Childbeans>();
                                        JSONArray jarray = jObject.getJSONArray("details");
                                        for(int i=0;i<jarray.length();i++)
                                        {
                                            JSONObject jdata = jarray.getJSONObject(i);
                                            Childbeans bean = new Childbeans();
                                            bean.pri_message_id=jdata.has("pri_message_id")?jdata.getString("pri_message_id"):"";
                                            bean.message_subject=jdata.has("message_subject")?jdata.getString("message_subject"):"";
                                            bean.message_desc=jdata.has("message_desc")?jdata.getString("message_desc"):"";
                                            bean.sendbyid=jdata.has("sendbyid")?jdata.getString("sendbyid") : "";
                                            bean.class_id=jdata.has("class_id")?jdata.getString("class_id"):"";
                                            bean.created_at=jdata.has("created_at")?jdata.getString("created_at"):"";
                                            bean.isab=jdata.has("isab")?jdata.getString("isab"):"";

                                            listmessage.add(bean);
                                        }

                                        if(listmessage!=null)
                                        {
                                            if(listmessage.size()>0)
                                            {
                                                setadapter();
                                            }
                                        }
                                    } else {
                                        String message = jObject.getString("msg");
                                        ApplicationData.showToast(getActivity(), message, false);
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
            }, ConstantApi.GET_EMERGENCY_MESSAGE, map);
            task.execute(ApplicationData.main_url + ConstantApi.GET_EMERGENCY_MESSAGE + ".php?");
        }
    }

    private void setadapter() {
        adapter = new EmergencyMessageAdapter(getActivity(),listmessage);
        lst_emergency.setAdapter(adapter);
    }


    public FragmentEmegencyMessage newInstance(boolean b) {
        return null;
    }
}
