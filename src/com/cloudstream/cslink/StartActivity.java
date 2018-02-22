package com.cloudstream.cslink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.cloudstream.cslink.parent.ParentHomeActivity;
import com.cloudstream.cslink.parent.ParentLoginActivity;
import com.cloudstream.cslink.teacher.TeacherLoginActivity;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        LinearLayout layoutTeacher = (LinearLayout) findViewById(R.id.linearlayout_start_teacher);
        layoutTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, TeacherLoginActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout layoutParent = (LinearLayout) findViewById(R.id.linearlayout_start_parent);
        layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, ParentLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
