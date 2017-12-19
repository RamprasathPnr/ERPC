package com.omneagate.erbc.Activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ReminderDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.AppProperties;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user1 on 13/6/16.
 */
public class ReminderActivity extends BaseActivity implements View.OnClickListener {

    TextView customername, customernumber, billcycledate, remindertype;
    EditText timeset;
    ArrayAdapter<String> reminderdays;
    ArrayList<String> reminderDueDateArray;
    ArrayList<String> reminderPerDayArray;
    TextInputLayout timeLay;
    MaterialBetterSpinner reminder_date, snooze_count;
    Button create_btn;
    ReminderDto reminderdto;

    CustomProgressDialog progressBar;
    private Calendar calendar;
    private DateFormat dateFormat;
    private DateFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remaindercreate);
        configureInitialPage();
    }

    private void configureInitialPage() {

        try {
            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            df = new SimpleDateFormat("dd-MMM-yyyy");
            networkConnection = new NetworkConnection(getApplicationContext());
            httpConnection = new HttpClientWrapper();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
            CustomerDto cusotmer = new CustomerDto();
            cusotmer.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            Intent intent = getIntent();
            String connenction = intent.getStringExtra("connectiondetail");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            reminderdto = gson.fromJson(connenction, ReminderDto.class);
            customername = (TextView) findViewById(R.id.consumer_name);
            customernumber = (TextView) findViewById(R.id.consumer_numbertxt);
            billcycledate = (TextView) findViewById(R.id.bill_cycle_datetxt);
            remindertype = (TextView) findViewById(R.id.reminder_month_txt);
            timeset = (EditText) findViewById(R.id.dates);
            create_btn = (Button) findViewById(R.id.create_btn);
            timeset.setShowSoftInputOnFocus(false);
            reminder_date = (MaterialBetterSpinner) findViewById(R.id.reminder_date);
            snooze_count = (MaterialBetterSpinner) findViewById(R.id.soonzetime);
            timeLay = (TextInputLayout) findViewById(R.id.layout_dob);
            customername.setText("" + reminderdto.getConnection().getConsumerName());
            customernumber.setText("" + reminderdto.getConnection().getConsumerDisplayNo());
            remindertype.setText(AppProperties.BILL_CYCLE_PERIOD_DAYS + " Days");

            /*calendar.setTime(dateFormat.parse(reminderdto.getConnection().getBillCycleFromDate()));
            String startTime = df.format(calendar.getTime());
            calendar.setTime(dateFormat.parse(reminderdto.getConnection().getBillCycleToDate()));
            String endTime = df.format(calendar.getTime());*/


            if (reminderdto.getReminderTime() == null) {
                timeset.setText("");
                reminder_date.setText("");
                snooze_count.setText("");
            } else {
                timeset.setText("" + reminderdto.getReminderTime());
                reminder_date.setText("" + reminderdto.getReminderDueDays());
                snooze_count.setText("" + reminderdto.getNoOfTimes());
            }

            reminderDueDateArray = new ArrayList<>();
            reminderPerDayArray = new ArrayList<>();

            reminderDueDateArray.add("0");
            reminderDueDateArray.add("1");
            reminderDueDateArray.add("2");
            reminderDueDateArray.add("3");

            reminderPerDayArray.add("1");
            reminderPerDayArray.add("2");
            reminderPerDayArray.add("3");

            reminderdays = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.dropdownrow, reminderDueDateArray);
            reminder_date.setAdapter(reminderdays);
            reminderdays = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.dropdownrow, reminderPerDayArray);
            snooze_count.setAdapter(reminderdays);
            timeset.setOnClickListener(this);
            create_btn.setOnClickListener(this);

            if (reminderdto.getConnection().isReminderAdded()) {
                create_btn.setText(getString(R.string.update));
                billcycledate.setText(Util.appDateFormat(reminderdto.getBillCycleFromDate())
                        + " TO " + Util.appDateFormat(reminderdto.getBillCycleToDate()));
            } else {
                create_btn.setText(getString(R.string.create));
                billcycledate.setText(Util.appDateFormat(reminderdto.getConnection().getBillCycleFromDate())
                        + " TO " + Util.appDateFormat(reminderdto.getConnection().getBillCycleToDate()));
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backapage = new Intent(ReminderActivity.this, ReminderListActivity.class);
        backapage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backapage);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.dates:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        try {
                            boolean isPM = (selectedHour >= 12);
                            timeset.setText(String.format("%02d:%02d %s", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute, isPM ? "PM" : "AM"));

                        } catch (Exception e) {
                            GlobalAppState.getInstance().trackException(e);
                            Log.e("Timeset", e.toString(), e);
                        }

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            case R.id.create_btn:
                if (networkConnection.isNetworkAvailable()) {
                    validate_reminder();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    private void validate_reminder() {
        try {
            if (!validatedate()) {
                return;
            }
            String remindercount = reminder_date.getText().toString();
            String snoozeStr = snooze_count.getText().toString();
            if (StringUtils.isEmpty(remindercount)) {

                reminder_date.setError(getString(R.string.reminder_count_error));
                return;
            }
            if (StringUtils.isEmpty(snoozeStr)) {

                snooze_count.setError(getString(R.string.snooze_error));
                return;
            }
            ReminderDto reminderRecord = getReminderdetails();
            reminderregister(reminderRecord);


        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("remindervalid", e.toString(), e);
        }


    }

    private boolean validatedate() {
        if (timeset.getText().toString().trim().isEmpty()) {

            timeLay.setError(getString(R.string.timer_error));
            requestFocus(timeset);

            return false;
        } else {
            timeLay.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private ReminderDto getReminderdetails() {
        try {
            CustomerDto customerRecord = DBHelper.getInstance(ReminderActivity.this).getcustomerData();
            ReminderDto remindet_data = new ReminderDto();
            String time_str = timeset.getText().toString();
            String reminderdue_str = reminder_date.getText().toString();
            String snooze_str = snooze_count.getText().toString();
            remindet_data.setCustomerId("" + customerRecord.getId());
            remindet_data.setCustomerEmail("" + customerRecord.getEmail());
            remindet_data.setCustomerMobile("" + customerRecord.getMobileNumber());
            remindet_data.setConnection(reminderdto.getConnection());
            remindet_data.setId(reminderdto.getId());
            remindet_data.setReminderDueDays("" + reminder_date.getText().toString());
            remindet_data.setNoOfTimes("" + snooze_count.getText().toString());
            System.out.println("Sham Checking from date :::" + reminderdto.getBillCycleFromDate());
            if (reminderdto.getConnection().isReminderAdded()) {
                remindet_data.setBillCycleFromDate(Util.appDateFormat(reminderdto.getBillCycleFromDate()));
                remindet_data.setBillCycleToDate(Util.appDateFormat(reminderdto.getBillCycleToDate()));
            } else {
                remindet_data.setBillCycleFromDate(Util.appDateFormat(reminderdto.getConnection().getBillCycleFromDate()));
                remindet_data.setBillCycleToDate(Util.appDateFormat(reminderdto.getConnection().getBillCycleToDate()));
            }

            remindet_data.setReminderTime(remindertype.getText().toString());
            remindet_data.setReminderTime(time_str);
            remindet_data.setReminderType(AppProperties.BILL_CYCLE_PERIOD_DAYS + " Days");

            return remindet_data;
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString(), e);
        }
        return null;
    }

    private void reminderregister(ReminderDto reminderdata) {
        try {
            progressBar = new CustomProgressDialog(ReminderActivity.this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/reminder/add";
                String login = new Gson().toJson(reminderdata);
                System.out.println("Sham Checking Reminder value :::" + login);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.REMINDER_ADD, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("reminderActivity", e.toString(), e);
        }

    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case REMINDER_ADD:
                dismissProgress();
                reminderresponse(message);
                break;
            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void reminderresponse(Bundle message) {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        Log.e("response_data", "" + response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        ReminderDto reminderresponse = gson.fromJson(response, ReminderDto.class);
        if (reminderresponse != null) {
            if (reminderresponse.getStatusCode() == 0) {
                AlertDialog alertdialog = new AlertDialog(ReminderActivity.this, getString(R.string.reminder_reponse));
                alertdialog.show();
            } else {
                if (reminderresponse.getErrorDescription() != null
                        && reminderresponse.getErrorDescription().length() > 0) {
                    Toast.makeText(getApplicationContext(), reminderresponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
        }
    }
}
