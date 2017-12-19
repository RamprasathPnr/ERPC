package com.omneagate.erbc.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.omneagate.erbc.Activity.GlobalAppState;

/**
 * Created by Shanthakumar on 10-08-2016.
 */
public class OtpMessageServices extends BroadcastReceiver {

    private static OtpListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d("Sham", "Enter the onReceive method :::");
            Bundle data = intent.getExtras();

            Object[] pdus = (Object[]) data.get("pdus");

            for (int i = 0; i < pdus.length; i++) {
                Log.d("Sham", "Enter the For loop method :::");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

                String sender = smsMessage.getDisplayOriginatingAddress();
                //You must check here if the sender is your provider and not another one with same text.

                String messageBody = smsMessage.getMessageBody();

                //Pass on the text to our listener.
                if (sender.equals("DZ-TNEPDS")
                        || sender.equals("IT-OAERPC")) {
                    mListener.messageReceived(messageBody);
                }
            }
        }catch (Exception e){
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    public static void bindListener(OtpListener listener) {
        try {
            Log.d("Sham", "Enter the bindListener method :::");
            mListener = listener;
        }catch (Exception e){
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }
}
