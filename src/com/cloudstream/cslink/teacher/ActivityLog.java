package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.cloudstream.cslink.R;

/**
 * Created by etech on 1/2/17.
 */
public class ActivityLog extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adres_screen_logop);

        TextView txtLog = (TextView) findViewById(R.id.txtLog);

        if (Global.logarr != null) {
            if (Global.logarr.size() > 0) {
                String message = "";
                for (String log : Global.logarr) {
                    message = message+ log + "\n\n";
                }
                txtLog.setText(message);
            }
        }
    }
}
