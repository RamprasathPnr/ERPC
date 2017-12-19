package com.omneagate.erbc.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.omneagate.erbc.R;


/*Custom progress dialog for user*/
public class CustomProgressDialog extends Dialog {

    //Constructor
    public CustomProgressDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progressbar);

    }

    @Override
    public void show() {
        super.show();
        Log.e("Progress bar", "Progress Bar appearance");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.e("Progress bar", "Progress Bar Dismiss");
    }
}
