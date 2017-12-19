package com.omneagate.erbc.Activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.Dialog.AlertDialog;
import com.omneagate.erbc.Dto.ConnectionDto;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.omneagate.erbc.Util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by user1 on 24/5/16.
 */
public class ViewMeterFragment extends BaseActivityFragment {
    View view;
    EditText lastreadDate, fromDate, toDate, serialnumber, lastmeter_value;
    MaterialBetterSpinner meterBrand, meterType;
    CustomProgressDialog progressBar;
    Button submit;
    String languageCode = GlobalAppState.language;
    private static final String TAG = ViewMeterFragment.class.getName();
    Bitmap bitmap_meter;
    private Target loadtarget;
    ImageView cardImg;

    public ViewMeterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkConnection = new NetworkConnection(getActivity());
        httpConnection = new HttpClientWrapper();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.viewmeter, container, false);
        configureInitialPage(view);
        return view;
    }

    public void loadBitmap(String url) {

        if (loadtarget == null) loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // do something with the Bitmap
                bitmap_meter=bitmap;
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                if (bitmap_meter != null)
                    bitmap_meter.compress(Bitmap.CompressFormat.JPEG, 25, stream1);
                Bitmap scaled =Bitmap.createScaledBitmap(bitmap_meter,100,150,true);
                cardImg.setImageBitmap(scaled);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }


        };

        Picasso.with(getActivity()).load(url).into(loadtarget);
    }

    private void configureInitialPage(View view) {
        try {

            meterType = (MaterialBetterSpinner) view.findViewById(R.id.metertype);
            meterBrand = (MaterialBetterSpinner) view.findViewById(R.id.meterbrand);
            serialnumber = (EditText) view.findViewById(R.id.serialnumber);
            lastmeter_value = (EditText) view.findViewById(R.id.meterreading);
            lastreadDate = (EditText) view.findViewById(R.id.dates);
            fromDate = (EditText) view.findViewById(R.id.from_date);
            toDate = (EditText) view.findViewById(R.id.to_date);
            submit = (Button) view.findViewById(R.id.button);
            submit.setVisibility(View.VISIBLE);
            if (ConnectionDetailActivity.flag.equals("0")) {
                submit.setVisibility(View.VISIBLE);
            } else {
                submit.setVisibility(View.INVISIBLE);
                lastmeter_value.setFocusable(false);
            }

            if (languageCode.equalsIgnoreCase("ta")) {
                meterType.setText("" + ConnectionDetailActivity.ConnectionDetail.getMeterType().getRegionalName());
                meterBrand.setText("" + ConnectionDetailActivity.ConnectionDetail.getMeterBrand().getRegionalName());
            } else {
                meterType.setText("" + ConnectionDetailActivity.ConnectionDetail.getMeterType().getName());
                meterBrand.setText("" + ConnectionDetailActivity.ConnectionDetail.getMeterBrand().getName());
            }

            serialnumber.setText("" + ConnectionDetailActivity.ConnectionDetail.getSerialNumber());
            lastmeter_value.setText("" + ConnectionDetailActivity.ConnectionDetail.getLastMeterReading());
            lastreadDate.setText(Util.appDateFormat(ConnectionDetailActivity.ConnectionDetail.getLastMeterReadingDate()));
            lastreadDate.setText(""+ConnectionDetailActivity.ConnectionDetail.getLastMeterReadingDate());
            fromDate.setText(Util.appDateFormat(ConnectionDetailActivity.ConnectionDetail.getBillCycleFromDate()));
            toDate.setText(Util.appDateFormat(ConnectionDetailActivity.ConnectionDetail.getBillCycleToDate()));
            fromDate.setText("" + ConnectionDetailActivity.ConnectionDetail.getBillCycleFromDate());
            toDate.setText("" + ConnectionDetailActivity.ConnectionDetail.getBillCycleToDate());
            cardImg = (ImageView) view.findViewById(R.id.upload_meterr);
            if (ConnectionDetailActivity.ConnectionDetail.getMeterImageUrl() != "" || ConnectionDetailActivity.ConnectionDetail.getMeterImageUrl() != "null") {
              Picasso.with(getActivity())
                      .load(ConnectionDetailActivity.ConnectionDetail.getMeterImageUrl())
                      .resize(100,150)
                      .into(cardImg);
                loadBitmap(ConnectionDetailActivity.ConnectionDetail.getMeterImageUrl());

            }
         /*   byte[] decodedString = Base64.decode(ConnectionDetailActivity.ConnectionDetail.getMeterImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            cardImg.setImageBitmap(decodedByte);*/
            serialnumber.setFocusable(false);
            lastmeter_value.setFocusable(false);
            lastreadDate.setFocusable(false);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerConnnction(ConnectionDetailActivity.ConnectionDetail);
                }
            });
            cardImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                       // Bitmap bitmap = ((BitmapDrawable) cardImg.getDrawable()).getBitmap();
                        dialog(R.layout.image_confirmation,bitmap_meter);
                       /* final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
                        mMaterialDialog.setCanceledOnTouchOutside(true);
                        mMaterialDialog.setPositiveButton("CANCEL", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                        Bitmap bitmap = ((BitmapDrawable) cardImg.getDrawable()).getBitmap();
                        Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 10), (int) (bitmap.getHeight() * 10), true);
                        // Bitmap  resized = Bitmap.createScaledBitmap(bitmap,100, 100, true);
                        Drawable drawe = new BitmapDrawable(getResources(), resized);
                        mMaterialDialog.setBackground(drawe);
                        mMaterialDialog.show();*/

                    } catch (Exception e) {
                        GlobalAppState.getInstance().trackException(e);
                        Log.e("error_emptyiamge", e.toString(), e);
                    }


                }
            });
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString());
        }


    }
    public View dialog(int cus_layout, Bitmap bm) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
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
        mLlSelect.setVisibility(View.GONE);

        mLlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

            }
        });

        alertDialog.setCancelable(true);
        alertDialog.show();

        return layout;
    }
    private void registerConnnction(ConnectionDto customer_details) {
        try {
            CustomerDto customerdto = new CustomerDto();
            customerdto.setId(DBHelper.getInstance(getActivity()).getCustomerId());
            customer_details.setCustomer(customerdto);
            progressBar = new CustomProgressDialog(getActivity());
            progressBar.setCanceledOnTouchOutside(false);

            if (networkConnection.isNetworkAvailable()) {
                String url = "/connection/add";
                String login = new Gson().toJson(customer_details);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.REG_CONNECTION, SyncHandler, RequestType.POST, se, getActivity());
            } else {
                dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
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
            case REG_CONNECTION:
                getconnenctionResponse(message);
                dismissProgress();
                break;


            default:
                dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private void getconnenctionResponse(Bundle message) {
        String response = message.getString(DBConstants.RESPONSE_DATA);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        ConnectionDto connectionResponse = gson.fromJson(response, ConnectionDto.class);
        if (connectionResponse != null) {
            if (connectionResponse.getStatusCode() == 0) {
                AlertDialog alertdialog = new AlertDialog(getActivity(), getString(R.string.connnecion_register_mdg));
                alertdialog.show();
            } else if (connectionResponse.getStatusCode() == 3104) {
                Toast.makeText(getActivity(), connectionResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), connectionResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

