package com.omneagate.erbc.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;


/**
 * BaseActivity is the base class for all activities
 */
public abstract class BaseActivity extends AppCompatActivity {

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
            Log.e("Base Activity","service Listner Type"+type);
            switch (type) {

                case LOGIN_USER:
                    processMessage(msg.getData(), ServiceListenerType.LOGIN_USER);
                    break;

                case PERSONAL_INFO:
                    processMessage(msg.getData(), ServiceListenerType.PERSONAL_INFO);
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

                case REGISTER_USER:
                    processMessage(msg.getData(), ServiceListenerType.REGISTER_USER);
                    break;

                case SEND_OTP:
                    processMessage(msg.getData(), ServiceListenerType.SEND_OTP);
                    break;

                case GET_REGION:
                    processMessage(msg.getData(), ServiceListenerType.GET_REGION);
                    break;

                case GET_SECTION:
                    processMessage(msg.getData(), ServiceListenerType.GET_SECTION);
                    break;

                case CONNECTION_LIST:
                    processMessage(msg.getData(), ServiceListenerType.CONNECTION_LIST);
                    break;

                case GET_CONNECTIONCHECK:
                    processMessage(msg.getData(), ServiceListenerType.GET_CONNECTIONCHECK);
                    break;

                case CONNECTION_DELETE:
                    processMessage(msg.getData(), ServiceListenerType.CONNECTION_DELETE);
                    break;

                case CALCUL_METER:
                    processMessage(msg.getData(), ServiceListenerType.CALCUL_METER);
                    break;

                case BILL_HISTORY:
                    processMessage(msg.getData(), ServiceListenerType.BILL_HISTORY);
                    break;
                case PAY_BILL:
                    processMessage(msg.getData(), ServiceListenerType.PAY_BILL);
                    break;

                case PAYMENT_LIST:
                    processMessage(msg.getData(), ServiceListenerType.PAYMENT_LIST);
                    break;

                case CONNECTION_INFO:
                    processMessage(msg.getData(), ServiceListenerType.CONNECTION_INFO);
                    break;

                case GET_DISCOM:
                    processMessage(msg.getData(), ServiceListenerType.GET_DISCOM);
                    break;

                case REMINDER_ADD:
                    processMessage(msg.getData(), ServiceListenerType.REMINDER_ADD);
                    break;

                case CHECKVERSION:
                    processMessage(msg.getData(), ServiceListenerType.CHECKVERSION);
                    break;

                case AUTOUPGRADE:
                    processMessage(msg.getData(), ServiceListenerType.AUTOUPGRADE);
                    break;

                case AUTOUPGRADE_ACTIVE:
                    processMessage(msg.getData(), ServiceListenerType.AUTOUPGRADE_ACTIVE);
                    break;

                case METER_INFO:
                    processMessage(msg.getData(), ServiceListenerType.METER_INFO);
                    break;

                case REG_CONNECTION:
                    processMessage(msg.getData(), ServiceListenerType.REG_CONNECTION);
                    break;

                case BILL_INFO:
                    processMessage(msg.getData(), ServiceListenerType.BILL_INFO);
                    break;

                case GRIEVANCE_CAT:
                    processMessage(msg.getData(), ServiceListenerType.GRIEVANCE_CAT);
                    break;

                case COMPLAINT_SUBCAT:
                    processMessage(msg.getData(), ServiceListenerType.COMPLAINT_SUBCAT);
                    break;

                case COMPLAINT_SUBMIT:
                    processMessage(msg.getData(), ServiceListenerType.COMPLAINT_SUBMIT);
                    break;

                case GRIEVANCE_LIST:
                    processMessage(msg.getData(), ServiceListenerType.GRIEVANCE_LIST);
                    break;

                case GRIEVANCE_FEEDBACK:
                    processMessage(msg.getData(), ServiceListenerType.GRIEVANCE_FEEDBACK);
                    break;

                case PAY_BILL_HISTORY:
                    processMessage(msg.getData(), ServiceListenerType.PAY_BILL_HISTORY);
                    break;

                case CONSUMER_TYPE:
                    processMessage(msg.getData(), ServiceListenerType.CONSUMER_TYPE);
                    break;

                case PHASE_TYPE:
                    processMessage(msg.getData(), ServiceListenerType.PHASE_TYPE);
                    break;
                case GETGENERATEBILLS:
                    processMessage(msg.getData(), ServiceListenerType.GETGENERATEBILLS);
                    break;
                case SENDBILLS:
                    processMessage(msg.getData(),ServiceListenerType.SENDBILLS);
                    break;
                case GETGRAPHDETAILS:
                    processMessage(msg.getData(),ServiceListenerType.GETGRAPHDETAILS);
                    break;

                case ADDAPPLIANCEBYCONNECTION:
                    processMessage(msg.getData(),ServiceListenerType.ADDAPPLIANCEBYCONNECTION);
                    break;

                case GETAPPLIANCEBYCONNECTION:
                    processMessage(msg.getData(),ServiceListenerType.GETAPPLIANCEBYCONNECTION);
                    break;

                case GETCHARGESFORAPPLIANCE:
                    processMessage(msg.getData(),ServiceListenerType.GETCHARGESFORAPPLIANCE);
                    break;
                case ADDAPPLIANCES:
                    processMessage(msg.getData(),ServiceListenerType.ADDAPPLIANCES);
                    break;
                case DELETEAPPLIANCES:
                    processMessage(msg.getData(),ServiceListenerType.DELETEAPPLIANCES);
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
    protected abstract void processMessage(Bundle message, ServiceListenerType what);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageCode =  GlobalAppState.language;
        if (languageCode == null) {
            languageCode = "en";
        }
        Util.changeLanguage(this, languageCode);
    }

    /*protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);



    }*/




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
