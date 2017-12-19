package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.DBHelper;

/**
 * Created by user1 on 28/4/16.
 */
public class HomepageActivity extends Fragment implements View.OnClickListener{

    LinearLayout connections,profile_lin,myusage_lin,billhistory_lin,payment_lin,reminder_lin;
    public HomepageActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try
        {
            View view = inflater.inflate(R.layout.homepageactivity, container, false);
            connections = (LinearLayout)view.findViewById(R.id.conenctionlay);
            profile_lin = (LinearLayout)view.findViewById(R.id.profileLin);
            myusage_lin = (LinearLayout)view.findViewById(R.id.lin_one);
            billhistory_lin = (LinearLayout)view.findViewById(R.id.billhistory_lin);
            payment_lin = (LinearLayout)view.findViewById(R.id.payment_lin);
            reminder_lin = (LinearLayout)view.findViewById(R.id.reminder_lin);
            int count = DBHelper.getInstance(getActivity()).getCustomerCount();
            connections.setOnClickListener(this);
            profile_lin.setOnClickListener(this);
            myusage_lin.setOnClickListener(this);
            billhistory_lin.setOnClickListener(this);
            payment_lin.setOnClickListener(this);
            reminder_lin.setOnClickListener(this);
            return view;
        }catch(Exception e)
        {
            GlobalAppState.getInstance().trackException(e);
            Log.e("HomepageActviityError",e.toString(),e);
        }


        return null;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.profileLin:
                profile_lin.setBackgroundColor(R.color.possible_result_points);
                Intent myprofile_page = new Intent(getActivity(), MyProfileActivity.class);
                myprofile_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myprofile_page);
                getActivity().finish();
                break;
            case R.id.conenctionlay:
                connections.setBackgroundColor(R.color.possible_result_points);
                Intent connection_page = new Intent(getActivity(), ConnectionListActivity.class);
                connection_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(connection_page);
                getActivity().finish();
                break;
            case R.id.billhistory_lin:
                billhistory_lin.setBackgroundColor(R.color.possible_result_points);
                Intent billhistory = new Intent(getActivity(), BillconnectionActivity.class);
                billhistory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(billhistory);
                getActivity().finish();
                break;
            case R.id.lin_one:
                myusage_lin.setBackgroundColor(R.color.possible_result_points);
                Intent myusage = new Intent(getActivity(), MyusageListActivity.class);
                myusage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myusage);
                getActivity().finish();
                break;
            case R.id.payment_lin:
                payment_lin.setBackgroundColor(R.color.possible_result_points);
                Intent payment_history = new Intent(getActivity(), PaymentConnectionActivity.class);
                payment_history.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(payment_history);
                getActivity().finish();
                break;
            case R.id.reminder_lin:
                reminder_lin.setBackgroundColor(R.color.possible_result_points);
                Intent reminderlist = new Intent(getActivity(), ReminderListActivity.class);
                reminderlist.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(reminderlist);
                getActivity().finish();
                break;
        }
    }
}
