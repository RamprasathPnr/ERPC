package com.omneagate.erbc;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.omneagate.erbc.Activity.BaseActivity;
import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Activity.LoginActivity;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Util.GMAILSender;
import com.omneagate.erbc.Util.MySharedPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Locale;

public class CrashActivity extends BaseActivity {
    Locale myLocale;

    GMAILSender gmailSender;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crashactivity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.e("testcrash", "test");
        sendLogFile();
        languageChcek();

    }


    @Override
    public void onBackPressed() {
    }


    public void continueLogin(View v) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }

    public String languageChcek() {
        String language = null;
        language = MySharedPreference.readString(getApplicationContext(),
                MySharedPreference.LANGUAGE_SELECT, "");
        setLocale(language);
        return language;
    }

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

    }

    private String extractLogToFile() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
            GlobalAppState.getInstance().trackException(e2);
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        // Make file name - file must be saved to external storage or it wont be readable by
        // the email app.
        String path = Environment.getExternalStorageDirectory() + "/" + "MyApp/";
        String fullName = path + "crashLog.txt";
        final File dir = new File(path);
        dir.mkdirs(); //create folders where write files
        final File file = new File(dir, "crashLog.txt");
        Log.e("File full name", fullName);
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
                    "logcat -d -v time";

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            // write output stream
            writer = new FileWriter(file);
            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
            writer.write("Device: " + model + "\n");
            writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                if (n == -1)
                    break;
                writer.write(buffer, 0, n);
            } while (true);

            reader.close();
            writer.close();
        } catch (IOException e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("IOException", e.toString(), e);
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                    GlobalAppState.getInstance().trackException(e1);
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                    GlobalAppState.getInstance().trackException(e1);
                }

            // You might want to write a failure message to the log here.
            return null;
        }

        return fullName;
    }

    private void sendLogFile() {
        try {
            String fullName = extractLogToFile();
            if (fullName == null)
                return;
            gmailSender = new GMAILSender("omneagatecrash@gmail.com", "finatelcrash");
            String[] toArr = {"m.bharathkumar@finateltech.in"};
            gmailSender.set_to(toArr);
            gmailSender.set_from("omneagatecrash@gmail.com");
            gmailSender.set_subject("Crashed on the ERPC App:" + new Date());
            gmailSender.setBody("Attached the crashLog with this.");
            gmailSender.addAttachment(fullName);
            new MyAsyncClass().execute();

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Exception in crash", e.toString(), e);
        }
    }


    class MyAsyncClass extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                gmailSender.send();
            } catch (Exception ex) {
                GlobalAppState.getInstance().trackException(ex);
                Log.e("MailApp", "Could not send email", ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


}
