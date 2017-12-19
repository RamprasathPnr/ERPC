package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Dto.BillDetailsDto;
import com.omneagate.erbc.Dto.BillhistoryList;
import com.omneagate.erbc.Dto.ConnectionCustomerDto;
import com.omneagate.erbc.Dto.DashboardDto;
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
import java.util.Date;
import java.util.List;

/**
 * Created by root on 21/10/16.
 */
public class GenerateBillActivity extends BaseActivity {

    CustomProgressDialog progressBar;
    List<BillDetailsDto> generateBillsList = new ArrayList<>();
    private GenerateBillAdapter mAdapter = new GenerateBillAdapter();
    SwipeMenuListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_bill);
        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.NoConnectionFound)).setVisibility(View.INVISIBLE);
        configureInitialPage();
    }


    private void generateBillsResonse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("Generate_Bill_Activity", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            DashboardDto generatebills = gson.fromJson(response, DashboardDto.class);
            dismissProgress();
            if (generatebills != null) {
                if (generatebills.getStatusCode() == 0) {
                    generateBillsList = generatebills.getCurrentBillDetailsDto();
                    listView.setAdapter(mAdapter);

                    if (generateBillsList.size() != 0) {
                        ((ImageView) findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.NoConnectionFound)).setVisibility(View.INVISIBLE);
                    }

                    Log.e("billHisotryCount", "" + generateBillsList.size());

                } else {
                    ((TextView) findViewById(R.id.NoConnectionFound)).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.NoConnectionFound)).setText(getString(R.string.no_bills_found));
                }

            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent in = new Intent(GenerateBillActivity.this, GenerateBillDetailsActivity.class);
                    in.putExtra("billdetails", new Gson().toJson(generateBillsList.get(position)));
                    startActivity(in);
                    finish();

                }
            });
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString());
        }

    }


    private void configureInitialPage() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setTitle(getString(R.string.generate_pay_ment));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
            listView = (SwipeMenuListView) findViewById(R.id.listView);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });
            networkConnection = new NetworkConnection(GenerateBillActivity.this);
            httpConnection = new HttpClientWrapper();

            GetGenerateBills();

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }


    private void GetGenerateBills() {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/getcustomerdueconnection/" + DBHelper.getInstance(GenerateBillActivity.this).getCustomerId();
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.GETGENERATEBILLS, SyncHandler, RequestType.GET, null, getApplicationContext());
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
        Log.e("process Message", "service type " + what);
        switch (what) {
            case GETGENERATEBILLS:
                generateBillsResonse(message);
                break;
            default:
                dismissProgress();
                ((TextView) findViewById(R.id.NoConnectionFound)).setText(getString(R.string.connectionRefused));
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

    class GenerateBillAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return generateBillsList.size();
        }

        @Override
        public Object getItem(int position) {
            return generateBillsList.get(position);
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
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.adapter_generate_bill, null);
                new ViewHolder(convertView);
            }
            try {
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.Service_connection_no = (TextView) convertView.findViewById(R.id.service_connection_number);
                holder.customer_name = (TextView) convertView.findViewById(R.id.customer_name);
                holder.current_bill_cycle_period = (TextView) convertView.findViewById(R.id.current_billcycle_period_date);
                holder.last_Reaing_taken = (TextView) convertView.findViewById(R.id.last_reading_taken);
                String consumerno = generateBillsList.get(position).getConsumerNumber().substring(0, 3)
                        + "-" + generateBillsList.get(position).getConsumerNumber().substring(3, 6)
                        + "-" + generateBillsList.get(position).getConsumerNumber().substring(6);
                holder.Service_connection_no.setText("" + consumerno);
                holder.customer_name.setText("" + generateBillsList.get(position).getConnection().getName());

                Date fromDate, toDate, lastReadingDate;
                DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                fromDate = df.parse(generateBillsList.get(position).getBillCycleFromDate());
                toDate = df.parse(generateBillsList.get(position).getBillCycleToDate());
                lastReadingDate = df.parse(generateBillsList.get(position).getPreviousReadingDate());
                String fromDateStr = df.format(fromDate).replace("-", " ");
                String toDateStr = df.format(toDate).replace("-", " ");
                String lastReadingDateStr = df.format(lastReadingDate).replace("-", " ");

                holder.current_bill_cycle_period.setText(fromDateStr + " - " + toDateStr);
                holder.last_Reaing_taken.setText("" + lastReadingDateStr);
            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                e.printStackTrace();
            }
            return convertView;
        }
    }


    class ViewHolder {

        public TextView Service_connection_no, customer_name, current_bill_cycle_period, last_Reaing_taken;

        public ViewHolder(View view) {

            view.setTag(this);
        }
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(GenerateBillActivity.this, LandingActivity.class);
        startActivity(in);
        finish();

    }
}
