package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Activity.Dialog.BillinfoDialog2;
import com.omneagate.erbc.Activity.Dialog.PaymentFailureDialog;
import com.omneagate.erbc.Dto.BillHistoryDto;
import com.omneagate.erbc.Dto.BillpayDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Dto.UnitConsumptionDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.MySharedPreference;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by user1 on 2/6/16.
 */

public class BillHistoryDetailActivity extends BaseActivity {

    BillHistoryDto billdetail;
    CustomProgressDialog progressBar;
    UnitConsumptionDto unitConsumptionDto;
    private static final String TAG = BillHistoryDetailActivity.class.getName();
    Button paynw,print;
    String consumername,consumernumber,consumeraddress,connectioncategory,consumercategory,invoice,billcycleperiod,billdateandtime,previouspaymentdate,previousreadingdate,previouspayment,previouspaymentdate1,currentmeterreading,currentreadingdate,payduedate,consumptionunits,amount,penaltyamount;
    String splitdate,transdate;
    String previousActivtyName;
    private boolean tipsClicked =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billhistorydetail);


        FloatingActionButton history_info = (FloatingActionButton) findViewById(R.id.fab);
        paynw = (Button) findViewById(R.id.paynow2);
        print = (Button) findViewById(R.id.print);


        configureInitialPage();
        historyInfo();
        history_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unitConsumptionDto != null) {
                    if (unitConsumptionDto.getStatusCode() == 0) {
                        Log.e("boolean", "" + unitConsumptionDto.getIsInternBill());

                        BillinfoDialog2 billinfo = new BillinfoDialog2(BillHistoryDetailActivity.this, unitConsumptionDto);
                        billinfo.show();
/*
                        if(unitConsumptionDto.getIsInternBill())
                        {
                            BillinfoDialog billinfo = new BillinfoDialog(BillHistoryDetailActivity.this,unitConsumptionDto);
                            billinfo.show();
                        }
                        else
                        {
                            BillinfoDialog2 billinfo = new BillinfoDialog2(BillHistoryDetailActivity.this,unitConsumptionDto);
                            billinfo.show();
                        }*/
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            }
        });

        paynw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paynowclick();
         //       callprint();
            }
        });
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tipsClicked) {
                    callprint();
                }else{
                    checkPrinterTips();
                }
            }
        });
    }

    private void configureInitialPage() {
        try {
            networkConnection = new NetworkConnection(this);
            httpConnection = new HttpClientWrapper();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setTitle(getString(R.string.bottom_bill));

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            Intent intent = getIntent();
            String connenction = intent.getStringExtra("billdetails");
            previousActivtyName = intent.getStringExtra("activityName");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            billdetail = gson.fromJson(connenction, BillHistoryDto.class);
            Log.e("boolena", "" + billdetail.getBillStatus());
            if (billdetail.getBillStatus().equals("true")) {
                paynw.setVisibility(View.INVISIBLE);
            } else {
                paynw.setVisibility(View.VISIBLE);
            }
            ((TextView) findViewById(R.id.consumer_name)).setText("" + billdetail.getConsumerName());
            consumername= billdetail.getConsumerName();
            ((TextView) findViewById(R.id.consumer_number)).setText("" + billdetail.getConsumerNumber());
            consumernumber=billdetail.getConsumerNumber();
            ((TextView) findViewById(R.id.consumer_address)).setText("" + billdetail.getConsumerAddressLine1());
            consumeraddress=billdetail.getConsumerAddressLine1();
            ((TextView) findViewById(R.id.taluk_address)).setText("" + billdetail.getConsumerVillage() + "," + billdetail.getConsumerTaluk());
            ((TextView) findViewById(R.id.district_address)).setText("" + billdetail.getConsumerDistrict() + "-" + billdetail.getConsumerPinCode()+ "\n" + "\n" +billdetail.getConsumerCountry());
            ((TextView) findViewById(R.id.address1)).setText("" + billdetail.getConsumerAddressLine2());
            ((TextView) findViewById(R.id.invoice)).setText("" + billdetail.getInvoiceNo());
            invoice=billdetail.getInvoiceNo();
            ((TextView) findViewById(R.id.bill_date)).setText("" + billdetail.getBillDate());
            billdateandtime=billdetail.getBillDate();
            ((TextView) findViewById(R.id.connection_type)).setText("" + billdetail.getConnectionType());
            connectioncategory=billdetail.getConnectionType();

            ((TextView) findViewById(R.id.consumertype)).setText("" + billdetail.getConsumerType());
            consumercategory=billdetail.getConsumerType();

            ((TextView) findViewById(R.id.consuption)).setText("" + billdetail.getConsumption() + " kWh");
            //((TextView) findViewById(R.id.cycledate)).setText("" + billdetail.getBillDate());
            String fromDate = billdetail.getBillCycleFromDate();
            String toDate = billdetail.getBillCycleToDate();
            ((TextView) findViewById(R.id.cycledate)).setText("" + fromDate.substring(0, fromDate.indexOf(' '))
                    + " to " + toDate.substring(0, toDate.indexOf(' ')));
            ((TextView) findViewById(R.id.approx_amt)).setText("" + getString(R.string.Rs) + " " + billdetail.getAmount());
            amount=billdetail.getAmount();
            ((TextView) findViewById(R.id.previous_reading)).setText("" + billdetail.getPreviousMeterReading() + " kWh");

            ((TextView) findViewById(R.id.previous_readingdate)).setText("" + billdetail.getPreviousMeterReadingDate());

            ((TextView) findViewById(R.id.current_reading)).setText("" + billdetail.getCurrentMeterReading() + " kWh");
            currentmeterreading=billdetail.getCurrentMeterReading();
            ((TextView) findViewById(R.id.txt_penalty)).setText("₹ " + billdetail.getPenaltyAmount());
            penaltyamount=billdetail.getPenaltyAmount();
            ((TextView) findViewById(R.id.payment_due_date)).setText(""+billdetail.getLastDueDate());
            payduedate=billdetail.getLastDueDate();
            ((TextView) findViewById(R.id.previous_payment)).setText(""+billdetail.getPreviousPayment());
            previouspayment=billdetail.getPreviousPayment();
            ((TextView) findViewById(R.id.previous_payment_date)).setText(""+billdetail.getPreviousPaymentDate());
            previouspaymentdate=billdetail.getPreviousPaymentDate();
            Log.e("paydate",previouspaymentdate);
           /* ((TextView) findViewById(R.id.penalty_charges_bpsc)).setText("₹ " + billdetail.getBpscCharges());

            ((TextView) findViewById(R.id.total_penalty_charges)).setText("₹ " + billdetail.getBpscCharges());

                    ((TextView) findViewById(R.id.re_connection_charges)).setText("" + billdetail.getReconnectionCharges());*/
            ((TextView) findViewById(R.id.current_reading_date)).setText(Util.appDateFormat(billdetail.getCurrentMeterReadingDate()));
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error", e.toString(), e);
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case BILL_INFO:
                billInfoResonse(message);
                dismissProgress();
                break;
            case PAY_BILL_HISTORY:
                pay_billresponse(message);
                dismissProgress();
                break;
            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void historyInfo() {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                JSONObject requestObject = new JSONObject();
                requestObject.put("billId", "" + billdetail.getId());
                System.out.println("Sham checking bill no :::" + requestObject.toString());
                StringEntity se = new StringEntity(requestObject.toString(), HTTP.UTF_8);
                String url = "/billhistory/getbillinfo";
                httpConnection.sendRequest(url, null, ServiceListenerType.BILL_INFO, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
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


    private void billInfoResonse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            unitConsumptionDto = gson.fromJson(response, UnitConsumptionDto.class);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void paynowclick() {
        BillpayDto requestbill = new BillpayDto();
        requestbill.setAmount(billdetail.getAmount());
        requestbill.setConnectionId("" + billdetail.getConnectionId());
        requestbill.setConsumption(billdetail.getConsumption());
        requestbill.setBillId("" + billdetail.getId());
        requestbill.setCustomerId("" + DBHelper.getInstance(this).getCustomerId());
        Log.e("request_data", "" + requestbill.toString());
        progressBar = new CustomProgressDialog(this);
        progressBar.setCanceledOnTouchOutside(false);
        if (networkConnection.isNetworkAvailable()) {
            try {
                String url = "/myusage/paybill";
                String request_bill = new Gson().toJson(requestbill);
                StringEntity se = null;
                se = new StringEntity(request_bill, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.PAY_BILL_HISTORY, SyncHandler, RequestType.POST, se, this);
            } catch (UnsupportedEncodingException e) {
                GlobalAppState.getInstance().trackException(e);
                e.printStackTrace();
            }
        } else {
            dismissProgress();
            Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
        }
    }

    private void pay_billresponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data_paybill", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BillpayDto pay_response = gson.fromJson(response, BillpayDto.class);
            if (pay_response.getStatusCode() == 0) {
                Intent patmnet_loading = new Intent(BillHistoryDetailActivity.this, PaymentLoaderActivity.class);
                patmnet_loading.putExtra("paybill", new Gson().toJson(pay_response));
                patmnet_loading.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(patmnet_loading);
                finish();
            } else {
                PaymentFailureDialog alertDialog = new PaymentFailureDialog(BillHistoryDetailActivity.this);
                alertDialog.show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        if (previousActivtyName.equalsIgnoreCase("BillHistroy")) {
            Intent in = new Intent(BillHistoryDetailActivity.this, LandingActivity.class);
            startActivity(in);
            finish();

        } else {
            Intent in = new Intent(BillHistoryDetailActivity.this, PayBillActivity.class);
            startActivity(in);
            finish();
        }
    }
    private String BluetoothPrintData() {

        StringBuilder textData = new StringBuilder();
        textData.append("    ");
        textData.append("-------------------------------------------------------------------\n");
        textData.append("              e-RPC\n");
        textData.append("         Bill Receipt\n");





        splitdate = billdateandtime;
        String[] parts = splitdate.split(" ");
        String part1 = parts[0];
        Log.e("par", part1);
        String part2 = parts[1];
        Log.e("par1", part2);








        textData.append("Consumer Name           : " + consumername + "\n");
        textData.append("Consumer Number         : " + consumernumber + "\n");
        textData.append("Invoice                 : " + invoice + "\n");
        textData.append("Bill Date               : " + part1 +"\n");
        textData.append("Connection Category     : " + connectioncategory + "\n");
        textData.append("Consumer Category       : " + consumercategory + "\n");
        textData.append("Amount                  : " + amount +" INR"+ "\n");
        textData.append("Current Meter Reading   : " + currentmeterreading+" kWh" + "\n");
        textData.append("Penalty Amount          : " + penaltyamount +"\n");
        textData.append("Pay Due Date            : " + payduedate + "\n");
        textData.append("Previous Payment        : " + previouspayment + "\n");
        textData.append("Previous Payment Date   : " + previouspaymentdate + "\n");



        textData.append("\n");
        textData.append("-------------------------------------------------------------------\n\n\n\n");
        textData.append("Customer  Signature --------- \n\n\n\n ");
        textData.append("Agent Signature  --------- \n");
        textData.append("----------------------------------------------------------------------------\n");
        textData.append("\n");
        textData.append("\n");
        return textData.toString();


    }

    private void checkPrinterTips() {
        try {
            int cCount =   MySharedPreference.readInteger(getApplicationContext(),"BluePrintCount",0);
            if (cCount <3) {
                cCount++;
                tipsClicked=false;
                MySharedPreference.writeInteger(getApplicationContext(),"BluePrintCount",cCount);
                Intent in = new Intent(BillHistoryDetailActivity.this,BluetoothPrintTipsActivity.class);
                in.putExtra("activityName","BillHistroyDetailActivity");
                startActivity(in);

            }else{
                callprint();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void callprint() {
        String printdata = BluetoothPrintData();
        BlueToothPrint_new printing = new BlueToothPrint_new(this);
        Log.e("printdata", printdata);
        printing.opendialog(printdata);


    }



}
