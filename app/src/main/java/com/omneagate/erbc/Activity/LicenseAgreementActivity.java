package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;

/**
 * Created by user1 on 12/7/16.
 */
public class LicenseAgreementActivity extends BaseActivity {

    CheckBox iagree;
    int ch_flag;
    Button agreeBtn;
    private Button mBtNoThanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.licence_agreement);
        agreeBtn = (Button) findViewById(R.id.agreebtn);
        iagree = (CheckBox) findViewById(R.id.check_agree_ch);
        mBtNoThanks = (Button) findViewById(R.id.btn_no_thanks);
        ch_flag = 0;
        iagree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    ch_flag = 1;
                    agreeBtn.setBackgroundResource(R.color.buttoncolor);
                } else {
                    ch_flag = 0;
                    agreeBtn.setBackgroundResource(R.color.dark_yellow);
                }

            }
        });
        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ch_flag == 1) {
                    Intent register_page = new Intent(LicenseAgreementActivity.this, RegisterationActivity.class);
                    register_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(register_page);
                    finish();
                } else {
                    Toast.makeText(LicenseAgreementActivity.this, getResources().getString(R.string.check_license),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_page = new Intent(LicenseAgreementActivity.this, LoginActivity.class);
                register_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(register_page);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent registerPage = new Intent(LicenseAgreementActivity.this, LoginActivity.class);
        registerPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registerPage);
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }
}
