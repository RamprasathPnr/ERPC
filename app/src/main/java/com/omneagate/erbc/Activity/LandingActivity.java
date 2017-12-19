package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.Dimension;
import com.omneagate.erbc.Activity.Dialog.LogoutDialog;
import com.omneagate.erbc.Dto.AppPropertiesDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Fragment.BillHistoryFragment;
import com.omneagate.erbc.Fragment.ComplaintsFragment;
import com.omneagate.erbc.Fragment.PaymentFragment;
import com.omneagate.erbc.Fragment.TariffDetailFragment;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.AppProperties;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class LandingActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private NavigationView navigationView;

    private LinearLayout mLlTariff;
    private LinearLayout mLlBill;
    private LinearLayout mLlPayment;

    private TariffDetailFragment mTariffFragment;
    private BillHistoryFragment mBillHistoryFragment;
    private PaymentFragment mPaymentFragment;

    private ImageView mIvTariff;
    //private ImageView mIvCompliant;
    private ImageView mIvBillHistory;
    private ImageView mIvPaymentHistory;

    private TextView mTvTariff;
    private TextView mTvBillHistory;
    private TextView mTvPaymentHistory;

    private NetworkConnection networkConnection;
    private HttpClientWrapper httpConnection;
    public TextView title;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            if (response != null
                    && !response.equalsIgnoreCase(getString(R.string.connectionRefused))) {
                Gson gson = new Gson();
                AppPropertiesDto appProperties = gson.fromJson(response, AppPropertiesDto.class);

                if (appProperties.getStatusCode() == 0) {
                    AppProperties.BILL_CYCLE_PERIOD_DAYS = appProperties.getBillCyclePeriodDays();
                    AppProperties.BILL_CYCLE_VARIATION_DAYS = appProperties.getBillCycleVariationDays();
                    AppProperties.OTP_EXPIRY_TIME = appProperties.getOtpExpiryTime();
                    AppProperties.OTP_LENGTH = appProperties.getOtpLength();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView) findViewById(R.id.title_toolbar);


        /*setTitle("");
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.outward_size));
*/
        networkConnection = new NetworkConnection(LandingActivity.this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        setNavigationValue();
        setupView();

        new GetAppConfiguration().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        setFragment(mTariffFragment);

        toolbar.setNavigationIcon(R.drawable.tool_icon);
    }
    public void changeSize(String name,float dem){

        Log.e("Landing","Name : "+name +"Dimesion : "+dem);
        title.setText(name);
        title.setTextSize(dem);

    }
    private void setupView() {

        mTariffFragment = new TariffDetailFragment();
        //mComplaintsFragment = new ComplaintsFragment();
        mBillHistoryFragment = new BillHistoryFragment();
        mPaymentFragment = new PaymentFragment();

        mTvTariff = (TextView) findViewById(R.id.txt_tariff);
        // mTvCompliant = (TextView) findViewById(R.id.txt_compliant);
        mTvBillHistory = (TextView) findViewById(R.id.txt_bill_history);
        mTvPaymentHistory = (TextView) findViewById(R.id.txt_payment_history);

        mIvTariff = (ImageView) findViewById(R.id.img_tariff);
        //  mIvCompliant = (ImageView) findViewById(R.id.img_complaint);
        mIvBillHistory = (ImageView) findViewById(R.id.img_bill);
        mIvPaymentHistory = (ImageView) findViewById(R.id.img_payment);

        mLlTariff = (LinearLayout) findViewById(R.id.ll_tariff);
        // mLlCompliant = (LinearLayout) findViewById(R.id.ll_complaint);
        mLlBill = (LinearLayout) findViewById(R.id.ll_bill_history);
        mLlPayment = (LinearLayout) findViewById(R.id.ll_payment);

        mLlTariff.setOnClickListener(this);
        //   mLlCompliant.setOnClickListener(this);
        mLlBill.setOnClickListener(this);
        mLlPayment.setOnClickListener(this);


    }

    private void setLayoutBackground() {

        mTvTariff.setTextColor(getResources().getColor(R.color.ash));
        // mTvCompliant.setTextColor(getResources().getColor(R.color.ash));
        mTvBillHistory.setTextColor(getResources().getColor(R.color.ash));
        mTvPaymentHistory.setTextColor(getResources().getColor(R.color.ash));

        mIvTariff.setImageDrawable(getResources().getDrawable(R.drawable.ic_dashboard_black));
        // mIvCompliant.setImageDrawable(getResources().getDrawable(R.drawable.complaints));
        mIvBillHistory.setImageDrawable(getResources().getDrawable(R.drawable.bill_history_bottom));
        mIvPaymentHistory.setImageDrawable(getResources().getDrawable(R.drawable.payment));

        mLlTariff.setBackgroundColor(Color.WHITE);
        //mLlCompliant.setBackgroundColor(Color.WHITE);
        mLlBill.setBackgroundColor(Color.WHITE);
        mLlPayment.setBackgroundColor(Color.WHITE);
    }

    private void setNavigationValue() {

        View header = navigationView.getHeaderView(0);
        TextView profileName = (TextView) header.findViewById(R.id.profilename);
        TextView firstLetter = (TextView) header.findViewById(R.id.imageView6);
        TextView versionName = (TextView) header.findViewById(R.id.version_name);
        versionName.setVisibility(View.GONE);

        CustomerDto customerRecord = DBHelper.getInstance(getApplicationContext()).getcustomerData();
        if (customerRecord != null) {
            profileName.setText("" + Util.capitalize(customerRecord.getFirstName())
                    + " " + Util.capitalize(customerRecord.getLastName()));
            char firstChar = customerRecord.getFirstName().toUpperCase().charAt(0);
            Log.e("firstLetter", "" + firstLetter);
            firstLetter.setText("" + firstChar);
        }

        versionName.setText(getString(R.string.version));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setFragment(mTariffFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     getMenuInflater().inflate(R.menu.landing, menu);
     /*   MenuInflater awesome = getMenuInflater();
        awesome.inflate(R.menu.landing, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            int end = spanString.length();
            spanString.setSpan(new RelativeSizeSpan(7.5f), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(spanString);
        }*/

        return true;
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.profile:
                Intent profilePage = new Intent(LandingActivity.this, MyProfileActivity.class);
                profilePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(profilePage);
                finish();
                break;

            case R.id.reminder:
                Intent reminderPage = new Intent(LandingActivity.this, ReminderListActivity.class);
                reminderPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(reminderPage);
                finish();
                break;

         /*   case R.id.usage:
                Intent myUsage = new Intent(LandingActivity.this, MyusageListActivity.class);
                myUsage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myUsage);
                finish();
                break;*/

            case R.id.logout1:
                LogoutDialog logoutDialog = new LogoutDialog(LandingActivity.this, getString(R.string.logoutString));
                logoutDialog.show();
                break;

            case R.id.about:
                Intent aboutPage = new Intent(LandingActivity.this, WebPageActivity.class);
                aboutPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                aboutPage.putExtra(DBConstants.INTENT_URL, "1");
                aboutPage.putExtra(DBConstants.INTENT_TITLE, this.getString(R.string.about_us));
                startActivity(aboutPage);
                finish();
                break;

            case R.id.appliance:
                Intent intent = new Intent(LandingActivity.this, UsageListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

            case R.id.feedback:
                Intent complaint = new Intent(getApplicationContext(), ComplaintListActivity.class);
                complaint.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(complaint);
                finish();
                break;

            case R.id.terms:
                Intent termsPage = new Intent(LandingActivity.this, WebPageActivity.class);
                termsPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                termsPage.putExtra(DBConstants.INTENT_URL, "2");
                termsPage.putExtra(DBConstants.INTENT_TITLE, this.getString(R.string.terms_condition));
                startActivity(termsPage);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_tariff:
                setLayoutBackground();
                mLlTariff.setBackgroundColor(getResources().getColor(R.color.ash));
                setFragment(mTariffFragment);
                mTvTariff.setTextColor(Color.WHITE);
                mIvTariff.setImageDrawable(getResources().getDrawable(R.drawable.ic_dashboard));
                break;

         /*   case R.id.ll_complaint:
                setLayoutBackground();
                mLlCompliant.setBackgroundColor(getResources().getColor(R.color.ash));
                setFragment(mComplaintsFragment);
                mTvCompliant.setTextColor(Color.WHITE);
                mIvCompliant.setImageDrawable(getResources().getDrawable(R.drawable.complaints_active));
                break;
*/
            case R.id.ll_bill_history:
                if (!networkConnection.isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                setLayoutBackground();
                mLlBill.setBackgroundColor(getResources().getColor(R.color.ash));
                setFragment(mBillHistoryFragment);
                mTvBillHistory.setTextColor(Color.WHITE);
                mIvBillHistory.setImageDrawable(getResources().getDrawable(R.drawable.bill_history_bottom_active));
                break;

            case R.id.ll_payment:
                if (!networkConnection.isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                    return;
                }
                setLayoutBackground();
                mLlPayment.setBackgroundColor(getResources().getColor(R.color.ash));
                setFragment(mPaymentFragment);
                mTvPaymentHistory.setTextColor(Color.WHITE);
                mIvPaymentHistory.setImageDrawable(getResources().getDrawable(R.drawable.payment_active));
                break;
        }
    }

    private class GetAppConfiguration extends AsyncTask<Object, Object, String> {

        @Override
        protected String doInBackground(Object... objects) {
            if (networkConnection.isNetworkAvailable()) {

                httpConnection = new HttpClientWrapper();
                String url = "/appproperties/get";
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGIN_USER, SyncHandler,
                        RequestType.GET, null, LandingActivity.this);

            }

            return null;
        }
    }

    private void setFragment(final Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter,
                R.anim.fragment_slide_right_exit);
        transaction.replace(R.id.landing_frame, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
