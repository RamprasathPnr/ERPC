package com.omneagate.erbc.Activity.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.omneagate.erbc.Activity.RegisterationActivity;
import com.omneagate.erbc.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user1 on 29/6/16.
 */
public class CalendarDialog extends Dialog implements View.OnClickListener{

    private final Activity context;  //    Context from the user
    MaterialCalendarView widget;//


    public CalendarDialog(Context context, Activity context1) {
        super(context);
        this.context = context1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_date_selection);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        widget.setMaximumDate(new Date());
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                ((RegisterationActivity) context).setToTextDate(sdf.format(widget.getSelectedDate().getDate()));
                dismiss();
                break;
            case R.id.buttonNwCancel:
                dismiss();
                break;
        }
    }

}
