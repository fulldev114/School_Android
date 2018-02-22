package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.Bean.RegisterBean;
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.common.utils.GlobalConstrants;
import com.xmpp.parent.Constant;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by etech on 1/6/16.
 */
public class RegisterParent2 extends Fragment implements View.OnClickListener {
    private static RegisterBean data;
    private static String parent1 = "", phono1 = "", email1 = "", pincode = "", confirm_pincode = "";
    private Activity mActivity;
    private EditText edtParent2;
    private EditText edtPhone2;
    private EditText edtEmail2;
    private RelativeLayout rel_register;
    private static int SUCCESS_REGISTER = 1;
    private static int FAILED_REGISTER = 2;
    private static int FAILED_DUPLICATED = 3;
    private static int FAILED_SEND_SMS = 4;
    private static int SUCCESS_REGISTER_NOPARENT2 = 5;
    MainProgress pDialog;
    private String url;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mActivity = getActivity();
        View v = inflater.inflate(R.layout.new_register_two, container, false);

        ApplicationData.currentpos = false;

        edtParent2 = (EditText) v.findViewById(R.id.edtParent2);
        edtPhone2 = (EditText) v.findViewById(R.id.edtPhone2);
        edtEmail2 = (EditText) v.findViewById(R.id.edtEmail2);

        rel_register = (RelativeLayout) v.findViewById(R.id.rel_register);
        rel_register.setOnClickListener(this);


        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getActivity().getCurrentFocus();
        if (view!=null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static RegisterParent2 newInstance(Bundle bn) {

        if (bn != null) {
            data = (RegisterBean) bn.getSerializable("data");
            if (data != null && data.getParent1_name() != null && data.getParent1_name().length() > 0) {
                parent1 = data.getParent1_name();
                phono1 = data.getParent1_phoneno();
                email1 = data.getEmail();
                pincode = data.getPincode();
                confirm_pincode = data.getConfirmpincode();
            }
        }
        return null;
    }

    public static RegisterParent2 newInstance(String s) {
        RegisterParent2 f = new RegisterParent2();
        Bundle b = new Bundle();
        b.putString("msg", s);

        f.setArguments(b);
        return f;
    }

    @Override
    public void onClick(View v) {

        if (parent1.equalsIgnoreCase("") || phono1.equalsIgnoreCase("") || pincode.equalsIgnoreCase("")) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_fill_require), true);
        } else if (edtPhone2.getText().toString().length() > 0 && !ApplicationData.isValidPhone(edtPhone2.getText().toString())) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalidparent2phone), true);
            edtPhone2.setFocusable(true);
        } else if (edtEmail2.getText().toString().length() > 0 && !ApplicationData.isValidEmail(edtEmail2.getText().toString())) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalid_email), true);
            edtEmail2.setFocusable(true);
        } else if (edtPhone2.getText().toString().length() == 8) {
            if (edtParent2.getText().toString().length() == 0) {
                ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_parent2_name), true);
                edtParent2.setFocusable(true);
            } else if(phono1.equalsIgnoreCase(edtParent2.getText().toString())){
                ApplicationData.showToast(mActivity, getResources().getString(R.string.diff_num), true);
            }
            else
            {
                register(true);
            }
        } else {
            showConfirmDialog();
        }
    }

    public void register(final boolean existParent2) {
        if (!GlobalConstrants.isWifiConnected(getActivity())) {
            return;
        }
        // TODO Auto-generated method stub
        if (pDialog == null)
            pDialog = new MainProgress(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.str_wait));
        pDialog.dismiss();
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String pName1 = "", pName2 = "";
        try {
            pName1 = URLEncoder.encode(data.getParent1_name(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            pName1 = "";
        }
        try {
            pName2 = URLEncoder.encode(edtParent2.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            pName2 = "";
        }
        url = ApplicationData.getlanguageAndApi(getActivity(), ConstantApi.NEW_REGISTER_PHONE)
                + "parent1=" + pName1
                + "&phone1=" + phono1
                + "&email1=" + email1
                + "&parent2=" + pName2
                + "&phone2=" + edtPhone2.getText().toString()
                + "&email2=" + edtEmail2.getText().toString()
                + "&pincode=" + pincode;



        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Auto-generated method stub
                        pDialog.dismiss();
                        try {
                            String flag = response.getString("flag");
                            if (Integer.parseInt(flag) == 1) {
                                if (response.has("Android")) {
                                    JSONObject jandroid = response.getJSONObject("Android");

                                    String forceUpdateApp = jandroid.getString("forceUpdateApp");
                                    String isVersionDifferent = jandroid.getString("isVersionDifferent");
                                    String MessageType = jandroid.getString("MessageType");
                                    String URL = jandroid.getString("URL");
                                    String skipbuttontitle = jandroid.getString("skipbuttontitle");
                                    String updatebuttontitle = jandroid.getString("updatebuttontitle");

                                    SharedPreferences shpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor =shpref.edit();
                                    editor.putString(Constant.ForceUpdate, forceUpdateApp);
                                    editor.putString(Constant.IsVersionDifferent, isVersionDifferent);
                                    editor.putString(Constant.Update_Message, MessageType);
                                    editor.putString(Constant.URL, URL);
                                    editor.putString(Constant.Skipbuttontitle, skipbuttontitle);
                                    editor.putString(Constant.Updatebuttontitle, updatebuttontitle);
                                    editor.commit();
                                }

                                if (existParent2)
                                    showMessage(SUCCESS_REGISTER);
                                else
                                    showMessage(SUCCESS_REGISTER_NOPARENT2);

                            } else {
                                String errcode = response.getString("errcode");
                                String msg = response.getString("msg");

                                ApplicationData.showMessage(getActivity(),"",msg,getString(R.string.str_ok));

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
                ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
            }
        });

        queue.add(jsObjRequest);
    }

    private void showMessage(final int res) throws Exception {
        final Dialog dlg = new Dialog(getActivity());
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.msgdialog);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView title = (TextView) dlg.findViewById(R.id.msgtitle);
        TextView content = (TextView) dlg.findViewById(R.id.msgcontent);
        if (res == SUCCESS_REGISTER_NOPARENT2) {
            title.setText(R.string.str_success);
            content.setText(getResources().getString(R.string.success_new_register_noparent2));
        } else if (res == SUCCESS_REGISTER) {
            title.setText(R.string.str_success);
            content.setText(getResources().getString(R.string.success_new_register));
        } /*else if (res == FAILED_DUPLICATED) {
            title.setText(R.string.str_title_error);
            content.setText(getResources().getString(R.string.failed_duplicated_phone));
        } else if (res == FAILED_SEND_SMS) {
            title.setText(R.string.str_title_error);
            content.setText(getResources().getString(R.string.failed_sms_send));
        } else if (res == FAILED_REGISTER) {
            title.setText(R.string.str_title_error);
            content.setText(getResources().getString(R.string.failed_new_register));
        }*/

        Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        dlg_btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    dlg.dismiss();
                    if (res == SUCCESS_REGISTER) {
                        ((RegisterMainActivity) getActivity()).finish();
                    } else if (res == SUCCESS_REGISTER_NOPARENT2) {
                        ((RegisterMainActivity) getActivity()).finish();
                        ApplicationData.showToast(mActivity, mActivity.getResources().getString(R.string.success_new_register), true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        SharedPreferences shpref = getActivity().getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        if ((shpref.getString(Constant.ForceUpdate,"").equalsIgnoreCase("Yes"))){
            Intent intent = new Intent(getActivity(), UpdateAvailableActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else if ((shpref.getString(Constant.IsVersionDifferent,"").equalsIgnoreCase("Yes"))){
                Intent intent = new Intent(getActivity(), UpdateAvailableActivity.class);
                startActivity(intent);
            }
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
    }

    public void showConfirmDialog() {
        final Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {

                    final Dialog dlg = new Dialog(mActivity);
                    dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dlg.setContentView(R.layout.msgdialog);
                    dlg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    TextView tv_title = (TextView) dlg.findViewById(R.id.msgtitle);
                    TextView tv_content = (TextView) dlg.findViewById(R.id.msgcontent);

                    tv_title.setText(mActivity.getResources().getString(R.string.str_confirm));
                    tv_content.setText(mActivity.getResources().getString(R.string.msg_confirm_register_noparent2));

                    Button dlg_btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
                    dlg_btn_ok.setText(R.string.str_yes);
                    dlg_btn_ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                dlg.dismiss();
                                register(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Button dlg_btn_cancel = (Button) dlg.findViewById(R.id.btn_cancel);
                    dlg_btn_cancel.setText(R.string.str_no);
                    dlg_btn_cancel.setVisibility(View.VISIBLE);
                    dlg_btn_cancel.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                dlg.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dlg.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
