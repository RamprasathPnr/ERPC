package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ReminderDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by user1 on 20/6/16.
 */
public class ReminderListActivity extends BaseActivity {

    private AppAdapter mAdapter = new AppAdapter();
    CustomProgressDialog progressBar;
    SwipeMenuListView listView;
    List<ReminderDto> connectionListRecord = new ArrayList<>();
    ConnectionDto ConnectionDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noreminder);
        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
        configureInitialPage();
    }

    private void configureInitialPage() {
        networkConnection = new NetworkConnection(getApplicationContext());
        httpConnection = new HttpClientWrapper();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        CustomerDto cusotmer = new CustomerDto();
        cusotmer.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
        connnectionList_Info(cusotmer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent nextpage = new Intent(ReminderListActivity.this, ReminderActivity.class);
                ReminderDto customer = connectionListRecord.get(position);
                nextpage.putExtra("connectiondetail", new Gson().toJson(customer));
                nextpage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(nextpage);
                finish();

            }
        });
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ConnectionCheckActivity.class));
                finish();
            }
        });*/

    }

    private void connnectionList_Info(CustomerDto connection) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/reminder/getbycustomer";
                String login = new Gson().toJson(connection);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_LIST, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionError));
                //  Toast.makeText(getApplicationContext(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return connectionListRecord.size();
        }

        @Override
        public ConnectionDto getItem(int position) {
            return connectionListRecord.get(position).getConnection();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.reminderitem, null);
                    new ViewHolder(convertView);
                }
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.connectionNumber = (TextView) convertView.findViewById(R.id.connenction_number);
                holder.connectionName = (TextView) convertView.findViewById(R.id.connectionname);
                holder.createdays = (TextView) convertView.findViewById(R.id.todate_id);
                holder.bell = (ImageView) convertView.findViewById(R.id.bellimage);
                holder.connectionNumber.setText(connectionListRecord.get(position).getConnection().getConsumerDisplayNo());
                holder.connectionName.setText(connectionListRecord.get(position).getConnection().getConsumerName());

                //holder.createdays.setText(connectionListRecord.get(position).getConnection().getBillCycleToDate());
                Log.e("bell_status", "" + connectionListRecord.get(position).getConnection().isReminderAdded());
                if (connectionListRecord.get(position).getConnection().isReminderAdded()) {
                    holder.bell.setVisibility(View.VISIBLE);
                    holder.createdays.setText(Util.appDateFormat(connectionListRecord.get(position)
                            .getBillCycleToDate()));
                } else {
                    holder.createdays.setText(Util.appDateFormat(connectionListRecord.get(position)
                            .getConnection().getBillCycleToDate()));
                    holder.bell.setVisibility(View.INVISIBLE);
                }

            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e("listviewException", e.toString(), e);
            }

            return convertView;
        }

        class ViewHolder {

            public TextView connectionNumber, connectionName, createdays;
            ImageView bell;

            public ViewHolder(View view) {

                view.setTag(this);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        Intent backpage = new Intent(ReminderListActivity.this, LandingActivity.class);
        startActivity(backpage);
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {

            case CONNECTION_LIST:
                dismissProgress();
                connectionlist_Response(message);
                break;

            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
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

    private void connectionlist_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("personal_Response", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ReminderDto reminderList;
            reminderList = gson.fromJson(response, ReminderDto.class);
            if (reminderList != null) {
                if (reminderList.getStatusCode() == 0) {
                    connectionListRecord = reminderList.getCustomerReminders();
                    listView.setAdapter(mAdapter);
                    if (connectionListRecord.size() != 0) {
                        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
                    }
                    Log.e("connectionCount", "" + connectionListRecord.size());
                } else {
                    ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.noreminder_found));
                }
            } else {
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionError));

            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception", e.toString());
        }
    }
}
