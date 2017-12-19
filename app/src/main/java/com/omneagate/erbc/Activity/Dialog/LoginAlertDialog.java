package com.omneagate.erbc.Activity.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.erbc.Activity.ConnectionListActivity;
import com.omneagate.erbc.Activity.LandingActivity;
import com.omneagate.erbc.Activity.LicenseAgreementActivity;
import com.omneagate.erbc.Activity.LoginActivity;
import com.omneagate.erbc.Activity.RegisterationActivity;
import com.omneagate.erbc.Activity.ReminderListActivity;
import com.omneagate.erbc.R;

/**
 * Created by root on 3/11/16.
 */
public class LoginAlertDialog extends Dialog implements
        View.OnClickListener {


    private final Context context;
    String title;
    String subtitle;

    /*Constructor class for this dialog*/
    public LoginAlertDialog(Context _context, String subheaderStr) {
        super(_context);
        context = _context;
        subtitle = subheaderStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_login_alert);
        setCancelable(false);
        ((TextView) findViewById(R.id.textViewNwText)).setText("" + subtitle);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
        Button cancelButton=(Button)findViewById(R.id.buttonNWCancel);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                dismiss();
                if (subtitle.equals(context.getString(R.string.login_success)) || subtitle.equals(context.getString(R.string.profileupdate))) {
                    Intent registerPage = new Intent(context, LandingActivity.class);
                    registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(registerPage);
                    ((Activity) context).finish();

                } else if (subtitle.equals(context.getString(R.string.connnecion_register_mdg))) {
                    Intent registerPage = new Intent(context, ConnectionListActivity.class);
                    registerPage.putExtra("before_page", "connectionActivity");
                    context.startActivity(registerPage);
                    ((Activity) context).finish();
                } else if (subtitle.equals(context.getString(R.string.connnecion_delete_mdg))) {
                    Intent registerPage = new Intent(context, ConnectionListActivity.class);
                    registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(registerPage);
                    ((Activity) context).finish();
                } else if (subtitle.equals(context.getString(R.string.logoutString))) {
                    Intent registerPage = new Intent(context, LoginActivity.class);
                    registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(registerPage);
                    ((Activity) context).finish();
                } else if (subtitle.equals(context.getString(R.string.otp_mismatch)) || subtitle.equals(context.getString(R.string.otp_expired)) || subtitle.equals(context.getString(R.string.payment_fail))) {

                } else if (subtitle.equals(context.getString(R.string.payment_success))) {
                    Intent registerPage = new Intent(context, LandingActivity.class);
                    registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(registerPage);
                    ((Activity) context).finish();
                } else if (subtitle.equals(context.getString(R.string.reminder_reponse))) {
                    Intent registerPage = new Intent(context, ReminderListActivity.class);
                    registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(registerPage);
                    ((Activity) context).finish();
                } else if (subtitle.equals(context.getString(R.string.register_mdg))) {
                    ((RegisterationActivity) context).call_otp();
                } else if (subtitle.equals(context.getString(R.string.complaint_reg_success))) {

                    Intent registerPage = new Intent(context, LandingActivity.class);
                    registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(registerPage);
                    ((Activity) context).finish();

                } else {
                    Intent homeIntent = new Intent(context, LicenseAgreementActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(homeIntent);
                    ((Activity) context).finish();
                }
                break;
            case R.id.buttonNWCancel:
                dismiss();
                break;
        }
    }
}
