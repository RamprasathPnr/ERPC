package com.omneagate.erbc.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.omneagate.erbc.R;

/**
 * Created by Shanthakumar on 29-07-2016.
 */
public class PaymentFailureDialog extends Dialog {
    private Context dialogContext;

    public PaymentFailureDialog(Context context) {
        super(context);
        this.dialogContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_bill_failure);
        setCancelable(false);

        int width = dialogContext.getResources().getDisplayMetrics().widthPixels;
        int height = dialogContext.getResources().getDisplayMetrics().heightPixels;
        int newWidth = (width * 80) / 100;
        int newHeight = (height * 32) / 100;
        View view_instance = (View) findViewById(R.id.ll_dialog);
        ViewGroup.LayoutParams params = view_instance.getLayoutParams();
        params.width = newWidth;
        params.height = newHeight;
        view_instance.setLayoutParams(params);

        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


}
