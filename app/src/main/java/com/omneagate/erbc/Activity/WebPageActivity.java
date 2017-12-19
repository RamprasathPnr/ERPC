package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;

public class WebPageActivity extends BaseActivity {

    private WebView myWebView;
    private CustomProgressDialog progressBar;
    private NetworkConnection networkConnection;
    private LinearLayout mLlWebLayout;
    private String title;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);

        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra(DBConstants.INTENT_TITLE);
            url = intent.getStringExtra(DBConstants.INTENT_URL);

        }
        networkConnection = new NetworkConnection(WebPageActivity.this);
        setupView();
        getData();
        /*if (networkConnection.isNetworkAvailable()) {
            getData();
        } else {
            myWebView.setVisibility(View.GONE);
            mLlWebLayout.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backPage = new Intent(WebPageActivity.this, LandingActivity.class);
        startActivity(backPage);
        finish();
    }

    private void getData() {

        /*progressBar = new CustomProgressDialog(WebPageActivity.this);
        progressBar.setCanceledOnTouchOutside(false);*/

        System.out.println("Sham Checking App Language :::" +
                Util.checkAppLanguage(WebPageActivity.this));
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        if (url.equalsIgnoreCase("1")) {
            if (Util.checkAppLanguage(WebPageActivity.this).equalsIgnoreCase("ta"))
                myWebView.loadUrl("file:///android_asset/about_us_tamil.html");
            else myWebView.loadUrl("file:///android_asset/aboutus.html");
        } else {
            if (Util.checkAppLanguage(WebPageActivity.this).equalsIgnoreCase("ta"))
                myWebView.loadUrl("file:///android_asset/terms_conditions_tamil.html");
            else myWebView.loadUrl("file:///android_asset/terms_conditions.html");
        }
    }

    private void setupView() {

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

        myWebView = (WebView) findViewById(R.id.my_web);
        getSupportActionBar().setTitle(title);

        //mLlWebLayout = (LinearLayout) findViewById(R.id.ll_web_layout);
    }
}
