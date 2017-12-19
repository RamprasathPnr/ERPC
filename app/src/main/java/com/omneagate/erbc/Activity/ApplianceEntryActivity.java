package com.omneagate.erbc.Activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.ApplianceInfoDialog;
import com.omneagate.erbc.Activity.Dialog.ConfigureApplianceDialog;
import com.omneagate.erbc.Activity.Dialog.ConfigureMiscellaneousDialog;
import com.omneagate.erbc.Adapter.ApplianceAdapter;
import com.omneagate.erbc.Adapter.RecyclerItemClickListener;
import com.omneagate.erbc.Dto.AddApplianceDto;
import com.omneagate.erbc.Dto.ApplianceDto;
import com.omneagate.erbc.Dto.ApplianceRequestDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.GenericDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.MySharedPreference;
import com.omneagate.erbc.Util.NetworkConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

public class ApplianceEntryActivity extends BaseActivity implements View.OnClickListener {

    public ApplianceAdapter mAdapter;
    private RecyclerView RvApplianceList;
    private CustomProgressDialog progressBar;
    List<ApplianceDto> applianceDtoList;
    AddApplianceDto applianceResponse;
    private Button ContinueBtn;

    int counter=0;
//    SharedPreferences app_preferences;

    boolean isMiscellauous =false;

    String cConnectionID,cConsumerType;
    private List<ApplianceDto> aApplianceQuantityAryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance_entry);
        Intent intent =getIntent();
       // cConnectionID = getIntent().getStringExtra("connectionID");
          cConnectionID = intent.getStringExtra("connectionID");
          cConsumerType = intent.getStringExtra("ConsumerType");

      /*  app_preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        counter = app_preferences.getInt("counter", 1);*/



        int cCount =   MySharedPreference.readInteger(getApplicationContext(),"counter",0);
        if (cCount <3) {
            cCount++;
            MySharedPreference.writeInteger(getApplicationContext(),"counter",cCount);
            startActivity(new Intent(ApplianceEntryActivity.this,ApplianceTipsActivity.class));

        }
       /* SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt("counter", +(counter+1));
        editor.commit();*/
        setupView();
        getApplianceByConnection(Integer.parseInt(cConnectionID));
    }


    private void setupView() {

        applianceDtoList = new ArrayList<>();
        networkConnection = new NetworkConnection(getApplicationContext());
        httpConnection = new HttpClientWrapper();

        progressBar = new CustomProgressDialog(ApplianceEntryActivity.this);
        progressBar.setCanceledOnTouchOutside(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(getResources().getString(R.string.cConfigureAppliancesTitle));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ContinueBtn = (Button) findViewById(R.id.ContinueBtn);
        ContinueBtn.setOnClickListener(this);
        RvApplianceList = (RecyclerView) findViewById(R.id.appliance_recycler);
        RvApplianceList.setLayoutManager(new LinearLayoutManager(ApplianceEntryActivity.this));
        setUpAdapter();
        FloatingActionButton App_info = (FloatingActionButton) findViewById(R.id.fab);
        App_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApplianceInfoDialog billinfo = new ApplianceInfoDialog(ApplianceEntryActivity.this);
                billinfo.show();

            }
        });

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case ADDAPPLIANCEBYCONNECTION:
                dismissProgress();
                addApplianceResponse(message);
                break;
            case GETAPPLIANCEBYCONNECTION:
                dismissProgress();
                getApplianceResponse(message);
                break;
            case ADDAPPLIANCES:
                dismissProgress();
                addmiscellaneousResponse(message);
                break;
            case DELETEAPPLIANCES:
                dismissProgress();
                deleteAppliancesResponse(message);
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

    private void addApplianceByConnection(List<ApplianceDto> applianceDtoList) {
        try {

            ApplianceRequestDto applianceRequestDto =new ApplianceRequestDto();
            GenericDto connectionDto =new GenericDto();
            connectionDto.setId(Integer.parseInt(cConnectionID));
            applianceRequestDto.setConnection(connectionDto);
            applianceRequestDto.setCustomerAppliance(applianceDtoList);

            if (networkConnection.isNetworkAvailable()) {
                String url = "/appliance/addallappliance";
                String login = new Gson().toJson(applianceRequestDto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.ADDAPPLIANCEBYCONNECTION, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   public void addMiscellaneous(ApplianceDto applianceDto){
       try {
           GenericDto conneGenericDto = new GenericDto();
           conneGenericDto.setId(Integer.parseInt(cConnectionID));
           applianceDto.setConnection(conneGenericDto);
           if (networkConnection.isNetworkAvailable()) {
               String url = "/appliance/addappliance";
               String login = new Gson().toJson(applianceDto);
               StringEntity se = new StringEntity(login, HTTP.UTF_8);
               progressBar.show();
               httpConnection.sendRequest(url, null, ServiceListenerType.ADDAPPLIANCES, SyncHandler, RequestType.POST, se, this);
           } else {
               dismissProgress();
               Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
           }

       }catch (Exception e){
           e.printStackTrace();
       }
   }
    public void DeleteAppliances(ApplianceDto applianceDto){
        try{
            if (networkConnection.isNetworkAvailable()) {
                String url = "/appliance/deleteappliance";
                String login = new Gson().toJson(applianceDto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.DELETEAPPLIANCES, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void addApplianceResponse(Bundle message) {
        try {

            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("ApplianceEntry", "<====  Add Appliance  response =====> " + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            AddApplianceDto applianceResponse = gson.fromJson(response, AddApplianceDto.class);
            if (applianceResponse.getStatusCode() == 0) {
                Intent in = new Intent(ApplianceEntryActivity.this, ApplianceListActivity.class);
                    in.putExtra("connectionID",cConnectionID);
                    in.putExtra("ConsumerType",cConsumerType);
                    startActivity(in);
                    ApplianceEntryActivity.this.finish();
            } else {
                Toast.makeText(ApplianceEntryActivity.this, applianceResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void addmiscellaneousResponse(Bundle message){
        try{
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("ApplianceEntry", "<====  Add Misc  response =====> " + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ApplianceDto applianceDto =gson.fromJson(response,ApplianceDto.class);
            if(applianceDto.getStatusCode() == 0){
                /* cConnectionID = intent.getStringExtra("connectionID");
          cConsumerType = intent.getStringExtra("ConsumerType");*/
             /*   if( DBConstants.applianceDetailsDtoArrayList !=null) {
                    DBConstants.applianceDetailsDtoArrayList.clear();
                }*/
                Intent in = new Intent(ApplianceEntryActivity.this, ApplianceEntryActivity.class);
                in.putExtra("connectionID", "" +cConnectionID);
                in.putExtra("ConsumerType", "" + cConsumerType);
                startActivity(in);
                ApplianceEntryActivity.this.finish();
             //   Toast.makeText(ApplianceEntryActivity.this, "Appliances added sucessfully", Toast.LENGTH_SHORT).show();

            }

        }catch (Exception e){

        }
    }
    private void deleteAppliancesResponse(Bundle message){
        try{
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("ApplianceEntry", "<====  Delete Appliance response =====> " + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ApplianceDto applianceDto =gson.fromJson(response,ApplianceDto.class);
            if(applianceDto.getStatusCode() == 0 ){
                Toast.makeText(ApplianceEntryActivity.this,"Appliance Removed Successfully",Toast.LENGTH_SHORT).show();
                Intent in = new Intent(ApplianceEntryActivity.this, ApplianceEntryActivity.class);
                in.putExtra("connectionID", "" +cConnectionID);
                in.putExtra("ConsumerType", "" + cConsumerType);
                startActivity(in);
                ApplianceEntryActivity.this.finish();

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sSetData(AddApplianceDto applianceResponse) {
        for (int i = 0; i < applianceResponse.getAppliancesCode().size(); i++) {

            boolean found = false;
            for (int j = 0; j < applianceDtoList.size(); j++) {
                if (applianceResponse.getAppliancesCode().get(i).equals(applianceDtoList.get(j).getApplianceCode())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                //Toast.makeText(ApplianceEntryActivity.this, "default appliance", Toast.LENGTH_SHORT).show();

            } else {
                isMiscellauous = true;
               // getCustomerAppliances(applianceResponse.getAppliancesCode().get(i), applianceResponse.getCustomerAppliance());
              //  Toast.makeText(ApplianceEntryActivity.this, "miscellaneous appliance", Toast.LENGTH_SHORT).show();
            }
        }

        if(isMiscellauous){

            Log.e("isMiscellauous","isMiscellauous");
            ApplianceDto applianceDto =new ApplianceDto();
            applianceDto.setApplianceName("Miscellaneous");
            applianceDto.setApplianceCode("19");
            applianceDto.setImageDrawableId(R.drawable.genric_icon);
            applianceDtoList.add(applianceDto);
            if(mAdapter!=null) {
                mAdapter.notifyDataSetChanged();
            }

//            mAdapter = new ApplianceAdapter(ApplianceEntryActivity.this, applianceDtoList);
//            RvApplianceList.setAdapter(mAdapter);


        }

    }

    /*@SuppressLint("LongLogTag")
    public void getCustomerAppliances(String code, List<ApplianceDto> customerAppliance) {
        for (ApplianceDto applianceDto : customerAppliance) {
            if (applianceDto.getApplianceCode().equals(code)) {
                applianceDtoList.add(applianceDto);
            }
        }

        Log.e("Total Appliance List Size", "" + applianceDtoList.size());

    }*/

    /* private void sSetData(AddApplianceDto applianceResponse) {

         int size = applianceResponse.getCustomerAppliance().size();

      for(int i=0; i<applianceDtoList.size();i++){

          for(int j = 0; j<applianceResponse.getCustomerAppliance().size(); j++){

              int code = Integer.parseInt(applianceResponse.getCustomerAppliance().get(j).getApplianceCode());

              if(applianceDtoList.get(i).getApplianceCode().contains(applianceResponse.getCustomerAppliance().get(j).getApplianceCode())){

                  Log.e("Appliance Name ",""+applianceResponse.getCustomerAppliance().get(j).getApplianceName());
                  Toast.makeText(ApplianceEntryActivity.this,"Appliance Name  "+applianceResponse.getCustomerAppliance().get(j).getApplianceName(),Toast.LENGTH_SHORT).show();

              }
          }
      }

     }
 */
    private void getApplianceByConnection(int connectionId) {
        try {
            GenericDto genericDto = new GenericDto();
            genericDto.setId(connectionId);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/appliance/getappliancesbyconnection";
                String login = new Gson().toJson(genericDto);
                Log.e("ApplianceEntry", "getApplianceByConnection" + login.toString());
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.GETAPPLIANCEBYCONNECTION, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    public void gGetApplianceQuantity(AddApplianceDto customerAppliance, int code) {
        try {
            aApplianceQuantityAryList.clear();
            aApplianceQuantityAryList.size();
            if (customerAppliance == null) {
                return;
            }

            for (ApplianceDto applianceDto : customerAppliance.getCustomerAppliance()) {
                int val = Integer.parseInt(applianceDto.getApplianceCode());
                if (val == code) {
                    aApplianceQuantityAryList.add(applianceDto);
                }
            }
            if(DBConstants.aNewApplianceQuantityAryList.size()>0){

                for (ApplianceDto applianceDto : DBConstants.aNewApplianceQuantityAryList) {
                    int val = Integer.parseInt(applianceDto.getApplianceCode());
                    if (val == code) {
                        aApplianceQuantityAryList.add(applianceDto);
                    }
                }
            }

            Log.e("Total aApplianceQuantityAryList  Size", "" + aApplianceQuantityAryList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getApplianceResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("ApplianceEntry", "<====  Get Appliance  response =====> " + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            applianceResponse = gson.fromJson(response, AddApplianceDto.class);
            if (applianceResponse.getStatusCode() == 0) {

                sSetData(applianceResponse);
//                Toast.makeText(ApplianceEntryActivity.this, "Get Appliance  response success ", Toast.LENGTH_SHORT).show();
//                  Log.e("ApplianceEntry","Response "+applianceResponse.toString());

            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpAdapter() {
        try {

            applianceDtoList = DBHelper.getInstance(ApplianceEntryActivity.this).getApplianceList();
            Gson gson = new Gson();
            String data = gson.toJson(applianceDtoList);
            Log.e("applianceDtoList json", "" + data);

            if (applianceDtoList != null) {

                if (applianceDtoList.size() > 0) {

                    mAdapter = new ApplianceAdapter(ApplianceEntryActivity.this, applianceDtoList);
                    RvApplianceList.setAdapter(mAdapter);
                    RvApplianceList.addOnItemTouchListener(
                            new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                    gGetApplianceQuantity(applianceResponse, Integer.parseInt(applianceDtoList.get(position).getApplianceCode()));
                                 //   Toast.makeText(ApplianceEntryActivity.this, "" + applianceDtoList.get(position).getApplianceCode(), Toast.LENGTH_SHORT).show();
                                    if (aApplianceQuantityAryList != null && aApplianceQuantityAryList.size() > 0) {
                                        if (applianceDtoList.get(position).isIsselected()) {
                                         //   holder.ll_aplliance.setBackgroundColor(Color.GRAY);
                                            v.setBackgroundColor(getResources().getColor(R.color.gray_1));

                                        } else {
                                          //  holder.ll_aplliance.setBackgroundColor(android.R.color.white);
                                            v.setBackgroundColor(getResources().getColor(R.color.white));
                                        }
//                                        v.setBackgroundColor(getResources().getColor(R.color.gray_1));

                                        if(Integer.parseInt(applianceDtoList.get(position).getApplianceCode()) == 19){

                                            applianceDtoList.get(position).setIsselected(true);

                                            ConfigureMiscellaneousDialog configureMiscellaneousDialog = new ConfigureMiscellaneousDialog(ApplianceEntryActivity.this, applianceDtoList.get(position).getApplianceName(), applianceDtoList.get(position).isStarRated(), position, applianceDtoList.get(position).getCapacityInWatts(), aApplianceQuantityAryList, applianceDtoList.get(position).getApplianceCode(), applianceDtoList.get(position).getImageDrawableId());
                                            configureMiscellaneousDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            configureMiscellaneousDialog.setCanceledOnTouchOutside(false);
                                            configureMiscellaneousDialog.setCancelable(false);
                                            configureMiscellaneousDialog.show();

                                            mAdapter.notifyDataSetChanged();


                                        }else {
                                            applianceDtoList.get(position).setIsselected(true);
                                            ConfigureApplianceDialog configureApplianceDialog = new ConfigureApplianceDialog(ApplianceEntryActivity.this, applianceDtoList.get(position).getApplianceName(), applianceDtoList.get(position).isStarRated(), position, applianceDtoList.get(position).getCapacityInWatts(), aApplianceQuantityAryList, applianceDtoList.get(position).getApplianceCode(), applianceDtoList.get(position).getImageDrawableId());
                                            configureApplianceDialog.setCanceledOnTouchOutside(false);
                                            configureApplianceDialog.setCancelable(false);
                                            configureApplianceDialog.show();

                                            mAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        if (applianceDtoList.get(position).isIsselected()) {
                                            //   holder.ll_aplliance.setBackgroundColor(Color.GRAY);
                                            v.setBackgroundColor(getResources().getColor(R.color.gray_1));

                                        } else {
                                            //  holder.ll_aplliance.setBackgroundColor(android.R.color.white);
                                            v.setBackgroundColor(getResources().getColor(R.color.white));
                                        }
//                                        v.setBackgroundColor(getResources().getColor(R.color.gray_1));
                                        applianceDtoList.get(position).setIsselected(true);
                                        ConfigureApplianceDialog configureApplianceDialog = new ConfigureApplianceDialog(ApplianceEntryActivity.this, applianceDtoList.get(position).getApplianceName(), applianceDtoList.get(position).isStarRated(), position, applianceDtoList.get(position).getCapacityInWatts(),applianceDtoList.get(position).getApplianceCode(), applianceDtoList.get(position).getImageDrawableId());
                                        configureApplianceDialog.setCanceledOnTouchOutside(false);
                                        configureApplianceDialog.setCancelable(false);
                                        configureApplianceDialog.show();

                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            })
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        DBConstants.applianceDetailsDtoArrayList.clear();
        DBConstants.aNewApplianceQuantityAryList.clear();
        if(applianceDtoList !=null) {
            applianceDtoList.clear();
        }
        Intent in = new Intent(ApplianceEntryActivity.this, MyusageListActivity.class);
        startActivity(in);
        finish();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ContinueBtn:
                if (DBConstants.applianceDetailsDtoArrayList == null && DBConstants.applianceDetailsDtoArrayList.size() == 0) {
                    Toast.makeText(ApplianceEntryActivity.this, getResources().getString(R.string.aApplianceError), Toast.LENGTH_SHORT).show();

                } else {

                    addApplianceByConnection(DBConstants.applianceDetailsDtoArrayList);
                    /*Intent in = new Intent(ApplianceEntryActivity.this, ApplianceListActivity.class);
                    in.putExtra("connectionID",cConnectionID);
                    in.putExtra("ConsumerType",cConsumerType);
                    startActivity(in);
                    ApplianceEntryActivity.this.finish();*/
                }

                break;

            default:
                break;
        }

    }
}
