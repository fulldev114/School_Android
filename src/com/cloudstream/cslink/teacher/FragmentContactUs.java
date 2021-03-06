package com.cloudstream.cslink.teacher;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

public class FragmentContactUs extends Fragment implements AsyncTaskCompleteListener<String> {

	private EditText message;

	String url;

	private LinearLayout send;
	MainProgress pDialog;
	private String teacher_id, teacher_name, teacher_email;
    private EditText editEmail;
    private String email_id;
    private String mess_age;

    public FragmentContactUs() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// setActionBArStyle();
		View rootView = inflater.inflate(R.layout.adres_contactus_fragment, container, false);

		SharedPreferences sharedpref = getActivity().getSharedPreferences("adminapp", 0);

		teacher_id = sharedpref.getString("teacher_id", "");
		teacher_name = sharedpref.getString("teacher_name", "");
		teacher_email = sharedpref.getString("email", "");

		message = (EditText) rootView.findViewById(R.id.editText1);
        editEmail = (EditText) rootView.findViewById(R.id.editEmail);
        try {
            teacher_email=URLDecoder.decode(teacher_email,"utf-8");
            editEmail.setText(teacher_email);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        send = (LinearLayout) rootView.findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

                email_id = editEmail.getText().toString();
                if(email_id.length()==0)
                {
                    ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_no_email), true);
                    return;
                }
                if ( !isValidEmail(email_id) ) {
                    ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_invalid_email), true);
                    return;
                }

                if (message.getText().length() > 0) {

                    teacher_name=teacher_name.replace(" ","%20");

					try {
						mess_age = URLEncoder.encode(message.getText().toString(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					url = ApplicationData.getlanguageAndApi(getActivity(),ConstantApi.SEND_FEEDBACK) + "teacher_id=" + teacher_id
							+ "&email=" + teacher_email
							+ "&name=" + teacher_name
							+ "&message=" + mess_age;
					send_message(url);
				} else {
					ApplicationData.showToast(getActivity(), getResources().getString(R.string.str_input_message), true);
				}
			}
		});

		return rootView;
	}

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                .matches();
    }

    private void send_message(String login_url2) {

		if (!GlobalConstrants.isWifiConnected(getActivity())) {
			return;
		}
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("email", teacher_email);
            params.put("name", teacher_name);
            params.put("message", mess_age);
            params.put("teacher_id", teacher_id);
            ETechAsyncTask task = new ETechAsyncTask(getActivity(), this, ConstantApi.SEND_FEEDBACK, params);
            task.execute(ApplicationData.main_url + ConstantApi.SEND_FEEDBACK + ".php?");

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public void onLowMemory() {
		super.onLowMemory();
	}

    @Override
    public void onTaskComplete(int statusCode, String responseMsg, String webserviceCb, Object tag) {
        if (statusCode == ETechAsyncTask.COMPLETED) {
            try {
                JSONObject jObject = new JSONObject(responseMsg);


                if (webserviceCb.equalsIgnoreCase(ConstantApi.SEND_FEEDBACK)) {
                    String flag = jObject.getString("flag");

                    if (Integer.parseInt(flag) == 1) {
                        ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_success_feedback), false);
                        message.setText("");
                    } else {
                        if(jObject.has("msg"))
                        {
                            String msg  = jObject.getString("msg");
                            ApplicationData.showToast(getActivity(), msg, false);
                        }
                        else
                        ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_operation_error), false);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                ApplicationData.showToast(getActivity(), getResources().getString(R.string.msg_operation_error), true);
            }
        } else if (statusCode == ETechAsyncTask.ERROR_NETWORK) {
            try {
                ApplicationData.showToast(getActivity(), R.string.msg_operation_error, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
