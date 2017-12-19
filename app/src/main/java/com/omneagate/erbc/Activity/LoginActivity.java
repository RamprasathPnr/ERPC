package com.omneagate.erbc.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Activity.Dialog.ChangeUrlDialog;
import com.omneagate.erbc.Activity.Dialog.LanguageSelectionDialog;
import com.omneagate.erbc.Activity.Dialog.LoginAlertDialog;
import com.omneagate.erbc.Activity.Dialog.MenuAdapter;
import com.omneagate.erbc.Adapter.SpinnerAdaptor;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.LoginDto;
import com.omneagate.erbc.Dto.LoginResponseDto;
import com.omneagate.erbc.Dto.MenuDataDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Dto.VersionUpgradeDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.NoDefaultSpinner;
import com.omneagate.erbc.Util.Util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    EditText countryCode, mobileNumber;
    CustomProgressDialog progressBar;
    TextInputLayout mobilenumberLay;
    LoginResponseDto loginResponse;
    ListPopupWindow popupWindow;
    String mobile_number_regex = "^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$";
    private static final String TAG = LoginActivity.class.getName();
    public static String mobile_number;
    private NoDefaultSpinner spinnerLanguage;
    private List<String> languageList;
    private SpinnerAdaptor spinnerAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setSpinnerValue();
        configureInitialPage();

        DBHelper.getInstance(this).insertCountry1();
        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence txtWatcherStr, int start, int before, int count) {
                if (mobileNumber.getText().toString().length() == 10) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mobileNumber.getWindowToken(), 0);
                }
            }
        });

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) Util.changeAppLanguage(LoginActivity.this, false);
                else Util.changeAppLanguage(LoginActivity.this, true);
                restartApplication();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Re-starts the application where language change take effects
    private void restartApplication() {
        Intent restart = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(restart);
        finish();

    }

    private void configureInitialPage() {


        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        countryCode = (EditText) findViewById(R.id.countrycode);
        mobileNumber = (EditText) findViewById(R.id.phonenumber);
        mobilenumberLay = (TextInputLayout) findViewById(R.id.layout_mobile);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(10);
        mobileNumber.setFilters(filterArray);

    }

    private void setSpinnerValue() {
        languageList = new ArrayList<>();
        languageList.add("English");
        languageList.add("தமிழ்");
        spinnerLanguage = (NoDefaultSpinner) findViewById(R.id.spinner_language);
        String language_ = "en";
        spinnerAdaptor = new SpinnerAdaptor(LoginActivity.this, languageList, language_);
        spinnerLanguage.setAdapter(spinnerAdaptor);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    //onclick event for login button
    public void userLogin(View view) {

       /* String name=null;
        Log.e("",name);*/


        try {

            if (networkConnection.isNetworkAvailable()) {
                InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (!validateMobilenumber()) {
                    return;
                }
                if (!validateMobilenumber1()) {
                    return;
                }

                if (!validCellPhone(mobileNumber.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.mobile_err), Toast.LENGTH_SHORT).show();
                    requestFocus(mobileNumber);
                    return;

                }
                LoginDto loginCredentials = getUsernamePassword();
                //authenticateUser(loginCredentials);
                checkUserApk();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }


    private LoginDto getUsernamePassword() {
        LoginDto loginCredentials = new LoginDto();
        String country_code = countryCode.getText().toString();
        String moible_number = mobileNumber.getText().toString();
        loginCredentials.setMode("APP");
        loginCredentials.setCountryCode(country_code);
        loginCredentials.setMobileNumber(moible_number);
        mobile_number = mobileNumber.getText().toString();
        return loginCredentials;
    }

    private void authenticateUser(LoginDto loginCredentials) {

        try {
            //  progressDialog = new SpotsDialog(this, R.style.Custom);
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/access/login";
                String login = new Gson().toJson(loginCredentials);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGIN_USER, SyncHandler,
                        RequestType.POST, se, this);
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {
            case LOGIN_USER:
                dismissProgress();
                userLoginResponse(message);
                break;
            case CHECKVERSION:
                dismissProgress();
                checkData(message);
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

    private boolean validateMobilenumber() {
        if (mobileNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.loginmobilenumber), Toast.LENGTH_SHORT).show();
            requestFocus(mobileNumber);
            return false;
        } else {
            mobilenumberLay.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateMobilenumber1() {
        if (mobileNumber.getText().toString().length() <= 9) {
            Toast.makeText(getApplicationContext(), getString(R.string.mobile_err), Toast.LENGTH_SHORT).show();
            requestFocus(mobileNumber);
            return false;
        } else {
            mobilenumberLay.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void userLoginResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            loginResponse = gson.fromJson(response, LoginResponseDto.class);
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            if (loginResponse != null) {
                if (loginResponse.getStatusCode() == 0) {
                    loginResponse.setCountrycode(countryCode.getText().toString());
                    loginResponse.setMobilenumber(mobileNumber.getText().toString());
                    String responsedata = new Gson().toJson(loginResponse);
                    Intent otp_page = new Intent(LoginActivity.this, OTP_activity.class);
                    otp_page.putExtra("message", responsedata);
                    otp_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(otp_page);
                    finish();
                } else if (loginResponse.getStatusCode() == 2015) {
                    LoginAlertDialog alertdialog = new LoginAlertDialog(LoginActivity.this, getString(R.string.header_msg));
                    alertdialog.show();
                } else if (loginResponse.getStatusCode() == 5000) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    Toast.makeText(getApplicationContext(), "" +
                            loginResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                }
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    public void showPopupMenu(View v) {

        List<MenuDataDto> menuDto = new ArrayList<>();
        menuDto.add(new MenuDataDto("Change URL", R.drawable.icon_server, "யுஆர்யல் மாற்று"));
        menuDto.add(new MenuDataDto("Language", R.drawable.icon_lang, "மொழி"));
        popupWindow = new ListPopupWindow(this);
        ListAdapter adapter = new MenuAdapter(this, menuDto); // The view ids to map the data to
        popupWindow.setAnchorView(v);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(700); // note: don't use pixels, use a dimen resource
        popupWindow.setOnItemClickListener(this); // the callback for when a list item is selected
        popupWindow.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        popupWindow.dismiss();
        switch (position) {
            case 0:
                new ChangeUrlDialog(this).show();
                break;
            case 1:
                new LanguageSelectionDialog(this).show();
                break;
        }
    }

    private void checkUserApk() {

        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                String url = "/versionupgrade/getversionupgrade";
                JSONObject update = new JSONObject();
                update.put("applicationType","PUBLIC_APP");
                StringEntity se = new StringEntity(update.toString(), HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.CHECKVERSION, SyncHandler,
                        RequestType.POST, se, getApplicationContext());
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void checkData(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("vesionUpgrade", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            VersionUpgradeDto versionUpgradeDto = gson.fromJson(response, VersionUpgradeDto.class);
            if (versionUpgradeDto == null || versionUpgradeDto.getUpgradeVersion() == 0 || StringUtils.isEmpty(versionUpgradeDto.getLocation())) {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.errorUpgrade), Toast.LENGTH_SHORT).show();
            } else {
                if (versionUpgradeDto.getStatusCode() == 0) {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    if (versionUpgradeDto.getUpgradeVersion() > pInfo.versionCode) {
                        dismissProgress();
                        Intent intent = new Intent(this, AutoUpgrataionActivity.class);
                        intent.putExtra("downloadPath", versionUpgradeDto.getLocation());
                        intent.putExtra("newVersion", versionUpgradeDto.getUpgradeVersion());
                        startActivity(intent);
                        finish();
                    } else {
                        LoginDto loginCredentials = getUsernamePassword();
                        authenticateUser(loginCredentials);
                    }
                } else if (versionUpgradeDto.getStatusCode() == 6002) {
                    dismissProgress();
                    Toast.makeText(getApplicationContext(), getString(R.string.version_not_avaiable), Toast.LENGTH_SHORT).show();
                } else {
                    dismissProgress();
                    Toast.makeText(getApplicationContext(), getString(R.string.errorUpgrade), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            dismissProgress();
            GlobalAppState.getInstance().trackException(e);
            Toast.makeText(getApplicationContext(), getString(R.string.errorUpgrade), Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString(), e);
        }
    }
}




