package com.omneagate.erbc.Activity.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.DBHelper;

import org.apache.commons.lang3.StringUtils;

/**
 * This dialog will appear on the time of user logout
 */
public class ChangeUrlDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public ChangeUrlDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.changeurl);
        setCancelable(false);
        String serverUrl = DBHelper.getInstance(context).getMasterData("serverUrl");
       ((EditText) findViewById(R.id.editTextUrl)).setText(serverUrl);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                if (storeInLocal()) {
                    dismiss();
                }
                break;
            case R.id.buttonNwCancel:
                InputMethodManager imm = (InputMethodManager) context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                dismiss();
                break;
        }
    }



    /**
     * Store changed ip in shared preference
     * returns true if value present else false
     */
    private boolean storeInLocal() {
        EditText urlText = (EditText) findViewById(R.id.editTextUrl);
        String url = urlText.getText().toString().trim();
        if (StringUtils.isEmpty(url) || url.length() < 4) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(urlText.getWindowToken(), 0);
        DBHelper.getInstance(context).updateMaserData("serverUrl", url);

        return true;
    }
}