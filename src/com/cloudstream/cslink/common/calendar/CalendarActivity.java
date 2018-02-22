package com.cloudstream.cslink.common.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.cloudstream.cslink.R;
import com.widget.textstyle.MyTextView_Signika_Bold;

public class CalendarActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar_activity);

        MyTextView_Signika_Bold txtTitle = (MyTextView_Signika_Bold)findViewById(R.id.textView1);
        txtTitle.setTextColor(getResources().getColor(R.color.color_white));
        txtTitle.setText(getResources().getText(R.string.calendar_title));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.main_container, new MaterialCalendarFragment()).commit();
        }
    }

    public void back(View v) {
        finish();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
