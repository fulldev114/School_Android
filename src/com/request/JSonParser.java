package com.request;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;


public class JSonParser {
	
	private final static String tag = "JSonParser";
	
	/*
	public static Object parse(Class objClass,JSONArray jArray) {
		try {
			Log.e(tag, "Wrapper > Array > jArray.length(): " + jArray.length());
			
			Object objArray = Array.newInstance(field.getType().getComponentType(), jArray.length());
			
			for(int i=0; i < jArray.length(); i++) {
				Object objElem = field.getType().getComponentType().newInstance();
				objElem = parse(field.getType().getComponentType(), jArray.getJSONObject(i));
				Array.set(objArray, i, objElem);
			}
			field.set(obj,objArray);
		}
		catch (Exception e) {
			Log.e(tag, "Exception: " + e);
		}
	}
	*/
	public static Object parse(Class objClass,JSONObject jObject) {
	//	Log.e(tag, "*************************parse*************************");
		Object obj = null;
		try {
			obj = objClass.newInstance();
		}
		catch (IllegalAccessException e) {
			Log.e(tag, "IllegalAccessException: " + e);
		}
		catch (InstantiationException e) {
			Log.e(tag, "InstantiationException: " + e);
		}

		Field[] classFields = objClass.getDeclaredFields();

		for (Field field : classFields) {
			field.setAccessible(true);
			//Log.e(tag, "field: " + field.getName() + " Wrapper:" + isWrapper(field.getType()) + " isArray():"+ field.getType().isArray());
			if(!isWrapper(field.getType())) {
				//Log.d(tag, "Wrapper");
				if(field.getType().isArray()) {
					//	Log.e(tag, "Wrapper > Array > field.getType: " + field.getType());
					//	Log.e(tag, "Wrapper > Array > field.getName: " + field.getName());
					try {
						JSONArray jArray = jObject.getJSONArray(field.getName());

						//Log.e(tag, "Wrapper > Array > jArray.length(): " + jArray.length());

						Object objArray = Array.newInstance(field.getType().getComponentType(), jArray.length());

						for(int i=0; i < jArray.length(); i++) {
							Object objElem = field.getType().getComponentType().newInstance();
							if(isWrapper(field.getType().getComponentType())){
								Object objElement = jArray.get(i);
								if(objElement !=null){
									if(field.getType().getComponentType()==Integer.TYPE) {
										objElem = Integer.parseInt(objElement.toString());
									}
									else if(field.getType().getComponentType()==Float.TYPE) {
										objElem = Float.parseFloat(objElement.toString());
									}
									else if(field.getType().getComponentType()==Long.TYPE) {
										objElem = Long.parseLong(objElement.toString());
									}
									else {
										objElem = objElement.toString();
									}
								}
							}else{
								objElem = parse(field.getType().getComponentType(), jArray.getJSONObject(i));
							}
							Array.set(objArray, i, objElem);
						}
						field.set(obj,objArray);
					}
					catch (Exception e) {
						Log.e(tag, "Exception Array: " + e);
					}
				}
				else{
					//Log.e(tag, "Wrapper > else > field.getType: " + field.getType());
					//	Log.e(tag, "Wrapper > else > field.getName: " + field.getName());

					try{ 
						Object objSub = field.getType().newInstance();

						JSONObject jSubObject = jObject.getJSONObject(field.getName());

						objSub =parse(field.getType(), jSubObject);
						field.set(obj,objSub);
					}
					catch (Exception e) {
						Log.e(tag, "else Exception: " + e);
					}
				}
			}
			else {
				//Log.e(tag, "else > field.getType: " + field.getType());
				//Log.e(tag, "else > field.getType: " + field.getName());

				try {
					field.setAccessible(true);
					if(field.getType()==Integer.TYPE) {
						//Log.e(tag, "else > Integer");
						field.set(obj,jObject.getInt((field.getName())));
					}
					else if(field.getType()==Float.TYPE) {
						//Log.e(tag, "else > Long");
						field.set(obj,jObject.getLong((field.getName())));
					}
					else if(field.getType()==Long.TYPE) {
						//Log.e(tag, "else > Long");
						field.set(obj,jObject.getLong((field.getName())));
					}
					else {
						//Log.e(tag, "else > else");
						field.set(obj,jObject.getString(field.getName()));
					}
				}
				catch (Exception e) {
					//Log.e(tag, "Exception: " + e);
				}
			}
		}
		return obj;
	}
	
	public static void showFields(Class objClass){
		//System.out.println("---Class:" + objClass.getCanonicalName());
		Field[] classFields = objClass.getDeclaredFields();
		for (Field field : classFields) {
			//System.out.println(field.getName() + " Wrapper:" + isWrapper(field.getType()) + " isArray():"+ field.getType().isArray() + " ");			
			if(!isWrapper(field.getType())){
				if(field.getType().isArray()){
					showFields(field.getType().getComponentType());
				}else{
					showFields(field.getType());
				}
			}
		}
	}
	
	public static boolean isWrapper(Class objClass) {
		boolean bStatus=false;
		if(objClass == Boolean.TYPE || objClass == Integer.TYPE || objClass== String.class || objClass == Double.TYPE || objClass ==  Float.TYPE || objClass == Long.TYPE){
			bStatus=true;
		}
		return bStatus;
	}
}
