package com.omneagate.erbc.Services;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;

public class HttpClientWrapper {


    /**
     * Used to send http request to FPS server and return the data back
     *
     * @param extra,requestData,method,entity
     */
    public void sendRequest(final String requestData, final Bundle extra,
                            final ServiceListenerType what, final Handler messageHandler,
                            final RequestType method, final StringEntity entity, final Context context) {


        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                BufferedReader in = null;
                Message msg = Message.obtain();
                msg.obj = what;
                try {
                    String serverUrl = DBHelper.getInstance(context).getMasterData("serverUrl");
                    String url = serverUrl+requestData;
                    Log.e("serverUrl", "serverUrl" + url);
                    URI website = new URI(url);
                    HttpResponse response = requestType(website, method, entity);
                    Log.e("HttpResponse", "HttpResponse received" + response);
                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    StringBuffer sb = new StringBuffer("");
                    String l;
                    String nl = System.getProperty("line.separator");
                    Log.e("getProperty", nl);
                    while ((l = in.readLine()) != null) {
                        Log.e("readLine", l);
                        sb.append(l + nl);
                    }
                    in.close();
                    String responseData = sb.toString();
                    Log.e("Response", responseData);
                    if (responseData.contains("timestamp") && responseData.contains("exception")) {
                        responseData = "";
                    }
                    Bundle b = new Bundle();
                    if (extra != null)
                        b.putAll(extra);
                    if (responseData.trim().length() != 0) {
                        b.putString(DBConstants.RESPONSE_DATA, responseData);
                    } else {
                        msg.obj = ServiceListenerType.ERROR_MSG;
                        b.putString(DBConstants.RESPONSE_DATA, "Empty Data");
                    }
                    msg.setData(b);
                } catch (SocketTimeoutException e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("SendRequest", "SocketTimeoutException", e);
                    msg.obj = ServiceListenerType.ERROR_MSG;
                    Bundle b = new Bundle();
                    if (extra != null)
                        b.putAll(extra);
                    b.putString(DBConstants.RESPONSE_DATA,
                            "Cannot establish connection to server. Please try again later.");
                    msg.setData(b);
                } catch (SocketException e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("SendRequest", "SocketException", e);
                    msg.obj = ServiceListenerType.ERROR_MSG;
                    Bundle b = new Bundle();
                    if (extra != null)
                        b.putAll(extra);
                    b.putString(DBConstants.RESPONSE_DATA,
                            context.getString(R.string.connectionError));
                    msg.setData(b);
                } catch (IOException e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("SendRequest", "IOException", e);
                    msg.obj = ServiceListenerType.ERROR_MSG;
                    Bundle b = new Bundle();
                    if (extra != null)
                        b.putAll(extra);
                    b.putString(DBConstants.RESPONSE_DATA, ""
                            + "Internal Error.Please Try Again");
                    msg.setData(b);
                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("SendRequest", "Exception", e);
                    msg.obj = ServiceListenerType.ERROR_MSG;
                    Bundle b = new Bundle();
                    if (extra != null)
                        b.putAll(extra);
                    b.putString(DBConstants.RESPONSE_DATA, context.getString(R.string.connectionRefused));
                    msg.setData(b);
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                        Log.e("HTTP", "Error", e);
                    }
                    messageHandler.sendMessage(msg);
                }

            }
        }.start();

    }

    /**
     * return http GET,POST and PUT method using parameters
     *  @param uri,method
     * @param uri
     */
    private HttpResponse requestType(URI uri, RequestType method,
                                     StringEntity entity) {
        try {
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 15000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            int timeoutSocket = 15000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient client = new DefaultHttpClient(httpParameters);
            switch (method) {
                case POST:
                    HttpPost postRequest = new HttpPost();
                    postRequest.setURI(uri);
                    postRequest.setHeader("Content-Type", "application/json");
                    postRequest.setEntity(entity);
                    return client.execute(postRequest);
                case PUT:
                    HttpPut putRequest = new HttpPut();
                    putRequest.setURI(uri);
                    putRequest.setHeader("Content-Type", "application/json");
                    putRequest.setEntity(entity);
                    return client.execute(putRequest);
                case GET:
                    HttpGet getRequest = new HttpGet();
                    getRequest.setURI(uri);
                    getRequest.setHeader("Content-Type", "application/json");
                    return client.execute(getRequest);
            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Cookie excep", e.toString(), e);
        }
        return null;
    }

}