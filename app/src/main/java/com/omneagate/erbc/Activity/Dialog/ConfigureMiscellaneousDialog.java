package com.omneagate.erbc.Activity.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.erbc.Activity.ApplianceEntryActivity;
import com.omneagate.erbc.Adapter.ApplianceAdapter;
import com.omneagate.erbc.Dto.ApplianceDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.DBConstants;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by root on 16/12/16.
 */
public class ConfigureMiscellaneousDialog extends Dialog {


    private TextView mTxtApplianceCategoryTitle;
    private RecyclerView miscellaneousRecyclerView;
    private Button saveButton;
    private Button cancelButton;
    private Context mContext;

    MiscellaneousAdapter aAdapter;

    List<ApplianceDto>mMiscellaneousApplianceList;

    int sSize = 0, aApplianceCodeMissCell, aApplianceImageIdMissCell;

    boolean isTrue=false, mStarFlag;
    String mApplianceName;
    float mWatts;
    public int pPosition;

    String[] HOURS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23","24"};
    String[] MINUTES = {"0", "15", "30", "45", "59"};

    public ConfigureMiscellaneousDialog(Context context, String appliance_name, boolean mStarFlag_, int position, float watts, List<ApplianceDto> miscellaneousApplianceList__, String applianceCode, int mImageId) {
        super(context);
        this.mContext = context;
        this.mApplianceName = appliance_name;
        this.mStarFlag = mStarFlag_;
        this.mWatts = watts;
        this.pPosition = position;
        this.mMiscellaneousApplianceList = miscellaneousApplianceList__;
        this.aApplianceCodeMissCell = Integer.parseInt(applianceCode);
        this.aApplianceImageIdMissCell = mImageId;


    }

    /*public ConfigureMiscellaneousDialog(Context context, String appliance_name, boolean mStarFlag_, int position, float capacityInWatts, String applianceCode, int mImageId) {
        super(context);
        this.mContext = context;
        this.aApplianceCodeMissCell = Integer.parseInt(applianceCode);
        this.aApplianceImageIdMissCell = mImageId;

    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_miscellaneous);
        configureIntialView();
    }

    private void configureIntialView() {
        try {
            saveButton = (Button) findViewById(R.id.saveButton);
            cancelButton = (Button) findViewById(R.id.cancelButton);
            miscellaneousRecyclerView = (RecyclerView) findViewById(R.id.miscellaneousRecyclerView);

            miscellaneousRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            miscellaneousRecyclerView.setNestedScrollingEnabled(false);
            aAdapter = new MiscellaneousAdapter(mContext, mStarFlag,mMiscellaneousApplianceList);
            miscellaneousRecyclerView.setAdapter(aAdapter);


            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setAdapterValidation();
                    if (isTrue) {
                        setApplianceDto();
                        dismiss();
                    }

                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ApplianceEntryActivity mTempClass = (ApplianceEntryActivity) mContext;
                    ApplianceAdapter applianceAdapter = mTempClass.mAdapter;
                    applianceAdapter.appliance_List.get(pPosition).setIsselected(false);
                    applianceAdapter.notifyDataSetChanged();
                    DBConstants.aNewApplianceQuantityAryList.clear();
                    if(mMiscellaneousApplianceList !=null) {     //added by ram
                        mMiscellaneousApplianceList.clear();
                    }
                    dismiss();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapterValidation() {
        try {
            aAdapter = (MiscellaneousAdapter) miscellaneousRecyclerView.getAdapter();

            for (int i = 0; i < aAdapter.getItemCount(); i++) {
                View selectedView = miscellaneousRecyclerView.getChildAt(i);


                EditText mWatts = (EditText) selectedView.findViewById(R.id.appliance_watts);
                EditText nApplianceName = (EditText) selectedView.findViewById(R.id.appliance_name);
                MaterialBetterSpinner mHours = (MaterialBetterSpinner) selectedView.findViewById(R.id.appliance_hours);
                MaterialBetterSpinner mMinutes = (MaterialBetterSpinner) selectedView.findViewById(R.id.appliance_minutes);
                RatingBar mApplianceRattingBar = (RatingBar) selectedView.findViewById(R.id.applianceRating);


                //  if (mStarFlag) {

                if (StringUtils.isEmpty(nApplianceName.getText().toString().trim())) {
                    mHours.setError(mContext.getResources().getString(R.string.mApplianceName));
                    isTrue = false;
                    break;
                } else if (mWatts.getText().toString().trim().length() == 0) {
                    // Toast.makeText(PassengerDetailsActivity.this,"Please enter your Email id",Toast.LENGTH_SHORT).show();
                    mWatts.setError(mContext.getResources().getString(R.string.mWattsError));
                    mWatts.requestFocus();
                    isTrue = false;
                    break;
                } else if (StringUtils.isEmpty(mHours.getText().toString().trim())) {
                    mHours.setError(mContext.getResources().getString(R.string.mHoursError));
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.mHoursError),Toast.LENGTH_SHORT).show();
                    isTrue = false;
                } else if (StringUtils.isEmpty(mMinutes.getText().toString().trim())) {
                    mMinutes.setError(mContext.getResources().getString(R.string.mMinutesError));
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.mMinutesError),Toast.LENGTH_SHORT).show();
                    isTrue = false;
                    break;
                }  else if (mHours.getText().toString().equalsIgnoreCase("0") && mMinutes.getText().toString().equalsIgnoreCase("0")) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.mValidTime), Toast.LENGTH_SHORT).show();
                    isTrue = false;
                    break;
                }/*else if (mApplianceRattingBar.getRating() == 0) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.mRattingError), Toast.LENGTH_SHORT).show();
                    isTrue = false;
                    mStarFlag=false;
                    break;
                } else if (mApplianceRattingBar.getRating()>0) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.mRattingError), Toast.LENGTH_SHORT).show();
                    mStarFlag=true;
                    break;
                } */ else {
                    isTrue = true;
                }

         /*   }else{
                if (StringUtils.isEmpty(mHours.getText().toString().trim())) {
                    mHours.setError(mContext.getResources().getString(R.string.mHoursError));
                    isTrue = false;
                } else if (StringUtils.isEmpty(mMinutes.getText().toString().trim())) {
                    mMinutes.setError(mContext.getResources().getString(R.string.mMinutesError));
                    isTrue = false;
                }  else if (mWatts.getText().toString().trim().length() == 0) {
                    // Toast.makeText(PassengerDetailsActivity.this,"Please enter your Email id",Toast.LENGTH_SHORT).show();
                    mWatts.setError(mContext.getResources().getString(R.string.mWattsError));
                    mWatts.requestFocus();
                    isTrue = false;
                    break;
                } else {
                    isTrue = true;
                }
            }*/


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    private void setApplianceDto() {
        aAdapter = (MiscellaneousAdapter) miscellaneousRecyclerView.getAdapter();
        for (int i = 0; i < aAdapter.getItemCount(); i++) {
            View selectedView = miscellaneousRecyclerView.getChildAt(i);
            //   ApplianceDetailsDto applianceDetailsDto = new ApplianceDetailsDto();
            ApplianceDto applianceDetailsDto = new ApplianceDto();

            EditText mWatts = (EditText) selectedView.findViewById(R.id.appliance_watts);
            EditText nApplianceName= (EditText) selectedView.findViewById(R.id.appliance_name);
            MaterialBetterSpinner mHours = (MaterialBetterSpinner) selectedView.findViewById(R.id.appliance_hours);
            MaterialBetterSpinner mMinutes = (MaterialBetterSpinner) selectedView.findViewById(R.id.appliance_minutes);
            RatingBar mApplianceRattingBar = (RatingBar) selectedView.findViewById(R.id.applianceRating);

            TextView mTxtApplianceID = (TextView) selectedView.findViewById(R.id.mTxtApplianceID_Miscell);
            TextView mTxtApplianceCode = (TextView) selectedView.findViewById(R.id.mTxtApplianceCode_Miscell);
            TextView mTxtApplianceImageId = (TextView) selectedView.findViewById(R.id.mTxtApplianceImageId_Miscell);

            String val = mTxtApplianceID.getText().toString();


            applianceDetailsDto.setApplianceName(nApplianceName.getText().toString().trim());

            applianceDetailsDto.setApplianceCode(mTxtApplianceCode.getText().toString());
            if(mTxtApplianceID.getText().toString()!=null && !mTxtApplianceID.getText().toString().equalsIgnoreCase("")){

                applianceDetailsDto.setId(Integer.parseInt(mTxtApplianceID.getText().toString()));

            }

            applianceDetailsDto.setImageDrawableId(Integer.parseInt(mTxtApplianceImageId.getText().toString()));
            float minutes = Integer.parseInt(mHours.getText().toString().trim()) * 60 + Integer.parseInt(mMinutes.getText().toString());
            applianceDetailsDto.setHoursUsed((long) minutes);
            applianceDetailsDto.setHours(mHours.getText().toString().trim());
            applianceDetailsDto.setMinutes(mMinutes.getText().toString());
          //  applianceDetailsDto.setRating(mApplianceRattingBar.getRating());
            applianceDetailsDto.setCapacityInWatts(Float.parseFloat(mWatts.getText().toString().trim()));

            //  float val = Float.parseFloat(mWatts.getText().toString());
            applianceDetailsDto.setConsumedUnits(cCalculateConsumedUnit(Float.parseFloat(mWatts.getText().toString()), mHours.getText().toString().trim(), mMinutes.getText().toString(), (int) mApplianceRattingBar.getRating()));
            applianceDetailsDto.setPosition(pPosition);
//            applianceDetailsDto.setStarRated(mStarFlag);

            DBConstants.applianceDetailsDtoArrayList.add(applianceDetailsDto);
            Log.e("applianceDetailsDtoArrayList Size ", "" + DBConstants.applianceDetailsDtoArrayList.size());
        }
    }

    private float cCalculateConsumedUnit(float watts, String hours, String minute, int rating) {

        float fFinalConsumedUnit;


        float minutes = Integer.parseInt(hours) * 60 + Integer.parseInt(minute);

       /* if (mStarFlag) {
            float sStarValue = DBHelper.getInstance(mContext).getStarPercentage(rating);
            float wWithOutStarResult = ((float) watts / 1000) * (minutes / 60);
            float sStarValue_ = (float) sStarValue / 100;
            float wWithStarResult = wWithOutStarResult * sStarValue_;

            fFinalConsumedUnit = wWithStarResult;

        } else {*/
            float wWithOutStarResult = ((float) watts / 1000) * (minutes / 60);
            fFinalConsumedUnit = wWithOutStarResult;
//        }


        return Float.parseFloat(String.format("%.02f", fFinalConsumedUnit));
    }

    class MiscellaneousAdapter extends RecyclerView.Adapter<MiscellaneousAdapter.Myholder> {

        Context context;

        boolean aStarFlag;
        List<ApplianceDto> miscellaneousApplianceList;
        ArrayAdapter<String> hHoursAdapter;
        ArrayAdapter<String> mMINUTESAdapter;

        /*public MiscellaneousAdapter(Context context, int count, boolean mStarFlag_) {
            this.context = context;
            this.aStarFlag = mStarFlag_;
            this.hHoursAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, HOURS);
            this.mMINUTESAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, MINUTES);
        }*/

        public MiscellaneousAdapter(Context mContext, boolean mStarFlag_, List<ApplianceDto> miscellaneousApplianceList) {
            this.context = mContext;
            this.aStarFlag = mStarFlag_;
            this.hHoursAdapter = new ArrayAdapter<String>(context, R.layout.dropdownrow, HOURS);
            this.mMINUTESAdapter = new ArrayAdapter<String>(context, R.layout.dropdownrow, MINUTES);
            this.miscellaneousApplianceList = miscellaneousApplianceList;
        }

        @Override
        public Myholder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Myholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_miscellaneous, parent, false));
        }

        @Override
        public void onBindViewHolder(final Myholder holder, final int position) {


            int hours = (int) (miscellaneousApplianceList.get(position).getHoursUsed() / 60);
            int mins = (int) (miscellaneousApplianceList.get(position).getHoursUsed() % 60);

            holder.appliance_name.setText(miscellaneousApplianceList.get(position).getApplianceName());
            holder.appliance_capacity.setText("" +(long) miscellaneousApplianceList.get(position).getCapacityInWatts());
            holder.appliance_rating_bar.setRating((float) miscellaneousApplianceList.get(position).getRating());
            holder.appliance_hours.setText("" + hours);
            holder.appliance_minutes.setText("" + mins);
            holder.appliance_hours.setAdapter(hHoursAdapter);
            holder.appliance_minutes.setAdapter(mMINUTESAdapter);

            holder.mTxtApplianceID_Miscell.setText("" + miscellaneousApplianceList.get(position).getId());
            holder.mTxtApplianceCode_Miscell.setText("" + aApplianceCodeMissCell);
            holder.mTxtApplianceImageId_Miscell.setText("" + aApplianceImageIdMissCell);
            holder.rRemoveLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  Toast.makeText(mContext,"Remove",Toast.LENGTH_SHORT).show();
                    oOpenDialog(miscellaneousApplianceList.get(position));
                }
            });
            holder.appliance_hours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String spinner1x = adapterView.getItemAtPosition(i).toString();
                    if (spinner1x.equals("24")) {
                        holder.appliance_minutes.setText("00");
                        holder.appliance_minutes.setEnabled(false);

                    }else{
                        holder.appliance_minutes.setEnabled(true);
                        mMINUTESAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdownrow, MINUTES);
                        holder.appliance_minutes.setAdapter(mMINUTESAdapter);
                    }
                }
            });

        }
        private void oOpenDialog(final ApplianceDto applianceDto) {

            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
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
            return miscellaneousApplianceList.size();
        }

        public class Myholder extends RecyclerView.ViewHolder {
            EditText appliance_name, appliance_capacity;
            RatingBar appliance_rating_bar;
            MaterialBetterSpinner appliance_hours, appliance_minutes;
            LinearLayout rRemoveLinearLayout;

            TextView mTxtApplianceID_Miscell, mTxtApplianceCode_Miscell, mTxtApplianceImageId_Miscell;


            public Myholder(View view) {
                super(view);
                appliance_name = (EditText) view.findViewById(R.id.appliance_name);
                appliance_capacity = (EditText) view.findViewById(R.id.appliance_watts);
                appliance_rating_bar = (RatingBar) view.findViewById(R.id.applianceRating);
                appliance_hours = (MaterialBetterSpinner) view.findViewById(R.id.appliance_hours);
                appliance_minutes = (MaterialBetterSpinner) view.findViewById(R.id.appliance_minutes);


                mTxtApplianceID_Miscell = (TextView) view.findViewById(R.id.mTxtApplianceID_Miscell);
                mTxtApplianceCode_Miscell = (TextView) view.findViewById(R.id.mTxtApplianceCode_Miscell);
                mTxtApplianceImageId_Miscell = (TextView) view.findViewById(R.id.mTxtApplianceImageId_Miscell);
                rRemoveLinearLayout = (LinearLayout) view.findViewById(R.id.rRemoveLinearLayout);


            }
        }
    }
}
