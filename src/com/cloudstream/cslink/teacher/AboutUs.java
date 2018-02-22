package com.cloudstream.cslink.teacher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;


public class AboutUs extends ActivityHeader {

    TextView txt_about;
    String language;
    // private TextView textView1;
    private LayoutInflater inflater;
    private RelativeLayout screenview;
    private TextView txt_link;
    private TextView txt_support;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        screenview = (RelativeLayout) inflater.inflate(R.layout.aboutus, null);
        relwrapp.addView(screenview);

        txt_about = (TextView) findViewById(R.id.txt_about);
        txt_link=(TextView)findViewById(R.id.txt_link);
        txt_support = (TextView) findViewById(R.id.txt_support);
        showheadermenu(AboutUs.this, getString(R.string.aboutus), R.color.white_light, false);


        txt_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationData.isphoto=true;
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
                    ApplicationData.isphoto=true;
                    //Intent i =getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                    Intent i = new Intent(Intent.ACTION_SEND);
                    //  i.setPackage("com.google.android.gm");
                    i.setData(Uri.parse("mailto:" + txt_support.getText().toString()));
                    i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ txt_support.getText().toString() });
                    i.setType("message/rfc822");

                    startActivity(Intent.createChooser(i, "Send email"));
                }
              /*  else
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

    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationData.isphoto=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        txt_about=null;
        language=null;
        inflater=null;
        screenview=null;
        txt_link=null;
        txt_support=null;
    }
}
