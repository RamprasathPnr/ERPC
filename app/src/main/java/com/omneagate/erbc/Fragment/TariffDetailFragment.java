package com.omneagate.erbc.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.omneagate.erbc.Activity.BlueToothPrint_new;
import com.omneagate.erbc.Activity.ConnectionListActivity;
import com.omneagate.erbc.Activity.Dialog.ConsumptionPatternDialog;
import com.omneagate.erbc.Activity.GenerateBillActivity;
import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Activity.LandingActivity;
import com.omneagate.erbc.Activity.MyusageListActivity;
import com.omneagate.erbc.Activity.PayBillActivity;
import com.omneagate.erbc.Adapter.UsageAdapter;
import com.omneagate.erbc.Dto.BillDetailsDto;
import com.omneagate.erbc.Dto.DashboardDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Dto.UsageMonitoringDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import com.github.mikephil.charting.animation.Easing;
//import com.github.mikephil.charting.data.PieEntry;
//import com.github.mikephil.charting.formatter.PercentFormatter;


public class TariffDetailFragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {

    private BarChart mChart;
    private ListView mLvUsage;
    private UsageAdapter adapter;
    //HttpConnection service
    public HttpClientWrapper httpConnection;

    private TextView mTvTotalBill;
    private TextView mTvNoConnection;
    private TextView mTvConnection;
    private TextView mTvNoBill;

    String SamplePrint;
    NetworkConnection networkConnection;

  /*  private TextView mTvUnitOne;
    private TextView mTvBillOne;

    private TextView mTvUnitTwo;
    private TextView mTvBillTwo;

    private TextView mTvUnitThree;
    private TextView mTvBillThree;*/

    private LinearLayout mLlManageCponnection;
    private LinearLayout mRlPayBill, ll_generate_bill;
    LinearLayout lin_generate_bill;
    private ScrollView mScrollView;
    private String ChartType, S_month;
    List<BillDetailsDto> billDetailDto;
    TextView unPaidAmount, generateBill, lastViewedConnection,toolbartitle,last_view_date,last_view_name;


    public TariffDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setTitle(getResources().getString(R.string.dashboard));
   //     ((AppCompatActivity) getActivity()).findViewById(R.id.title_toolbar)

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_tariff_details, container, false);
       /* Configuration config = getActivity().getResources().getConfiguration();
        float dimevalue;
        if (config.smallestScreenWidthDp >= 720) {
            dimevalue = getResources().getDimension(R.dimen.title_value_T10);

        } else if (config.smallestScreenWidthDp >= 600) {
            dimevalue = getResources().getDimension(R.dimen.title_value_T7);
        } else {
            dimevalue = getResources().getDimension(R.dimen.title_value_m);
        }
        ((LandingActivity) getActivity()).changeSize(getResources().getString(R.string.dashboard), dimevalue);*/
        setupView(v);

        billDetailDto = new ArrayList<BillDetailsDto>();
        new GetHomePageDetails().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return v;
    }

    private void setupView(View v) {
        networkConnection = new NetworkConnection(getActivity());
        mTvTotalBill = (TextView) v.findViewById(R.id.txt_total_bill);
        mTvNoConnection = (TextView) v.findViewById(R.id.txt_no_connection);

        mScrollView = (ScrollView) v.findViewById(R.id.tariff_scrollView);

      /*  mTvUnitOne = (TextView) v.findViewById(R.id.txt_unite_one);
        mTvUnitTwo = (TextView) v.findViewById(R.id.txt_unite_two);
        mTvUnitThree = (TextView) v.findViewById(R.id.txt_unite_three);

        mTvBillOne = (TextView) v.findViewById(R.id.txt_bill_one);
        mTvBillTwo = (TextView) v.findViewById(R.id.txt_bill_two);
        mTvBillThree = (TextView) v.findViewById(R.id.txt_bill_three);*/

        mTvConnection = (TextView) v.findViewById(R.id.txt_connection);
        mTvNoBill = (TextView) v.findViewById(R.id.txt_no_bill);
        mTvNoConnection = (TextView) v.findViewById(R.id.txt_no_connection);
        unPaidAmount = (TextView) v.findViewById(R.id.unpaid_amount);
        lastViewedConnection = (TextView) v.findViewById(R.id.last_view_connection);
        last_view_date =(TextView) v.findViewById(R.id.last_view_date);
        last_view_name =(TextView) v.findViewById(R.id.lastViewedName);
        generateBill = (TextView) v.findViewById(R.id.generate_bill_);
        mLvUsage = (ListView) v.findViewById(R.id.lv_usage);

        mLlManageCponnection = (LinearLayout) v.findViewById(R.id.ll_manage_connection);
        mLlManageCponnection.setOnClickListener(this);
        mRlPayBill = (LinearLayout) v.findViewById(R.id.ll_pay_bill);
        mRlPayBill.setOnClickListener(this);


        ll_generate_bill = (LinearLayout) v.findViewById(R.id.ll_generate_bill);
        ll_generate_bill.setOnClickListener(this);
        lin_generate_bill = (LinearLayout) v.findViewById(R.id.lin_generate_bill);
        lin_generate_bill.setOnClickListener(this);

        mLvUsage.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mChart = (BarChart) v.findViewById(R.id.barchart);
        mChart.setNoDataText(getActivity().getString(R.string.no_chart_data));
        mChart.setOnChartValueSelectedListener(this);

    }


    protected final Handler SyncHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ServiceListenerType type = (ServiceListenerType) msg.obj;
            String response = msg.getData().getString(DBConstants.RESPONSE_DATA);
            switch (type) {
                case DASHBOARD_GETDETAILS:
                    Log.e("DASHBOARD_GETDETAILS ", "response_data" + response);
                    if (response != null
                            && !response.equalsIgnoreCase("Server Connection Error")) {
                        Gson gson = new Gson();
                        DashboardDto dashboard = gson.fromJson(response, DashboardDto.class);

                        if (dashboard.getStatusCode() == 0) {
                            setTextValue(dashboard);
                        } else {
                            hideChart();
                        }
                    } else {
                        hideChart();
                    }
                    break;
                case DASHBOARD_CONNECTIONDETAILS:
                    try {
                        Log.e("CONNECTIONDETAILS ", "response_data" + response);
                        if (response != null
                                && !response.equalsIgnoreCase("Server Connection Error")) {
                            Gson gson = new Gson();
                            DashboardDto dashboard = gson.fromJson(response, DashboardDto.class);

                            if (dashboard.getStatusCode() == 0) {
                                ConsumptionPatternDialog dialog = new ConsumptionPatternDialog(getActivity(), dashboard.getContents(), ChartType, S_month);
                                dialog.show();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.internalError), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.internalError), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                    //    Toast.makeText(getActivity(), "server Internal Error", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(),  getString(R.string.internalError), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }


        }
    };

/*
    private void setupChats() {

        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        //mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(false);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(false);
        mChart.setDrawEntryLabels(false);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);

        mChart.setEntryLabelTextSize(5);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setEnabled(false);

        mChart.setRotationX(5);

        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });


    }
*/

    private void hideChart() {

        mChart.setVisibility(View.GONE);
        mLvUsage.setVisibility(View.GONE);
        mTvNoConnection.setVisibility(View.VISIBLE);
        mTvNoBill.setVisibility(View.VISIBLE);

    }

    private void setTextValue(DashboardDto dashboard) {
        try {

            //         mTvTotalBill.setText("₹ " + dashboard.getBillAmount());
            //          SamplePrint = mTvTotalBill.getText().toString();


            if (dashboard.getConnectionCount() < 10) {
                mTvConnection.setText("0" + dashboard.getConnectionCount());
            } else
                mTvConnection.setText("" + dashboard.getConnectionCount());

            generateBill.setText("" + dashboard.getConnectionDueCount());
            lastViewedConnection.setText("" + dashboard.getLastViewedConnection());
            unPaidAmount.setText("" + dashboard.getUnpaidBillCount());
            last_view_name.setText("" +dashboard.getLastViewedConsumerName());
       //     last_view_date.setText("(LastViewed : " +dashboard.getLastViewedDate()+")");
            last_view_date.setText("("+getString(R.string.last_viewed) +": " +dashboard.getLastViewedDate()+")");

            if (dashboard.getUsageMonitoring() != null
                    && dashboard.getUsageMonitoring().size() > 0) {
                List<UsageMonitoringDto> usageMonitoring = dashboard.getUsageMonitoring();
          /*  mTvUnitOne.setText(usageMonitoring.get(0).getUnitsConsumed() + " kWh");
            mTvUnitTwo.setText(usageMonitoring.get(1).getUnitsConsumed() + " kWh");
            mTvUnitThree.setText(usageMonitoring.get(2).getUnitsConsumed() + " kWh");

            mTvBillOne.setText("₹ " + usageMonitoring.get(0).getBillAmount());
            mTvBillTwo.setText("₹ " + usageMonitoring.get(1).getBillAmount());
            mTvBillThree.setText("₹ " + usageMonitoring.get(2).getBillAmount());*/
            }

            //  setData(dashboard.getCurrentelectricityUsage());
            setData(dashboard.getUsageMonitoring());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_manage_connection:
                //mRlConnection.setBackgroundColor(getActivity().getResources().getColor(R.color.challanColor));

                if (!networkConnection.isNetworkAvailable()) {
                    Toast.makeText(getActivity(), getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent connectionPage = new Intent(getActivity(), ConnectionListActivity.class);
                connectionPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(connectionPage);
                getActivity().finish();
                break;

            case R.id.ll_generate_bill:
                if (!networkConnection.isNetworkAvailable()) {
                    Toast.makeText(getActivity(), getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent myUsage = new Intent(getActivity(), MyusageListActivity.class);
                myUsage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myUsage);
                getActivity().finish();
                break;

            case R.id.ll_pay_bill:
                if (!networkConnection.isNetworkAvailable()) {
                    Toast.makeText(getActivity(), getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                    return;
                }

                //mRlPayBill.setBackgroundColor(getActivity().getResources().getColor(R.color.challanColor));
                //  Intent billHistory = new Intent(getActivity(), PayBillActivity.class);
                Intent billHistory = new Intent(getActivity(), PayBillActivity.class);
                billHistory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(billHistory);
                getActivity().finish();
                break;
            case R.id.lin_generate_bill:
                if (!networkConnection.isNetworkAvailable()) {
                    Toast.makeText(getActivity(), getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                // myusage_lin.setBackgroundColor(R.color.possible_result_points);
                Intent myusage = new Intent(getActivity(), GenerateBillActivity.class);
                myusage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myusage);
                getActivity().finish();
        }
    }
//
//    @Override
//    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//        String Month = mChart.getData().getXVals().get(e.getXIndex());
//        Log.e(" BarChart Activity ", " " + Month);
//        Log.e(" BarChart Index  ", "" + e.getXIndex());
//        S_month = Month;
//        if (dataSetIndex == 0) {
//            ChartType = "Amount";
//        } else {
//            ChartType = "Consumed Units";
//        }
//
//
//        new GetConnectionData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Month);
//        mChart.highlightValues(null);
//
//    }
//
//    @Override
//    public void onNothingSelected() {
//
//    }

     @Override
     public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

         if (!networkConnection.isNetworkAvailable()) {
            Toast.makeText(getActivity(), getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
            mChart.highlightValues(null);
            return;
        }

        String Month = mChart.getData().getXVals().get(e.getXIndex());
        Log.e(" BarChart Activity ", " " + Month);
        Log.e(" BarChart Index  ", "" + e.getXIndex());
        S_month = Month;
        if (dataSetIndex == 0) {
            ChartType = "Amount";
        } else {
            ChartType = "Consumed Units";
        }


        new GetConnectionData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Month);
        mChart.highlightValues(null);


    }
    /* @Override
        public void onValueSelected(Entry e, int dataSetIndex) {

            String Month = mChart.getData().getXVals().get(e.getXIndex());
            Log.e(" BarChart Activity "," "+Month);
            Log.e(" BarChart Index  ",""+ e.getXIndex());

            if (dataSetIndex == 0) {
                ChartType="Amount";
            }else {
                ChartType="Consumed Units";
            }


            new GetConnectionData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Month);

            *//*switch (e.getXIndex()){
            case 0:
                Toast.makeText(getActivity(),"Month "+ Month +" Type "+type ,Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getActivity(),"Month "+ Month +" Type "+type ,Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getActivity(),"Month "+ Month +" Type "+type ,Toast.LENGTH_SHORT).show();
            case 3:
                Toast.makeText(getActivity(),"Month "+ Month +" Type "+type ,Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(getActivity(),"Month "+ Month +" Type "+type ,Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(getActivity(),"Month "+ Month +" Type "+type , Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

    }*//*
    }
*/
    @Override
    public void onNothingSelected() {

    }
    private class GetHomePageDetails extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object... params) {
            try {
                System.out.println("Sham Enter the GetHomePageDetails method :::");

                httpConnection = new HttpClientWrapper();
                String url = "/dashboard/getdetails";
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("id", DBHelper.getInstance(getContext()).getCustomerId());
                System.out.println("Sham jsonObject :::" + jsonObject.toString());
                StringEntity se = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.DASHBOARD_GETDETAILS, SyncHandler,
                        RequestType.POST, se, getActivity());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private class GetConnectionData extends AsyncTask<String, Object, String> {
        @Override
        protected String doInBackground(String... param) {
            try {
                httpConnection = new HttpClientWrapper();
                String url = "/dashboard/getdetailswithconnection";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("customerId", DBHelper.getInstance(getContext()).getCustomerId());
                jsonObject.put("month", param[0]);
                StringEntity se = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.DASHBOARD_CONNECTIONDETAILS, SyncHandler,
                        RequestType.POST, se, getActivity());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void setData(List<UsageMonitoringDto> usageMonitoringDto) {
        try {
            if (usageMonitoringDto != null && usageMonitoringDto.size() > 0) {

                ArrayList<BarDataSet> dataSets = null;

                ArrayList<String> MonthList = new ArrayList<>();
                ArrayList<BarEntry> AmountList = new ArrayList<>();
                ArrayList<BarEntry> ConsumedUnitsList = new ArrayList<>();

                for (int i = 0; i < usageMonitoringDto.size(); i++) {
                    MonthList.add(usageMonitoringDto.get(i).getMonth());
                    AmountList.add(new BarEntry(Float.parseFloat(usageMonitoringDto.get(i).getBillAmount()), i));
                    ConsumedUnitsList.add(new BarEntry(Float.parseFloat(usageMonitoringDto.get(i).getUnitsConsumed()), i));
                }

                BarDataSet barDataSet1 = new BarDataSet(AmountList, getString(R.string.amount_in_inr));
                barDataSet1.setColor(Color.parseColor("#8B51FB"));

                BarDataSet barDataSet2 = new BarDataSet(ConsumedUnitsList, getString(R.string.consumption_units_kwh));
                barDataSet2.setColor(Color.parseColor("#DB6485"));

                Configuration config = getActivity().getResources().getConfiguration();

                if (config.smallestScreenWidthDp >= 720) {
                    barDataSet1.setValueTextSize(12f);  // 10-inch tablet and above
                    barDataSet2.setValueTextSize(12f);
                }
                else if (config.smallestScreenWidthDp >= 600) {
                    barDataSet1.setValueTextSize(10f); // 7-inch tablet and above
                    barDataSet2.setValueTextSize(10f);

                }
                else {
                    // For mobile devices
                }

                dataSets = new ArrayList<>();
                dataSets.add(barDataSet1);
                dataSets.add(barDataSet2);

                setUpChart(dataSets, MonthList);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpChart(ArrayList<BarDataSet> dateSet, ArrayList<String> xAxisValue) {
        try {

            BarData data = new BarData(xAxisValue, dateSet);
            mChart.setData(data);
            mChart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer
            mChart.setDrawGridBackground(false);
            mChart.getAxisLeft().setDrawGridLines(false);
            mChart.getXAxis().setDrawGridLines(false);
            //   mChart.setOnChartValueSelectedListener(this);



            YAxis yAxis = mChart.getAxisLeft();
            yAxis.setDrawGridLines(false);


            yAxis = mChart.getAxisRight();
            yAxis.setDrawGridLines(false);
            mChart.getAxisRight().setDrawLabels(false);


            Legend legend=mChart.getLegend();
            Configuration config = getActivity().getResources().getConfiguration();

            if (config.smallestScreenWidthDp >= 720) {
                legend.setTextSize(getResources().getDimension(R.dimen.legendsize_seven));

            } else if (config.smallestScreenWidthDp >= 600) {
                legend.setTextSize(getResources().getDimension(R.dimen.legendsize_seven));
            } else {
                legend.setTextSize(getResources().getDimension(R.dimen.legendsize));
            }

            mChart.setDescription("");
            mChart.animateXY(2000, 2000);


            mChart.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String BluetoothPrintData() {

        StringBuilder textData = new StringBuilder();
        textData.append("    ");
        textData.append("--------------------------------\n");
        textData.append("         TNCSC LIMITED\n");
        textData.append("       DPC Purchase Bill\n");

        textData.append("" + SamplePrint + "\n");

      /*  textData.append("" + trans_id + "\n");
        textData.append("" + trans_date + "\n");
        textData.append("" + connection_name + "\n");
        textData.append("" + connection_number +"\n");

        textData.append("" + amount_paid +"\n");*/

      /*  textData.append("--------------------------------\n");
        if (dpcProcurementDto != null && farmer_registration_dto != null) {
            textData.append("BILL NO : " + "  " + dpcProcurementDto.getProcurementReceiptNo() + "\n");
            textData.append(new SimpleDateFormat("dd/mm/yyyy  hh:mm:ss a").format(new Date()) + "\n");
            textData.append("--------------------------------\n");
            DpcDistrictDto dpcdistrictdto = dpcProcurementDto.getDpcProfileDto().getDpcDistrictDto();
            if (dpcdistrictdto != null) {
                long districtid = dpcProcurementDto.getDpcProfileDto().getDpcDistrictDto().getId();
                dpcdistrictdto = DBHelper.getInstance(this).getDistrictName_byid(districtid, dpcdistrictdto);
                textData.append("District   : " + " " + dpcdistrictdto.getName().trim() + "\n");
            }
            DpcTalukDto dpctalukdto = dpcProcurementDto.getDpcProfileDto().getDpcTalukDto();
            if (dpctalukdto != null) {
                long talukid = dpcProcurementDto.getDpcProfileDto().getDpcTalukDto().getId();
                dpctalukdto = DBHelper.getInstance(this).getTalukName_byid(talukid, dpctalukdto);
                textData.append("Taluk      : " + " " + dpctalukdto.getName() + "\n");
            }
            textData.append("DPC Name   : " + " " + dpcProcurementDto.getDpcProfileDto().getName() + "\n");
            textData.append("DPC Code   : " + " " + dpcProcurementDto.getDpcProfileDto().getGeneratedCode() + "\n");
            textData.append("--------------------------------\n");
            textData.append(getstring(R.string.name) + "           " + farmer_registration_dto.getFarmerName() + "\n");
            textData.append(getstring(R.string.address) + "        " + farmer_registration_dto.getAddress1() + "\n");
            textData.append(getstring(R.string.gradename) + "          " + dpcProcurementDto.getPaddyGradeDto().getName() + "\n");
            textData.append(getstring(R.string.txt_Paddy_Category) + "      " + dpcProcurementDto.getPaddyCategoryDto().getName() + "\n");
            textData.append(getstring(R.string.text_lot_number) + "     " + dpcProcurementDto.getLotNumber() + "\n");
            textData.append(getstring(R.string.text_moistureonly) + "       " + dpcProcurementDto.getMoistureContent() + "\n");
            textData.append(getstring(R.string.print_Number_of_Bags) + "     " + dpcProcurementDto.getNumberOfBags() + "\n");
            textData.append(getstring(R.string.print_netwt) + "         " + dpcProcurementDto.getNetWeight() + "\n");
            textData.append(getstring(R.string.print_pur_rate) + "   " + dpcProcurementDto.getDpcPaddyRateDto().getPurchaseRate() + "\n");
            textData.append(getstring(R.string.print_Bon_rate) + "   " + dpcProcurementDto.getDpcPaddyRateDto().getBonusRate() + "\n");
            textData.append(getstring(R.string.print_total) + "    " + dpcProcurementDto.getTotalAmount() + "\n");
            textData.append(getstring(R.string.print_gradecut) + "      " + dpcProcurementDto.getGradeCut() + "\n");
            textData.append(getstring(R.string.print_moisturecut) + "   " + dpcProcurementDto.getMoistureCut() + "\n");
            textData.append(getstring(R.string.print_totalcutamount) + "  " + dpcProcurementDto.getTotalCutAmount() + "\n");
            textData.append(getstring(R.string.print_netamt) + "        " + dpcProcurementDto.getNetAmount() + "\n");
            split_text(textData, dpcProcurementDto.getNetAmount());
//            split_text(textData,153650);
//            textData.append("  " + EnglishNumberToWords.convert(dpcProcurementDto.getNetAmount()) + " only ");
        }*/
        textData.append("\n");
        textData.append("--------------------------------\n\n\n\n");
        textData.append("SIGN OF THE  FARMER\n\n\n\n");
        textData.append("SIGN OF THE BILL CLERK \n");
        textData.append("--------------------------------\n");
        textData.append("\n");
        textData.append("\n");
        return textData.toString();


    }

    private void callprint() {
        String printdata = BluetoothPrintData();
        BlueToothPrint_new printing = new BlueToothPrint_new(getActivity());
        Log.e("printdata", printdata);
        printing.opendialog(printdata);
    }
}
