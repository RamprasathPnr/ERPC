package com.omneagate.erbc.Activity.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.erbc.Dto.SlabCategoryRangeDto;
import com.omneagate.erbc.Dto.UnitConsumptionDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.Util;

/**
 * Created by user1 on 18/7/16.
 */

public class BillinfoDialog2 extends Dialog implements View.OnClickListener {

    private final Context context;
    private UnitConsumptionDto UnitConsumptiondto;
    private boolean isTamil = false;

    public BillinfoDialog2(Context context, UnitConsumptionDto consumptionlist) {

        super(context);
        this.context = context;
        UnitConsumptiondto = consumptionlist;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.trail);
        setCancelable(false);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        ((TextView) findViewById(R.id.fixed_amt)).setText("" + UnitConsumptiondto.getFixedCharges());
        ((TextView) findViewById(R.id.txt_penalty)).setText("" + UnitConsumptiondto.getPenaltyAmount());
        ((TextView) findViewById(R.id.slab)).setText("Slab :" + UnitConsumptiondto.getSlabCategory());
        ((TextView) findViewById(R.id.total_amt)).setText("" + UnitConsumptiondto.getTotalAmount());
        ((TextView) findViewById(R.id.split_uni)).setText("" + UnitConsumptiondto.getUnitsConsumed());
        ((TextView) findViewById(R.id.tot_amo)).setText("" + UnitConsumptiondto.getAmount());
        okButton.setOnClickListener(this);

        LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.charge100lay);
        fpsInwardLinearLayout.removeAllViews();
        Log.e("Detail", "" + UnitConsumptiondto.getSlabCategoryRange().size());
        if (Util.checkAppLanguage(context).equalsIgnoreCase("ta"))
            isTamil = true;
        for (int position = 0; position < UnitConsumptiondto.getSlabCategoryRange().size(); position++) {
            LayoutInflater lin = LayoutInflater.from(context);
            fpsInwardLinearLayout.addView(returnView(lin, UnitConsumptiondto.getSlabCategoryRange().get(position), position));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                dismiss();
        }
    }

    private View returnView(LayoutInflater entitle, SlabCategoryRangeDto data, final int itemPosition) {
        View convertView = entitle.inflate(R.layout.trail2, null);
       // TextView slabText = (TextView) convertView.findViewById(R.id.change_100unit);
        TextView slabUnit = (TextView) convertView.findViewById(R.id.unit_100);
        TextView slabAmount = (TextView) convertView.findViewById(R.id.amount_100);
        TextView cost_unit = (TextView) convertView.findViewById(R.id.cost_unit);
        TextView cost = (TextView) convertView.findViewById(R.id.cost);

      /*  if (isTamil) slabText.setText(data.getSlabTextRegional());
        else slabText.setText(data.getSlabText());*/
        slabAmount.setText(data.getSlabAmount());
        slabUnit.setText(data.getSlabText());
        cost_unit.setText(data.getCostPerUnit());
        cost.setText(data.getSlabUnit());
        return convertView;
    }
}
