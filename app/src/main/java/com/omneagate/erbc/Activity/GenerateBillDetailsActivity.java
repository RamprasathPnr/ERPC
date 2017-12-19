package com.omneagate.erbc.Activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.DatePickerDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.PaymentFailureDialog;
import com.omneagate.erbc.Dto.BillDetailsDto;
import com.omneagate.erbc.Dto.BillpayDto;
import com.omneagate.erbc.Dto.DashboardDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.GenerateBillResponse;
import com.omneagate.erbc.Dto.GenericDto;
import com.omneagate.erbc.Dto.ResponseDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.GPSService;
import com.omneagate.erbc.Util.NetworkConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.drakeet.materialdialog.MaterialDialog;

public class GenerateBillDetailsActivity extends BaseActivity implements View.OnClickListener {

    private BillDetailsDto billDetailsDto = new BillDetailsDto();
    private TextInputLayout meterreadLay;
    private EditText meterread;
    private ImageView cap_image, capture;
    private Button calculationBtn, button_pay, button_cancel;
    String eb_meter_photo = "";
    private static final int CAMERA_REQUEST_METER = 2;
    private CustomProgressDialog progressBar;
    private boolean isBillGenerated = false;


    private Bitmap bitpmap_card;
    Uri imageUri = null;
    private String filename;
    private String folder_name = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ERPC/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_bill_details);
        configureInitialPage();
        hidePaymentLayout();
    }

    private void configureInitialPage() {
        try {

            networkConnection = new NetworkConnection(this);
            httpConnection = new HttpClientWrapper();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setTitle(getString(R.string.generate_pay_ment));
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
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            billDetailsDto = gson.fromJson(connenction, BillDetailsDto.class);
            ((TextView) findViewById(R.id.tv_consumer_number)).setText("" + billDetailsDto.getConsumerNumber());
            ((TextView) findViewById(R.id.tv_previous_meter_reading)).setText("" + billDetailsDto.getPreviousReading());
            ((TextView) findViewById(R.id.tv_previous_bill_generated)).setText("" + billDetailsDto.getCreatedDate());
            ((TextView) findViewById(R.id.tv_cycle_bill_period)).setText(billDetailsDto.getBillCycleFromDate() + " To " + billDetailsDto.getBillCycleToDate());
            meterread = (EditText) findViewById(R.id.meterread);
            meterreadLay = (TextInputLayout) findViewById(R.id.layout_meter);
            capture = (ImageView) findViewById(R.id.capturebtn);
            cap_image = (ImageView) findViewById(R.id.captureimage);
            calculationBtn = (Button) findViewById(R.id.calcul_btn);
            button_pay = (Button) findViewById(R.id.button_pay);
            button_cancel = (Button) findViewById(R.id.buttonNwCancel);
            button_cancel.setOnClickListener(this);
            button_pay.setOnClickListener(this);
            meterread.setOnClickListener(this);
            calculationBtn.setOnClickListener(this);
            capture.setOnClickListener(this);
            cap_image.setOnClickListener(this);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    private void hidePaymentLayout() {
        try {
            ((Button) findViewById(R.id.buttonNwCancel)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.button_pay)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.total_amount)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tv_total_amount_labl)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tv_total_consumed_units_labl)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.total_consumed_units)).setVisibility(View.GONE);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    private void showPaymentLayout() {
        try {
            ((Button) findViewById(R.id.buttonNwCancel)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.button_pay)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.total_amount)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_total_amount_labl)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_total_consumed_units_labl)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.total_consumed_units)).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    private boolean validatemeterread() {
        if (meterread.getText().toString().trim().isEmpty()) {
            meterreadLay.setError(getString(R.string.meterreadvalue));
            requestFocus(meterread);
            return false;
        } else {
            meterreadLay.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    void takePicture(int selected) {

        filename = "fname_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ERPC/");
        if (!file.exists()) {
            file.mkdirs();
        } else System.out.println("Sham Enter the File Else  method :::");
        File outputFile = new File(file.toString(), filename);
        imageUri = Uri.fromFile(outputFile);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, selected);
      /*  Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, selected);*/
    }

    private void requestCameraPermission(int REQUEST_CAMERA) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA);
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case SENDBILLS:
                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                dismissProgress();
                meterresonse(message);
                break;
            case PAY_BILL:
                dismissProgress();
                pay_billresponse(message);
                break;
            default:
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            //  bitpmap_card = (Bitmap) data.getExtras().get("data");
            bitpmap_card = getbitmap();
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            if (bitpmap_card != null)
                bitpmap_card.compress(Bitmap.CompressFormat.JPEG, 25, stream1);
            byte[] byteArray1 = stream1.toByteArray();
            eb_meter_photo = Base64.encodeToString(byteArray1, Base64.DEFAULT);

            ((ImageView) findViewById(R.id.captureimage)).setVisibility(View.VISIBLE);
            Bitmap bitmap = Bitmap.createScaledBitmap(bitpmap_card, 100, 100, true);
            ((ImageView) findViewById(R.id.captureimage)).setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("imagecapture", e.toString());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.meterread:
                try {
                    meterread.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    //  meterread.setInputType(InputType.TYPE_CLASS_NUMBER);
                    meterread.requestFocus();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(meterread, InputMethodManager.SHOW_FORCED);


                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("meterreadclick", e.toString(), e);
                }
                break;

            case R.id.calcul_btn:
                try {


                    if (!validatemeterread()) {
                        return;
                    }
                    if (eb_meter_photo.equals("") || eb_meter_photo.equals(null)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.meter_image_empty),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (ActivityCompat.checkSelfPermission(GenerateBillDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(GenerateBillDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(GenerateBillDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        return;
                    } else {
                        GenerateBill();
                    }


                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("calculationclick", e.toString(), e);
                }
                break;

            case R.id.captureimage:

                try {
                    dialog(R.layout.image_confirmation, bitpmap_card);

/*
                    final MaterialDialog mMaterialDialog = new MaterialDialog(GenerateBillDetailsActivity.this);
                    mMaterialDialog.setCanceledOnTouchOutside(true);
                    mMaterialDialog.setPositiveButton("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
                    mMaterialDialog.setNegativeButton("DELETE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cap_image.setVisibility(View.INVISIBLE);
                            mMaterialDialog.dismiss();
                        }
                    });
                    Bitmap bitmap = ((BitmapDrawable) cap_image.getDrawable()).getBitmap();
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 10), (int) (bitmap.getHeight() * 10), true);
                    Drawable drawe = new BitmapDrawable(getResources(), resized);
                    mMaterialDialog.setBackground(drawe);
                    mMaterialDialog.show();
                    if(isBillGenerated) {
                        mMaterialDialog.getNegativeButton().setVisibility(View.INVISIBLE);
                        mMaterialDialog.getPositiveButton().setVisibility(View.INVISIBLE);
                    }*/

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("Errror", "" + e.toString());

                }
                break;
            case R.id.capturebtn:

                boolean isGranted;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestCameraPermission(CAMERA_REQUEST_METER);
                } else {
                    takePicture(CAMERA_REQUEST_METER);
                }

                break;
            case R.id.button_pay:
                paynowclick();
                break;
            case R.id.buttonNwCancel:
                clearTempFile();
                Intent in = new Intent(GenerateBillDetailsActivity.this, LandingActivity.class);
                startActivity(in);
                finish();
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

    private void meterresonse(Bundle message) {
        try {
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("Meter Response", "before dto" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            GenerateBillResponse responseDto = gson.fromJson(response, GenerateBillResponse.class);

            // Log.e("Meter Response", "after dto " + billDetailsDto.toString());
            if (responseDto.getStatusCode() == 0) {
                billDetailsDto = responseDto.getCurrentMeterDetailsDto();
                showPaymentLayout();
                calculationBtn.setClickable(false);
                meterread.setEnabled(false);
                capture.setEnabled(false);
                isBillGenerated = true;
                eb_meter_photo = "";
                // ((LinearLayout) findViewById(R.id.displayvalue)).setVisibility(View.VISIBLE);
                //  ((LinearLayout) findViewById(R.id.clickimagelay)).setVisibility(View.GONE);
                /*((Button) findViewById(R.id.paynow2)).setVisibility(View.VISIBLE);*/
                //((ImageView) findViewById(R.id.captureimage)).setVisibility(View.INVISIBLE);

                ((TextView) findViewById(R.id.total_consumed_units)).setText("" + billDetailsDto.getConsumption());
                ((TextView) findViewById(R.id.total_amount)).setText(" ₹ " + billDetailsDto.getApproxCharge());


            } else if (responseDto.getStatusCode() == 3114) {
                Toast.makeText(getApplicationContext(), "" + responseDto.getErrorDescription(),
                        Toast.LENGTH_SHORT).show();
            } else if (responseDto.getStatusCode() == 4101) {
                Toast.makeText(getApplicationContext(), "" + responseDto.getErrorDescription(),
                        Toast.LENGTH_SHORT).show();
            } else if (responseDto.getStatusCode() == 3119) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.my_usage_meter_error), Toast.LENGTH_SHORT).show();
            } else if (responseDto.getStatusCode() == 4501) {
                Toast.makeText(getApplicationContext(), "" + responseDto.getErrorDescription(),
                        Toast.LENGTH_SHORT).show();
            }
            else if(responseDto.getStatusCode() ==2000)
            {
                Toast.makeText(getApplicationContext(), getString(R.string.my_usage_meter_srever_error),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString());
        }
    }

    private void pay_billresponse(Bundle message) {
        try {

            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("Pay Bill", "<====  Meter calculation  response =====> " + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BillpayDto pay_response = gson.fromJson(response, BillpayDto.class);
            Log.e("Pay Bill", "<====  Meter calculation  after dto =====> " + pay_response.toString());
            if (pay_response.getStatusCode() == 0) {
                clearTempFile();
                Intent patmnet_loading = new Intent(GenerateBillDetailsActivity.this, PaymentLoaderActivity.class);
                patmnet_loading.putExtra("paybill", new Gson().toJson(pay_response));
                patmnet_loading.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(patmnet_loading);
                finish();
            } else {

                PaymentFailureDialog alertDialog = new PaymentFailureDialog(GenerateBillDetailsActivity.this);
                alertDialog.show();

            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Pay Bill", "Exception while Paying Bills" + e.toString());
        }
    }

    private void paynowclick() {
        try {

            BillpayDto requestbill = new BillpayDto();
            requestbill.setAmount(billDetailsDto.getApproxCharge());
            requestbill.setConnectionId("" + billDetailsDto.getConnection().getId());
            requestbill.setConsumption(billDetailsDto.getConsumption().toString());
            requestbill.setBillId(billDetailsDto.getBillId().toString());
            requestbill.setCustomerId("" + DBHelper.getInstance(this).getCustomerId());
            Log.e("Pay Bill", "<====  Meter calculation  request called =====> " + requestbill.toString());
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                try {
                    String url = "/myusage/paybill";
                    String request_bill = new Gson().toJson(requestbill);
                    StringEntity se = null;
                    se = new StringEntity(request_bill, HTTP.UTF_8);
                    progressBar.show();
                    httpConnection.sendRequest(url, null, ServiceListenerType.PAY_BILL, SyncHandler, RequestType.POST, se, this);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Pay Bill", "Exception while creating payment request" + e.toString());
            e.printStackTrace();
        }
    }

    public View dialog(int cus_layout, Bitmap bm) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = inflater.inflate(cus_layout,
                null);

        ImageView img = (ImageView) layout.findViewById(R.id.imageView10);
//        bm = Bitmap.createScaledBitmap(bm, 300, 500, true);
        bm = Bitmap.createBitmap(bm);
        img.setImageBitmap(bm);
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Button mLlCancel = (Button) layout.findViewById(R.id.cancel);
        mLlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        Button mLlSelect = (Button) layout.findViewById(R.id.delete);

        mLlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cap_image.setVisibility(View.INVISIBLE);
                cap_image.setImageBitmap(null);
                alertDialog.dismiss();

            }
        });

        alertDialog.setCancelable(true);
        alertDialog.show();
        if (isBillGenerated) {
            mLlSelect.setVisibility(View.INVISIBLE);
            mLlCancel.setVisibility(View.INVISIBLE);
        }
        return layout;
    }

    private Bitmap getbitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(folder_name + filename, options);
    }

    private void clearTempFile() {
        try {
            File dir = new File(folder_name);
            if (dir != null && dir.listFiles() != null) {
                for (File tempFile : dir.listFiles()) {
                    tempFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GenerateBill() {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();
            Log.e("Send Bill", "<==== Generate Bill requested =====>");
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c.getTime());
            GPSService mGPSService = new GPSService(getApplicationContext());
            Location locationB = mGPSService.getLocation();
            if (locationB != null) {
                Log.e("Location", locationB.getLatitude() + "----" + locationB.getLongitude());

            billDetailsDto.setLatitude(locationB.getLatitude());
            billDetailsDto.setLongitude(locationB.getLongitude());
            }else
            {
                showSettingsAlert();
                billDetailsDto.setLatitude(locationB.getLatitude());
                billDetailsDto.setLongitude(locationB.getLongitude());
            }
            billDetailsDto.setMeterImage(eb_meter_photo);
            billDetailsDto.setCurrentMeterReading(Long.parseLong("" + meterread.getText().toString()));
            billDetailsDto.setCurrentReadingDate(formattedDate);
            GenericDto genericDto = new GenericDto();
            genericDto.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
            billDetailsDto.setCustomer(genericDto);

           // Log.e("Send Bill", "<==== Generate Bill requested =====> " + billDetailsDto.toString());

            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/generatebill";
                String login = new Gson().toJson(billDetailsDto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.SENDBILLS, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Send Bill", "Exception while sending bills" + e.toString());

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        clearTempFile();
        Intent in = new Intent(GenerateBillDetailsActivity.this, GenerateBillActivity.class);
        startActivity(in);
        finish();
    }
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GenerateBillDetailsActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}
