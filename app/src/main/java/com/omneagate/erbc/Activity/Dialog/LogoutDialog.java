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

import com.omneagate.erbc.Activity.ConnectionDetailActivity;
import com.omneagate.erbc.Activity.ConnectionListActivity;
import com.omneagate.erbc.Activity.LoginActivity;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.DBHelper;

/**
 * Created by user1 on 11/5/16.
 */
public class LogoutDialog extends Dialog implements
        View.OnClickListener {


    private final Context context;

    String subtitle;

    /*Constructor class for this dialog*/
    public LogoutDialog(Context _context, String title) {
        super(_context);
        context = _context;
        subtitle = title;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.logoutdialog);
        setCancelable(false);
        TextView text = (TextView) findViewById(R.id.textViewNwText);
        text.setText(subtitle);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        Button cancelButton = (Button) findViewById(R.id.buttoncancel);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:

                if (subtitle.equals(context.getString(R.string.logoutString))) {
                    dismiss();
                    DBHelper.getInstance(context).deleteConnection();
                    Intent registerPage = new Intent(context, LoginActivity.class);
                    registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(registerPage);
                    ((Activity) context).finish();
                } else if (subtitle.equalsIgnoreCase(context.getString(R.string.connnecion_delte_mdg1))) {
                    dismiss();
                    ((ConnectionListActivity) context).deleteConnection();
//                    ((ConnectionDetailActivity) context).deleteConnection();
                } else {
                    dismiss();
//                    ((ConnectionListActivity) context).deleteConnection();
                    ((ConnectionDetailActivity) context).deleteConnection();
                }
                break;
            case R.id.buttoncancel:
                dismiss();
                break;

        }
    }
}
