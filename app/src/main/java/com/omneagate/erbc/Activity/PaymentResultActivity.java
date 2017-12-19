package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Dto.BillpayDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.MySharedPreference;

/**
 * Created by user1 on 2/7/16.
 */

public class PaymentResultActivity extends BaseActivity {

    TextView trans_id, trans_date, connection_name, connection_number, amount_paid, pay_status, pay_descriptions;
    String transid,transdate,connectionname,connectionnumber,amountpaid,amountforprint,splitdate;
    BillpayDto trans_detail;
    private boolean tipsClicked =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_success);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        String connenction = intent.getStringExtra("paybill");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        trans_detail = gson.fromJson(connenction, BillpayDto.class);
        configurationViews();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent pay_result = new Intent(PaymentResultActivity.this, LandingActivity.class);
        pay_result.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(pay_result);
        finish();
    }

    private void configurationViews() {
        Button backtodash = (Button) findViewById(R.id.backto);
        Button print = (Button) findViewById(R.id.backto1);
        trans_id = (TextView) findViewById(R.id.transction_txt);
        pay_status = (TextView) findViewById(R.id.pay_status);
        pay_descriptions = (TextView) findViewById(R.id.pay_descriptions);
        trans_date = (TextView) findViewById(R.id.tranms_date_txt);
        connection_name = (TextView) findViewById(R.id.consumer_name_txt);
        connection_number = (TextView) findViewById(R.id.consumer_num_txt);
        amount_paid = (TextView) findViewById(R.id.amt_paid_txt);
        trans_id.setText("" + trans_detail.getTransactionId());
        transid=trans_id.getText().toString();
        Log.e("arroe","------------------------------------------>");
        Log.e("date",trans_detail.getTransactionDate());
        Log.e("arro","------------------------------------------->");
        trans_date.setText("" + trans_detail.getTransactionDate());
        transdate=trans_date.getText().toString();
        connection_name.setText("" + trans_detail.getConsumerName());
        connectionname=connection_name.getText().toString();
        connection_number.setText("" + trans_detail.getConsumerNumber());
        connectionnumber=connection_number.getText().toString();
        amount_paid.setText("INR " + trans_detail.getAmountPaid());

        amountpaid=amount_paid.getText().toString();
        backtodash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  callprint();

                Intent pay_result = new Intent(PaymentResultActivity.this, LandingActivity.class);
                pay_result.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(pay_result);
                finish();
            }


        });
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipsClicked){
                    callprint();
                }else{
                    checkPrinterTips();
                }


              /*  Intent pay_result = new Intent(PaymentResultActivity.this, LandingActivity.class);
                pay_result.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(pay_result);
                finish();*/
            }


        });
    }

    private String BluetoothPrintData() {

        StringBuilder textData = new StringBuilder();
        textData.append("    ");
        textData.append("-------------------------------------------------------------------\n");
        textData.append("              e-RPC\n");
        textData.append("         Payment Receipt\n");





        splitdate = transdate;
        String[] parts = splitdate.split(" ");
        String part1 = parts[0];
        Log.e("par", part1);
        String part2 = parts[1];
        Log.e("par1", part2);




        textData.append("Transaction Id    : " + transid + "\n");
        textData.append("Transaction Date  : " + part1 + "\n");
        textData.append("Transaction Time  : " + part2 + "\n");
        textData.append("Consumer Name     : " + connectionname + "\n");
        textData.append("Consumer Number   : " + connectionnumber +"\n");
        textData.append("Amount Paid       : " + amountpaid + "\n");



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
                MySharedPreference.writeInteger(getApplicationContext(),"BluePrintCount",cCount);

                Intent in =new Intent(PaymentResultActivity.this,BluetoothPrintTipsActivity.class);
                in.putExtra("activityName","PaymentResultActivity");
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
