package com.cloudstream.cslink.teacher;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;
import com.xmpp.teacher.Constant;

/**
 * Created by etech on 15/7/16.
 */
public class EmergencyAlertActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView imgback;
    private TextView text_title;
    private LinearLayout btnbck;
    private View view_system, view_custom, view_message;
    private TextView txt_message, txt_custom, txt_system;
    private RelativeLayout rel_system,rel_custom,rel_message;
    private FrameLayout lin_fragmentemergency;
    private String teacher_id,school_id;
    private FragmentManager fragmentManager;
    private Fragment emergencyfragment;
    private int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adres_emergency_screen);

        initcomponent();

        SharedPreferences sharedpref = getSharedPreferences(Constant.USER_FILENAME, 0);
        teacher_id = sharedpref.getString("teacher_id", "");
        school_id = sharedpref.getString("school_id", "");

        fragmentManager = getFragmentManager();
        emergencyfragment = new FragmentSystem();

        fragmentManager.beginTransaction().replace(R.id.lin_fragmentemergency, emergencyfragment).commit();

    }

    private void initcomponent() {
        imgback = (ImageView) findViewById(R.id.imgback);
        text_title = (TextView) findViewById(R.id.textView1);
        btnbck = (LinearLayout) findViewById(R.id.btnbck);
        view_system = (View) findViewById(R.id.view_system);
        view_custom = (View) findViewById(R.id.view_custom);
        view_message = (View) findViewById(R.id.view_message);
        txt_message = (TextView) findViewById(R.id.txt_message);
        txt_custom = (TextView) findViewById(R.id.txt_custom);
        txt_system = (TextView) findViewById(R.id.txt_system);
        rel_system = (RelativeLayout) findViewById(R.id.rel_system);
        rel_custom = (RelativeLayout) findViewById(R.id.rel_custom);
        rel_message = (RelativeLayout) findViewById(R.id.rel_message);
        lin_fragmentemergency=(FrameLayout)findViewById(R.id.lin_fragmentemergency);

        rel_custom.setOnClickListener(this);
        rel_message.setOnClickListener(this);
        rel_system.setOnClickListener(this);
        imgback.setOnClickListener(this);

        text_title.setText(getString(R.string.emergency_msg));
        text_title.setTextColor(getResources().getColor(R.color.color_blue_p));

        txt_message.setText(getString(R.string.str_message_history).replace(" :",""));

       /* if(getIntent().hasExtra("selectedval"))
        {
            flag=getIntent().getIntExtra("selectedval",0);
            if(flag!=0) {
                changelayout(flag);
                emergencyfragment = new FragmentEmegencyMessage();
                if(emergencyfragment!=null) {
                    fragmentManager.beginTransaction().replace(R.id.lin_fragmentemergency, emergencyfragment).commit();
                }
            }

        }*/

    }

    @Override
    public void onClick(View v) {

        emergencyfragment=null;
        int selectedval=0;
        if(v.getId()==R.id.rel_system)
        {
            emergencyfragment = new FragmentSystem();

            selectedval=1;
        }
        else if(v.getId()==R.id.rel_custom)
        {
            emergencyfragment = new FragmentCustom();

            selectedval=2;
        }
        else if(v.getId()==R.id.rel_message)
        {
            emergencyfragment = new FragmentEmegencyMessage();
            selectedval=3;
        }
        else if(v.getId()==R.id.imgback)
        {
           /* if(flag!=0)
            {
                Intent maini = new Intent(EmergencyAlertActivity.this,MainActivity.class);
                startActivity(maini);
            }*/
            finish();
        }
        if(emergencyfragment!=null) {
            fragmentManager.beginTransaction().replace(R.id.lin_fragmentemergency, emergencyfragment).commit();
        }
        changelayout(selectedval);

    }

    private void changelayout(int selectedval) {
        if(selectedval==1)
        {
            rel_system.setBackgroundResource(R.drawable.view_line_top_background);
            rel_custom.setBackgroundResource(R.drawable.view_line_bottom_background);
            rel_message.setBackgroundResource(R.drawable.view_line_bottom_background);

            txt_system.setTextColor(getResources().getColor(R.color.color_blue_p));
            txt_custom.setTextColor(getResources().getColor(R.color.white_light));
            txt_message.setTextColor(getResources().getColor(R.color.white_light));

        }
        else if(selectedval==2)
        {
            rel_system.setBackgroundResource(R.drawable.view_line_bottom_background);
            rel_custom.setBackgroundResource(R.drawable.view_line_top_background);
            rel_message.setBackgroundResource(R.drawable.view_line_bottom_background);

            txt_system.setTextColor(getResources().getColor(R.color.white_light));
            txt_custom.setTextColor(getResources().getColor(R.color.color_blue_p));
            txt_message.setTextColor(getResources().getColor(R.color.white_light));
        }
        else if(selectedval==3)
        {

            rel_system.setBackgroundResource(R.drawable.view_line_bottom_background);
            rel_custom.setBackgroundResource(R.drawable.view_line_bottom_background);
            rel_message.setBackgroundResource(R.drawable.view_line_top_background);

            txt_system.setTextColor(getResources().getColor(R.color.white_light));
            txt_custom.setTextColor(getResources().getColor(R.color.white_light));
            txt_message.setTextColor(getResources().getColor(R.color.color_blue_p));
        }
    }

    public void removeactivity() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgback=null;
        text_title=null;
        btnbck=null;
        view_system=null; view_custom=null; view_message=null;
        txt_message=null; txt_custom=null; txt_system=null;
        rel_system=null;rel_custom=null;rel_message=null;
        lin_fragmentemergency=null;
        teacher_id=null;school_id=null;
        fragmentManager=null;
        emergencyfragment=null;

        System.gc();
    }
}
