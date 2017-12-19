package com.omneagate.erbc.Activity.Dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.erbc.Activity.ApplianceEntryActivity;
import com.omneagate.erbc.Adapter.ApplianceAdapter;
import com.omneagate.erbc.Dto.ApplianceDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 4/12/16.
 */
public class ConfigureApplianceDialog extends Dialog implements View.OnClickListener {
    int sSize=0,aApplianceCode,aApplianceImageId;
    int interval = 5;
    // private SeekBar seekBar;
    private ImageView plus, minus;
    private TextView txValue, title;
    private int sValue = 1;

    Button mCancelBtn, mSetBtn;
    RecyclerView mQuantityRecyclerView;
    QuantityAdapter adapter;
    Context mContext;
    boolean isTrue = false, mStarFlag;
    String mApplianceName;
    float mWatts;
    public List<ApplianceDto> aApplianceQuantityAryList =new ArrayList<ApplianceDto>();

    String[] HOURS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23","24"};
   // String[] MINUTES = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
     String []MINUTES ={"0","15","30","45","59"};



    public int pPosition;

    public ConfigureApplianceDialog(Context context, String appliance_name, boolean mStarFlag_, int position, float watts,List<ApplianceDto> aApplianceQuantityAryList_,String applianceCode,int mImageId) {
        super(context);
        this.mContext = context;
        this.mApplianceName = appliance_name;
        this.mStarFlag = mStarFlag_;
        this.mWatts = watts;
        this.aApplianceQuantityAryList = aApplianceQuantityAryList_;
        this.aApplianceCode = Integer.parseInt(applianceCode);
        this.aApplianceImageId = mImageId;
        //  this.applianceDetailsDtoArrayList = new ArrayList<ApplianceDetailsDto>();

        this.pPosition = position;

    }

    public ConfigureApplianceDialog(Context context, String appliance_name, boolean mStarFlag_, int position, float capacityInWatts, String applianceCode,int mImageId) {
        super(context);
        this.mContext = context;
        this.mApplianceName = appliance_name;
        this.mStarFlag = mStarFlag_;
        this.mWatts = capacityInWatts;
        this.aApplianceCode = Integer.parseInt(applianceCode);
        this.aApplianceImageId = mImageId;
        //  this.applianceDetailsDtoArrayList = new ArrayList<ApplianceDetailsDto>();


        this.pPosition = position;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_configure_appliance);

        mCancelBtn = (Button) findViewById(R.id.cCancelBtn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApplianceEntryActivity mTempClass = (ApplianceEntryActivity) mContext;
                ApplianceAdapter applianceAdapter = mTempClass.mAdapter;
                applianceAdapter.appliance_List.get(pPosition).setIsselected(false);
                applianceAdapter.notifyDataSetChanged();
                DBConstants.aNewApplianceQuantityAryList.clear();
                if(aApplianceQuantityAryList !=null)       //added by ram
                aApplianceQuantityAryList.clear();
                aApplianceQuantityAryList=null;
                dismiss();
            }
        });
        mSetBtn = (Button) findViewById(R.id.sSaveBtn);
        mSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAdapterValidation();
                if (isTrue) {
                    setApplianceDto();
                    dismiss();
                    /*if(DBConstants.applianceDetailsDtoArrayList.size()==0 || DBConstants.applianceDetailsDtoArrayList == null){
                        ApplianceEntryActivity mTempClass = (ApplianceEntryActivity) mContext;
                        ApplianceAdapter applianceAdapter = mTempClass.mAdapter;
                        applianceAdapter.appliance_List.get(pPosition).setIsselected(false);
                        applianceAdapter.notifyDataSetChanged();
                        DBConstants.applianceDetailsDtoArrayList.clear();
                        if(aApplianceQuantityAryList !=null) {       //added by ram
                            aApplianceQuantityAryList.clear();
                            aApplianceQuantityAryList = null;
                        }
                        dismiss();
                    }else {
                        dismiss();
                    }*/
                }
            }
        });
        mQuantityRecyclerView = (RecyclerView) findViewById(R.id.mQuantityRecyclerView);
        mQuantityRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mQuantityRecyclerView.setNestedScrollingEnabled(false);


        if(this.aApplianceQuantityAryList != null && this.aApplianceQuantityAryList.size() > 0){
            adapter = new QuantityAdapter(mContext, aApplianceQuantityAryList.size(), mStarFlag,aApplianceQuantityAryList);
            mQuantityRecyclerView.setAdapter(adapter);
        }else {
            adapter = new QuantityAdapter(mContext, sValue, mStarFlag);
            mQuantityRecyclerView.setAdapter(adapter);
        }


        plus = (ImageView) findViewById(R.id.add_quantity);

        minus = (ImageView) findViewById(R.id.remove_quantity);
        txValue = (TextView) findViewById(R.id.mTxtQuantity);
        title = (TextView) findViewById(R.id.mTxtApplianceCategoryTitle);
        title.setText(this.mApplianceName);


        if(this.aApplianceQuantityAryList != null && this.aApplianceQuantityAryList.size() > 0) {

            txValue.setText("" + aApplianceQuantityAryList.size());
            sValue =  aApplianceQuantityAryList.size();
        }else{
            txValue.setText("" + sValue);
        }
        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
    }

    @SuppressLint("LongLogTag")
    private void setApplianceDto() {
        try {

            adapter = (QuantityAdapter) mQuantityRecyclerView.getAdapter();
            for (int i = 0; i < adapter.getItemCount(); i++) {
                View selectedView = mQuantityRecyclerView.getChildAt(i);
                //   ApplianceDetailsDto applianceDetailsDto = new ApplianceDetailsDto();
                ApplianceDto applianceDetailsDto = new ApplianceDto();

                MaterialBetterSpinner mHours = (MaterialBetterSpinner) selectedView.findViewById(R.id.mHours);
                MaterialBetterSpinner mMinutes = (MaterialBetterSpinner) selectedView.findViewById(R.id.mMinutes);
                RatingBar mApplianceRattingBar = (RatingBar) selectedView.findViewById(R.id.mApplianceRattingBar);
                TextView mWatts = (TextView) selectedView.findViewById(R.id.mWattsEdditTxt);
                TextView mTxtApplianceID = (TextView) selectedView.findViewById(R.id.mTxtApplianceID);
                TextView mTxtApplianceCode = (TextView) selectedView.findViewById(R.id.mTxtApplianceCode);
                TextView mTxtApplianceImageId = (TextView) selectedView.findViewById(R.id.mTxtApplianceImageId);

                String val = mTxtApplianceID.getText().toString();

                if (mTxtApplianceID.getText().toString().isEmpty() || mTxtApplianceID.getText().toString() == null || mTxtApplianceID.getText().length() == 0) {
                    ApplianceDto applianceDto = new ApplianceDto();
                    float minutes = Integer.parseInt(mHours.getText().toString().trim()) * 60 + Integer.parseInt(mMinutes.getText().toString());
                    applianceDto.setApplianceName(title.getText().toString());
                    applianceDto.setApplianceCode(mTxtApplianceCode.getText().toString());
                    applianceDto.setRating(Double.parseDouble(String.valueOf(mApplianceRattingBar.getRating())));
                    applianceDto.setHoursUsed((long) minutes);
                    applianceDto.setStarRated(mStarFlag);
                    applianceDto.setCapacityInWatts(Float.parseFloat(mWatts.getText().toString().trim()));
                    DBConstants.aNewApplianceQuantityAryList.add(applianceDto);
                    aApplianceQuantityAryList.add(applianceDto);


                }


                applianceDetailsDto.setApplianceName(title.getText().toString());

                applianceDetailsDto.setApplianceCode(mTxtApplianceCode.getText().toString());
                if (mTxtApplianceID.getText().toString() != null && !mTxtApplianceID.getText().toString().equalsIgnoreCase("")) {

                    applianceDetailsDto.setId(Integer.parseInt(mTxtApplianceID.getText().toString()));

                }

                applianceDetailsDto.setImageDrawableId(Integer.parseInt(mTxtApplianceImageId.getText().toString()));
            /*applianceDetailsDto.setQuantity(txValue.getText().toString().trim());
            applianceDetailsDto.setHours(mHours.getText().toString().trim());
            applianceDetailsDto.setMinutes(mMinutes.getText().toString());
            applianceDetailsDto.setRatting("" + mApplianceRattingBar.getRating());
            applianceDetailsDto.setWatts(mWatts.getText().toString().trim());*/

                //    applianceDetailsDto.setQuantity(txValue.getText().toString().trim());
                float minutes = Integer.parseInt(mHours.getText().toString().trim()) * 60 + Integer.parseInt(mMinutes.getText().toString());
                applianceDetailsDto.setHoursUsed((long) minutes);
                applianceDetailsDto.setHours(mHours.getText().toString().trim());
                applianceDetailsDto.setMinutes(mMinutes.getText().toString());
                applianceDetailsDto.setRating(mApplianceRattingBar.getRating());
                applianceDetailsDto.setCapacityInWatts(Float.parseFloat(mWatts.getText().toString().trim()));

                //  float val = Float.parseFloat(mWatts.getText().toString());
                applianceDetailsDto.setConsumedUnits(cCalculateConsumedUnit(Float.parseFloat(mWatts.getText().toString()), mHours.getText().toString().trim(), mMinutes.getText().toString(), (int) mApplianceRattingBar.getRating()));
                applianceDetailsDto.setPosition(pPosition);
                applianceDetailsDto.setStarRated(mStarFlag);


                DBConstants.applianceDetailsDtoArrayList.add(applianceDetailsDto);
                Log.e("applianceDetailsDtoArrayList Size ", "" + DBConstants.applianceDetailsDtoArrayList.size());
            }
        }catch (Exception e){
           Log.e("Exception","Setting appliances data in dto"+e.toString());
        }
    }

    private float cCalculateConsumedUnit(float watts, String hours, String minute, int rating) {

        float fFinalConsumedUnit;


        float minutes = Integer.parseInt(hours) * 60 + Integer.parseInt(minute);

        if (mStarFlag) {
            float sStarValue = DBHelper.getInstance(mContext).getStarPercentage(rating);
            float wWithOutStarResult = ((float) watts / 1000) * (minutes / 60);
            float temp_starUnits = wWithOutStarResult;
            float sStarValue_ = (float) sStarValue / 100;
            float wWithStarResult = wWithOutStarResult-(temp_starUnits * sStarValue_);
            fFinalConsumedUnit = wWithStarResult;

        } else {
            float wWithOutStarResult = ((float) watts / 1000) * (minutes / 60);
            fFinalConsumedUnit = wWithOutStarResult;
        }


        return Float.parseFloat(String.format("%.02f", fFinalConsumedUnit));
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.add_quantity:
                sSize++;
                sValue++;
             //   mSetBtn.setEnabled(true);
                txValue.setText("" + sValue);
                // mAdapter.notifyItemInserted(position);
                if (adapter == null) {
                    adapter = new QuantityAdapter(mContext, sValue, mStarFlag);
                    mQuantityRecyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                break;

            case R.id.remove_quantity:

                if(this.aApplianceQuantityAryList != null && this.aApplianceQuantityAryList.size() > 0) {

                    if (sValue <= aApplianceQuantityAryList.size()) {
                        txValue.setText(""+aApplianceQuantityAryList.size());
                        //  mSetBtn.setEnabled(false);
                    } else {
                        //   mSetBtn.setEnabled(false);
                        sValue--;
                        sSize--;
                        txValue.setText("" + sValue);
                        // adapter.notifyItemRemoved(sValue-1);
                        //                    adapter = new QuantityAdapter(mContext, sValue, mStarFlag);
                        //                    mQuantityRecyclerView.setAdapter(adapter);
                        //                    adapter.notifyDataSetChanged();
                        if (adapter == null) {
                            adapter = new QuantityAdapter(mContext, sValue, mStarFlag);
                            mQuantityRecyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }

                }else {
                            if (sValue <= 1) {
                                txValue.setText("1");
                                //  mSetBtn.setEnabled(false);
                            } else {
                                //   mSetBtn.setEnabled(false);
                                sValue--;
                                sSize--;
                                txValue.setText("" + sValue);
                                // adapter.notifyItemRemoved(sValue-1);
        //                    adapter = new QuantityAdapter(mContext, sValue, mStarFlag);
        //                    mQuantityRecyclerView.setAdapter(adapter);
        //                    adapter.notifyDataSetChanged();
                                if (adapter == null) {
                                    adapter = new QuantityAdapter(mContext, sValue, mStarFlag);
                                    mQuantityRecyclerView.setAdapter(adapter);
                                } else {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                }
                break;

            default:
                break;
        }

    }

    private void setAdapterValidation() {
       try {
            adapter = (QuantityAdapter) mQuantityRecyclerView.getAdapter();
            for (int i = 0; i < adapter.getItemCount(); i++) {


                View selectedView = mQuantityRecyclerView.getChildAt(i);
                TextView mWatts = (TextView) selectedView.findViewById(R.id.mWattsEdditTxt);
                MaterialBetterSpinner mHours = (MaterialBetterSpinner) selectedView.findViewById(R.id.mHours);
                MaterialBetterSpinner mMinutes = (MaterialBetterSpinner) selectedView.findViewById(R.id.mMinutes);
                RatingBar mApplianceRattingBar = (RatingBar) selectedView.findViewById(R.id.mApplianceRattingBar);

                if (mStarFlag) {

                    if (StringUtils.isEmpty(mHours.getText().toString().trim())) {
                        mHours.setError(mContext.getResources().getString(R.string.mHoursError));
                        Toast.makeText(mContext,mContext.getResources().getString(R.string.mHoursError),Toast.LENGTH_SHORT).show();
                        isTrue = false;
                        break;
                    } else if (StringUtils.isEmpty(mMinutes.getText().toString().trim())) {
                        mMinutes.setError(mContext.getResources().getString(R.string.mMinutesError));
                        Toast.makeText(mContext,mContext.getResources().getString(R.string.mMinutesError),Toast.LENGTH_SHORT).show();
                        isTrue = false;
                        break;
                    } else if (mHours.getText().toString().equalsIgnoreCase("0") && mMinutes.getText().toString().equalsIgnoreCase("0")) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.mValidTime), Toast.LENGTH_SHORT).show();
                        isTrue = false;
                        break;
                    } else if (mApplianceRattingBar.getRating() == 0) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.mRattingError), Toast.LENGTH_SHORT).show();
                        isTrue = false;
                        break;
                    } else if (mWatts.getText().toString().trim().length() == 0) {
                        // Toast.makeText(PassengerDetailsActivity.this,"Please enter your Email id",Toast.LENGTH_SHORT).show();
                        mWatts.setError(mContext.getResources().getString(R.string.mWattsError));
                        mWatts.requestFocus();
                        isTrue = false;
                        break;
                    } else {
                        isTrue = true;
                    }

                } else {
                    if (StringUtils.isEmpty(mHours.getText().toString().trim())) {
                        mHours.setError(mContext.getResources().getString(R.string.mHoursError));
                        isTrue = false;
                        break;
                    } else if (StringUtils.isEmpty(mMinutes.getText().toString().trim())) {
                        mMinutes.setError(mContext.getResources().getString(R.string.mMinutesError));
                        isTrue = false;
                        break;
                    } else if (mWatts.getText().toString().trim().length() == 0) {
                        // Toast.makeText(PassengerDetailsActivity.this,"Please enter your Email id",Toast.LENGTH_SHORT).show();
                        mWatts.setError(mContext.getResources().getString(R.string.mWattsError));
                        mWatts.requestFocus();
                        isTrue = false;
                        break;
                    } else {
                        isTrue = true;
                    }
                }


            }
        }catch (Exception e){
            Log.e("Exception","while validating appliances"+e.toString());
        }
    }


    class QuantityAdapter extends RecyclerView.Adapter<QuantityAdapter.Myholder> {
        Context context;

        boolean aStarFlag;
        List<ApplianceDto> aApplianceQuantityAryList;
        ArrayAdapter<String> hHoursAdapter; //= new ArrayAdapter<String>(this,R.layout.dropdownrow, HOURS);
        ArrayAdapter<String> mMINUTESAdapter;//= new ArrayAdapter<String>(this,R.layout.dropdownrow, MINUTES);

        public QuantityAdapter(Context context, int count, boolean mStarFlag_) {
            this.context = context;
            sSize = count;
            this.aStarFlag = mStarFlag_;
            this.hHoursAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, HOURS);
            this. mMINUTESAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, MINUTES);
        }

        public QuantityAdapter(Context mContext, int sValue, boolean mStarFlag_, List<ApplianceDto> aApplianceQuantityAryList__) {
            this.context = mContext;
            sSize = sValue;
            this.aStarFlag = mStarFlag_;
            this. hHoursAdapter = new ArrayAdapter<String>(context, R.layout.dropdownrow, HOURS);
            this.  mMINUTESAdapter = new ArrayAdapter<String>(context, R.layout.dropdownrow, MINUTES);
            this.aApplianceQuantityAryList = aApplianceQuantityAryList__;
        }

        @Override
        public Myholder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Myholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.quanity_adapter, parent, false));
        }

        @Override
        public void onBindViewHolder(final Myholder holder, final int position) {
            this.hHoursAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, HOURS);
            this. mMINUTESAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, MINUTES);
            if (this.aStarFlag) {
                if(this.aApplianceQuantityAryList != null && this.aApplianceQuantityAryList.size() > 0){

                    /*
                    for(int k=0; k < talukAdapter.getCount(); k++) {
                        if(subdist.trim().equals(talukAdapter.getItem(k).toString())){
                            taluk.setText(talukAdapter.getItem(k).toString());
                            break;
                        }
                    }
                    this.taluk.setAdapter(talukAdapter);*/
                    if( this.aApplianceQuantityAryList != null &&this.aApplianceQuantityAryList.size() > position) {
                        int hours = (int) (aApplianceQuantityAryList.get(position).getHoursUsed() / 60);
                        int mins = (int) (aApplianceQuantityAryList.get(position).getHoursUsed() % 60);

                        holder.mHours.setText("" + hours);
                        holder.mTxtApplianceID.setText("" + "" + aApplianceQuantityAryList.get(position).getId());
                        holder.mTxtApplianceCode.setText("" +aApplianceCode);
                        holder.mTxtApplianceImageId.setText("" +aApplianceImageId);
                        holder.mHours.setAdapter(hHoursAdapter);
                        holder.mMinutes.setText("" + mins);
                        holder.mMinutes.setAdapter(mMINUTESAdapter);
                        holder.dDeleteImageView.setVisibility(View.VISIBLE);
                        holder.lLinearRattingBarLayout.setVisibility(View.VISIBLE);
                        holder.mApplianceRattingBar.setRating((float) aApplianceQuantityAryList.get(position).getRating());
                        holder.mWatts.setText("" + (long)aApplianceQuantityAryList.get(position).getCapacityInWatts());
                    }else{
                        holder.mHours.setAdapter(hHoursAdapter);
                        holder.mMinutes.setAdapter(mMINUTESAdapter);
                        holder.lLinearRattingBarLayout.setVisibility(View.VISIBLE);
                        holder.mWatts.setText("" +(long)mWatts);
                        holder.dDeleteImageView.setVisibility(View.GONE);
                        holder.mTxtApplianceCode.setText("" +aApplianceCode);
                        holder.mTxtApplianceImageId.setText("" +aApplianceImageId);
                    }

                }else {
                    holder.mHours.setAdapter(hHoursAdapter);
                    holder.mMinutes.setAdapter(mMINUTESAdapter);
                    holder.dDeleteImageView.setVisibility(View.GONE);
                    holder.lLinearRattingBarLayout.setVisibility(View.VISIBLE);
                    holder.mTxtApplianceCode.setText("" + aApplianceCode);
                    holder.mTxtApplianceImageId.setText("" +aApplianceImageId);
                    holder.mWatts.setText("" +(long)mWatts);
                }
                holder.dDeleteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        oOpenDialog(aApplianceQuantityAryList.get(position));

                    }
                });
                holder.mApplianceRattingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                    @Override
                    public void onRatingChanged(RatingBar ratingBar,float rating, boolean fromUser) {
                   //     Toast.makeText(context, String.valueOf(rating), Toast.LENGTH_LONG).show();
                    }
                });
               holder.mHours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                       String spinner1x = adapterView.getItemAtPosition(i).toString();
                       if (spinner1x.equals("24")) {
                           holder.mMinutes.setText("00");
                           holder.mMinutes.setEnabled(false);

                       }else{
                           holder.mMinutes.setEnabled(true);
                           mMINUTESAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, MINUTES);
                           holder.mMinutes.setAdapter(mMINUTESAdapter);
                       }
                   }
               });
            } else {
                if( this.aApplianceQuantityAryList != null && this.aApplianceQuantityAryList.size() > position) {
                    int hours = (int) (aApplianceQuantityAryList.get(position).getHoursUsed() / 60);
                    int mins = (int) (aApplianceQuantityAryList.get(position).getHoursUsed() % 60);

                    holder.mHours.setText("" + hours);
                    holder.mTxtApplianceID.setText("" + "" + aApplianceQuantityAryList.get(position).getId());
                    holder.mTxtApplianceCode.setText("" +aApplianceCode);
                    holder.mTxtApplianceImageId.setText("" +aApplianceImageId);
                    holder.mHours.setAdapter(hHoursAdapter);
                    holder.lLinearRattingBarLayout.setVisibility(View.GONE);
                    holder.dDeleteImageView.setVisibility(View.VISIBLE);
                    holder.mMinutes.setText("" + mins);
                    holder.mMinutes.setAdapter(mMINUTESAdapter);
                    holder.mWatts.setText("" + aApplianceQuantityAryList.get(position).getCapacityInWatts());
                }else{
                    holder.mHours.setAdapter(hHoursAdapter);
                    holder.mMinutes.setAdapter(mMINUTESAdapter);
                    holder.mWatts.setText("" + mWatts);
                    holder.dDeleteImageView.setVisibility(View.GONE);
                    holder.mTxtApplianceCode.setText("" +aApplianceCode);
                    holder.mTxtApplianceImageId.setText("" +aApplianceImageId);
                    holder.lLinearRattingBarLayout.setVisibility(View.GONE);
                }
                holder.mHours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String spinner1x = adapterView.getItemAtPosition(i).toString();
                        if (spinner1x.equals("24")) {
                            holder.mMinutes.setText("00");
                            holder.mMinutes.setEnabled(false);

                        }else{
                            holder.mMinutes.setEnabled(true);
                            mMINUTESAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, MINUTES);
                            holder.mMinutes.setAdapter(mMINUTESAdapter);
                        }
                    }
                });
                holder.dDeleteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        oOpenDialog(aApplianceQuantityAryList.get(position));

                    }
                });
//                holder.mWatts.setText(""+mWatts);
//                holder.mHours.setAdapter(hHoursAdapter);
//                holder.mMinutes.setAdapter(mMINUTESAdapter);
//                holder.mTxtApplianceCode.setText("" + aApplianceCode);


            }

        }

        private void oOpenDialog(final ApplianceDto applianceDto) {

            AlertDialog alertDialog = new AlertDialog.Builder(
                    mContext).create();

           //  Setting Dialog Title
         //   alertDialog.setTitle(""+applianceDto.getApplianceName());

            // Setting Dialog Message
            alertDialog.setMessage(mContext.getResources().getString(R.string.dDeleteQuantity));

//            // Setting Icon to Dialog
//            alertDialog.setIcon(R.drawable.tick);

            alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {


                    // Write your code here to execute after dialog closed
                  //  Toast.makeText(mContext, "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    ApplianceDto dto = new ApplianceDto();
                    dto.setId(applianceDto.getId());
                    ((ApplianceEntryActivity) context).DeleteAppliances(applianceDto);
                    dismiss();
                }
            });


            // Showing Alert Message
            alertDialog.show();
        }

        @Override
        public int getItemCount() {
//            if(true){
//                return sSize;
//            }else {
            return sSize;
//            }
        }

        public class Myholder extends RecyclerView.ViewHolder {
            TextView mWatts,mTxtApplianceID,mTxtApplianceCode,mTxtApplianceImageId;
            RatingBar mApplianceRattingBar;
            LinearLayout lLinearRattingBarLayout;

            MaterialBetterSpinner mHours, mMinutes;
            ImageView dDeleteImageView;

            public Myholder(View view) {
                super(view);
                dDeleteImageView = (ImageView) view.findViewById(R.id.dDeleteImageView);
                mWatts = (TextView) view.findViewById(R.id.mWattsEdditTxt);
                mTxtApplianceID = (TextView) view.findViewById(R.id.mTxtApplianceID);
                mTxtApplianceCode = (TextView) view.findViewById(R.id.mTxtApplianceCode);
                mTxtApplianceImageId = (TextView) view.findViewById(R.id.mTxtApplianceImageId);
                lLinearRattingBarLayout = (LinearLayout) view.findViewById(R.id.lLinearRattingBarLayout);
                mHours = (MaterialBetterSpinner) view.findViewById(R.id.mHours);
                mMinutes = (MaterialBetterSpinner) view.findViewById(R.id.mMinutes);
                mApplianceRattingBar = (RatingBar) view.findViewById(R.id.mApplianceRattingBar);
            }
        }
    }




}
