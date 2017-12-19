package com.omneagate.erbc.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.erbc.Activity.ConnectionRegisterationActivity;
import com.omneagate.erbc.R;

/**
 * Created by Shanthakumar on 05-08-2016.
 */
public class ImageRemoveDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private String title;
    private int which;

    public ImageRemoveDialog(Context context, String title, int which) {
        super(context);
        this.mContext = context;
        this.title = title;
        this.which = which;
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
        text.setText(title);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        Button cancelButton = (Button) findViewById(R.id.buttoncancel);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonNwOk:
                dismiss();
                switch (which) {
                    case 1:
                        ((ConnectionRegisterationActivity) mContext).hideCaptureCard();
                        break;
                    case 2:
                        ((ConnectionRegisterationActivity) mContext).hideCaptureBill();
                        break;
                    case 3:
                        ((ConnectionRegisterationActivity) mContext).hideCaptureMeter();
                        break;
                }
                break;

            case R.id.buttoncancel:
                dismiss();
                break;
        }
    }
}
