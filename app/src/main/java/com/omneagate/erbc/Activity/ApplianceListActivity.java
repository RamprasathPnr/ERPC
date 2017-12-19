package com.omneagate.erbc.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplianceListActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mApplianceRecyclerView;
    private Button mShowAnalysisBtn;
    private ApplianceRecyclerView adapter;
    private CustomProgressDialog progressBar;

    TextView mTxtUnitsDay,mTxtUnitsMonth,mTxtAmountMonth,mTxtDayAmount;

    String cConnectionID,cConsumerType;
    float tTotalUnits,tTempUnits=0;
    private ArrayList<ApplianceListDto> applianceList =new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance);
        Intent intent =getIntent();
        this.cConnectionID = intent.getStringExtra("connectionID");
        this.cConsumerType = intent.getStringExtra("ConsumerType");
        ConfigureInitialView();

    }

    private void ConfigureInitialView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(getResources().getString(R.string.cConsumptionDetailsTitle));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        networkConnection = new NetworkConnection(getApplicationContext());
        httpConnection = new HttpClientWrapper();
        progressBar = new CustomProgressDialog(ApplianceListActivity.this);
        progressBar.setCanceledOnTouchOutside(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTxtUnitsDay = (TextView)findViewById(R.id.mTxtUnitsDay);
        mTxtUnitsMonth = (TextView)findViewById(R.id.mTxtUnitsMonth);
        mTxtAmountMonth = (TextView)findViewById(R.id.mTxtAmountMonth);
        mTxtDayAmount = (TextView)findViewById(R.id.mTxtDayAmount);

        mShowAnalysisBtn = (Button) findViewById(R.id.mBtnShowAnalysis);
        mShowAnalysisBtn.setOnClickListener(this);
        mApplianceRecyclerView = (RecyclerView) findViewById(R.id.mApplianceRecyclerView);
        mApplianceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mApplianceRecyclerView.setNestedScrollingEnabled(false);
        if(DBConstants.applianceDetailsDtoArrayList!=null) {
            comparingList(DBConstants.applianceDetailsDtoArrayList);

            for (int i=0 ; i<DBConstants.applianceDetailsDtoArrayList.size();i++) {

                tTotalUnits =   tTempUnits + DBConstants.applianceDetailsDtoArrayList.get(i).getConsumedUnits();
                tTempUnits =tTotalUnits;
            }


            long ConsumedUnits =Math.round(tTotalUnits *30);
            Log.e("ApplianceList","Consumed units"+ConsumedUnits);
            mTxtUnitsDay.setText(String.format("%.2f",tTotalUnits)+" kWh");
            mTxtUnitsMonth.setText(String.format("%.2f",tTotalUnits*30)+" kWh");

            getApplianceCharges(Integer.parseInt(cConsumerType),ConsumedUnits);
        }

        List<ApplianceDto> applianceDtoList = DBHelper.getInstance(ApplianceListActivity.this).getApplianceList();
        adapter = new ApplianceRecyclerView(ApplianceListActivity.this, applianceList);
        mApplianceRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {


        DBConstants.applianceDetailsDtoArrayList.clear();
        DBConstants.aNewApplianceQuantityAryList.clear();

        startActivity(new Intent(ApplianceListActivity.this,ApplianceEntryActivity.class).putExtra("connectionID",cConnectionID).putExtra("ConsumerType",cConsumerType));
        ApplianceListActivity.this.finish();
    }
    private void comparingList(List<ApplianceDto> serverList){
        try{
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

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getApplianceCharges(int consumerID, long unitsumed) {
        try {
            Log.e("ApplianceList","after Consumed units"+unitsumed);
            UnitConsumptionDto unitConsumptionDto = new UnitConsumptionDto();
            ConsumerTypeDto consumerType = new ConsumerTypeDto();
            consumerType.setId(consumerID);
            unitConsumptionDto.setConsumerType(consumerType);
            unitConsumptionDto.setUnitsConsumed(unitsumed);


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
    private void applianceChargesResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("applianceCharges ", "Response : " + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            UnitConsumptionDto unitConsumptionDto = gson.fromJson(response, UnitConsumptionDto.class);
            if (unitConsumptionDto != null) {
                if (unitConsumptionDto.getStatusCode() == 0) {
                  // unitConsumptionDto.getAmount()
                    mTxtAmountMonth.setText(unitConsumptionDto.getAmount()+" INR");
                    float amount = Float.parseFloat(unitConsumptionDto.getAmount());
                    float dayAmount = amount/30;

                    mTxtDayAmount.setText(Math.round(dayAmount)+" INR");
                }else{

                    Toast.makeText(ApplianceListActivity.this,unitConsumptionDto.getErrorDescription(),Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBtnShowAnalysis:
                Intent analysisactivity = new Intent(ApplianceListActivity.this, ApplianceAnalysisActivity.class);
                analysisactivity.putExtra("connectionID",cConnectionID);
                analysisactivity.putExtra("ConsumerType",cConsumerType);
                startActivity(analysisactivity);
                finish();
                break;
            default:
                break;
        }

    }

    private class ApplianceRecyclerView extends RecyclerView.Adapter<ApplianceRecyclerView.MyViewHolder> {

        public  Context mContext;
        private ArrayList<ApplianceListDto> applianceDtoList;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mTxtApplianceName, mTxtApplianceUnit,aApplianceQuantityTxt;
            ImageView mImgmApplianceIcon;


            public MyViewHolder(View view) {
                super(view);
                mTxtApplianceName = (TextView) view.findViewById(R.id.appliance_name);
                aApplianceQuantityTxt = (TextView) view.findViewById(R.id.aApplianceQuantityTxt);
                mTxtApplianceUnit = (TextView) view.findViewById(R.id.units_applianceTxt);
                mImgmApplianceIcon = (ImageView) view.findViewById(R.id.appliance_img);

            }
        }

        public ApplianceRecyclerView(Context context, ArrayList<ApplianceListDto> applianceDtoList) {
            this.mContext = context;
            this.applianceDtoList = applianceDtoList;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.appliance__adapter, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.mTxtApplianceName.setText(applianceDtoList.get(position).getAApplianceName());
            holder.aApplianceQuantityTxt.setVisibility(View.VISIBLE);
            holder.aApplianceQuantityTxt.setText(""+applianceDtoList.get(position).getQQuantity());
            holder.mTxtApplianceUnit.setText(String.format("%.02f", applianceDtoList.get(position).getTTotalConsumed()));
            holder.mImgmApplianceIcon.setImageResource(applianceDtoList.get(position).getIImageDrawableId());

        }


        @Override
        public int getItemCount() {
            return applianceDtoList.size();
        }

        public List<ApplianceListDto> getDetails() {
            return applianceDtoList;
        }
    }

}
