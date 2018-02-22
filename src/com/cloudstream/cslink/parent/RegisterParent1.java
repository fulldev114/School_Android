package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudstream.cslink.R;
import com.common.Bean.RegisterBean;

/**
 * Created by etech on 1/6/16.
 */
public class RegisterParent1 extends Fragment implements View.OnClickListener {
    private Activity mActivity;
    private EditText edtParent1;
    private EditText edtPhone1;
    private EditText edtEmail1;
    private EditText edtPincode;
    private EditText edtConfirmcode;
    private RelativeLayout textView2;
    private LinearLayout btnbck;
    RegisterParent2 parent1 = null;
    private LinearLayout register_one;
    String parent11="",phone11="",email11="",pincode11="",confirm="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mActivity = getActivity();
        View v = inflater.inflate(R.layout.new_register_one, container, false);

        edtParent1 = (EditText) v.findViewById(R.id.edtParent1);
        edtPhone1 = (EditText) v.findViewById(R.id.edtPhone1);
        edtEmail1 = (EditText) v.findViewById(R.id.edtEmail1);
        edtPincode = (EditText) v.findViewById(R.id.edtPincode);
        edtConfirmcode = (EditText) v.findViewById(R.id.edtConfirmcode);
        textView2 = (RelativeLayout) v.findViewById(R.id.textView2);
        register_one = (LinearLayout) v.findViewById(R.id.register_one);

        textView2.setOnClickListener(this);
        ApplicationData.currentpos = true;

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtParent1.getWindowToken(), 0);

        return v;
    }

    public static RegisterParent1 newInstance(String s) {
        RegisterParent1 f = new RegisterParent1();
        Bundle b = new Bundle();
        b.putString("msg", s);

        f.setArguments(b);
        return f;
    }


    @Override
    public void onClick(View v) {
        Boolean check=validation();

        if(check) {
            passdata();
        }
    }

    private Boolean validation() {
        if (edtParent1.getText().length() == 0) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_parent1_name), true);
            edtParent1.setFocusable(true);
            return false;
        } else if (edtPhone1.getText().length() == 0) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_parent1_phone), true);
            edtParent1.setFocusable(true);
            return false;
        } else if (!ApplicationData.isValidPhone(edtPhone1.getText().toString())) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalidparent1phone), true);
            edtPhone1.setFocusable(true);
            return false;
        }
        else if(edtEmail1.getText().toString().length()>0 && !ApplicationData.isValidEmail(edtEmail1.getText().toString()))
        {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_invalid_email), true);
            edtEmail1.setFocusable(true);
            return false;
        }
        else if (edtPincode.getText().length() == 0) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_pincode), true);
            edtPincode.setFocusable(true);
            return false;
        }
        else if(edtPincode.getText().length()<4)
        {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.pincode_error), true);
            edtPincode.setFocusable(true);
            return false;
        }
        else if (edtConfirmcode.getText().length() == 0) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_input_confirmcode), true);
            edtConfirmcode.setFocusable(true);
            return false;
        } else if (!edtPincode.getText().toString().equalsIgnoreCase(edtConfirmcode.getText().toString())) {
            ApplicationData.showToast(mActivity, getResources().getString(R.string.msg_not_matched_pincode), true);
            edtConfirmcode.setFocusable(true);
            return false;
        } else {
            return true;
        }

    }

    public void passdata() {
            Fragment fragment = new RegisterParent2();
            Bundle bn = new Bundle();
            RegisterBean bean = new RegisterBean();
            bean.setParent1_name(edtParent1.getText().toString());
            bean.setParent1_phoneno(edtPhone1.getText().toString());
            bean.setEmail(edtEmail1.getText().toString());
            bean.setPincode(edtPincode.getText().toString());
            bean.setConfirmpincode(edtConfirmcode.getText().toString());
            bn.putSerializable("data", bean);
            parent1 = RegisterParent2.newInstance(bn);

            ((RegisterMainActivity) getActivity()).nextpage(1);
    }


}
