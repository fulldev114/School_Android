package com.github.orangegangsters.lollipin.lib.textstyle;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by etech on 31/5/16.
 */
public class MyEditText_Signika_Light extends EditText {
    private Context context;

    public MyEditText_Signika_Light(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public MyEditText_Signika_Light(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }


    public MyEditText_Signika_Light(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyEditText_Signika_Light(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init() {
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Signika-Light.ttf"));
    }
}
