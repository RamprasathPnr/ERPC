package com.omneagate.erbc.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.omneagate.erbc.Activity.Dialog.PaymentFailureDialog;
import com.omneagate.erbc.Dto.BillpayDto;
import com.omneagate.erbc.Dto.ConnectionCustomerDto;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.Dto.CurrentMeterDetailsDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
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
import java.util.ArrayList;
import java.util.Calendar;

//import com.github.mikephil.charting.animation.AnimationEasing;

/**
 * Created by user1 on 31/5/16.
 */
public class MeterCalculationActivity extends BaseActivity implements View.OnClickListener {

    EditText meterread, billcycledate, unitconsumed, amount;
    TextInputLayout meterreadLay;
    ConnectionDto connectionDto;
    CheckBox bymonthly;
    String eb_meter_photo = "";
    CustomProgressDialog progressBar;
    ResponseDto meterResponse;
    private static final int CAMERA_REQUEST_METER = 2;
    String bill_id, amount_approx, consumed_unit;
    ImageView cap_image, capture;
    Button paynowBtn, paylaterBtn;
    Button calculationBtn;
    boolean isBillGnerated = false;
    ArrayList<String> connectionName;


    Bitmap bitpmap_card;
    Uri imageUri = null;
    private String filename;
    private String folder_name = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ERPC/";

    private LineChart mChart;
    private ArrayAdapter<String> filterTypeAdapter;
    private Spinner filterTypeSpinner;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metercalculation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.backa);
        if (ActivityCompat.checkSelfPermission(MeterCalculationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MeterCalculationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        configurationViews();
        //Calendar set to the current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
        String formatted = format1.format(calendar.getTime());
        Log.e("MyApp", formatted);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void configurationViews() {
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        Intent intent = getIntent();
        String connenction = intent.getStringExtra("connection");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        connectionDto = gson.fromJson(connenction, ConnectionDto.class);
        Log.e("connection", "" + connectionDto.toString());

       /* paynowBtn = (Button) findViewById(R.id.paynow2);
         paylaterBtn = (Button) findViewById(R.id.paylater);*/

        calculationBtn = (Button) findViewById(R.id.calcul_btn);
        capture = (ImageView) findViewById(R.id.capturebtn);
        cap_image = (ImageView) findViewById(R.id.captureimage);
        meterread = (EditText) findViewById(R.id.meterread);
        billcycledate = (EditText) findViewById(R.id.billcycledate);
        unitconsumed = (EditText) findViewById(R.id.unitconsumed);
        amount = (EditText) findViewById(R.id.amount);

        meterreadLay = (TextInputLayout) findViewById(R.id.layout_meter);
        bymonthly = (CheckBox) findViewById(R.id.mymonthly);
        bymonthly.setVisibility(View.GONE);
   //     filterTypeSpinner = (Spinner) findViewById(R.id.filterTypeSpinner);
      //  filterTypeSpinner = (Spinner) findViewById(R.id.filterTypeSpinner);

        ((TextView) findViewById(R.id.mTxtSaveElec)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        ((LinearLayout) findViewById(R.id.Lin_appliance)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Metercalculation", " connectionID " + connectionDto.getId());
                Log.e("Metercalculation", " ConsumerType " + connectionDto.getConsumerType().getId());
                Intent in = new Intent(MeterCalculationActivity.this, ApplianceEntryActivity.class);
                in.putExtra("connectionID", "" + connectionDto.getId());
                in.putExtra("ConsumerType", "" + connectionDto.getConsumerType().getId());
                startActivity(in);
                finish();
                //startActivity(new Intent(MeterCalculationActivity.this,ApplianceEntryActivity.class).putExtra("connectionID",""+connectionDto.getId()));

            }
        });

        connectionName = new ArrayList<>();
        connectionName.add("Today"); // Tday
        connectionName.add("Last 7 Days"); //Last Seven Days
        connectionName.add("Month"); //T
        //  Today ,Last 7 days,Last 3 month


       /* CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(this, connectionName);
        filterTypeSpinner.setAdapter(customSpinnerAdapter);*/






        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.filterTypeSpinner);
        spinner.setItems(connectionName);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                getLineChart(connectionName.get(position).toString());
              //  Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        mChart = (LineChart) findViewById(R.id.linechart);
        mChart.setNoDataText(getString(R.string.no_chart_data));
        getLineChart("Today");



     /*   filterTypeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, connectionName);
        filterTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterTypeSpinner.setAdapter(filterTypeAdapter);
        filterTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                getLineChart(connectionName.get(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


     /*   paynowBtn.setVisibility(View.INVISIBLE);
        paylaterBtn.setVisibility(View.INVISIBLE);
        if (connectionDto.isCheckCycleDate()) {
            // bymonthly.setVisibility(View.VISIBLE);
            paynowBtn.setVisibility(View.VISIBLE);
            paylaterBtn.setVisibility(View.VISIBLE);

        }*/
        meterread.setOnClickListener(this);
        calculationBtn.setOnClickListener(this);
        //    paynowBtn.setOnClickListener(this);
        capture.setOnClickListener(this);
        cap_image.setOnClickListener(this);


        //  getLineChart("Day");

        //   paylaterBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

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
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }


                    if (!validatemeterread()) {
                        return;
                    }
                    if (eb_meter_photo.equals("") || eb_meter_photo.equals(null)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.meter_image_empty),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    calculationClick();
                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("calculationclick", e.toString(), e);
                }
                break;


        /*    case R.id.paynow2:
                try {

                    paynowclick();
                } catch (Exception e) {
                    Log.e("paynowClick", e.toString(), e);
                }
                break;*/

            case R.id.captureimage:

                try {
                    keypad_hide();
                    dialog(R.layout.image_confirmation, bitpmap_card);

                  /*  final MaterialDialog mMaterialDialog = new MaterialDialog(MeterCalculationActivity.this);
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
                    if(isBillGnerated){
                        mMaterialDialog.getPositiveButton().setVisibility(View.INVISIBLE);
                        mMaterialDialog.getNegativeButton().setVisibility(View.INVISIBLE);
                    }*/

                } catch (Exception e) {
                    GlobalAppState.getInstance().trackException(e);
                    Log.e("Errror", "" + e.toString());

                }
                break;
          /*  case R.id.paylater:
                Intent i=new Intent(MeterCalculationActivity.this,LandingActivity.class);
                startActivity(i);
                break;*/
            case R.id.capturebtn:

                boolean isGranted;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Camera permission has not been granted.

                    requestCameraPermission(CAMERA_REQUEST_METER);


                } else {

                    takePicture(CAMERA_REQUEST_METER);

                }
//                Intent cameraClick1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraClick1, CAMERA_REQUEST_METER);
                break;
        }
    }

    private void getLineChart(String filtertype) {
        try {
            Log.e("MeterCalculation","filter type "+filtertype);
            if (filtertype.contains("Today")) {
                filtertype = "day";
            } else if (filtertype.contains("Last 7 Days")) {
                filtertype = "week";
            } else {
                filtertype = "month";
            }
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {

                ConnectionCustomerDto connectionCustomerDto = new ConnectionCustomerDto();
                GenericDto customer = new GenericDto();
                GenericDto connection = new GenericDto();
                connection.setId(connectionDto.getId());
                customer.setId(DBHelper.getInstance(this).getCustomerId());
                connectionCustomerDto.setSearchFilter(filtertype);
                connectionCustomerDto.setConnection(connection);
                connectionCustomerDto.setCustomer(customer);
                String url = "/myusage/getgraphdetails";
                String login = new Gson().toJson(connectionCustomerDto);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.GETGRAPHDETAILS, SyncHandler, RequestType.POST, se, getApplicationContext());
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLineChart(ArrayList<String> xValue, ArrayList<Entry> yValue) {
        try {



            ArrayList<String> xVals = xValue;

            ArrayList<Entry> yVals = yValue;

            LineDataSet set1;

            set1 = new LineDataSet(yVals, getString(R.string.consumption_units_kwh));
            set1.setFillAlpha(110);

            set1.setFillColor(Color.parseColor("#F5A9A9"));

            // set the line to be drawn like this "- - - - - -"
            // set1.enableDashedLine(10f, 5f, 0f);
            // set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.parseColor("#E05059"));
            set1.setCircleColor(Color.parseColor("#E05059"));
            set1.setLineWidth(1f);
            // set1.setCircleRadius(3f);
            set1.setDrawCircleHole(true);

            Configuration config = getApplicationContext().getResources().getConfiguration();

            if (config.smallestScreenWidthDp >= 720) {
                set1.setValueTextSize(12f);  // 10-inch tablet and above

            }
            else if (config.smallestScreenWidthDp >= 600) {
                set1.setValueTextSize(10f); // 7-inch tablet and above
            }
            else {
                set1.setValueTextSize(6f);
            }


            set1.setDrawFilled(true);

            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            mChart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer
            mChart.setDrawGridBackground(false);
            mChart.getAxisLeft().setDrawGridLines(false);
            mChart.getXAxis().setDrawGridLines(false);
            mChart.getAxisRight().setDrawLabels(false);
            mChart.getAxisLeft().setDrawLabels(false);

            YAxis yAxis = mChart.getAxisLeft();
            yAxis.setDrawGridLines(false);

            yAxis = mChart.getAxisRight();
            yAxis.setDrawGridLines(false);
            mChart.getAxisRight().setDrawLabels(false);

            mChart.setDescription("");
            mChart.setTouchEnabled(false);
            Legend legend=mChart.getLegend();
            legend.setForm(Legend.LegendForm.CIRCLE);


            Configuration config1 = getApplicationContext().getResources().getConfiguration();

            if (config1.smallestScreenWidthDp >= 720) {
                legend.setTextSize(getApplicationContext().getResources().getDimension(R.dimen.legendsize_seven));

            } else if (config1.smallestScreenWidthDp >= 600) {
                legend.setTextSize(getApplicationContext().getResources().getDimension(R.dimen.legendsize_seven));
            } else {
              //  legend.setTextSize(getApplicationContext().getResources().getDimension(R.dimen.legendsize));
            }

            // set data
            XAxis xAxis = mChart.getXAxis();
            xAxis.setAvoidFirstLastClipping(true);
            xAxis.setLabelRotationAngle(270f);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            mChart.animateXY(3000, 3000);
            mChart.setData(data);

        } catch (Exception e) {
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

    private void calculationClick() {
        try {

            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = df.format(c.getTime());
            GPSService mGPSService = new GPSService(getApplicationContext());
            Location locationB = mGPSService.getLocation();
            if (locationB != null) {
                Log.e("Location", locationB.getLatitude() + "----" + locationB.getLongitude());
            } else {
                progressBar.cancel();
                showSettingsAlert();
                //  Toast.makeText(getApplicationContext(),"on",Toast.LENGTH_SHORT).show();
            }
            CurrentMeterDetailsDto cuurentdetail = new CurrentMeterDetailsDto();
            CustomerDto customer = new CustomerDto();
            customer.setId(DBHelper.getInstance(getApplicationContext()).getCustomerId());
            cuurentdetail.setCustomer(customer);
            cuurentdetail.setConnection(connectionDto);
            cuurentdetail.setCurrentMeterReading("" + meterread.getText().toString());
            cuurentdetail.setLatitude("" + locationB.getLatitude());
         /*   Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -2);
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
            String formatted = format1.format(calendar.getTime());
            if(formatted.equals(connectionDto.getBillCycleToDate())){
                cuurentdetail.setConfirm("true");
            }
            else
            {
                cuurentdetail.setConfirm("false");
            }*/

          /*  if(bymonthly.isChecked())
            {
                cuurentdetail.setConfirm("true");
                paynowBtn.setVisibility(View.VISIBLE);
            }
            else
            {
                cuurentdetail.setConfirm("false");
                paynowBtn.setVisibility(View.INVISIBLE);
            }*/
            Log.e("checkbox", "" + bymonthly.isChecked());
            cuurentdetail.setLongitude("" + locationB.getLongitude());
            cuurentdetail.setCurrentReadingDate(formattedDate);
            cuurentdetail.setMeterImage(eb_meter_photo);
            Log.e("currentdetiaqlDro", "" + cuurentdetail.toString());

            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/getconsumptiondetail";
                String login = new Gson().toJson(cuurentdetail);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CALCUL_METER, SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error", e.toString(), e);
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {
            case CALCUL_METER:
                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                dismissProgress();
                meterresonse(message);
                break;
            case PAY_BILL:
                dismissProgress();
                pay_billresponse(message);
                break;
            case GETGRAPHDETAILS:
                dismissProgress();
                GetGrapghDetailsReponse(message);
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

    public View dialog(int cus_layout, Bitmap bm) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View layout = inflater.inflate(cus_layout,
                null);

        ImageView img = (ImageView) layout.findViewById(R.id.imageView10);
//        bm = Bitmap.createScaledBitmap(bm, 300, 500, true);
        bm = Bitmap.createBitmap(bm);
        img.setImageBitmap(bm);
        builder.setView(layout);
        final android.support.v7.app.AlertDialog alertDialog = builder.create();
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
        if (isBillGnerated) {
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            // bitpmap_card = (Bitmap) data.getExtras().get("data");
            bitpmap_card = getbitmap();
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            bitpmap_card.compress(Bitmap.CompressFormat.JPEG, 50, stream1);
            byte[] byteArray1 = stream1.toByteArray();
            eb_meter_photo = Base64.encodeToString(byteArray1, Base64.DEFAULT);
            ((ImageView) findViewById(R.id.captureimage)).setVisibility(View.VISIBLE);
            Bitmap bitmap = Bitmap.createScaledBitmap(bitpmap_card, 100, 150, true);
            ((ImageView) findViewById(R.id.captureimage)).setImageBitmap(bitmap);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("imagecapture", e.toString());
        }
    }

    private void meterresonse(Bundle message) {
        try {
            billcycledate.setText("");
            unitconsumed.setText("");
            amount.setText("");
            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data_metr", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            meterResponse = gson.fromJson(response, ResponseDto.class);
            if (meterResponse.getStatusCode() == 0) {
                calculationBtn.setClickable(false);
                meterread.setEnabled(false);
                capture.setEnabled(false);
                bymonthly.setEnabled(false);
                eb_meter_photo = "";
                isBillGnerated = true;
                ((LinearLayout) findViewById(R.id.displayvalue)).setVisibility(View.VISIBLE);
                //  ((LinearLayout) findViewById(R.id.clickimagelay)).setVisibility(View.GONE);
                /*((Button) findViewById(R.id.paynow2)).setVisibility(View.VISIBLE);*/
                //((ImageView) findViewById(R.id.captureimage)).setVisibility(View.INVISIBLE);

                unitconsumed.setText(meterResponse.getCurrentMeterDetailsDto().getConsumption());
                amount.setText(" ₹ " + meterResponse.getCurrentMeterDetailsDto().getApproxCharge());
                bill_id = "" + meterResponse.getCurrentMeterDetailsDto().getBillId();
                amount_approx = meterResponse.getCurrentMeterDetailsDto().getApproxCharge();
                consumed_unit = meterResponse.getCurrentMeterDetailsDto().getConsumption();
                billcycledate.setText(meterResponse.getCurrentMeterDetailsDto().getBillCycleFromDate() + " to " + meterResponse.getCurrentMeterDetailsDto().getBillCycleToDate() + "");

                getLineChart("Today");
            } else if (meterResponse.getStatusCode() == 3114) {
                Toast.makeText(getApplicationContext(), "" + meterResponse.getErrorDescription(),
                        Toast.LENGTH_SHORT).show();
            } else if (meterResponse.getStatusCode() == 4101) {
                Toast.makeText(getApplicationContext(), "" + meterResponse.getErrorDescription(),
                        Toast.LENGTH_SHORT).show();
            } else if (meterResponse.getStatusCode() == 3119) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.my_usage_meter_error), Toast.LENGTH_SHORT).show();
            } else if (meterResponse.getStatusCode() == 4501) {
                Toast.makeText(getApplicationContext(), "" + meterResponse.getErrorDescription(),
                        Toast.LENGTH_SHORT).show();
            } else if (meterResponse.getStatusCode() == 2000) {
                Toast.makeText(getApplicationContext(), getString(R.string.my_usage_meter_srever_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.connectionError),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString());
        }
    }

    private void paynowclick() {

        BillpayDto requestbill = new BillpayDto();
        requestbill.setAmount(amount_approx);
        requestbill.setConnectionId("" + connectionDto.getId());
        requestbill.setConsumption(consumed_unit);
        requestbill.setBillId(bill_id);
        requestbill.setCustomerId("" + DBHelper.getInstance(this).getCustomerId());
        Log.e("request_data", "" + requestbill.toString());
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
                GlobalAppState.getInstance().trackException(e);
                e.printStackTrace();
            }
        } else {
            dismissProgress();
            Toast.makeText(getApplicationContext(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
        }
    }

    private void pay_billresponse(Bundle message) {
        try {

            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data_paybill", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BillpayDto pay_response = gson.fromJson(response, BillpayDto.class);
            if (pay_response.getStatusCode() == 0) {
                clearTempFile();
                Intent patmnet_loading = new Intent(MeterCalculationActivity.this, PaymentLoaderActivity.class);
                patmnet_loading.putExtra("paybill", new Gson().toJson(pay_response));
                patmnet_loading.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(patmnet_loading);
                finish();
            } else {


                // As per tester requesting payment failure alert dialog should be display red color
                // So we change the alert dialog
                /*AlertDialog alertdialog = new AlertDialog(MeterCalculationActivity.this, getString(R.string.payment_fail));
                alertdialog.show();*/
                PaymentFailureDialog alertDialog = new PaymentFailureDialog(MeterCalculationActivity.this);
                alertDialog.show();

            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString());
        }
    }


    private void GetGrapghDetailsReponse(Bundle message) {
        try {
            dismissProgress();
            Log.e("MeterCalculatin", "Response Called");
            String response = message.getString(DBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            ResponseDto responsedto = gson.fromJson(response, ResponseDto.class);
            Log.e("MeterCalculatin", "Response " + response.toString());

            if (responsedto.getStatusCode() == 0 && responsedto.getContents().size() > 0) {
                ArrayList<String> xVals = new ArrayList<String>();
                ArrayList<Entry> yVals = new ArrayList<Entry>();
                for (int i = 0; i < responsedto.getContents().size(); i++) {
                    xVals.add(responsedto.getContents().get(i).getLabel());
                    yVals.add(new Entry(responsedto.getContents().get(i).getUnitsConsumed(), i));

                }

                loadLineChart(xVals, yVals);

            }else{
                try {
                    if(mChart !=null) {
                        mChart.clear();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void keypad_hide() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearTempFile();
        Intent homepage = new Intent(MeterCalculationActivity.this, MyusageListActivity.class);
        homepage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homepage);
        finish();
    }

    /**
     * Method to request permission for camera
     */
    private void requestCameraPermission(int REQUEST_CAMERA) {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA);
    }

    /**
     * Method to launch camera after permission accepted from user
     */
    void takePicture(int selected) {

        filename = "fname_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ERPC/");
        if (!file.exists()) {
            file.mkdirs();
        } else System.out.println("Sham Enter the File Else  method :::");
        File outputFile = new File(file.toString(), filename);
        imageUri = Uri.fromFile(outputFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, selected);
       /* Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, selected);*/
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MeterCalculationActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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

 /*   @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MeterCalculation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.omneagate.erbc.Activity/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MeterCalculation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.omneagate.erbc.Activity/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }*/
    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> asr;

        public CustomSpinnerAdapter(Context context, ArrayList<String> asr) {
            this.asr = asr;
            activity = context;
        }


        public int getCount() {
            return asr.size();
        }

        public Object getItem(int i) {
            return asr.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(MeterCalculationActivity.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position));

            txt.setTextColor(Color.parseColor("#000000"));
            return txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(getApplicationContext());
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(16);

           txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_two, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return txt;
        }

    }

}
