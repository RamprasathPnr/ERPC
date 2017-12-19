package com.omneagate.erbc.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.omneagate.erbc.Activity.ConnectionCheckActivity;
import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Activity.LandingActivity;
import com.omneagate.erbc.Activity.PaymentHistoryActivity;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Shanthakumar on 18-07-2016.
 */
public class PaymentFragment extends Fragment {

    private AppAdapter mAdapter = new AppAdapter();
    private CustomProgressDialog progressBar;
    private SwipeMenuListView listView;
    private List<ConnectionDto> connectionListRecord = new ArrayList<>();
    private ConnectionDto ConnectionDetail;
    private String before_Activity;
    private NetworkConnection networkConnection;
    private HttpClientWrapper httpConnection;

    private TextView mTvNoConnection;
    private ImageView mIvNoConnection;

    private DateFormat dateFormat;
    private Calendar calendar;

    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  mIvNoConnection.setVisibility(View.INVISIBLE);
        mTvNoConnection.setVisibility(View.INVISIBLE);*/
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.payment));
        /*Configuration config = getActivity().getResources().getConfiguration();
        float dimevalue;
        if (config.smallestScreenWidthDp >= 720) {
            dimevalue = getResources().getDimension(R.dimen.title_value_T10);

        } else if (config.smallestScreenWidthDp >= 600) {
            dimevalue = getResources().getDimension(R.dimen.title_value_T7);
        } else {
            dimevalue = getResources().getDimension(R.dimen.title_value_m);
        }
        ((LandingActivity) getActivity()).changeSize(getResources().getString(R.string.payment), dimevalue);*/
      //  ((LandingActivity) getActivity()).changeSize("Payment History");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_payment, container, false);
     /*   mIvNoConnection.setVisibility(View.INVISIBLE);
        mTvNoConnection.setVisibility(View.INVISIBLE);*/


        configureInitialPage(v);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String ConnectionDetail = "" + connectionListRecord.get(position).getId();
                Intent nextpage = new Intent(getActivity(), PaymentHistoryActivity.class);
                nextpage.putExtra("connenction_id", ConnectionDetail);
                startActivity(nextpage);
                getActivity().finish();
            }
        });
        return v;
    }

    private void configureInitialPage(View v) {

        mTvNoConnection = (TextView) v.findViewById(R.id.txt_no_connection);
        mTvNoConnection.setVisibility(View.INVISIBLE);
        mIvNoConnection = (ImageView) v.findViewById(R.id.img_no_connection);
        mIvNoConnection.setVisibility(View.INVISIBLE);

        networkConnection = new NetworkConnection(getContext());
        httpConnection = new HttpClientWrapper();

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        listView = (SwipeMenuListView) v.findViewById(R.id.sl_bill_list);
        CustomerDto cusotmer = new CustomerDto();
        cusotmer.setId(DBHelper.getInstance(getContext()).getCustomerId());
        connectionListInfo(cusotmer);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ConnectionCheckActivity.class));
                getActivity().finish();
            }
        });

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
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


    private void connectionListInfo(CustomerDto connection) {
        try {
            progressBar = new CustomProgressDialog(getActivity());
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/customer/getconnectionbypayment";
                String login = new Gson().toJson(connection);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_LIST,
                        SyncHandler, RequestType.POST, se, getActivity());
            } else {
                dismissProgress();

                mTvNoConnection.setText(getString(R.string.connectionRefused));
                //  Toast.makeText(getApplicationContext(),getString(R.string.connectionRefused),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("paymentconnection", e.toString(), e);
        }
    }

    /*Handler used to get response from server*/
    protected final Handler SyncHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("Sham Enter the handleMessage method :::");

            ServiceListenerType type = (ServiceListenerType) msg.obj;
            String response = msg.getData().getString(DBConstants.RESPONSE_DATA);

            switch (type) {

                case CONNECTION_LIST:
                    dismissProgress();
                    connectionListResponse(response);
                    break;

                default:
                    dismissProgress();
                    Toast.makeText(getActivity(),
                            getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void connectionListResponse(String response) {
        try {

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
                        mIvNoConnection.setVisibility(View.INVISIBLE);
                        mTvNoConnection.setVisibility(View.INVISIBLE);
                    }
                    Log.e("connectionCount", "" + connectionList.getConnectionDto().size());
                } else {
                    mTvNoConnection.setVisibility(View.VISIBLE);
                    mTvNoConnection.setText(getString(R.string.nodata_found));
                }

            } else {
                mTvNoConnection.setVisibility(View.VISIBLE);
                mTvNoConnection.setText(getString(R.string.connectionError));
            }


        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception", e.toString());
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
                    convertView = View.inflate(getContext(), R.layout.connection_item, null);
                    new ViewHolder(convertView);
                }
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.connectionNumber = (TextView) convertView.findViewById(R.id.connection_number);
                holder.connectionName = (TextView) convertView.findViewById(R.id.connectionname);
                holder.connectionTaluk = (TextView) convertView.findViewById(R.id.talukid);
                holder.createDays = (TextView) convertView.findViewById(R.id.cdays);
                holder.month_year = (TextView) convertView.findViewById(R.id.month_year);
                holder.txt_bill_paid = (TextView) convertView.findViewById(R.id.txt_bill_paid);
                //holder.connectionNumber.setText(connectionListRecord.get(position).getConsumerNumber());
                holder.connectionNumber.setText(connectionListRecord.get(position).getConsumerDisplayNo());
                holder.connectionName.setText(connectionListRecord.get(position).getConsumerName());
                holder.connectionTaluk.setText(connectionListRecord.get(position).getDistrict().getName());
                holder.txt_bill_paid.setText("");


                /*String[] items1 = connectionListRecord.get(position).getCreatedDate().split("-");
                String d1 = items1[0];
                String m1 = items1[1];
                String y1 = items1[2];
                int d = Integer.parseInt(d1);
                int y = Integer.parseInt(y1);
                holder.createDays.setText("" + d);
                holder.month_year.setText(m1 + " " + y);*/

                calendar.setTime(dateFormat.parse(connectionListRecord.get(position).getCreatedDate()));
                holder.createDays.setText("" + calendar.get(Calendar.DATE));
                holder.month_year.setText(new SimpleDateFormat("MMM").format(calendar.getTime()) + " " + calendar.get(Calendar.YEAR));

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
            public TextView connectionNumber;
            public TextView connectionName;
            public TextView connectionTaluk;
            public TextView createDays;
            public TextView month_year;
            public TextView txt_bill_paid;

            public ViewHolder(View view) {
                view.setTag(this);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        GlobalAppState.getInstance().trackScreenView("Payment Fragment");
    }
}
