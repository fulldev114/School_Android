package com.cloudstream.cslink.parent;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.parent.Childbeans;
import com.adapter.parent.ParentListAdapter;
import com.adapter.parent.TeacherListAdapter;
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

import java.util.ArrayList;

public class FragmentOtherParent extends Fragment {

    private ListView _teacherlist;
    private MainProgress pDialog;
    ParentListAdapter adapter;
    private Dialog dlg;

    public FragmentOtherParent() {

    }

    ArrayList<Childbeans> parentList = null;
    String url, _parent_id, child_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.message_fragment, container,false);

        SharedPreferences sharedpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, 0);

        _parent_id = sharedpref.getString("parent_id", "");
        child_id = sharedpref.getString("childid", "");
        _teacherlist = (ListView) rootView.findViewById(R.id.listView_message);
        _teacherlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                // TODO Auto-generated method stub
                try {
                    if (dlg != null && dlg.isShowing())
                        dlg.dismiss();
                    dlg = new Dialog(getActivity());
                    dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dlg.setContentView(R.layout.msgdialog);
                    dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                    TextView tv_title = (TextView) dlg.findViewById(R.id.msgtitle);
                    final TextView tv_content = (TextView) dlg.findViewById(R.id.msgcontent);

                    tv_title.setText(parentList.get(position).sendername);
                    tv_content.setText(getResources().getString(R.string.number) + " " + parentList.get(position).child_moblie);

                    Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
                    Button dlg_btn_cle = (Button) dlg.findViewById(R.id.btn_cancel);

                    dlg_btn_ok.setText(getString(R.string.call));
                    dlg_btn_cle.setText(getString(R.string.cancel));
                    dlg_btn_cle.setVisibility(View.VISIBLE);
                    dlg_btn_ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" +tv_content.getText().toString()));
                                startActivity(intent);
                                dlg.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dlg_btn_cle.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                dlg.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dlg.setCanceledOnTouchOutside(false);
                    dlg.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.GET_PARENT_LIST) + "userid=" + child_id;

        parentList = new ArrayList<Childbeans>();
        adapter = new ParentListAdapter(getActivity(), parentList, false);
        _teacherlist.setAdapter(adapter);

        getParentList(url);

        return rootView;
    }

    private void getParentList(String login_url2) {
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
                                JSONObject All_Messages = response.getJSONObject("All Childs");
                                JSONArray parents = All_Messages.getJSONArray("parents");
                                if (parentList != null)
                                    parentList.clear();
                                parentList = new ArrayList<Childbeans>();

                                for (int i = 0; parents.length() > i; i++) {
                                    JSONObject c = parents.getJSONObject(i);
                                    Childbeans childbeans = new Childbeans();
                                    childbeans.sendername = c.getString("parentname");
                                    childbeans.child_name = " (" + c.getString("childname") + ")";
                                    childbeans.subject_name = c.getString("mobile");
                                    childbeans.child_moblie = c.getString("mobile");
                                    childbeans.senderimage = c.getString("image");
                                    parentList.add(childbeans);
                                }
                                setlist_toadapter();
                                pDialog.dismiss();
                            } else {
                                if(response.has("msg")) {
                                    String msg = response.getString("msg");
                                    ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_parent_not_exist), true);
                                }
                                pDialog.dismiss();
                            }
                        } catch (Exception e) {
                            pDialog.dismiss();
                            ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_operation_error), true);
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
        /*adapter = new TeacherListAdapter(getActivity(), parentList);
		_teacherlist.setAdapter(adapter);*/
        adapter.updateListAdapter(parentList);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
