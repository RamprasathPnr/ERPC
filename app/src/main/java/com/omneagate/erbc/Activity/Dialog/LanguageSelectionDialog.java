package com.omneagate.erbc.Activity.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.MySharedPreference;
import com.omneagate.erbc.Util.Util;


/**
 * This dialog will appear on the time of user logout
 */
public class LanguageSelectionDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user

    private boolean tamil = false;

    /*Constructor class for this dialog*/
    public LanguageSelectionDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_language);
        setCancelable(false);
        /*((TextView) findViewById(R.id.textViewNwTitleEn)).setText(new Util().unicodeToLocalLanguage(context.getResources().getString(R.string.languageSelectionEn)));
        ((TextView) findViewById(R.id.textViewNwTitleTamil)).setText(new Util().unicodeToLocalLanguage(context.getResources().getString(R.string.languageSelectionTa)));*/


        setTamilText((TextView) findViewById(R.id.textViewNwTitleEn), R.string.languageSelectionEn);
        setTamilText((TextView) findViewById(R.id.textViewNwTitleTamil), R.string.languageSelectionTa);
        setTamilText((TextView) findViewById(R.id.tamilText), R.string.tamil);
        setTamilText((Button) findViewById(R.id.buttonNwOk), R.string.ok);
        setTamilText((Button) findViewById(R.id.buttonNwCancel), R.string.cancel);

        if (GlobalAppState.language.equals("ta")) {
            tamil = true;
        }

        setBackGround(tamil);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        cancelButton.setOnClickListener(this);
        findViewById(R.id.language_english).setOnClickListener(this);
        findViewById(R.id.language_tamil).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                changeLanguage();
                dismiss();
                break;
            case R.id.buttonNwCancel:
                dismiss();
                break;
            case R.id.language_english:
                tamil = false;
                setBackGround(tamil);
                break;
            case R.id.language_tamil:
                tamil = true;
                setBackGround(tamil);
                break;
        }
    }

    private void setBackGround(boolean isTamil) {
        if (isTamil) {

            findViewById(R.id.tamilSelection).setVisibility(View.VISIBLE);
            findViewById(R.id.englishSelection).setVisibility(View.GONE);
            findViewById(R.id.language_english).setBackgroundResource(R.drawable.background_grey);
            findViewById(R.id.language_tamil).setBackgroundResource(R.drawable.background_pink);
        } else {

            findViewById(R.id.tamilSelection).setVisibility(View.GONE);
            findViewById(R.id.englishSelection).setVisibility(View.VISIBLE);
            findViewById(R.id.language_english).setBackgroundResource(R.drawable.background_pink);
            findViewById(R.id.language_tamil).setBackgroundResource(R.drawable.background_grey);
        }
    }


    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(RadioButton textName, int id) {
       // textName.setText( context.getString(id));
          textName.setText("Button");
    }

    //Re-starts the application where language change take effects
    private void restartApplication() {
        Intent restart = context.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(context.getBaseContext().getPackageName());
        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(restart);
        context.finish();

    }

   /*
     Used to change language
    */
    private void changeLanguage() {

        if (tamil) {
            Util.changeLanguage(context, "ta");
            String language = "ta";
            MySharedPreference.writeString(context.getApplicationContext(),
                    MySharedPreference.LANGUAGE_SELECT, language);
            Log.e("lan", "ta");

        } else {
            Util.changeLanguage(context, "en");
            String language = "en";
            MySharedPreference.writeString(context.getApplicationContext(),
                    MySharedPreference.LANGUAGE_SELECT, language);
            Log.e("lan", "en");
        }
        restartApplication();
    }




    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
//        textName.setText( new Util().unicodeToLocalLanguage(context.getResources().getString(id)));
    }

    public void setTamilText(Button btnName, int id) {
//        btnName.setText( new Util().unicodeToLocalLanguage(context.getResources().getString(id)));
    }
}