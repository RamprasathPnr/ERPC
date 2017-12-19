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
import com.omneagate.erbc.Dto.PaymentHistoryDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.MySharedPreference;

/**
 * Created by user1 on 20/6/16.
 */

public class PaymentHistorydetail extends BaseActivity {

    PaymentHistoryDto paymentdetail;
    String transId,transDate,consumerName,consumerNumber,amountPaid,splitDate;
    private boolean tipsClicked =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymentdetail);
        configureInitialPage();
    }

    private void configureInitialPage() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setTitle(getString(R.string.bottom_payment));
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
            String connenction = intent.getStringExtra("paymentdetails");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            paymentdetail = gson.fromJson(connenction, PaymentHistoryDto.class);
            Log.e("consumername", "" + paymentdetail.getConsumerName());
            Log.e("customername", "" + paymentdetail.getCustomerName());
            ((TextView) findViewById(R.id.approx_amt)).setText("" + getString(R.string.Rs) + " " + paymentdetail.getBillAmount());
            ((TextView) findViewById(R.id.trans_id)).setText("" + paymentdetail.getTransactionId());
             transId = ((TextView) findViewById(R.id.trans_id)).getText().toString();
            ((TextView) findViewById(R.id.consumer_name)).setText("" + paymentdetail.getConsumerName());
             consumerName = ((TextView) findViewById(R.id.consumer_name)).getText().toString();
            ((TextView) findViewById(R.id.consumer_number)).setText("" + paymentdetail.getConsumerDisplayNo());
             consumerNumber= ((TextView) findViewById(R.id.consumer_number)).getText().toString();
            ((TextView) findViewById(R.id.consumer_address)).setText("" + paymentdetail.getConsumerAddress1());
            ((TextView) findViewById(R.id.address1)).setText("" + paymentdetail.getConsumerAddress2());
            ((TextView) findViewById(R.id.taluk_address)).setText("" + paymentdetail.getConsumerVillage() + "," + paymentdetail.getConsumerTaluk());
            ((TextView) findViewById(R.id.country_address)).setText("" + paymentdetail.getConsumerCountry() + "-" + paymentdetail.getConsumerPinCode());
            ((TextView) findViewById(R.id.invoice)).setText("" + paymentdetail.getBillInvoiceNumber());
            ((TextView) findViewById(R.id.bill_date)).setText("" + paymentdetail.getBillDate());
            ((TextView) findViewById(R.id.consuption)).setText("" + paymentdetail.getConsumption() + " kWh");
            ((TextView) findViewById(R.id.customer_names)).setText("" + paymentdetail.getCustomerName());
            ((TextView) findViewById(R.id.customer_address1)).setText("" + paymentdetail.getCustomerAddress1());
            ((TextView) findViewById(R.id.customeraddress2)).setText("" + paymentdetail.getCustomerAddress2());
            ((TextView) findViewById(R.id.reconnection_charges)).setText("" + getString(R.string.Rs) + " " + paymentdetail.getReconnectionCharges());
            ((TextView) findViewById(R.id.bpsc_charges)).setText("" + getString(R.string.Rs) + " " + paymentdetail.getBpscCharges());
            ((TextView) findViewById(R.id.amout_paid)).setText("" + getString(R.string.Rs) + " " + paymentdetail.getAmountPaid());
             amountPaid = "INR "+paymentdetail.getAmountPaid();
            ((TextView)findViewById(R.id.TransactionDate)).setText(""+paymentdetail.getTransactionDate());
             transDate = ((TextView) findViewById(R.id.TransactionDate)).getText().toString();
            if (paymentdetail.isPaymentStatus()) {
                ((TextView) findViewById(R.id.trans_status)).setText("Success");
            } else {
                ((TextView) findViewById(R.id.trans_status)).setText("Failure");
            }

            ((TextView) findViewById(R.id.customertaluk_address)).setText("" + paymentdetail.getCustomerVillage() + "," + paymentdetail.getCustomerTaluk());
            ((TextView) findViewById(R.id.customercountry_address)).setText("" + paymentdetail.getCustomerCountry() + "-" + paymentdetail.getCustomerPinCode());
            Button print = (Button) findViewById(R.id.print);
            print.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tipsClicked){
                        callprint();
                    }else{
                        checkPrinterTips();
                    }

                }
            });

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error", e.toString(), e);
        }
    }

    private String BluetoothPrintData() {

        StringBuilder textData = new StringBuilder();
        textData.append("    ");
        textData.append("-------------------------------------------------------------------\n");
        textData.append("              e-RPC\n");
        textData.append("         Payment Receipt\n");





        splitDate = transDate;
        String[] parts = splitDate.split(" ");
        String part1 = parts[0];
        Log.e("par", part1);
        String part2 = parts[1];
        Log.e("par1", part2);




        textData.append("Transaction Id    : " + transId + "\n");
        textData.append("Transaction Date  : " + part1 + "\n");
        textData.append("Transaction Time  : " + part2 + "\n");
        textData.append("Consumer Name     : " + consumerName + "\n");
        textData.append("Consumer Number   : " + consumerNumber +"\n");
        textData.append("Amount Paid       : " + amountPaid + "\n");



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
                tipsClicked=true;
                cCount++;
                MySharedPreference.writeInteger(getApplicationContext(),"BluePrintCount",cCount);
                Intent in = new Intent(PaymentHistorydetail.this,BluetoothPrintTipsActivity.class);
                in.putExtra("activityName","PaymentHistroyDetail");
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

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        Intent in =new Intent(PaymentHistorydetail.this,LandingActivity.class);
        startActivity(in);
        finish();

    }
}
