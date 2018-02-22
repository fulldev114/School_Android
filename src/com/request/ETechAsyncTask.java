package com.request;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/* LYM */
import com.cloudstream.cslink.parent.ApplicationData;
import com.cloudstream.cslink.R;
import com.common.dialog.MainProgress;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ETechAsyncTask extends AsyncTask<String, Void, String> {
    public static final int REQUEST_METHOD_GET = 1;
    public static final int REQUEST_METHOD_POST = 2;
    public static final int CANCELED = 101;
    public static final int COMPLETED = 102;
    public static final int ERROR = 103;
    public static final int ERROR_NETWORK = 104;

    private static final String twoHyphens = "--";
    private static final String boundary = "---------------------------";
    private static final String CRLF = "\r\n";
    private final String TAG = "ETechAsyncTask";
    private MainProgress pDialog;

    private Context context = null;

    public int status = ERROR;
    public boolean isMultiPartData = false;
    public boolean isJson = false;
    public boolean showProgressDialog = false;

    private AsyncTaskCompleteListener<String> callback = null;

    private HttpClient httpClient;
    private HttpGet httpget;
    private HttpPost httpPost;
    private HttpResponse response;
    private String responseString = "";

    private HashMap<String, Object> paramValues = null;
    private Boolean isNetAvailable = false;
    private ConnectionDetector checkConnection;

    private int requestMethod = REQUEST_METHOD_GET;


    private final OnDismissListener onDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (!ETechAsyncTask.this.isCancelled()) {
                if (ETechAsyncTask.this.cancel(false)) {
                    responseString = "Loading canced.";
                    // callback.onTaskComplete(CANCELED, responseString,
                    // webviceCb);
                }
            }
        }
    };

    private String webserviceCb = "";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                callback.onTaskComplete(ERROR, responseString, webserviceCb, "");
            } else {
                callback.onTaskComplete(ERROR, responseString, webserviceCb, "");
            }

            if (!ETechAsyncTask.this.isCancelled()) {
                ETechAsyncTask.this.cancel(false);
            }
        }
    };


    public ETechAsyncTask(Activity context,
                          AsyncTaskCompleteListener<String> cb, String webserviceCb,
                          HashMap<String, Object> paramValues) {
        this(context, cb, webserviceCb, paramValues, REQUEST_METHOD_GET, false);

    }

    public ETechAsyncTask(Activity context,
                          AsyncTaskCompleteListener<String> cb, String webserviceCb,
                          HashMap<String, Object> paramValues, int requestMethod) {
        this(context, cb, webserviceCb, paramValues, requestMethod, false);
    }

    public ETechAsyncTask(Activity context,
                          AsyncTaskCompleteListener<String> cb, String webserviceCb,
                          HashMap<String, Object> paramValues, int requestMethod,
                          boolean isMultiPartData) {
        this.context = context;
        callback = cb;
        this.webserviceCb = webserviceCb;
        if (paramValues != null) {
            this.paramValues = paramValues;
            Log.v("map", paramValues.toString());
        }
        this.requestMethod = requestMethod;
        this.isMultiPartData = isMultiPartData;
        if (showProgressDialog) {
            pDialog = new MainProgress(context);
            pDialog.setCancelable(false);
            pDialog.setMessage(context.getResources().getString(R.string.str_wait));
            pDialog.show();
        }

        checkConnection = new ConnectionDetector(context);
    }

    public ETechAsyncTask(Context context,
                          AsyncTaskCompleteListener<String> cb, String webserviceCb,
                          HashMap<String, Object> paramValues, int requestMethod,
                          boolean isMultiPartData, boolean isJson) {
        this.context = context;
        this.callback = cb;
        this.webserviceCb = webserviceCb;
        this.isJson = isJson;
        this.requestMethod = requestMethod;
        this.isMultiPartData = isMultiPartData;

        if (paramValues != null) {
            this.paramValues = paramValues;
            Log.v("map", paramValues.toString());
        }

        if (showProgressDialog) {
            pDialog = new MainProgress(context);
            pDialog.setCancelable(false);
            pDialog.setMessage(context.getResources().getString(R.string.str_wait));
            pDialog.show();
        }

    }

    public void hideProgressDialog() {
        pDialog = null;
        showProgressDialog = false;
    }

    @Override
    protected void onPreExecute() {
        if (pDialog == null) {
            pDialog = new MainProgress(context);
            pDialog.setCancelable(false);
            pDialog.setMessage(context.getResources().getString(R.string.str_wait));
            pDialog.show();
        //    pDialog.setOnDismissListener(onDismissListener);
        }

    }

    @Override
    protected String doInBackground(String... url) {
        isNetAvailable = checkConnection.isConnectingToInternet();
        if (isNetAvailable)
        {

            try {
                String strUrl = url[0];

                HttpParams httpParameters = new BasicHttpParams();
                int timeoutSocket = 60000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                httpClient = new DefaultHttpClient(httpParameters);

                paramValues.put("language",ApplicationData.getlanguage(context));
                paramValues.put("os", "android");

                if (requestMethod == REQUEST_METHOD_GET) {

                    int timeoutConnection = 30000;
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

                    if (paramValues != null) {
                        ArrayList<String> arrKey = new ArrayList<String>(paramValues.keySet());

                        for (String string : arrKey) {
                            strUrl = strUrl + string + "=" + paramValues.get(string) + "&";

                        }
                        Log.d("url", strUrl);
                    }

                    httpget = new HttpGet(strUrl);
                    response = httpClient.execute(httpget);
                }
                else {
                    String strOnlyURL = strUrl;

                    if (!isMultiPartData) {

                        if(!isJson) {
                            int timeoutConnection = 30000;
                            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

                            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                            Iterator<String> iterParamKeys = paramValues.keySet().iterator();
                            String strKey = null;
                            String strValue = null;

                            while (iterParamKeys.hasNext()) {
                                strKey = strValue = null;
                                strKey = iterParamKeys.next();
                                strValue = paramValues.get(strKey).toString();
                                if (strKey != null) {
                                    pairs.add(new BasicNameValuePair(strKey, strValue));
                                }
                            }

                            httpPost = new HttpPost(strOnlyURL);
                            Log.d("url", strUrl);
                            if (pairs.size() > 0) {
                                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "utf-8");
                                httpPost.setEntity(entity);
                            }
                        }
                        else {

                            //List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                            //Iterator<String> iterParamKeys = paramValues.keySet().iterator();

                            httpPost = new HttpPost(strOnlyURL);
                          //  httpget.addHeader("Content-Type", "application/json");

                            JSONObject obj = (JSONObject)paramValues.get("jsondata");
                            httpPost.setEntity(new StringEntity(obj.toString(), "UTF8"));
                        }
                    }
                    else {
                        int timeoutConnection = 600000;
                        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

                        MultipartEntity entity = new MultipartEntity(
                                HttpMultipartMode.BROWSER_COMPATIBLE);

                        for (String key : paramValues.keySet()) {

                            Object objValue = paramValues.get(key);

                            if (objValue instanceof FileObject) {
                                writeFile(entity, key, objValue);
                            }
                            else {
                                writeField(entity, key, paramValues.get(key).toString());
                            }
                        }

                        httpPost = new HttpPost(strOnlyURL);
                        httpPost.setEntity(entity);
                    }
                    response = httpClient.execute(httpPost);
                    Log.v("response code", response.getStatusLine().getStatusCode() + "");
                }

                HttpEntity entity = response.getEntity();
                if (entity != null) {

                    InputStream instream = entity.getContent();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(instream, "UTF-8"));

                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.d("ParsingData", "Response String : " + line);
                    }

                    status = COMPLETED;
                    responseString = sb.toString();
                    Log.d("DataParsigFile", "Status Get : " + status + ", Response String : " + responseString);
                    instream.close();
                }
            }
            catch (UnknownHostException e) {
//				EtechLog.error(ETechAsyncTask.class
//						+ "doInBackground() : Exception : ", e);
                Log.e(TAG, " doInBackground > UnknownHostException : " + e);
                responseString = e.getMessage();
                status = ERROR;
                // handler.sendMessage(handler.obtainMessage(0));
            } catch (Exception e) {
                Log.e(TAG, " doInBackground > Exception : " + e, e);
                responseString = e.getMessage();
                status = ERROR;
//				EtechLog.error(ETechAsyncTask.class
//						+ "doInBackground() : Exception : ", e);
                // handler.sendMessage(handler.obtainMessage(1));
            }

            return responseString;
        } else {
            status = ERROR_NETWORK;
            ETechAsyncTask.this.cancel(isNetAvailable);
        }
        return null;
    }

    private void printentity(MultipartEntity entity) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(
                    (int) entity.getContentLength());
            entity.writeTo(out);
            byte[] entityContentAsBytes = out.toByteArray();
            // or convert to string
            String entityContentAsString = new String(out.toByteArray());
            Log.e("printentity", "entityContentAsString : "
                    + entityContentAsString);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeField(MultipartEntity entity, String key, String value) {
        FormBodyPart bodyPart;
        try {
            Log.d(TAG, "==============-------->value: " + value);
            bodyPart = new FormBodyPart(key, new StringBody(value,
                    Charset.forName("UTF-8")));
            entity.addPart(bodyPart);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void writeFile(MultipartEntity entity, String key, Object objValue) {
        FileObject fileObj = (FileObject) objValue;

        ByteArrayBody bab = new ByteArrayBody(fileObj.getByteData(),
                fileObj.getContentType(), fileObj.getFileName());

        entity.addPart(key, bab);
    }

    public void cancelTask() {
        try {
            Log.e("ETechAsyncTask", "CANCELED");
            status = CANCELED;
            ETechAsyncTask.this.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onProgressUpdate() {

    }

    protected void onProgressUpdate(Integer... progress) {

    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onPostExecute(String responseString) {
        super.onPostExecute(responseString);

            if (pDialog != null) {
                pDialog.dismiss();
            }


        Log.e("ETechAsyncTask:onPostExecute()", "status : " + status + ", Response : " + responseString);

        if (responseString != null) {
            callback.onTaskComplete(status, responseString, webserviceCb, "");
        } else {
            callback.onTaskComplete(status,
                   "Could not connect with server, please check your network connection", webserviceCb, "");
            /*
			 * else callback.onTaskComplete(ERROR,context.getString(R.string.
			 * response_error_msg),webserviceCb);
			 */
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCancelled() {
        super.onCancelled();
        try {
            if (pDialog != null) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
        }
        status = CANCELED;
        Log.e("ETechAsyncTask:onCancelled()", "status : " + status);
        callback.onTaskComplete(status,
                "Could not connect with server, please check your network connection", webserviceCb, "");
    }

    private void writeFormField(ByteArrayOutputStream baos, String fieldName,
                                String fieldValue) throws IOException {
        baos.write((twoHyphens + boundary + CRLF).getBytes());
        baos.write(("Content-Disposition: form-data;name=\"" + fieldName + "\"" + CRLF)
                .getBytes("UTF-8"));
        baos.write((CRLF + fieldValue + CRLF).getBytes());
    }

    private void writeFileField(ByteArrayOutputStream baos, String fieldName,
                                String fileName, String contentType, byte[] buf) throws IOException {
        baos.write((twoHyphens + boundary + CRLF).getBytes());
        baos.write(("Content-Disposition: form-data;name=\"" + fieldName
                + "\";filename=\"" + fileName + "\"" + CRLF).getBytes("UTF-8"));
        baos.write(("Content-Type: " + contentType + CRLF + CRLF).getBytes());
        writeFile(baos, buf);
        baos.write(CRLF.getBytes());
    }

    private void writeFile(ByteArrayOutputStream baos, byte[] buf) {
        try {
            byte[] data = new byte[20000];
            int nRead;
            InputStream fileInputStream = new ByteArrayInputStream(buf);
            while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
                Log.e("", "writeFile :: nRead : " + nRead);
                baos.write(data, 0, nRead);
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
