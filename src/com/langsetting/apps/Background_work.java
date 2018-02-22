package com.langsetting.apps;

import java.util.Date;

import android.util.Log;

public class Background_work 
{
	public static Date d1,d2;
	public static boolean is_background= false;
	//Date d1 = ...
	//		Date d2 = ...
	//		long diffMs = d1.getTime() - d2.getTime();
	//		long diffSec = diffMs / 1000;
	//		long min = diffSec / 60;
		//	long sec = diffSec % 60;
		//	System.out.println("The difference is "+min+" minutes and "+sec+" seconds.");
	public static void set_background_time()
	{
		is_background=true;
		d1=new Date();
		Log.e("", ""+d1.getTime());
	}
	
	public static void set_front_time()
	{
		
		d2 = new Date();
		Log.e("", ""+d2.getTime());
		
	}
	
	public static boolean check_layout_pincode()
	{
		is_background=false;
		long diffms = d2.getTime() - d1.getTime();
		long difsecon = diffms/1000;
		int min =(int)(difsecon/60);
		Log.e("", ""+difsecon);
		if(difsecon>60)
		{
			Log.e("", ""+true);
			return true;
		}
		else
		{
			Log.e("", ""+false);
			return false;
		}
		
		//return false;
	}
}
