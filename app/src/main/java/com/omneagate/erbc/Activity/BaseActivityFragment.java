package com.omneagate.erbc.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.NetworkConnection;

/**
 * Created by user1 on 5/5/16.
 */
public class BaseActivityFragment extends Fragment {

    //Network connectivity
    NetworkConnection networkConnection;

    //HttpConnection service
    public HttpClientWrapper httpConnection;


    /*Handler used to get response from server*/
    protected final Handler SyncHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ServiceListenerType type = (ServiceListenerType) msg.obj;
            switch (type) {
                case LOGIN_USER:
                    processMessage(msg.getData(), ServiceListenerType.LOGIN_USER);
                    break;

                case GETSTATE:
                    processMessage(msg.getData(), ServiceListenerType.GETSTATE);
                    break;
                case GET_DISTRICT:
                    processMessage(msg.getData(), ServiceListenerType.GET_DISTRICT);
                    break;
                case GET_TALUK:
                    processMessage(msg.getData(), ServiceListenerType.GET_TALUK);
                    break;

                case GET_VILLAGE:
                    processMessage(msg.getData(), ServiceListenerType.GET_VILLAGE);
                    break;
                case CONNECTION_INFO:
                    processMessage(msg.getData(), ServiceListenerType.CONNECTION_INFO);
                    break;
                case METER_INFO:
                    processMessage(msg.getData(), ServiceListenerType.METER_INFO);
                    break;
                case REG_CONNECTION:
                    processMessage(msg.getData(), ServiceListenerType.REG_CONNECTION);
                    break;


                default:
                    processMessage(msg.getData(), ServiceListenerType.ERROR_MSG);
                    break;
            }
        }

    };


    /*
     * abstract method for all activity
     * */
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public String unicodeToLocalLanguage(String keyString) {
        String unicodeString = null;
        try {
            unicodeString = new String(keyString.getBytes(), "UTF8");
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Exception while UTF", keyString);
        }
        return unicodeString;
    }
}

