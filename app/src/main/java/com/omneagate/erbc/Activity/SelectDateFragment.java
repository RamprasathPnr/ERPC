package com.omneagate.erbc.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import com.omneagate.erbc.R;
import java.util.Calendar;

/**
 * Created by user1 on 27/4/16.
 */
public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    String months[] = {"","Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};

    @Override
    public DatePickerDialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }


    public void populateSetDate(int year, int month, int day) {
        EditText datepicker =  (EditText)getActivity().findViewById(R.id.dates);
        datepicker.setText(day+"-"+months[month]+"-"+year);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        populateSetDate(year, monthOfYear+1, dayOfMonth);

    }


}
