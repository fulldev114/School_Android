package com.github.orangegangsters.lollipin.lib.textstyle;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by etech on 31/5/16.
 */
public class MyTextView_Signika_Semibold extends TextView {
    private Context context;

    public MyTextView_Signika_Semibold(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public MyTextView_Signika_Semibold(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }


    public MyTextView_Signika_Semibold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyTextView_Signika_Semibold(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init() {
        setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Signika-Semibold.ttf"));
    }
}
