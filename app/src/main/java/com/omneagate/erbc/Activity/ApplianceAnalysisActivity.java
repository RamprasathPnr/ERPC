package com.omneagate.erbc.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Dto.ApplianceDto;
import com.omneagate.erbc.Dto.ApplianceListDto;
import com.omneagate.erbc.Dto.ConsumerTypeDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Dto.UnitConsumptionDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ApplianceAnalysisActivity extends BaseActivity {

    private Toolbar toolbar;
    private BarChart mChart;
    private RecyclerView appliance_recyclerview;
    private CustomProgressDialog progressBar;
    private ApplianceAnalysisAdapter adapter;
    String cConnectionID, cConsumerType;
    String[] HOURS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23","24"};
    // String[] MINUTES = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
    String[] MINUTES = {"0", "15", "30", "45", "59"};
    ArrayAdapter<String> hHoursAdapter;
    ArrayAdapter<String> mMinutesAdapter;
    private TextView tvConsumedUnits, tvAmount;
    private List<ApplianceDto> applianceanalysisList;
    private ArrayList<ApplianceListDto> applianceList =new ArrayList<>();
    private boolean isTamil = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance_analysis);
        Intent intent = getIntent();
        this.cConnectionID = intent.getStringExtra("connectionID");
        this.cConsumerType = intent.getStringExtra("ConsumerType");
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(getResources().getString(R.string.cConsumptionAnalysisTitle));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        networkConnection = new NetworkConnection(getApplicationContext());
        httpConnection = new HttpClientWrapper();
        progressBar = new CustomProgressDialog(ApplianceAnalysisActivity.this);
        progressBar.setCanceledOnTouchOutside(false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mChart = (BarChart) findViewById(R.id.barchart);
        tvConsumedUnits = (TextView) findViewById(R.id.tvconsumedunits);
        tvAmount = (TextView) findViewById(R.id.tvamount);
        appliance_recyclerview = (RecyclerView) findViewById(R.id.appliance_recycler);
        appliance_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        applianceanalysisList = DBConstants.applianceDetailsDtoArrayList;
        adapter = new ApplianceAnalysisAdapter(this, applianceanalysisList);
        appliance_recyclerview.setAdapter(adapter);


        comparingList(applianceanalysisList);
        CalculateAmount(applianceanalysisList);

        if (Util.checkAppLanguage(ApplianceAnalysisActivity.this).equalsIgnoreCase("ta")){
            isTamil = true;
        }


    }

    private void CalculateAmount(List<ApplianceDto> applianceList) {
        try {
            float totalConsumedUnits = 0;
            for (int i = 0; i < applianceList.size(); i++) {
                totalConsumedUnits += applianceList.get(i).getConsumedUnits();

            }



            getApplianceCharges(Integer.parseInt(cConsumerType), totalConsumedUnits);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void comparingList(List<ApplianceDto> serverList){
        try{
            applianceList.clear();
            Map<String, List<ApplianceDto>> map = new HashMap<String, List<ApplianceDto>>();

            for (ApplianceDto detailsDto : serverList) {
                String key  = detailsDto.getApplianceCode();
                if(map.containsKey(key)){
                    List<ApplianceDto> list = map.get(key);
                    list.add(detailsDto);

                }else{
                    List<ApplianceDto> list = new ArrayList<ApplianceDto>();
                    list.add(detailsDto);
                    map.put(key, list);
                }

            }

            for(String key:map.keySet())
            {
                List<ApplianceDto> appli=map.get(key);
                int count=appli.size();
                float sum=0;
                for(ApplianceDto c:appli)
                {
                    sum=sum+c.getConsumedUnits();
                }
                //   Log.info("key"+key+"count"+count+"sum"+sum);
                ApplianceListDto applianceListDto =new ApplianceListDto();
                applianceListDto.setAApplianceCode(key);
                applianceListDto.setQQuantity(count);
                applianceListDto.setTTotalConsumed(sum);
                applianceListDto.setIImageDrawableId(appli.get(0).getImageDrawableId());
                if(appli.get(0).getApplianceCode().equalsIgnoreCase("19")) {
                    applianceListDto.setAApplianceName("Miscellaneous");
                }else{
                    applianceListDto.setAApplianceName(appli.get(0).getApplianceName());
                }
                applianceList.add(applianceListDto);
                setData(applianceList);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setData(List<ApplianceListDto> applianceList) {
        try {
            if (applianceList != null && applianceList.size() > 0) {

                ArrayList<BarDataSet> dataSets = null;

                ArrayList<String> appliancename = new ArrayList<>();
                ArrayList<BarEntry> consumedUnits = new ArrayList<>();
                ArrayList<Integer> color = new ArrayList<>();


                for (int i = 0; i < applianceList.size(); i++) {
                    appliancename.add(applianceList.get(i).getAApplianceName());
                    consumedUnits.add(new BarEntry((applianceList.get(i).getTTotalConsumed()), i));
                    Random rnd = new Random();
                    int colorcode = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    color.add(colorcode);


                }

                BarDataSet barDataSet1 = new BarDataSet(consumedUnits, "");
                barDataSet1.setColors(color);


                Configuration config = getApplicationContext().getResources().getConfiguration();

                if (config.smallestScreenWidthDp >= 720) {
                    barDataSet1.setValueTextSize(12f);  // 10-inch tablet and above
                } else if (config.smallestScreenWidthDp >= 600) {
                    barDataSet1.setValueTextSize(10f); // 7-inch tablet and above

                } else {
                    // For mobile devices
                }

                dataSets = new ArrayList<>();
                dataSets.add(barDataSet1);

                setUpChart(dataSets, appliancename);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {

        Intent analysisactivity = new Intent(ApplianceAnalysisActivity.this, ApplianceListActivity.class).putExtra("connectionID", cConnectionID).putExtra("ConsumerType", cConsumerType);
        startActivity(analysisactivity);
        finish();
    }

    private void setUpChart(ArrayList<BarDataSet> dateSet, ArrayList<String> xAxisValue) {
        try {

            BarData data = new BarData(xAxisValue, dateSet);
            mChart.setData(data);
            mChart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer
            mChart.setDrawGridBackground(false);
            mChart.getAxisLeft().setDrawGridLines(false);
            mChart.getAxisRight().setDrawLabels(false);
            mChart.getXAxis().setDrawGridLines(false);

            mChart.getLegend().setEnabled(false);
            //   mChart.setOnChartValueSelectedListener(this);


            YAxis yAxis = mChart.getAxisLeft();
            yAxis.setDrawGridLines(false);
            yAxis.setEnabled(false);


            yAxis = mChart.getAxisRight();
            yAxis.setDrawGridLines(false);
            yAxis.setEnabled(false);


            mChart.getAxisRight().setDrawLabels(false);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setLabelsToSkip(0);

            Legend legend = mChart.getLegend();
            legend.setTextSize(getResources().getDimension(R.dimen.legendsize));
           // xAxis.setAvoidFirstLastClipping(true);
            xAxis.setLabelRotationAngle(270f);
            mChart.setDescription("");
            mChart.animateXY(2000, 2000);


            mChart.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case GETCHARGESFORAPPLIANCE:
                dismissProgress();
                applianceChargesResponse(message);
                break;
            default:
                dismissProgress();
                break;
        }


    }

    private void getApplianceCharges(int consumerID, float unitsumed) {
        try {

            tvConsumedUnits.setText(String.format("%.2f", unitsumed) + " kWh");
            long ConsumedUnits = Math.round(unitsumed*30);
            UnitConsumptionDto unitConsumptionDto = new UnitConsumptionDto();
            ConsumerTypeDto consumerType = new ConsumerTypeDto();
            consumerType.setId(consumerID);
            unitConsumptionDto.setConsumerType(consumerType);
            unitConsumptionDto.setUnitsConsumed(ConsumedUnits);


            if (networkConnection.isNetworkAvailable()) {
                String url = "/appliance/getchargesforappliances";
                String login = new Gson().toJson(unitConsumptionDto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.GETCHARGESFORAPPLIANCE, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applianceChargesResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("applianceCharges ", "Response : " + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            UnitConsumptionDto unitConsumptionDto = gson.fromJson(response, UnitConsumptionDto.class);
            if (unitConsumptionDto != null) {
                if (unitConsumptionDto.getStatusCode() == 0) {

                    float amount = Float.parseFloat(unitConsumptionDto.getAmount());
                    float dayAmount = amount/30;

                    tvAmount.setText(Math.round(dayAmount)+" INR");

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    private float cCalculateConsumedUnit(float watts, String hours, String minute, int rating, boolean mStarFlag) {

        float fFinalConsumedUnit;


        float minutes = Integer.parseInt(hours) * 60 + Integer.parseInt(minute);

        if (mStarFlag) {
            float sStarValue = DBHelper.getInstance(ApplianceAnalysisActivity.this).getStarPercentage(rating);
            float wWithOutStarResult = ((float) watts / 1000) * (minutes / 60);
            float temp_starUnits = wWithOutStarResult;
            float sStarValue_ = (float) sStarValue / 100;
            float wWithStarResult = wWithOutStarResult-(temp_starUnits * sStarValue_);

            fFinalConsumedUnit = wWithStarResult;

        } else {
            float wWithOutStarResult = ((float) watts / 1000) * (minutes / 60);
            fFinalConsumedUnit = wWithOutStarResult;
        }


        return Float.parseFloat(String.format("%.02f", fFinalConsumedUnit));
    }

    public class ApplianceAnalysisAdapter extends RecyclerView.Adapter<ApplianceAnalysisAdapter.ApplianceAnalysisHolder> {
        private Context mContext;
        private List<ApplianceDto> applianceList;


        public ApplianceAnalysisAdapter(Context context, List<ApplianceDto> applianceList) {
            this.mContext = context;
            this.applianceList = applianceList;
            hHoursAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, HOURS);
            mMinutesAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, MINUTES);


        }

        public class ApplianceAnalysisHolder extends RecyclerView.ViewHolder {
            private MaterialBetterSpinner mHours, mMinutes;
            private RatingBar mApplianceRattingBar;
            private ImageView mApplianceImage;
            TextView mApplianceNameTxt;

            LinearLayout lLinearRatingLayout,lLinearPowerSavingsTipLayout;


            public ApplianceAnalysisHolder(View v) {
                super(v);
                mHours = (MaterialBetterSpinner) v.findViewById(R.id.mHours);
                mMinutes = (MaterialBetterSpinner) v.findViewById(R.id.mMinutes);
                mApplianceRattingBar = (RatingBar) v.findViewById(R.id.mApplianceRattingBar);
                mApplianceImage = (ImageView) v.findViewById(R.id.appliance_Img);
                lLinearRatingLayout = (LinearLayout) v.findViewById(R.id.lLinearRatingLayout);
                lLinearPowerSavingsTipLayout = (LinearLayout) v.findViewById(R.id.lLinearPowerSavingsTipLayout);
                mApplianceNameTxt = (TextView) v.findViewById(R.id.mApplianceNameTxt);

            }

        }

        @Override
        public ApplianceAnalysisHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_appliane_analysis, parent, false);

            return new ApplianceAnalysisHolder(itemView);
        }
        private void aApplianceTipsDialog(String uURL) {

            // Create custom dialog object
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // Include dialog.xml file
            dialog.setContentView(R.layout.tips_dialog_layout);
            // Set dialog title
            dialog.setTitle(null);

            // set values for custom dialog components - text, image and button
            WebView webView = (WebView) dialog.findViewById(R.id.tTipsWebView);
         //   webView.loadUrl("file:///android_asset/cancel.html");
            webView.loadUrl(uURL);


            dialog.show();

            ((LinearLayout) dialog.findViewById(R.id.btn_close_Linear)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    //   Toast.makeText(CancelTicketActivity.this,"Success",Toast.LENGTH_SHORT).show();

                }
            });


        }
        @Override
        public void onBindViewHolder(final ApplianceAnalysisHolder holder, final int position) {
            hHoursAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, HOURS);
            mMinutesAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, MINUTES);

            holder.mHours.setText(applianceList.get(position).getHours());
            holder.mMinutes.setText(applianceList.get(position).getMinutes());
            holder.mApplianceNameTxt.setText(applianceList.get(position).getApplianceName());
            holder.mApplianceImage.setImageResource(applianceList.get(position).getImageDrawableId());
            holder.mApplianceRattingBar.setRating((float)applianceList.get(position).getRating());

            holder. lLinearPowerSavingsTipLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("3")) {
                        //Refrigerator
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/refrigerator.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/refrigerator_tamil.html");
                        }

                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("4")) {
                        //Lamps (Bulb)
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/lighting.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/lighting_tamil.html");
                        }

                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("1")) {
                        //TV
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/tv.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/tv_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("5")) {
                        //Lamps (CFL)
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/lighting.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/lighting_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("6")) {
                        //Tube Lights
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/lighting.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/lighting_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("7")) {
                        //Electric Dry Iron
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/iron.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/iron_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("8")) {
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/microwave_oven.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/microwave_oven_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("9")) {
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/electric_toaster.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/electric_toaster_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("10")) {
                        //Storage Water Heater
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/water-heater.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/water-heater_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("11")) {
                        //Instant Water Heater
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/water-heater.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/water-heater_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("12")) {
                        //Washing Machine
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/washing-machine.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/washing-machine_tamil.html");
                        }

                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("13")) {
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/mixers_grinders.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/mixers_grinders_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("14")) {
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/mixers_grinders.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/mixers_grinders_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("15")) {
                        //Charger
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/computer-laptop-chargers.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/computer-laptop-chargers_tamil.html");
                        }

                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("16")) {
                        //Emergency Lights
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/lighting.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/lighting_tamil.html");
                        }
                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("17")) {
                        //Air Conditioner
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/air-condition.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/air-condition_tamil.html");
                        }


                    } else if (applianceList.get(position).getApplianceCode().equalsIgnoreCase("18")) {
                        //Pump set
                        if (!isTamil) {
                            aApplianceTipsDialog("file:///android_asset/motor.html");
                        } else {
                            aApplianceTipsDialog("file:///android_asset/motor_tamil.html");
                        }

                    } else {
                        //Air Conditioner
                        Toast.makeText(ApplianceAnalysisActivity.this, getResources().getString(R.string.tTipsNotAvailable), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if(applianceList.get(position).isStarRated()){
                holder.lLinearRatingLayout.setVisibility(View.VISIBLE);
            }else{
                holder.lLinearRatingLayout.setVisibility(View.GONE);
            }
            holder.mHours.setAdapter(hHoursAdapter);

            holder.mMinutes.setAdapter(mMinutesAdapter);
            holder.mApplianceRattingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                    try {
                        Log.e("Appliance Analysis","after rating "+ applianceanalysisList.get(position).getRating());
                        applianceanalysisList.get(position).setRating(rating);
                        Log.e("Appliance Analysis","after rating "+ applianceanalysisList.get(position).getRating());
                        float totalConsumedUnits = cCalculateConsumedUnit(applianceanalysisList.get(position).getCapacityInWatts(),
                                applianceanalysisList.get(position).getHours(),
                                applianceanalysisList.get(position).getMinutes(),
                                (int) rating, applianceanalysisList.get(position).isStarRated());
                        applianceanalysisList.get(position).setConsumedUnits(totalConsumedUnits);
                        Log.e("Appliance Analysis","totalConsumedUnits"+totalConsumedUnits);
                        CalculateAmount(applianceanalysisList);
                        comparingList(applianceanalysisList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            holder.mMinutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        String mMinutes = mMinutesAdapter.getItem(i);
                        applianceanalysisList.get(position).setMinutes(mMinutes);
                        float totalConsumedUnits = cCalculateConsumedUnit(applianceanalysisList.get(position).getCapacityInWatts(),
                                applianceanalysisList.get(position).getHours(), applianceanalysisList.get(position).getMinutes(),
                                (int)applianceanalysisList.get(position).getRating(),
                                applianceanalysisList.get(position).isStarRated());

                        applianceanalysisList.get(position).setConsumedUnits(totalConsumedUnits);

                        CalculateAmount(applianceanalysisList);
                        comparingList(applianceanalysisList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.mHours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        String hHours = hHoursAdapter.getItem(i);
                        if (hHours.equals("24")) {
                            holder.mMinutes.setText("00");
                            applianceanalysisList.get(position).setMinutes("00");
                            holder.mMinutes.setEnabled(false);

                        }else{
                            holder.mMinutes.setEnabled(true);
                            mMinutesAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, MINUTES);
                            holder.mMinutes.setAdapter(mMinutesAdapter);
                        }
                        applianceanalysisList.get(position).setHours(hHours);

                        float totalConsumedUnits = cCalculateConsumedUnit(applianceanalysisList.get(position).getCapacityInWatts(),
                                applianceanalysisList.get(position).getHours(), applianceanalysisList.get(position).getMinutes(),
                                (int)applianceanalysisList.get(position).getRating(),
                                applianceanalysisList.get(position).isStarRated());

                        applianceanalysisList.get(position).setConsumedUnits(totalConsumedUnits);

                        CalculateAmount(applianceanalysisList);
                        comparingList(applianceanalysisList);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return applianceList.size();
        }

    }

}
