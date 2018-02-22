package com.widget.textstyle;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by etech on 31/5/16.
 */
public class MyEdiText_Signika_Regular_Chat extends EditText implements TextWatcher {
    private Context context;

    private static final int TypingInterval = 800;

    //your listener interface that you implement anonymously from the Activity
    public interface OnTypingModified
    {
        public void onIsTypingModified(EditText view, boolean isTyping);
    }

    private OnTypingModified typingChangedListener;

    //members you would need for the small thread that declares that you have stopped typing
    private boolean currentTypingState = false;
    private Handler handler = new Handler();
    private Runnable stoppedTypingNotifier = new Runnable()
    {
        @Override
        public void run()
        {
            //part A of the magic...
            if(null != typingChangedListener)
            {
                typingChangedListener.onIsTypingModified(MyEdiText_Signika_Regular_Chat.this, false);
                currentTypingState = false;
            }
        }
    };
    public MyEdiText_Signika_Regular_Chat(Context context) {
        super(context);
        this.context=context;
        init();
        this.addTextChangedListener(this);
    }

    private void init() {
        setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Signika-Regular.ttf"));
    }

    public void setOnTypingModified(OnTypingModified typingChangedListener)
    {
        this.typingChangedListener = typingChangedListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(null != typingChangedListener)
        {
            if(!currentTypingState)
            {
                typingChangedListener.onIsTypingModified(this, true);
                currentTypingState = true;
            }

            handler.removeCallbacks(stoppedTypingNotifier);
            handler.postDelayed(stoppedTypingNotifier, TypingInterval);
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }
}
