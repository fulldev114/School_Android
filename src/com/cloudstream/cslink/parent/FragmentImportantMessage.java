package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.adapter.parent.AbsentListAdapter;
import com.adapter.parent.Childbeans;
import com.adapter.parent.GroupMessageAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.xmpp.parent.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentImportantMessage extends Fragment {

    private ListView _teacherlist;
    private MainProgress pDialog;
    AbsentListAdapter adapter;
    private boolean isAbsent = false;
    private Fragment mActivity;

    public FragmentImportantMessage() {
    }

    ArrayList<Childbeans> messageList = null;
    String getmessage_url, _parent_id, child_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = this;
        View rootView = inflater.inflate(R.layout.message_fragment, container, false);

        SharedPreferences sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);

        _parent_id = sharedpref.getString("parent_id", "");

        if (isAbsent) {
            ((MainActivity) getActivity()).title.setText(getString(R.string.title_absent_notice));
            ((MainActivity) getActivity()).title.setTextColor(Color.parseColor("#F6BB04"));
        }

        child_id = sharedpref.getString("childid", "");

        _teacherlist = (ListView) rootView.findViewById(R.id.listView_message);
        _teacherlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                try {
                    if (isAbsent) {
                        ApplicationData.showMessage(getActivity(), getResources().getString(R.string.title_absent_notice), messageList.get(position).message_desc, getResources().getString(R.string.str_ok));
                    } else {
                        ApplicationData.showMessage(getActivity(), getResources().getString(R.string.title_important_notice), messageList.get(position).message_desc, getResources().getString(R.string.str_ok));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        getmessage_url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.GET_IMPORTANT_MESSAGE) + "userid=" + child_id + "&isab=" + (isAbsent ? "1" : "0");

        messageList = new ArrayList<Childbeans>();
        adapter = new AbsentListAdapter(getActivity(), messageList);
        _teacherlist.setAdapter(adapter);

        getmessage(getmessage_url);

        return rootView;
    }

    private void getmessage(String login_url2) {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(getActivity())) {
            return;
        }
        if (pDialog == null)
            pDialog = new MainProgress(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, login_url2, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Auto-generated method stub
                        try {
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                JSONObject All_Messages = response.getJSONObject("All Messages");
                                JSONArray received = All_Messages.getJSONArray("received");
                                if (messageList != null)
                                    messageList.clear();
                                messageList = new ArrayList<Childbeans>();

                                for (int i = 0; received.length() > i; i++) {
                                    JSONObject c = received.getJSONObject(i);
                                    Childbeans childbeans = new Childbeans();
                                    childbeans.message_id = c.getString("message_id");
                                    childbeans.sendername = c.getString("fromname");
//									childbeans.subject_name = c.getString("message_subject");
                                    childbeans.subject_name = c.getString("mm.message_desc");
                                    childbeans.message_desc = c.getString("mm.message_desc");
                                    childbeans.senderimage = c.getString("image");
                                    childbeans.created_at = c.getString("created_at");//.substring(0,10);
                                    messageList.add(childbeans);
                                }
                                setlist_toadapter();
                                pDialog.dismiss();
                            } else {
                                if (response.has("msg")) {
                                    String msg = response.getString("flag");
                                    ApplicationData.showToast(getActivity(), msg, false);
                                }
                                pDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_operation_error), false);
            }
        });
        queue.add(jsObjRequest);
    }

    protected void setlist_toadapter() {
        // TODO Auto-generated method stub
        /*adapter = new TeacherListAdapter(getActivity(), messageList);
        _teacherlist.setAdapter(adapter);*/
        adapter.updateListAdapter(messageList);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void setAbsent(boolean isAb) {
        this.isAbsent = isAb;
    }

    public void setadapter(String alert, String message, String type, int kidid, int from_id, String kidname,
                           String teachername, String teacherimage) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createdate = sdf.format(Calendar.getInstance().getTime());

        Childbeans childbeans = new Childbeans();
        childbeans.message_id = "";
        childbeans.sendername = teachername;
        childbeans.subject_name = message;
        childbeans.message_desc = message;
        childbeans.senderimage = teacherimage;
        childbeans.created_at = createdate;//.substring(0,10);

        messageList.add(0, childbeans);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.updateListAdapter(messageList);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationData.setAbsentactivity(mActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationData.setAbsentactivity(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationData.setAbsentactivity(null);
    }
}
