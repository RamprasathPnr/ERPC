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

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 17/6/16.
 */
public class PaymentConnectionActivity extends BaseActivity {


    private AppAdapter mAdapter = new AppAdapter();
    CustomProgressDialog progressBar;
    SwipeMenuListView listView;
    List<ConnectionDto> connectionListRecord = new ArrayList<>();
    String ConnectionDetail;
    String before_Activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectionlist);
        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
        configureInitialPage();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ConnectionDetail = "" + connectionListRecord.get(position).getId();
                Log.e("connectionDto",""+ConnectionDetail.toString());
                Intent nextpage = new Intent(PaymentConnectionActivity.this, PaymentHistoryActivity.class);
                nextpage.putExtra("connenction_id", ConnectionDetail);
                startActivity(nextpage);
                finish();
            }
        });
    }

    private void configureInitialPage() {
        networkConnection = new NetworkConnection(getApplicationContext());
        httpConnection = new HttpClientWrapper();

        //   Intent intent = getIntent();
        //   before_Activity = intent.getStringExtra("before_page");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


    }


    private void connnectionList_Info(CustomerDto connection) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/customer/getconnectionbypayment";
                String login = new Gson().toJson(connection);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_LIST, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionRefused));
                //  Toast.makeText(getApplicationContext(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("paymentconnection", e.toString(), e);
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
                    convertView = View.inflate(getApplicationContext(), R.layout.connection_item, null);
                    new ViewHolder(convertView);
                }
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.connectionNumber = (TextView) convertView.findViewById(R.id.connection_number);
                holder.connectionName = (TextView) convertView.findViewById(R.id.connectionname);
                holder.connectionTaluk = (TextView) convertView.findViewById(R.id.talukid);
                holder.createdays = (TextView) convertView.findViewById(R.id.cdays);
                holder.month_year = (TextView) convertView.findViewById(R.id.month_year);
                holder.connectionNumber.setText(connectionListRecord.get(position).getConsumerNumber());
                holder.connectionName.setText(connectionListRecord.get(position).getConsumerName());
                holder.connectionTaluk.setText(connectionListRecord.get(position).getDistrict().getName());
                String[] items1 = connectionListRecord.get(position).getCreatedDate().split("-");
                String d1 = items1[0];
                String m1 = items1[1];
                String y1 = items1[2];
                int d = Integer.parseInt(d1);
                int y = Integer.parseInt(y1);
                holder.createdays.setText("" + d);
                holder.month_year.setText(m1 + " " + y);
               /* if (position == 0) {
                    holder.createdays.setBackgroundColor(Color.BLUE);
                }
                else if (position % 2 == 1) {
                    holder.createdays.setBackgroundColor(Color.RED);
                }
                else if (position % 2 == 0) {
                    holder.createdays.setBackgroundColor(Color.BLUE);
                }*/
            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e("listviewException", e.toString(), e);
            }

            return convertView;
        }

        class ViewHolder {

            public TextView connectionNumber, connectionName, connectionTaluk, createdays, month_year;

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
        super.onBackPressed();
        Intent backpage = new Intent(PaymentConnectionActivity.this, LandingActivity.class);
        backpage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
            ConnectionCheckDto connectionList;
            connectionList = gson.fromJson(response, ConnectionCheckDto.class);
            if (connectionList != null) {
                if (connectionList.getStatusCode() == 0) {
                    connectionListRecord = connectionList.getConnectionDto();
                    listView.setAdapter(mAdapter);
                    if (connectionList.getConnectionDto().size() != 0) {
                        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
                        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
                    }
                    Log.e("connectionCount", "" + connectionList.getConnectionDto().size());
                } else {
                    ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.nodata_found));
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

