package com.cloudstream.cslink.teacher;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloudstream.cslink.R;
import com.common.view.AttachmentMap;
import com.common.view.TouchImageView;


import java.io.File;
import java.util.ArrayList;

/**
 * Created by etm-11 on 21/6/16.
 */
public class SlideShowActivity extends Activity {
    private String TAG ="SlideShowActivity";
    private ArrayList<AttachmentMap> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    private ImageButton btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adres_fragment_image_slider);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        lblCount = (TextView)findViewById(R.id.lbl_count);
        lblTitle = (TextView)findViewById(R.id.title);
        lblDate = (TextView) findViewById(R.id.date);
        btnClose = (ImageButton) findViewById(R.id.lbl_close);

        images = (ArrayList<AttachmentMap>)getIntent().getSerializableExtra("ImageList");
        //selectedPosition = getIntent().getIntExtra("position", 0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        btnClose.setOnClickListener(closeSliderCliclListener);
    }
    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }
    View.OnClickListener closeSliderCliclListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());

        AttachmentMap image = images.get(position);
        lblTitle.setText(image.getMapName());
       // lblDate.setText(image.getTimestamp());
    }

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.adres_image_fullscreen_preview, container, false);

            TouchImageView imageViewPreview = (TouchImageView) view.findViewById(R.id.image_preview);

            AttachmentMap image = images.get(position);
            String url_static= "http://api.androidhive.info/images/glide/small/deadpool.jpg";
            //Constants.BASE_IMAGE_URL+image.getAttachmentName()/storage/emulated/0/CSadm/profile_img.png
            if(image.getAttachmentName().startsWith("/storage"))
            {
                File filepath= new File(image.getAttachmentName());
                Glide.with(SlideShowActivity.this).load(filepath)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageViewPreview);

            }
            else {
                if(image.getAttachmentName().startsWith("http"))
                {
                    Glide.with(SlideShowActivity.this).load(image.getAttachmentName())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageViewPreview);
                }
                else {
                    Glide.with(SlideShowActivity.this).load(ApplicationData.web_server_url + "uploads/" + image.getAttachmentName())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageViewPreview);
                }
            }
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
