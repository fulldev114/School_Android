package com.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.TextView;

import com.cloudstream.cslink.R;

public class MainProgress extends Dialog implements View.OnClickListener{
    private TextView 	txt_msg;
    Animation 			animation;
    Context 			mContext;

    boolean   cancelable = false;
    public MainProgress(Context context) {
        super(context, R.style.custom_dialog_theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.progress);
        mContext = context;
        setCancelable(false);

        txt_msg = (TextView)findViewById(R.id.txt_msg);
        findViewById(R.id.rootView).setOnClickListener(this);
        findViewById(R.id.layout1).setOnClickListener(this);
        txt_msg.setOnClickListener(this);
    }

    public void setMessage(final String msg)
    {
        txt_msg.setText(msg);
    }

    @Override
    public void dismiss() {
        try{
            super.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.rootView:
                if ( cancelable )
                    dismiss();
                break;
        }
    }

    @Override
    public void show() {
        try{
            super.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        cancelable = flag;
    }
}
