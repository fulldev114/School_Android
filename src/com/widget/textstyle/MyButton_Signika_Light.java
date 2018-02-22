package com.widget.textstyle;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by etech on 31/5/16.
 */
public class MyButton_Signika_Light extends Button {
    private Context context;

    public MyButton_Signika_Light(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public MyButton_Signika_Light(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }


    public MyButton_Signika_Light(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyButton_Signika_Light(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init() {
        setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Signika-Light.ttf"));
    }
}
