package com.langsetting.apps;

import android.content.Context;
import android.content.SharedPreferences;

import com.xmpp.parent.Constant;

public class Change_lang 
{
	SharedPreferences sharedpref ;//= getActivity().getSharedPreferences(
			//"absentapp", 0);
	String[] english=
		{"Add Photo"//0
			,"Take Photo"
			,"Cancel"//2
			,"Choose from library"
			,"Please write message"};
	
	
	
	String[] norwegian={"Legg til bilde","Ta bilde","Avbryt","Velg bilde fra galleriet","Skriv her"};
	public Change_lang(Context c) 
	{
		// TODO Auto-generated constructor stub
		sharedpref = c.getSharedPreferences(
                Constant.USER_FILENAME, 0);
	}
	
	public String get_name(int i)
	{
		if ( sharedpref.getString("language", "").equalsIgnoreCase("english")) 
		{
			return english[i];
		}
		else
		{
			return norwegian[i];
			
		}
		
	}
	
}
