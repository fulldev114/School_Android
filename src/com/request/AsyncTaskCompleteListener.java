package com.request;


public interface AsyncTaskCompleteListener<T> 
{
	public void onTaskComplete(int statusCode, T result, T webserviceCb, Object tag);
}
