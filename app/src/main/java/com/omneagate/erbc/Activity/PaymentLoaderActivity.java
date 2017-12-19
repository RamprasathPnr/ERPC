package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Dto.BillpayDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;

/**
 * Created by user1 on 1/7/16.
 */
public class PaymentLoaderActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_loader);

        Intent intent = getIntent();
        String connenction = intent.getStringExtra("paybill");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        final BillpayDto trans_detail = gson.fromJson(connenction, BillpayDto.class);
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    GlobalAppState.getInstance().trackException(e);
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(PaymentLoaderActivity.this, PaymentResultActivity.class);
                    intent.putExtra("paybill", new Gson().toJson(trans_detail));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
