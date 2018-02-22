package com.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.xmpp.parent.Constant;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by etech on 24/5/16.
 */
public class SharedPreferFile {
    private final Context context;

    public SharedPreferFile(Context context) {
        this.context=context;
    }

    public void setvalue(String filename, int modePrivate, HashMap<String, String> map) {
        SharedPreferences shf = context.getSharedPreferences(filename, modePrivate);
        SharedPreferences.Editor ed= shf.edit();

        Iterator i = map.keySet().iterator();
        while(i.hasNext())
        {
            String key = (String) i.next();
            ed.putString(key, (String) map.get(key));
            Log.e("key and Value >> ",key+"::"+ (String) map.get(key));
        }
        ed.commit();
    }


    public String getJidUser() {
        SharedPreferences shf = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        return shf.getString("jid","");
    }


    public String getJidPassword() {
        SharedPreferences shf = context.getSharedPreferences(Constant.USER_FILENAME, Context.MODE_PRIVATE);
        return shf.getString("jid_pwd","");
    }



}
