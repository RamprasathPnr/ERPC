package com.omneagate.erbc.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Dto.UsageMonitoringDto;
import com.omneagate.erbc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 19/10/16.
 */
public class ConsumptionPatternDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private List<UsageMonitoringDto> usageMonitoringDto;
    private BarChart mChart;
    private String ChartType,s_month;
    private TextView serviceConnection,month;
    ImageView close_button;

    public ConsumptionPatternDialog(Context context, List<UsageMonitoringDto> usageMonitoringDto,String ChartType,String month) {
        super(context);
        this.context=context;
        this.usageMonitoringDto=usageMonitoringDto;
        this.ChartType=ChartType;
        this.s_month=month;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_consumption_pattern);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mChart=(BarChart)findViewById(R.id.chart_dialog);
        mChart.setNoDataText(context.getString(R.string.no_chart_data));
        serviceConnection=(TextView)findViewById(R.id.servcer_connection);
        month=(TextView)findViewById(R.id.month);
        close_button=(ImageView)findViewById(R.id.close_button);
        close_button.setOnClickListener(this);
        setData(usageMonitoringDto);
    }

    private void setData(List<UsageMonitoringDto> usageMonitoringDto) {
        try {
            // this is the top line
            if (usageMonitoringDto != null && usageMonitoringDto.size() > 0) {

                ArrayList<BarDataSet> dataSets = null;

                ArrayList<String> connectionList = new ArrayList<>();
                ArrayList<BarEntry> consumedUnits = new ArrayList<>();
                ArrayList<BarEntry> amount = new ArrayList<>();


                for (int i = 0; i < usageMonitoringDto.size(); i++) {

                    connectionList.add(usageMonitoringDto.get(i).getConsumerNumber().substring(usageMonitoringDto.get(i).getConsumerNumber().length() -4));
                    consumedUnits.add(new BarEntry(Float.parseFloat(usageMonitoringDto.get(i).getUnitsConsumed()), i));
                    amount.add(new BarEntry(Float.parseFloat(usageMonitoringDto.get(i).getBillAmount()), i));
                }

                Log.e("Consumption values "," connection List size "+connectionList.size());
                Log.e("Consumption values "," connection List values "+connectionList.toString());
                Log.e("Consumption values "," Amounts List size "+amount.size());
                Log.e("Consumption values "," Consumed Units List Size"+consumedUnits.size());

                String type;
                int color;
                ArrayList<BarEntry> barentrylist;
                if (ChartType.equalsIgnoreCase("Amount")) {
                    type = context.getString(R.string.amount_in_inr);
                    color = Color.parseColor("#8B51FB");
                    barentrylist = amount;

                } else {
                    type = context.getString(R.string.consumption_units_kwh);
                    color = Color.parseColor("#DB6485");
                    barentrylist = consumedUnits;
                }
                serviceConnection.setText(context.getString(R.string.Service_connection_vs)+" "+type);
                month.setText(""+s_month);
                BarDataSet barDataSet1 = new BarDataSet(barentrylist, type);
                barDataSet1.setColor(color);
                dataSets = new ArrayList<>();
                dataSets.add(barDataSet1);
                Configuration config = context.getResources().getConfiguration();

                if (config.smallestScreenWidthDp >= 720) {
                    barDataSet1.setValueTextSize(12f);  // 10-inch tablet and above

                }
                else if (config.smallestScreenWidthDp >= 600) {
                    barDataSet1.setValueTextSize(10f); // 7-inch tablet and above

                }
                else {
                    // For mobile devices
                }

                setUpChart(dataSets, connectionList);

            }

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    private void setUpChart(ArrayList<BarDataSet> dateSet, ArrayList<String> xAxisValue) {
        try {

            BarData data = new BarData(xAxisValue, dateSet);
            mChart.setData(data);
            mChart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer
            mChart.setDrawGridBackground(false);
            mChart.getAxisLeft().setDrawGridLines(false);
            mChart.getAxisRight().setDrawLabels(false);
            mChart.getXAxis().setDrawGridLines(false);

            XAxis xAxis = mChart.getXAxis();


            YAxis yAxis = mChart.getAxisLeft();
            yAxis.setDrawGridLines(false);

            yAxis = mChart.getAxisRight();
            yAxis.setDrawGridLines(false);
            yAxis.setEnabled(false);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
           // xAxis.setAdjustXLabels(false);

            Legend legend=mChart.getLegend();
            Configuration config = context.getResources().getConfiguration();

            if (config.smallestScreenWidthDp >= 720) {
                legend.setTextSize(context.getResources().getDimension(R.dimen.legendsize_seven));

            } else if (config.smallestScreenWidthDp >= 600) {
                legend.setTextSize(context.getResources().getDimension(R.dimen.legendsize_seven));
            } else {
                legend.setTextSize(context.getResources().getDimension(R.dimen.legendsize));
            }


            mChart.setDescription("");
            mChart.animateXY(2000, 2000);
            mChart.invalidate();

          /*  Paint mValuPaint = mChart.getPaint(Chart.PAINT_CENTER_TEXT );
            mValuPaint.setTextSize(10);*/

        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close_button:
                dismiss();
                break;
        }

    }
}
