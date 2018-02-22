package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.parent.Childbeans;
import com.adapter.parent.ChildrenListAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.langsetting.apps.Background_work;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;
import com.xmpp.parent.Constant;
import com.xmpp.parent.XMPPMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SelectChildActivity extends Activity implements AsyncTaskCompleteListener<String> {

    ImageView _back;
    ArrayList<Childbeans> arrayList = null;
    String[] videoNames;
    String child_array, Response = "";
    String parent_id, registrationid, language, parent_name;
    public boolean b = false;
    ListView list;
    TextView btnAddChild;
    ChildrenListAdapter cAdapter;
    Activity mActivity = null;
    String noti_type = "";
    int noti_kidid = 0, noti_from_id = 0;
    private TextView txt_title;
    MainProgress pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selectchild_activity);


        SharedPreferences myPrefs = getSharedPreferences("absentapp", Context.MODE_PRIVATE);
        SharedPreferences myPrefs_emer = getSharedPreferences(Constant.EMERGENCY_FILENAME, Context.MODE_PRIVATE);

        if (myPrefs.getString("childname", "") != null && myPrefs.getString("childname", "").length() > 0) {

            Intent in = new Intent(SelectChildActivity.this, MainActivity.class);
            in.putExtra("noti_kidid", noti_kidid);
            in.putExtra("noti_type", noti_type);
            in.putExtra("noti_from_id", noti_from_id);
            int frag = 0;
            if (noti_type.equals("abi"))
                frag = 4;
            else if (noti_type.equals("abn"))
                frag = 3;
            else if (noti_type.equals("ch"))
                frag = 2;

            in.putExtra("fragment", frag);

            startActivity(in);
            finish();

        }

        Intent intent = getIntent();
        if (intent.hasExtra("noti_type")) {        // push noti getted.
            noti_type = intent.getStringExtra("noti_type");
        }
        if (intent.hasExtra("noti_kidid")) {
            noti_kidid = intent.getIntExtra("noti_kidid", 0);
        }
        if (intent.hasExtra("noti_from_id")) {
            noti_from_id = intent.getIntExtra("noti_from_id", 0);
        }

        mActivity = this;


        _back = (ImageView) findViewById(R.id.imgback);
        _back.setVisibility(View.INVISIBLE);
        _back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences sharedpref = getSharedPreferences("absentapp", 0);

        child_array = sharedpref.getString("child_array", "");
        parent_id = sharedpref.getString("parent_id", "");
        parent_name = sharedpref.getString("parent_name", "");

        b = true;

        registrationid = sharedpref.getString("registrationId", "");

        list = (ListView) findViewById(R.id.list_children);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (arrayList.get(position).jid != null && arrayList.get(position).jid.length() > 0) {


                    if (arrayList.get(position).jid.substring(0, arrayList.get(position).jid.lastIndexOf("@")).length() > 0) {

                        new chatclass(position).execute();
                    }
                }
            }
        });


        btnAddChild = (TextView) findViewById(R.id.btnAddChild);
        btnAddChild.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SelectChildActivity.this, AddChildActivity.class);
                startActivity(in);
            }
        });

        txt_title = (TextView) findViewById(R.id.textView1);
        txt_title.setText(getString(R.string.str_select_student));
        arrayList = new ArrayList<Childbeans>();


//		new SendRegistrationId().execute();
    }

    private void goMainActivity(int position) {
        if (arrayList.get(position).name.length() == 0) {
            try {
                ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_alert), getResources().getString(R.string.msg_not_active_child), getResources().getString(R.string.str_ok));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();

        editor.putString("childname", arrayList.get(position).child_name);
        editor.putString("childid", arrayList.get(position).user_id);
        editor.putString("childArray", child_array);
        editor.putString("school_id", arrayList.get(position).school_id);
        editor.putString("school_class_id", arrayList.get(position).school_class_id);
        editor.putString("image", arrayList.get(position).child_image);
        editor.putString("jid", arrayList.get(position).jid);
        editor.putString("jid_pwd", arrayList.get(position).jid_pwd);
        editor.putString("schoolname", arrayList.get(position).school_name);
        editor.commit();

        Intent in = new Intent(SelectChildActivity.this, MainActivity.class);
        in.putExtra("noti_kidid", noti_kidid);
        in.putExtra("noti_type", noti_type);
        in.putExtra("noti_from_id", noti_from_id);
        int frag = 0;
        if (noti_type.equals("abi"))
            frag = 4;
        else if (noti_type.equals("abn"))
            frag = 3;
        else if (noti_type.equals("ch"))
            frag = 2;

        in.putExtra("fragment", frag);

        startActivity(in);
        finish();
    }

    private void loadChildrenList() {

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("parent_id", parent_id);
            params.put("os", "android");
            ETechAsyncTask task = new ETechAsyncTask(SelectChildActivity.this, this, ConstantApi.GET_CHILD_BY_PHONE, params);
            task.execute(ApplicationData.main_url + ConstantApi.GET_CHILD_BY_PHONE + ".php?");

        } catch (Exception e) {
            e.printStackTrace();
        }
       /* String url2 = ApplicationData.main_url
                + "get_childs_by_phone.php?parent_id=" + parent_id;

        Log.e("url:", url2);
        if (pDialog == null)
            pDialog = new MainProgress(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.dismiss();
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url2, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String flag = response.getString("flag");
                            Log.e("response : ", response.toString());

                            if (Integer.parseInt(flag) == 1) {

                                if (arrayList != null)
                                    arrayList.clear();
                                arrayList = new ArrayList<Childbeans>();
                                int noti_position = 0;

                                try {
                                    JSONObject job = response.getJSONObject("All Childs");
                                    JSONArray jsonObj = job.getJSONArray("childs");
                                    Log.e("jsonObj size :: ",jsonObj.length()+"");
                                    child_array = jsonObj.toString();
                                    int adtuallength = jsonObj.length();
                                    videoNames = new String[adtuallength];
                                    for (int i = 0; i < jsonObj.length(); i++) {
                                        Childbeans childbeans = new Childbeans();
                                        JSONObject c = jsonObj.getJSONObject(i);
                                        childbeans.child_image = c.getString("child_image");
                                        childbeans.child_name = c.getString("child_name");
                                        videoNames[i] = new String(c.getString("child_name"));
                                        childbeans.school_name = c.getString("school_name");
                                        childbeans.child_moblie = c.getString("child_moblie");
                                        childbeans.school_class_id = c.getString("school_class_id");
                                        childbeans.user_id = c.getString("user_id");
                                        if (String.valueOf(noti_kidid).equals(childbeans.user_id))
                                            noti_position = i;
                                        childbeans.child_gender = c.getString("child_gender");
                                        childbeans.child_age = c.getString("child_age");
                                        childbeans.school_id = c.getString("school_id");
                                        childbeans.name = c.getString("parentname");
                                        childbeans.badge = c.getInt("badge") + c.getInt("abi_badge") + c.getInt("abn_badge");
                                        arrayList.add(childbeans);
                                    }
                                    Log.e("array size :: ",arrayList.size()+"");
                                    // looping through All Contacts
                                } catch (JSONException e) {
                                    System.out.println("Andy Error " + e);
                                    e.printStackTrace();
                                }

                                cAdapter = new ChildrenListAdapter(SelectChildActivity.this, arrayList, 0);
                                list.setAdapter(cAdapter);
                                //cAdapter.updateListAdapter(arrayList);

                                if (!noti_type.equals("")) {
                                    goMainActivity(noti_position);
                                }
                                pDialog.dismiss();
                            } else {
                                pDialog.dismiss();

                            }
                            // finish();


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        } catch (
                                Exception e
                                )

                        {
                            e.printStackTrace();
                            ApplicationData.showToast(SelectChildActivity.this, getResources().getString(R.string.msg_operation_error), true);
                        }
                    }
                }

                , new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                ApplicationData.showToast(SelectChildActivity.this, getResources().getString(R.string.msg_operation_error), true);
            }
        }

        );
        queue.add(jsObjRequest);*/


    }

    public void back() {
        finish();
    }

    // ....on click on forgot password..//
    public void conti(View v) {

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
       /* Background_work.set_background_time();
        b = false;*/
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        loadChildrenList();
        //  if (myPrefs.getString("childname", "") == null && myPrefs.getString("childname", "").length() == 0) {

        //}
       /* if (b) {
        } else {
            Background_work.set_front_time();
            if (Background_work.check_layout_pincode()) {
                Intent i = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(i);
            }
        }*/
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTaskComplete(int statusCode, String responseMsg, String webserviceCb, Object tag) {
        if (statusCode == ETechAsyncTask.COMPLETED) {
            try {
                JSONObject jObject = new JSONObject(responseMsg);
                String resMsg = jObject
                        .getString(ConstantApi.REQ_RESPONSE_MSG);

                if (webserviceCb.equalsIgnoreCase(ConstantApi.GET_CHILD_BY_PHONE)) {
                    String flag = jObject.getString("flag");

                    if (Integer.parseInt(flag) == 1) {

                        if (arrayList != null)
                            arrayList.clear();
                        arrayList = new ArrayList<Childbeans>();
                        int noti_position = 0;

                        try {
                            JSONObject job = jObject.getJSONObject("All Childs");
                            JSONArray jsonObj = job.getJSONArray("childs");
                            child_array = jsonObj.toString();
                            int adtuallength = jsonObj.length();
                            videoNames = new String[adtuallength];
                            for (int i = 0; i < jsonObj.length(); i++) {
                                Childbeans childbeans = new Childbeans();
                                JSONObject c = jsonObj.getJSONObject(i);
                                childbeans.child_image = c.getString("child_image");
                                childbeans.child_name = c.getString("child_name");
                                videoNames[i] = new String(c.getString("child_name"));
                                childbeans.school_name = c.getString("school_name");
                                childbeans.child_moblie = c.getString("child_moblie");
                                childbeans.school_class_id = c.getString("school_class_id");
                                childbeans.user_id = c.getString("user_id");

                                if (String.valueOf(noti_kidid).equals(childbeans.user_id))
                                    noti_position = i;
                                childbeans.child_gender = c.getString("child_gender");
                                childbeans.child_age = c.getString("child_age");
                                childbeans.school_id = c.getString("school_id");
                                childbeans.name = c.getString("parentname");
                                childbeans.jid = c.getString("jid");
                                childbeans.jid_pwd = c.getString("key");
                                childbeans.badge = c.getInt("badge") + c.getInt("abi_badge") + c.getInt("abn_badge");
                                arrayList.add(childbeans);

                            }

                            SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPrefs.edit();
                            editor.putString("child_array", child_array);
                            editor.commit();

                            // looping through All Contacts
                        } catch (JSONException e) {
                            System.out.println("Andy Error " + e);
                            e.printStackTrace();
                        }

                        cAdapter = new ChildrenListAdapter(SelectChildActivity.this, arrayList, 0);
                        list.setAdapter(cAdapter);
                        cAdapter.notifyDataSetChanged();
                        //cAdapter.updateListAdapter(arrayList);

                        if (!noti_type.equals("")) {
                            goMainActivity(noti_position);
                        }
                    } else {
                        if (jObject.has("msg")) {
                            String msg = jObject.getString("msg");
                            ApplicationData.showToast(SelectChildActivity.this, msg, false);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ApplicationData.showToast(SelectChildActivity.this, getResources().getString(R.string.msg_operation_error), true);
            }
        } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
            try {
                ApplicationData.showToast(SelectChildActivity.this, R.string.msg_operation_error, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private class chatclass extends AsyncTask<Void, Void, Void> {
        int pos;
        SharedPreferences myPrefs = getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        MainProgress pdialog = null;

        public chatclass(int position) {
            this.pos = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pdialog == null) {
                pdialog = new MainProgress(SelectChildActivity.this);
                pdialog.setCancelable(false);
                pdialog.setMessage(getResources().getString(R.string.str_wait));
                pdialog.dismiss();
                pdialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (myPrefs.getString("jid", "") != null && myPrefs.getString("jid", "").length() > 0) {
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("offlinetag", "0");
                editor.commit();
                try {
                    //     XMPPMethod.connect(SelectChildActivity.this, arrayList.get(pos).jid, "5222", arrayList.get(pos).jid_pwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pdialog != null) {
                pdialog.dismiss();
            }

            goMainActivity(pos);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _back = null;
        arrayList = null;
        videoNames = null;
        child_array = null;
        Response = null;
        parent_id = null;
        registrationid = null;
        language = null;
        parent_name = null;
        list = null;
        btnAddChild = null;
        cAdapter = null;
        mActivity = null;
        noti_type = null;
        txt_title = null;
        pDialog = null;

        System.gc();
    }
}
