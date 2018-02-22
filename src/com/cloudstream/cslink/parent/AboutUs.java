package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cloudstream.cslink.R;
import com.common.utils.ConstantApi;
import com.langsetting.apps.Background_work;
import com.request.AsyncTaskCompleteListener;
import com.request.ETechAsyncTask;

import org.json.JSONObject;

public class AboutUs extends Activity {

    private TextView txt_about;
    private String language;
    private LinearLayout btnbck;
    private TextView textView1;
    private ImageView imgback;
    private TextView txt_link;
    private TextView txt_support;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aboutus);

        txt_about = (TextView) findViewById(R.id.txt_about);

        btnbck = (LinearLayout) findViewById(R.id.btnbck);
        imgback = (ImageView) findViewById(R.id.imgback);
        textView1 = (TextView) findViewById(R.id.textView1);

        textView1.setText(getString(R.string.aboutus));
        textView1.setMovementMethod(LinkMovementMethod.getInstance());
        txt_link = (TextView) findViewById(R.id.txt_link);
        txt_support = (TextView) findViewById(R.id.txt_support);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Aboutus", "go to back");
                finish();
            }
        });

        txt_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.cslink.no"));
                startActivity(viewIntent);
            }
        });

        txt_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isinstalled = ApplicationData.appInstalledOrNot(AboutUs.this, "com.google.android.gm");
                // if (isinstalled)
                {
                    //Intent i =getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                    //startActivity(mailClient);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    //  i.setPackage("com.google.android.gm");
                    i.setData(Uri.parse("mailto:" + txt_support.getText().toString()));
                    i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{txt_support.getText().toString()});
                    i.setType("message/rfc822");
                    //i.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);

                    startActivity(Intent.createChooser(i, "Send email"));
                }
                /*else
                {
                    try {
                        ApplicationData.showMessage(AboutUs.this,"",getString(R.string.gmail_installation),getString(R.string.str_ok));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
            }
        });
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        txt_about = null;
        language = null;
        btnbck = null;
        textView1 = null;
        imgback = null;
        txt_link = null;
        txt_support = null;

        System.gc();
    }
}
