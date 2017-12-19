package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.omneagate.erbc.Dto.BillHistoryDto;
import com.omneagate.erbc.Dto.BillhistoryList;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.Dto.ConnectionCustomerDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.GenericDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by user1 on 1/6/16.
 */
public class BillHistoryActivity extends BaseActivity {

    SwipeMenuListView listView;
    ConnectionDto ConnectionDetail;
    CustomProgressDialog progressBar;
    List<BillHistoryDto> billHisotryListRecord = new ArrayList<>();
    private AppAdapter mAdapter = new AppAdapter();
    private DateFormat dateFormat;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectionlist);
        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
        configureInitialPage();

    }

    private void configureInitialPage() {
        try {
            networkConnection = new NetworkConnection(this);
            httpConnection = new HttpClientWrapper();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setTitle(getString(R.string.bottom_bill));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.INVISIBLE);
            listView = (SwipeMenuListView) findViewById(R.id.listView);
            CustomerDto cutomer = new CustomerDto();
            cutomer.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in =new Intent(BillHistoryActivity.this,LandingActivity.class);
                    startActivity(in);
                    finish();
                }
            });
            Intent intent = getIntent();
            String connenction = intent.getStringExtra("connenctionDto");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionDetail = gson.fromJson(connenction, ConnectionDto.class);
            GenericDto customer = new GenericDto();
            GenericDto connection = new GenericDto();
            connection.setId(ConnectionDetail.getId());

            customer.setId(DBHelper.getInstance(this).getCustomerId());
            Log.e("connnectionid", "" + ConnectionDetail.getId());
            ConnectionCustomerDto conectiondto = new ConnectionCustomerDto();
            conectiondto.setConnection(connection);
            conectiondto.setCustomer(customer);
            billhistorylist(conectiondto);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent nextpage = new Intent(BillHistoryActivity.this, BillHistoryDetailActivity.class);
                nextpage.putExtra("billdetails", new Gson().toJson(billHisotryListRecord.get(position)));
                nextpage.putExtra("activityName","BillHistroy");
                startActivity(nextpage);
                finish();

            }
        });

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    }

    private void billhistorylist(ConnectionCustomerDto jsondata) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/myusage/getbillhistory";
                String login = new Gson().toJson(jsondata);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                Log.e("bill_hisotory_request", "" + login.toString());
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.BILL_HISTORY, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {

            case BILL_HISTORY:
                dismissProgress();
                billhistory_Response(message);
                break;
            default:
                dismissProgress();
                ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionRefused));
                // Toast.makeText(getApplicationContext(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }

    private void billhistory_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("billhisitroy_response", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BillhistoryList billhistory = gson.fromJson(response, BillhistoryList.class);
            if (billhistory != null) {
                if (billhistory.getStatusCode() == 0) {
                    billHisotryListRecord = billhistory.getBillHistoryList();
                    listView.setAdapter(mAdapter);

                    if (billhistory.getBillHistoryList().size() != 0) {
                        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
                    }

                    Log.e("billHisotryCount", "" + billhistory.getBillHistoryList().size());

                } else {
                    ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);

                    ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.nodata_found));
                }

            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString());
        }

    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return billHisotryListRecord.size();
        }

        @Override
        public BillHistoryDto getItem(int position) {
            return billHisotryListRecord.get(position);
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
                    convertView = View.inflate(getApplicationContext(), R.layout.usagelist, null);
                    new ViewHolder(convertView);
                }
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.connectionNumber = (TextView) convertView.findViewById(R.id.connection_number);
                holder.connectionName = (TextView) convertView.findViewById(R.id.connectionname);
                holder.createdays = (TextView) convertView.findViewById(R.id.cdays);
                holder.month_year = (TextView) convertView.findViewById(R.id.month_year);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.kvah_id = (TextView) convertView.findViewById(R.id.kvah_id);
                holder.payment_status = (TextView) convertView.findViewById(R.id.payment_status);
                holder.connectionNumber.setText(billHisotryListRecord.get(position).getConsumerDisplayNo());
                holder.price.setText("" + billHisotryListRecord.get(position).getAmount());
                holder.kvah_id.setText("" + billHisotryListRecord.get(position).getConsumption() + " kWh");
                holder.connectionName.setText(billHisotryListRecord.get(position).getConsumerName());
                if (billHisotryListRecord.get(position).getBillStatus().equalsIgnoreCase("true")) {
                    holder.payment_status.setText(getResources().getString(R.string.paid));
                    holder.payment_status.setTextColor(Color.parseColor("#39b54a"));
                } else {
                    holder.payment_status.setText(getResources().getString(R.string.nupaid));
                    holder.payment_status.setTextColor(Color.parseColor("#FF9800"));
                }

                /*String[] items1 = billHisotryListRecord.get(position).getCurrentMeterReadingDate().split("-");
                String d1 = items1[0];
                String m1 = items1[1];
                String y1 = items1[2];
                int d = Integer.parseInt(d1);
                int y = Integer.parseInt(y1);
                holder.createdays.setText("" + d);
                holder.month_year.setText(m1 + " " + y);*/

                calendar.setTime(dateFormat.parse(billHisotryListRecord.get(position).getCurrentMeterReadingDate()));
                holder.createdays.setText("" + calendar.get(Calendar.DATE));
                holder.month_year.setText(new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + calendar.get(Calendar.YEAR));

            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e("listviewException", e.toString(), e);
            }

            return convertView;
        }

        class ViewHolder {

            public TextView connectionNumber, connectionName, payment_status, createdays, month_year, price, kvah_id;

            public ViewHolder(View view) {

                view.setTag(this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent in =new Intent(BillHistoryActivity.this,LandingActivity.class);
        startActivity(in);
        finish();
    }
}