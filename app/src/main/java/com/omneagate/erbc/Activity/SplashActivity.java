package com.omneagate.erbc.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.InsertIntoDatabase;
import com.omneagate.erbc.Util.MySharedPreference;

import java.util.Locale;

//import com.google.android.gcm.GCMRegistrar;
//import com.omneagate.erbc.Gcm.ServerUtilities;

/*import static com.omneagate.erbc.Gcm.CommonUtilities.DISPLAY_MESSAGE_ACTION;
//import static com.omneagate.erbc.Gcm.CommonUtilities.EXTRA_MESSAGE;
import static com.omneagate.erbc.Gcm.CommonUtilities.SENDER_ID;
import static com.omneagate.erbc.Gcm.CommonUtilities.SERVER_URL;*/

/**
 * Created by user1 on 26/4/16.
 */
public class SplashActivity extends Activity {
    Locale myLocale;
    AsyncTask<Void, Void, Void> mRegisterTask;
    private TextView mTvAppTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        checkNotNull(SERVER_URL, "SERVER_URL");
//        checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
//        GCMRegistrar.checkDevice(this);
//        // Make sure the manifest was properly set - comment out this line
//        // while developing the app, then uncomment it when it's ready.
//        GCMRegistrar.checkManifest(this);

        setContentView(R.layout.splashactivity);
        mTvAppTitle = (TextView) findViewById(R.id.textView4);
//        regGcm();

        Thread timerThread = new Thread() {
            public void run() {

                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    GlobalAppState.getInstance().trackException(e);
                    e.printStackTrace();
                } finally {
                    SQLiteDatabase db = new DBHelper(getApplicationContext()).getWritableDatabase();
                    if (DBHelper.getInstance(getApplicationContext()).getCheck()) {
                        DBHelper.getInstance(getApplicationContext()).insertValues();

                    }
                    InsertIntoDatabase database = new InsertIntoDatabase(SplashActivity.this);
                    database.insertIntoDatabase();

                    Log.e("user_count", "" + DBHelper.getInstance(getApplicationContext()).getCustomerCount());


                    if (DBHelper.getInstance(getApplicationContext()).getCustomerCount() == 0) {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LandingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        };
        languageChcek();
        timerThread.start();
    }

    public String languageChcek() {
        String language = null;
        language = MySharedPreference.readString(getApplicationContext(),
                MySharedPreference.LANGUAGE_SELECT, "");
        setLocale(language);
        return language;
    }

    /*public void regGcm(){
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
//                msg.append(getString(R.string.already_registered) + "\n");

            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }*/


    public void setLocale(String lang) {
        if (lang == null
                || lang.length() <= 0
                || lang.equalsIgnoreCase(""))
            lang = "en";
        GlobalAppState.language = lang;
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        mTvAppTitle.setText(getString(R.string.erpc));
        /*Intent refresh = new Intent(this, AndroidLocalize.class);
        startActivity(refresh);
        finish();*/
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
//                    String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
//                    GlobalData.notificationTxt = newMessage.toString();
//                    Log.e("onReceive newMessage in splash Activity ", GlobalData.notificationTxt);
//            Toast.makeText(getBaseContext(), GlobalData.notificationTxt, Toast.LENGTH_LONG).show();
                    //   msg.append(newMessage + "\n");
                }
            };

}
