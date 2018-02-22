package com.cloudstream.cslink.common.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.adapter.ActivitiesBeans;
import com.adapter.teacher.ActivitiesAdapter;
import com.cloudstream.cslink.R;
import com.widget.textstyle.MyTextView_Signika_Bold;

import java.util.ArrayList;

public class ActivitiesActivity extends Activity /* implements AsyncTaskCompleteListener<String>*/ {
    Activity mActivity;

    ImageView mBack;
    ArrayList<ActivitiesBeans> mActivitiesArray = null;
    ListView mltActivities;
    ActivitiesAdapter mActivitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activities_activity);

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

        mltActivities = (ListView) findViewById(R.id.lstActivities);
        mltActivities.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                if (mActivitiesArray.get(position).jid != null && arrayList.get(position).jid.length() > 0) {
//
//
//                    if (mActivitiesArray.get(position).jid.substring(0, arrayList.get(position).jid.lastIndexOf("@")).length() > 0) {
//
//                        new chatclass(position).execute();
//                    }
//                }
                Intent i = new Intent(mActivity, ActivityDetailActivity.class);
                startActivity(i);
            }
        });


        mActivitiesArray = new ArrayList<ActivitiesBeans>();

        mActivitiesAdapter = new ActivitiesAdapter(ActivitiesActivity.this, mActivitiesArray);
        mltActivities.setAdapter(mActivitiesAdapter);
        mActivitiesAdapter.notifyDataSetChanged();
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
