package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.ActivitiesBeans;
import com.adapter.teacher.ActivitiesAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudstream.cslink.R;
import com.common.dialog.MainProgress;
import com.common.utils.ConstantApi;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.langsetting.apps.Background_work;
import com.request.AsyncTaskCompleteListener;
import com.widget.textstyle.MyTextView_Signika_Bold;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ActivityDetailActivity extends Activity /* implements AsyncTaskCompleteListener<String>*/ {
    Activity mActivity;

    ImageView mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_activity);

        mActivity = this;

        mBack = (ImageView)findViewById(R.id.imgback);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        MyTextView_Signika_Bold txtTitle = (MyTextView_Signika_Bold)findViewById(R.id.textView1);
        txtTitle.setTextColor(getResources().getColor(R.color.text_color_yellow));
        txtTitle.setText(getResources().getText(R.string.activities_title));

    }

    private void loadActivitiesList() {

//        try {
//            HashMap<String, Object> params = new HashMap<String, Object>();
//            params.put("parent_id", parent_id);
//            params.put("os", "android");
//            ETechAsyncTask task = new ETechAsyncTask(SelectChildActivity.this, this, ConstantApi.GET_CHILD_BY_PHONE, params);
//            task.execute(ApplicationData.main_url + ConstantApi.GET_CHILD_BY_PHONE + ".php?");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void back() {
        finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
       /* Background_work.set_background_time();
        b = false;*/
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadActivitiesList();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBack = null;

        System.gc();
    }
}
