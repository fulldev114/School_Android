package com.common;

/**
 * Created by Administrator on 11/14/2015.
 */


import android.util.Log;

import com.common.BuildConfig;

public class DebugConfig {
    public static boolean DEBUG = BuildConfig.DEBUG;


    public static void println(String msg)
    {
        if (DEBUG)
        {
            System.out.println(msg);
        }
    }

    public static void info(String tag,String msg)
    {
        if (DEBUG)
        {
            Log.i(tag,msg);
        }
    }

    public static void warn(String tag,String msg)
    {
        if (DEBUG)
        {
            Log.w(tag, msg);
        }
    }

    public static void warn(String tag,String msg, Exception e)
    {
        if (DEBUG)
        {
            Log.w(tag, msg + e!=null&&e.toString()!=null?e.toString():"=null");
        }
    }

    public static void debug(String tag,String msg)
    {
        if (DEBUG)
        {
            Log.d(tag,msg);
        }
    }

    public static void debug(String tag, String msg, Exception e)
    {
        if (DEBUG)
        {
            Log.d(tag,msg + e!=null&&e.toString()!=null?e.toString():"=null");
        }
    }

    public static void error(String tag,String msg)
    {
        if (DEBUG)
        {
            Log.e(tag,msg);
        }
    }

    public static void error(String tag, String msg, Exception e)
    {
        if (DEBUG)
        {
            Log.e(tag,msg + e!=null&&e.toString()!=null?e.toString():"=null");
        }
    }
}
