package com.omneagate.erbc.Activity;

import android.content.Intent;
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

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Dto.ConnectionCheckDto;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user1 on 27/5/16.
 */
public class MyusageListActivity extends BaseActivity {

    private AppAdapter mAdapter = new AppAdapter();
    CustomProgressDialog progressBar;
    SwipeMenuListView listView;
    List<ConnectionDto> connectionListRecord = new ArrayList<>();
    ConnectionDto ConnectionDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectionlist);
        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
        try {
            networkConnection = new NetworkConnection(getApplicationContext());
            httpConnection = new HttpClientWrapper();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setTitle(getString(R.string.my_usage));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.INVISIBLE);
            listView = (SwipeMenuListView) findViewById(R.id.listView);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent homepage = new Intent(MyusageListActivity.this, LandingActivity.class);
                    startActivity(homepage);
                    finish();
                }
            });
            CustomerDto cusotmer = new CustomerDto();
            cusotmer.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
            connnectionList_Info(cusotmer);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    connectionListRecord.get(position);
                    Intent nextpage = new Intent(getApplicationContext(), MeterCalculationActivity.class);
                    nextpage.putExtra("connection", new Gson().toJson(connectionListRecord.get(position)));

                    startActivity(nextpage);
                    finish();

                }
            });

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", "" + e.toString(), e);
        }

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
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionRefused));
                // Toast.makeText(getApplicationContext(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void connnectionList_Info(CustomerDto connection) {
        try {

            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/customer/getconnectionformyusage";
                String login = new Gson().toJson(connection);
                Log.e("My Usage :: ", login);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_LIST, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                //((TextView)findViewById(R.id.textView5)).setText(getString(R.string.connectionError));
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionRefused));
                // Toast.makeText(getApplicationContext(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }


    private void connectionlist_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("personal_Response", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionCheckDto connectionList;
            connectionList = gson.fromJson(response, ConnectionCheckDto.class);
            if (connectionList != null) {
                if (connectionList.getStatusCode() == 0) {

                    connectionListRecord = connectionList.getConnectionDto();
                    Collections.sort(connectionListRecord, new Util.CustomComparator());
                    listView.setAdapter(mAdapter);

                    if (connectionList.getConnectionDto().size() != 0) {
                        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
                    }

                    Log.e("connectionCount", "" + connectionList.getConnectionDto().size());
                } else {
                    ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.nodata_found));
                    //Toast.makeText(getApplicationContext(),connectionList.getErrorDescription(), Toast.LENGTH_SHORT).show();
                }
            } else {
                ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionError));
                // Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception", e.toString());
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


    class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return connectionListRecord.size();
        }

        @Override
        public ConnectionDto getItem(int position) {
            return connectionListRecord.get(position);
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
                holder.billCycleDate = (TextView) convertView.findViewById(R.id.payment_status);
                //holder.connectionNumber.setText(connectionListRecord.get(position).getConsumerNumber());
                holder.connectionNumber.setText(connectionListRecord.get(position).getConsumerDisplayNo());
                holder.connectionName.setText(connectionListRecord.get(position).getConsumerName() + " / " + connectionListRecord.get(position).getDistrict().getName());
                if (connectionListRecord.get(position).getLastViewDate() == null
                        || connectionListRecord.get(position).getLastViewDate() == "") {
                    String[] items1 = connectionListRecord.get(position).getLastMeterReadingDate().split("-");
                    String d1 = items1[0];
                    String m1 = items1[1];
                    String y1 = items1[2];
                    int d = Integer.parseInt(d1);
                    int y = Integer.parseInt(y1);
                    holder.createdays.setText("" + d);
                    holder.month_year.setText(m1 + " " + y);
                } else {
                    String[] items1 = connectionListRecord.get(position).getLastViewDate().split("-");
                    String d1 = items1[0];
                    String m1 = items1[1];
                    String y1 = items1[2];
                    int d = Integer.parseInt(d1);
                    int y = Integer.parseInt(y1);
                    holder.createdays.setText("" + d);
                    holder.month_year.setText(m1 + " " + y);
                    if (connectionListRecord.get(position).getLastViewAmount() != null)
                        holder.price.setText("" + connectionListRecord.get(position).getLastViewAmount());
                    else
                        holder.price.setText("0");


                    holder.kvah_id.setText("" + connectionListRecord.get(position).getLastViewConsumption() + " kWh");
                   /* holder.billCycleDate.setText("Last Reading Date : " +
                            connectionListRecord.get(position).getLastMeterReadingDate() +
                            "\nNext Reading Date : " + connectionListRecord.get(position).getNextMeterReadingDate());
*/
                    holder.billCycleDate.setText(getString(R.string.last_reading_date) +": " +
                            connectionListRecord.get(position).getLastMeterReadingDate() +
                            "\n"+ getString(R.string.next_reading_date) +": " + connectionListRecord.get(position).getNextMeterReadingDate());
                }


            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e("listviewException", e.toString(), e);
            }

            return convertView;
        }

        class ViewHolder {
            public TextView connectionNumber, connectionName, createdays, month_year, price, kvah_id;
            public TextView billCycleDate;

            public ViewHolder(View view) {
                view.setTag(this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Intent homepage = new Intent(MyusageListActivity.this, LandingActivity.class);
            startActivity(homepage);
            finish();

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString(), e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("destory", "finished");
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();

    }

}