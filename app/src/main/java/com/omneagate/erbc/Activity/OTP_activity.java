package com.omneagate.erbc.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Dto.CustomerOtpTrackDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.LoginDto;
import com.omneagate.erbc.Dto.LoginResponseDto;
import com.omneagate.erbc.Dto.OTP_Response;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Services.OtpListener;
import com.omneagate.erbc.Services.OtpMessageServices;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

/**
 * Created by user1 on 28/4/16.
 */
public class OTP_activity extends BaseActivity implements OtpListener {

    LoginResponseDto loginresponse;
    EditText otp_password;
    TextInputLayout otp_Lay;
    CustomerOtpTrackDto sendOTPDto;
    CustomProgressDialog progressBar;
    OTP_Response otpResponse;
    TextView timer_count, resendOtp;
    private static final String TAG = OTP_activity.class.getName();
    private static final int SMS_REQUEST_METER = 5;
    private boolean getOTP = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_activity);
        OtpMessageServices.bindListener(OTP_activity.this);
      /*  int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            requestReceiveSMSPermission(SMS_REQUEST_METER);
        }*/

        if (ActivityCompat.checkSelfPermission(OTP_activity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(OTP_activity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 0);

        }
        try {
            networkConnection = new NetworkConnection(this);
            httpConnection = new HttpClientWrapper();
            String message = getIntent().getStringExtra("message");
            loginresponse = new Gson().fromJson(message, LoginResponseDto.class);
            Log.e("loginresponse_dto", "" + loginresponse.getOtp());
            sendOTPDto = new CustomerOtpTrackDto();
            // sendOTPDto.setOtp(loginresponse.getOtp());
            sendOTPDto.setCountryCode(loginresponse.getCountrycode());
            sendOTPDto.setMobile(loginresponse.getMobilenumber());
            otp_password = (EditText) findViewById(R.id.otp_passsword);
            otp_Lay = (TextInputLayout) findViewById(R.id.layout_otp);
            timer_count = (TextView) findViewById(R.id.timer1);
            resendOtp = (TextView) findViewById(R.id.resend);
            countdoun_timer();
            resendOtp.setVisibility(View.INVISIBLE);
            resendOtp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginDto loginCredentials = new LoginDto();
                    loginCredentials.setCountryCode(loginresponse.getCountrycode());
                    loginCredentials.setMobileNumber(loginresponse.getMobilenumber());
                    authenticateUser(loginCredentials);
                }
            });

            otp_password.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable arg0) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence txtWatcherStr, int start, int before, int count) {
                    if (otp_password.getText().toString().length() == 7) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(otp_password.getWindowToken(), 0);
                    }
                }
            });
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    public void otpSubmit(View view) {
        try {
            if (!validateOTP()) {
                return;
            }
            sendOTPDto.setOtp("" + otp_password.getText().toString());
            sendOTPDto.setMode("APP");
            sendOtpToServer(sendOTPDto);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }

    }

    private boolean validateOTP() {
        if (otp_password.getText().toString().trim().isEmpty()) {
            otp_password.setError(getString(R.string.otp_empty));
            requestFocus(otp_password);
            return false;
        } else {
            otp_Lay.setErrorEnabled(false);
        }
        return true;
    }


    private void requestFocus(View view) {
        try {
            if (view.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }catch (Exception e){
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    private void sendOtpToServer(CustomerOtpTrackDto send_otp) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/access/authenticate";
                String login = new Gson().toJson(send_otp);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.SEND_OTP, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Util.messageBar(OTP_activity.this, getString(R.string.connectionRefused));
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

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {
            case SEND_OTP:
                dismissProgress();
                otp_Response(message);
                break;
            case LOGIN_USER:
                dismissProgress();
                userLoginResponse(message);
                break;
            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void otp_Response(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            otpResponse = gson.fromJson(response, OTP_Response.class);
            if (otpResponse != null) {
                if (otpResponse.getStatusCode() == 0) {
                    DBHelper.getInstance(getApplicationContext()).insertCustomer(otpResponse.getCustomerDto());
                    AlertDialog alertdialog = new AlertDialog(OTP_activity.this, getString(R.string.login_success));
                    alertdialog.show();
                } else if (otpResponse.getStatusCode() == 2020) {

                    Toast.makeText(getApplicationContext(), getString(R.string.otp_mismatch), Toast.LENGTH_SHORT).show();
                } else if (otpResponse.getStatusCode() == 2019) {
                    Toast.makeText(getApplicationContext(), getString(R.string.otp_expired), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        try {
            Intent backpage = new Intent(OTP_activity.this, LoginActivity.class);
            backpage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backpage);
            finish();

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString());
        }
    }

    public void countdoun_timer() {
        CountDownTimer cT = new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
                String v = String.format("%02d", millisUntilFinished / 60000);
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                timer_count.setText(v + ":" + String.format("%02d", va));
            }

            public void onFinish() {
                resendOtp.setVisibility(View.VISIBLE);
                timer_count.setText("00:00");
                resendOtp.setTextColor(getResources().getColor(R.color.white));
                resendOtp.setClickable(true);
            }
        };
        cT.start();
    }

    private void authenticateUser(LoginDto loginCredentials) {
        try {
            otp_password.setText("");
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/access/login";
                String login = new Gson().toJson(loginCredentials);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGIN_USER, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Util.messageBar(OTP_activity.this, getString(R.string.connectionRefused));
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void userLoginResponse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            loginresponse = gson.fromJson(response, LoginResponseDto.class);
            if (loginresponse != null) {
                if (loginresponse.getStatusCode() == 0 && loginresponse.getOtp() != null) {
                    resendOtp.setTextColor(getResources().getColor(R.color.gray_2));
                    resendOtp.setClickable(false);
                    countdoun_timer();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    private void requestReceiveSMSPermission(int REQUEST) {
        // Camera permission has not been granted yet. Request it directly.
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                    REQUEST);
        } catch (Exception se) {
            GlobalAppState.getInstance().trackException(se);
            Log.d("FragmentCreate", "You don't have permissions");

            //  errortext.setVisibility(View.VISIBLE);
            //errortext.setText("Please provide Location permission to continue, Settings->Apps->RecommendedApp->Permissions");
            Toast.makeText(this, "Please provide location permissions to continue", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void messageReceived(String messageText) {
        try {
            Log.d("Text", messageText);
            progressBar = new CustomProgressDialog(this);
            progressBar.show();
            if (getOTP) {
                getOTP = false;
                sendOTPDto.setOtp("" + messageText.replaceFirst(".*?(\\d+).*", "$1"));
                otp_password.setText(sendOTPDto.getOtp());
                sendOTPDto.setMode("APP");
                progressBar.dismiss();
                sendOtpToServer(sendOTPDto);
            } else {
                progressBar.dismiss();
            }
        }catch (Exception e){
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }
}
