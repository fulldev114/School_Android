package com.cloudstream.cslink.common.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudstream.cslink.R;
/**
 * Created by Maximilian on 9/1/14.
 */
public class SavedEventsAdapter extends BaseAdapter {
    // Variables
    private Context mContext;
    private static ViewHolder mHolder;

    private static class ViewHolder {
        TextView mTitle;
        TextView mAbout;
        ImageView mImageView;
        View mDivider;
    }

    // Constructor
    public SavedEventsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if (MaterialCalendarFragment.mNumEventsOnDay != 0 || MaterialCalendarFragment.mNumEventsOnDay != -1) {
            return MaterialCalendarFragment.mNumEventsOnDay;
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_material_saved_event_item, parent, false);

            mHolder = new ViewHolder();

            if (convertView != null) {
                // FindViewById
                mHolder.mTitle = (TextView) convertView.findViewById(R.id.saved_event_title_textView);
                mHolder.mAbout = (TextView) convertView.findViewById(R.id.saved_event_about_textView);
                mHolder.mImageView = (ImageView) convertView.findViewById(R.id.saved_event_imageView);
                convertView.setTag(mHolder);
            }
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        // Animates in each cell when added to the ListView
        Animation animateIn = AnimationUtils.loadAnimation(mContext, R.anim.listview_top_down);
        if (convertView != null && animateIn != null) {
            convertView.startAnimation(animateIn);
        }

        if (mHolder.mTitle != null) {
            setEventTitle();
        }

        if (mHolder.mAbout != null) {
            setEventAbout();
        }

        if (mHolder.mImageView != null) {
            setEventImage();

        }

        return convertView;
    }

    private void setEventTitle() {
            mHolder.mTitle.setText("Material Calendar");
    }

    private void setEventAbout() {
            mHolder.mAbout.setText("Thanks for choosing MaterialCalendar. Feel free to use this open source project " +
                    "in your next Android app.");
    }

    private void setEventImage() {
        // Set event item image (bitmap, drawable, uri...)
       //     mHolder.mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_calendar));
    }
}