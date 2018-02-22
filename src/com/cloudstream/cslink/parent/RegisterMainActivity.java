package com.cloudstream.cslink.parent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudstream.cslink.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by etech on 1/6/16.
 */
public class RegisterMainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    private TextView textView1;
    private ImageView imgback;
    private LinearLayout btnbck;
    public ViewPager pager;
    private int current_item;
    private ImageView one_image;
    private ImageView two_image;
    private ViewPager viewPager_dots;
    private List<ImageView> dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.regsiter_fragment);

        btnbck = (LinearLayout) findViewById(R.id.btnbck);
        textView1 = (TextView) findViewById(R.id.textView1);
        imgback = (ImageView) findViewById(R.id.imgback);

        textView1.setText(getString(R.string.str_register));

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pager = (ViewPager) findViewById(R.id.viewPager);

        pager.setAdapter(new RegisterAdapter(getSupportFragmentManager()));

        pager.addOnPageChangeListener(this);

        //set dots
        LinearLayout dotsLayout = (LinearLayout)findViewById(R.id.lin_dots);
        dots = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageDrawable(getResources().getDrawable(R.drawable.circle_white_empty));
            dot.setPadding(10,10,10,10);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dotsLayout.addView(dot, params);

            dots.add(dot);

        }
    }



    private class RegisterAdapter extends FragmentPagerAdapter {
        public RegisterAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            current_item = pos;
            switch (pos) {
                case 0:
                    dots.get(0).setImageResource(R.drawable.circle_blue_full);
                    return RegisterParent1.newInstance("regoister1,one");
                case 1:
                    return RegisterParent2.newInstance("regoister2,two");
                default:
                    return RegisterParent1.newInstance("regoister1,one");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }


        public void setCount(int count) {
            if (count > 0 && count <= 2) {
                current_item = count;
            }
        }
    }


    public void nextpage(int position) {
        pager.setCurrentItem(position);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


    }

    @Override
    public void onPageSelected(int position) {
        selectDot(position);
    }

    private void selectDot(int position) {
        for(int i = 0; i < 2; i++) {
            Resources res = getResources();
            int drawableId = (i==position)?(R.drawable.circle_blue_full):(R.drawable.circle_white_empty);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
        View view = getCurrentFocus();
        if (view!=null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }




}
