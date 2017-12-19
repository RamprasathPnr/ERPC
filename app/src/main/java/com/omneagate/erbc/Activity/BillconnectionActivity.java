package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by user1 on 15/6/16.
 */
public class BillconnectionActivity extends BaseActivity {


    private AppAdapter mAdapter = new AppAdapter();
    CustomProgressDialog progressBar;
    SwipeMenuListView listView;
    List<ConnectionDto> connectionListRecord = new ArrayList<>();
    ConnectionDto ConnectionDetail;
    String before_Activity;
    private DateFormat dateFormat;
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectionlist);
        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
        configureInitialPage();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ConnectionDetail = connectionListRecord.get(position);
                Intent nextpage = new Intent(BillconnectionActivity.this, BillHistoryActivity.class);
                nextpage.putExtra("connenctionDto", new Gson().toJson(ConnectionDetail));
                startActivity(nextpage);
                finish();
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                createMenu1(menu);
            }

            private void createMenu1(SwipeMenu menu) {
               /* SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                item1.setWidth(dp2px(90));
                item1.setIcon(R.drawable.edit);
                menu.addMenuItem(item1);*/
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                item2.setWidth(dp2px(90));
                item2.setIcon(R.drawable.delte);
                menu.addMenuItem(item2);
            }
        };
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ConnectionDto ConnectionRow = connectionListRecord.get(position);
                switch (index) {
                    case 0:
                        deleteConnection(ConnectionRow);
                        break;
                }
                return false;
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
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
      /*  if(before_Activity.equals("connectionActivity"))
        {
            fab.setVisibility(View.VISIBLE);
        }*/
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ConnectionCheckActivity.class));
                finish();
            }
        });
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    }

    private void deleteConnection(ConnectionDto connectionItem) {
        try {
            CustomerDto customerdto = new CustomerDto();
            customerdto.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
            connectionItem.setCustomer(customerdto);

            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/deleteconsumer";
                String login = new Gson().toJson(connectionItem);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_DELETE, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();

                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }


    }

    private void connnectionList_Info(CustomerDto connection) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/customer/getviewedconnections";
                String login = new Gson().toJson(connection);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_LIST, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionRefused));
                //Toast.makeText(getApplicationContext(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
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
                //holder.connectionNumber.setText(connectionListRecord.get(position).getConsumerNumber());
                holder.connectionNumber.setText(connectionListRecord.get(position).getConsumerDisplayNo());
                holder.connectionName.setText(connectionListRecord.get(position).getConsumerName());
                holder.connectionTaluk.setText(connectionListRecord.get(position).getDistrict().getName());
                calendar.setTime(dateFormat.parse(connectionListRecord.get(position).getCreatedDate()));
                holder.createdays.setText("" + calendar.get(Calendar.DATE));
                holder.month_year.setText(new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + calendar.get(Calendar.YEAR));
                /*String[] items1 = connectionListRecord.get(position).getCreatedDate().split("-");
                String d1 = items1[0];
                String m1 = items1[1];
                String y1 = items1[2];
                int d = Integer.parseInt(d1);
                int y = Integer.parseInt(y1);
                holder.createdays.setText("" + d);
                holder.month_year.setText(m1 + " " + y);*/
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
        Intent backpage = new Intent(BillconnectionActivity.this, LandingActivity.class);
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

            case CONNECTION_DELETE:
                dismissProgress();
                connectionDelete_Response(message);

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

    private void connectionDelete_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("connectionRespoonse", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionCheckDto connectionList;
            connectionList = gson.fromJson(response, ConnectionCheckDto.class);
            if (connectionList != null) {
                if (connectionList.getStatusCode() == 0) {
                    AlertDialog alertdialog = new AlertDialog(BillconnectionActivity.this, getString(R.string.connnecion_delete_mdg));
                    alertdialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception", e.toString());
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
                        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
                    }
                    Log.e("connectionCount", "" + connectionList.getConnectionDto().size());
                } else {
                    ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.nodata_found));
                    //  Toast.makeText(getApplicationContext(),connectionList.getErrorDescription() , Toast.LENGTH_SHORT).show();
                }
            } else {
                ((TextView) findViewById(R.id.textView5)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.textView5)).setText(getString(R.string.connectionError));

            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception", e.toString());
        }
    }
}

