package com.omneagate.erbc.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.omneagate.erbc.Activity.ApplianceEntryActivity;
import com.omneagate.erbc.Dto.ApplianceDto;
import com.omneagate.erbc.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by ftuser on 15/12/16.
 */
public class ApplianceInfoDialog extends Dialog {

    String[] HOURS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    String[] MINUTES = {"0", "15", "30", "45", "59"};
    ArrayAdapter<String> hHoursAdapter; //= new ArrayAdapter<String>(this,R.layout.dropdownrow, HOURS);
    ArrayAdapter<String> mMINUTESAdapter;
    Button mCancelBtn, mSaveBtn;
    MaterialBetterSpinner mHours, mMinutes;
    RatingBar mApplianceRattingBar;
    EditText mWatts, mApplianceName;

    String sWatts, sApplianceName;
    String sRate;
    private Context context;


    public ApplianceInfoDialog(Context context) {
        super(context);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.appliance_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mHours = (MaterialBetterSpinner) findViewById(R.id.mHours);
        mMinutes = (MaterialBetterSpinner) findViewById(R.id.mMinutes);
        mApplianceRattingBar = (RatingBar) findViewById(R.id.mApplianceRattingBar);
        mWatts = (EditText) findViewById(R.id.watts);
        mApplianceName = (EditText) findViewById(R.id.appliance_name);
        mCancelBtn = (Button) findViewById(R.id.cCancelBtn);
        mSaveBtn = (Button) findViewById(R.id.sSaveBtn);
        mHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });
        mMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAdapterValidation();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                dismiss();

            }
        });


        hHoursAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdownrow, HOURS);
        mHours.setAdapter(hHoursAdapter);
        mMINUTESAdapter = new ArrayAdapter<String>(getContext(), R.layout.dropdownrow, MINUTES);
        mMinutes.setAdapter(mMINUTESAdapter);
        sWatts = mWatts.getText().toString();
        sApplianceName = mApplianceName.getText().toString();


        mApplianceRattingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {


                sRate = String.valueOf(rating);
                Log.e("rate", String.valueOf(rating));

            }
        });


    }

    private void setAdapterValidation() {

        if (mApplianceName.getText().toString().length() == 0) {
            mApplianceName.setError(getContext().getResources().getString(R.string.mApplianceName));
            mApplianceName.requestFocus();
        } else if (mApplianceName.getText().toString().length() > 75) {
            mApplianceName.setError(getContext().getResources().getString(R.string.mValidApplianceName));
            mApplianceName.requestFocus();

        } else if (mWatts.getText().toString().length() == 0) {
            mWatts.setError(getContext().getResources().getString(R.string.mWattsError));
            mWatts.requestFocus();

        } else if(mWatts.getText().toString().length() > 5){
            mWatts.setError(getContext().getResources().getString(R.string.mValidWattsError));
            mWatts.requestFocus();

        }
        else if (StringUtils.isEmpty(mHours.getText().toString().trim())) {
            Toast.makeText(getContext(),context.getResources().getString(R.string.mHoursError), Toast.LENGTH_SHORT).show();
            return;

        } else if (StringUtils.isEmpty(mMinutes.getText().toString().trim())) {
            Toast.makeText(getContext(), context.getResources().getString(R.string.mMinutesError), Toast.LENGTH_SHORT).show();


        }else if (mHours.getText().toString().equalsIgnoreCase("0") && mMinutes.getText().toString().equalsIgnoreCase("0")) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.mValidTime), Toast.LENGTH_SHORT).show();
            return;
        } else if (mWatts.getText().toString().trim().length() == 0) {
            // Toast.makeText(PassengerDetailsActivity.this,"Please enter your Email id",Toast.LENGTH_SHORT).show();
            mWatts.setError(getContext().getResources().getString(R.string.mWattsError));
            mWatts.requestFocus();


        } else {
            try {
                ApplianceDto applianceDto = new ApplianceDto();
                applianceDto.setApplianceName(mApplianceName.getText().toString());
                applianceDto.setApplianceCode("19");
                float minutes = Integer.parseInt(mHours.getText().toString().trim()) * 60 + Integer.parseInt(mMinutes.getText().toString());
                applianceDto.setHoursUsed((long) minutes);
                applianceDto.setCapacityInWatts(Float.parseFloat(mWatts.getText().toString()));
                applianceDto.setRating(mApplianceRattingBar.getRating());
                if (mApplianceRattingBar.getRating() == 0) {
                    applianceDto.setStarRated(false);
                } else {
                    applianceDto.setStarRated(true);
                }

                ((ApplianceEntryActivity) context).addMiscellaneous(applianceDto);
                dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }


}
