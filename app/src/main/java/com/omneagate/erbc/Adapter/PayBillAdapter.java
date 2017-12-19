package com.omneagate.erbc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omneagate.erbc.Activity.BillHistoryDetailActivity;
import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Dto.BillHistoryDto;
import com.omneagate.erbc.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Shanthakumar on 29-09-2016.
 */
public class PayBillAdapter extends RecyclerView.Adapter<PayBillAdapter.PayBillHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<BillHistoryDto> billHistoryList;
    private DateFormat dateFormat;
    private Calendar calendar;

    public PayBillAdapter(Context c, List<BillHistoryDto> billHistoryList) {

        this.mContext = c;
        this.billHistoryList = billHistoryList;
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public PayBillHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView =
                mLayoutInflater.inflate(R.layout.myusagelist, parent, false);

        return new PayBillHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PayBillHolder holder, final int position) {

        try {
            holder.connectionNumber.setText(billHistoryList.get(position).getConsumerDisplayNo());
            holder.price.setText("INR " + billHistoryList.get(position).getAmount());
            holder.kvah_id.setText("" + billHistoryList.get(position).getConsumption() + " kWh");
            holder.connectionName.setText(billHistoryList.get(position).getConsumerName());
            holder.dueDate.setText(billHistoryList.get(position).getLastDueDate());
            if (billHistoryList.get(position).getBillStatus().equalsIgnoreCase("true")) {
                holder.payment_status.setText(mContext.getString(R.string.paid));
                holder.payment_status.setTextColor(Color.parseColor("#39b54a"));
            } else {
                holder.payment_status.setText(mContext.getString(R.string.nupaid));
               // holder.payment_status.setTextColor(Color.parseColor("#FF9800"));
                holder.payment_status.setTextColor(Color.parseColor("#061994"));
            }
          //  holder.regionName.setText(billHistoryList.get(position).getRegion().getName());
            calendar.setTime(dateFormat.parse(billHistoryList.get(position)
                    .getCurrentMeterReadingDate()));
//            holder.createdays.setText("" + calendar.get(Calendar.DATE));
//            holder.month_year.setText(new SimpleDateFormat("MMM").format(calendar.getTime())  + " " + calendar.get(Calendar.YEAR));

            holder.mLlMyUsage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent nextpage = new Intent(mContext, BillHistoryDetailActivity.class);
                    nextpage.putExtra("billdetails",
                            new Gson().toJson(billHistoryList.get(position)));
                    nextpage.putExtra("activityName","PayBillActivity");
                    mContext.startActivity(nextpage);
                }
            });
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("listviewException", e.toString(), e);
        }
    }

    @Override
    public int getItemCount() {
        return billHistoryList.size();
    }

    public class PayBillHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLlMyUsage;
        private TextView connectionNumber;
        private TextView connectionName;
        private TextView payment_status;
     //   private TextView regionName;
//        private TextView createdays;
//        private TextView month_year;
        private TextView price;
        private TextView dueDate;
        private TextView kvah_id;

        public PayBillHolder(View v) {
            super(v);

            mLlMyUsage = (LinearLayout) v.findViewById(R.id.ll_my_usage);
            connectionNumber = (TextView) v.findViewById(R.id.connection_number);
            connectionName = (TextView) v.findViewById(R.id.connectionname);
         //   regionName =(TextView) v.findViewById(R.id.Region_name);

//            createdays = (TextView) v.findViewById(R.id.cdays);
//            month_year = (TextView) v.findViewById(R.id.month_year);
            price = (TextView) v.findViewById(R.id.price);
            kvah_id = (TextView) v.findViewById(R.id.kvah_id);
            payment_status = (TextView) v.findViewById(R.id.payment_status);
            dueDate=(TextView) v.findViewById(R.id.lastDuedate);

        }
    }
}
