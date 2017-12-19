package com.omneagate.erbc.Activity.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.erbc.Activity.ConnectionListActivity;
import com.omneagate.erbc.Activity.ConnectionRegisterationActivity;
import com.omneagate.erbc.Activity.LoginActivity;
import com.omneagate.erbc.R;

/**
 * Created by Shanthakumar on 05-08-2016.
 */
public class BackPressedDialog extends Dialog {
    private Context dialogContext;
    private String title;
    private TextView mTextViewTitle;
    private Button mButtonCancel;
    private Button mButtonExit;

    public BackPressedDialog(Context context, String title) {
        super(context);
        this.dialogContext = context;
        this.title = title;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_back_press);


//        int width = dialogContext.getResources().getDisplayMetrics().widthPixels;
//        int height = dialogContext.getResources().getDisplayMetrics().heightPixels;
//        int newWidth = (width * 80) / 100;
//        int newHeight = (height * 34) / 100;
//        View view_instance = (View) findViewById(R.id.ll_dialog);
//        ViewGroup.LayoutParams params = view_instance.getLayoutParams();
//        params.width = newWidth;
//        params.height = newHeight;
//        view_instance.setLayoutParams(params);

        mTextViewTitle = (TextView) findViewById(R.id.txt_title);
        mTextViewTitle.setText(title);
        mButtonCancel = (Button) findViewById(R.id.btn_Cancel);
        mButtonExit = (Button) findViewById(R.id.btn_exit);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                if (title.equalsIgnoreCase(dialogContext.getString(R.string.title_registration))) {
                    Intent registerPage = new Intent(dialogContext, LoginActivity.class);
                    registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogContext.startActivity(registerPage);
                    ((Activity) dialogContext).finish();

                } else if (title.equalsIgnoreCase(dialogContext.getString(R.string.title_connection))) {
                    new ConnectionRegisterationActivity(). clearTempFile();

                    Intent registerPage = new Intent(dialogContext, ConnectionListActivity.class);
                    registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogContext.startActivity(registerPage);
                    ((Activity) dialogContext).finish();
                }
            }
        });
    }
}
