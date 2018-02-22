package com.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CSlinkImageView extends ImageView{

    public CSlinkImageView(Context context) {
        super(context);
    }

    public CSlinkImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CSlinkImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try{
            super.onDraw(canvas);
        } catch(OutOfMemoryError ome) {
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
