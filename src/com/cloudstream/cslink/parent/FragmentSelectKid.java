package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.common.utils.GlobalConstrants;
import com.xmpp.parent.Constant;
import com.xmpp.parent.XMPPMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentSelectKid extends Fragment {

    ArrayList<Childbeans> arrayList = null;

    String[] videoNames;

    String child_array, Response = "";

    String parent_id, registrationid, language;

    ListView list;
    ChildrenListAdapter cAdapter;

    private MainProgress pDialog;

    Activity mActivity;
    private TextView btnAddChild;

    public FragmentSelectKid() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // setActionBArStyle();
        View rootView = inflater.inflate(R.layout.selectkid_fragment,
                container, false);

        SharedPreferences sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);

        child_array = sharedpref.getString("child_array", "");
        parent_id = sharedpref.getString("parent_id", "");

        mActivity = getActivity();
        registrationid = sharedpref.getString("registrationId", "");

        list = (ListView) rootView.findViewById(R.id.list_children);
        btnAddChild = (TextView) rootView.findViewById(R.id.btnAddChild);
        arrayList = new ArrayList<Childbeans>();

        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), AddChildActivity.class);
                startActivity(in);
            }
        });

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                if (arrayList.get(position).name.length() == 0) {
                    try {
                        ApplicationData.showMessage(mActivity, getResources().getString(R.string.str_alert), getResources().getString(R.string.msg_not_active_child), getResources().getString(R.string.str_ok));
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                SharedPreferences myPrefs = mActivity.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("childname", arrayList.get(position).child_name);
                editor.putString("childid", arrayList.get(position).user_id);
                editor.putString("childArray", child_array);
                editor.putString("school_id", arrayList.get(position).school_id);
                editor.putString("school_class_id", arrayList.get(position).school_class_id);
                editor.putString("image", arrayList.get(position).child_image);
               /* editor.putString("jid", arrayList.get(position).jid);
                editor.putString("jid_pwd", arrayList.get(position).jid_pwd);*/
                editor.commit();

                if (arrayList.get(position).jid != null && arrayList.get(position).jid.length() > 0) {
                    new chatclass(getActivity(), position).execute();
                }

            }
        });


        cAdapter = new ChildrenListAdapter(mActivity, arrayList, 1);
        list.setAdapter(cAdapter);

//		loadChildrenList();
 //       getChildList();
        return rootView;
    }

    private void getChildList() {
        // TODO Auto-generated method stub
        if (!GlobalConstrants.isWifiConnected(mActivity)) {
            return;
        }

        if (pDialog == null)
            pDialog = new MainProgress(mActivity);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.dismiss();
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(mActivity);

        String url2 = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.GET_CHILD_BY_PHONE)
                + "parent_id=" + parent_id;


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url2, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pDialog.dismiss();
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                if (response.isNull("All Childs")) {
                                    child_array = "";
                                } else {
                                    JSONObject allchilds = response.getJSONObject("All Childs");
                                    JSONArray childs = allchilds.getJSONArray("childs");
                                    child_array = childs.toString();
                                }
                                SharedPreferences myPrefs = mActivity.getSharedPreferences("absentapp", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = myPrefs.edit();
                                editor.putString("child_array", child_array);
                                editor.commit();
                            } else {
                                if (response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(getActivity(), msg, false);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        loadChildrenList();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                pDialog.dismiss();

                Toast.makeText(mActivity, getResources().getString(R.string.msg_operation_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    private void loadChildrenList() {
        if (arrayList != null)
            arrayList.clear();
        arrayList = new ArrayList<Childbeans>();
        if (child_array.equalsIgnoreCase("")) {

        } else {
            try {
                JSONArray jsonObj = new JSONArray(child_array);
                int adtuallength = jsonObj.length();
                videoNames = new String[adtuallength];
                for (int i = 0; i < adtuallength; i++) {
                    JSONObject c = jsonObj.getJSONObject(i);
                   /* if (c.getString("parentname").length() == 0)
                        continue;*/
                    Childbeans childbeans = new Childbeans();
                    childbeans.child_image = c.getString("child_image");
                    childbeans.child_name = c.getString("child_name");
                    videoNames[i] = new String(c.getString("child_name"));
                    childbeans.name=c.getString("parentname");
                    childbeans.school_name = c.getString("school_name");
                    childbeans.child_moblie = c.getString("child_moblie");
                    childbeans.school_class_id = c.getString("school_class_id");
                    childbeans.user_id = c.getString("user_id");
                    childbeans.child_gender = c.getString("child_gender");
                    childbeans.child_age = c.getString("child_age");
                    childbeans.school_id = c.getString("school_id");
                    childbeans.jid = c.getString("jid");
                    childbeans.jid_pwd = c.getString("key");
                    childbeans.badge=0;
                  //  childbeans.badge = c.getInt("badge") + c.getInt("abi_badge") + c.getInt("abn_badge");
                  /*  DatabaseHelper db = DatabaseHelper.getDBAdapterInstance(getActivity());
                    String query = "select * from badge_table where KidId="+'"'+childbeans.user_id+'"';
                    HashMap<String, String> messagelist = db.selSingleRecordFromDB(query, null);
                    if(messagelist!=null && messagelist.size()>0)
                    {
                        childbeans.badge=Integer.parseInt(messagelist.get("Badge"));
                    }*/
                    arrayList.add(childbeans);
                }
                // looping through All Contacts
            } catch (JSONException e) {
                System.out.println("Andy Error " + e);
                e.printStackTrace();
            }
        }

		/*cAdapter = new ChildrenListAdapter(mActivity, arrayList);
        list.setAdapter(cAdapter);*/
        cAdapter.updateListAdapter(arrayList);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private class chatclass extends AsyncTask<Void, Void, Void> {
        private Context context;
        private int pos = 0;
        int position;
        SharedPreferences myPrefs = getActivity().getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        private MainProgress pdialog;

        public chatclass(Context context, int pos) {
            this.context = context;
            this.pos = pos;
        }

        /* public chatclass(int position) {
             this.position=position;

         }*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new MainProgress(mActivity);
            pdialog.setMessage(context.getString(R.string.str_wait));
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (myPrefs.getString("jid", "") != null && myPrefs.getString("jid", "").length() > 0) {
                try {
                    XMPPMethod.disconnection(mActivity, myPrefs.getString("jid", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            editor.putString("jid", arrayList.get(pos).jid);
            editor.putString("jid_pwd", arrayList.get(pos).jid_pwd);
            editor.commit();
            try {
               // mActivity.startService(new Intent(mActivity, MessageService.class));
                if (arrayList.get(pos).jid.substring(0, arrayList.get(pos).jid.lastIndexOf("@")).length() > 0) {
                    XMPPMethod.connect(mActivity, arrayList.get(pos).jid, "5222", arrayList.get(pos).jid_pwd);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (pdialog != null && pdialog.isShowing()) {
                pdialog.dismiss();
            }

            Intent in = new Intent(mActivity, MainActivity.class);
            in.putExtra("fragment", 0);
            startActivity(in);
            mActivity.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getChildList();
    }
}
