package com.omneagate.erbc.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
import com.omneagate.erbc.Activity.BillHistoryActivity;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Activity.LandingActivity;
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
import java.util.Comparator;
import java.util.List;

/**
 * Created by Shanthakumar on 18-07-2016.
 */
public class BillHistoryFragment extends Fragment {

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


    public BillHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      /*  mIvNoConnection.setVisibility(View.INVISIBLE);
        mTvNoConnection.setVisibility(View.INVISIBLE);*/

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.bill_history));
       /* Configuration config = getActivity().getResources().getConfiguration();
        float dimevalue;
        if (config.smallestScreenWidthDp >= 720) {
            dimevalue = getResources().getDimension(R.dimen.title_value_T10);

        } else if (config.smallestScreenWidthDp >= 600) {
            dimevalue = getResources().getDimension(R.dimen.title_value_T7);

        } else {
            dimevalue = getResources().getDimension(R.dimen.title_value_m);
        }
        ((LandingActivity) getActivity()).changeSize(getResources().getString(R.string.bill_history), dimevalue);
       // ((LandingActivity) getActivity()).changeSize("Bill histroy");*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bill_history, container, false);
      /*  mIvNoConnection.setVisibility(View.INVISIBLE);
        mTvNoConnection.setVisibility(View.INVISIBLE);*/

        configureInitialPage(v);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ConnectionDetail = connectionListRecord.get(position);
                Intent nextpage = new Intent(getActivity(), BillHistoryActivity.class);
                nextpage.putExtra("connenctionDto", new Gson().toJson(ConnectionDetail));
                startActivity(nextpage);
                getActivity().finish();
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                createMenu1(menu);
            }

            private void createMenu1(SwipeMenu menu) {

                SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
                item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                item2.setWidth(dp2px(90));
                item2.setIcon(R.drawable.delte);
                menu.addMenuItem(item2);
            }
        };
        // listView.setMenuCreator(creator);

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


        return v;
    }

    private void deleteConnection(ConnectionDto connectionItem) {
        try {
            CustomerDto customerdto = new CustomerDto();
            customerdto.setId(DBHelper.getInstance(getActivity()).getCustomerId());
            connectionItem.setCustomer(customerdto);

            progressBar = new CustomProgressDialog(getContext());
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/deleteconsumer";
                String login = new Gson().toJson(connectionItem);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_DELETE,
                        SyncHandler, RequestType.POST, se, getActivity());
            } else {
                dismissProgress();

                Toast.makeText(getActivity(), getString(R.string.connectionRefused),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void configureInitialPage(View v) {

       /* mTvNoConnection = (TextView) v.findViewById(R.id.txt_no_connection);
        mIvNoConnection = (ImageView) v.findViewById(R.id.img_no_connection);*/
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
        connnectionList_Info(cusotmer);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    }

    private void connnectionList_Info(CustomerDto connection) {
        try {
            progressBar = new CustomProgressDialog(getContext());
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/customer/getviewedconnections";
                String login = new Gson().toJson(connection);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONNECTION_LIST,
                        SyncHandler, RequestType.POST, se, getActivity());
            } else {
                dismissProgress();
                mTvNoConnection.setVisibility(View.VISIBLE);
                mTvNoConnection.setText(getString(R.string.connectionRefused));
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void connectionListResponse(String data) {

        if (data != null
                && !data.equalsIgnoreCase("Server Connection Error")) {
            Gson gson = new Gson();
            ConnectionCheckDto connectionList = gson.fromJson(data, ConnectionCheckDto.class);

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

        } else {
            mTvNoConnection.setVisibility(View.VISIBLE);
            mTvNoConnection.setText(getString(R.string.connectionError));
        }
    }

    private void connectionDeleteResponse(String response) {
        try {
            Log.e("connectionResponse", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ConnectionCheckDto connectionList;
            connectionList = gson.fromJson(response, ConnectionCheckDto.class);
            if (connectionList != null) {
                if (connectionList.getStatusCode() == 0) {
                    AlertDialog alertdialog = new AlertDialog(getActivity(),
                            getString(R.string.connnecion_delete_mdg));
                    alertdialog.show();
                } else {
                    Toast.makeText(getActivity(),
                            getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(),
                        getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("exception", e.toString());
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

                case CONNECTION_DELETE:
                    dismissProgress();
                    connectionDeleteResponse(response);

                    break;
                default:
                    dismissProgress();
                    Toast.makeText(getActivity(),
                            getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
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
                holder.billPaid = (TextView) convertView.findViewById(R.id.txt_bill_paid);

                //holder.connectionNumber.setText(connectionListRecord.get(position).getConsumerNumber());
                holder.connectionNumber.setText(connectionListRecord.get(position).getConsumerDisplayNo());
                holder.connectionName.setText(Util.capitalize(connectionListRecord.get(position).getConsumerName()));
                holder.connectionTaluk.setText(connectionListRecord.get(position).getDistrict().getName());
                if (connectionListRecord.get(position).isBillStatus()) {
//                    holder.billPaid.setText("Paid");
//                    holder.billPaid.setTextColor(getResources().getColor(R.color.greencolor));
                } else {
//                    holder.billPaid.setText("Yet to Pay");
//                    holder.billPaid.setTextColor(getResources().getColor(R.color.subColor1));
                }


                calendar.setTime(dateFormat.parse(connectionListRecord.get(position).getCreatedDate()));
                /*String[] items1 = connectionListRecord.get(position).getCreatedDate().split("-");
                String d1 = items1[0];
                String m1 = items1[1];
                String y1 = items1[2];
                int d = Integer.parseInt(d1);
                int y = Integer.parseInt(y1);*/
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
            public TextView billPaid;

            public ViewHolder(View view) {
                view.setTag(this);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        GlobalAppState.getInstance().trackScreenView("BillHistory Fragment");
    }
}