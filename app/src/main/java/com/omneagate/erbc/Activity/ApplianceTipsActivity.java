package com.omneagate.erbc.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;


public class ApplianceTipsActivity extends AppCompatActivity {
    NetworkConnection networkConnection;
    HttpClientWrapper httpConnection;
    private Button ok_button;
    private boolean isTamil = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Util.checkAppLanguage(ApplianceTipsActivity.this).equalsIgnoreCase("ta"))
            isTamil = true;

        if (isTamil) {
            setContentView(R.layout.activity_appliancetips_tamil);
        } else {
            setContentView(R.layout.activity_appliancetips);
        }

        super.onCreate(savedInstanceState);
        ok_button = (Button) findViewById(R.id.ok_button);

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
