package com.widget.textstyle;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by etech on 10/10/16.
 */
public class CustomGridView extends GridView {
    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
        getLayoutParams().height = getMeasuredHeight();
       /* super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width;

        // Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            // Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else { // Be whatever you want
            width = desiredWidth;
        }

        // MUST CALL THIS

        setMeasuredDimension(width, mHeight);*/
    }

    int mHeight;

    public void SetHeight(int height) {
        mHeight = height;
    }
}
