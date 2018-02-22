package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;
import com.common.view.CircularImageView;

/**
 * Created by etech on 25/7/16.
 */
public class ActivityHeader extends Activity {
    protected LinearLayout relwrapp;
    protected CircularImageView profileimage, img_childpic;
    protected ImageView imgback;
    protected TextView name, subject, txt_typing, txt_title;
    protected LinearLayout userprofile, lin_header, lin_information;
    private RelativeLayout rel_user_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adres_header_activity);
        relwrapp = (LinearLayout) findViewById(R.id.relwrapp);
        profileimage = (CircularImageView) findViewById(R.id.imageView2);
        imgback = (ImageView) findViewById(R.id.imgback);
        name = (TextView) findViewById(R.id.textView2);
        subject = (TextView) findViewById(R.id.textView3);
        txt_typing = (TextView) findViewById(R.id.txt_typing);
        lin_header = (LinearLayout) findViewById(R.id.lin_header);
        userprofile = (LinearLayout) findViewById(R.id.userprofile);
        rel_user_img = (RelativeLayout) findViewById(R.id.rel_user_img);
        txt_title = (TextView) findViewById(R.id.textView1);
        img_childpic = (CircularImageView) findViewById(R.id.img_pic);
        lin_information = (LinearLayout) findViewById(R.id.information);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void showheadermenu(Activity activity, String title, int color, boolean infoavailable) {
        txt_title.setText(title);
        txt_title.setTextColor(getResources().getColor(color));

        if (infoavailable)
            lin_information.setVisibility(View.VISIBLE);
        else
            lin_information.setVisibility(View.GONE);
    }

    public void showheaderusermenu(Activity activity, String title, int color, boolean isUserprofile) {
        if (isUserprofile) {
            userprofile.setVisibility(View.VISIBLE);
            txt_title.setVisibility(View.GONE);
        } else {
            userprofile.setVisibility(View.GONE);
            txt_title.setText(title);
            txt_title.setTextColor(getResources().getColor(color));
        }
    }

    public void showHeaderUserAndInfoMenu() {

        userprofile.setVisibility(View.VISIBLE);
        txt_title.setVisibility(View.GONE);
        lin_information.setVisibility(View.VISIBLE);

    }
}
