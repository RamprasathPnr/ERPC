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
import com.omneagate.erbc.Dto.SlabCategoryRangeDto;
import com.omneagate.erbc.Dto.UnitConsumptionDto;
import com.omneagate.erbc.R;
import java.util.List;

/**
 * Created by user1 on 18/7/16.
 */
public class BillinfoDialog extends Dialog implements View.OnClickListener{

    private final Context context;
    UnitConsumptionDto UnitConsumptiondto;

    public BillinfoDialog(Context context, UnitConsumptionDto consumptionlist) {
        super(context);

        this.context = context;
        UnitConsumptiondto = consumptionlist;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.billpayedinfo);
        setCancelable(false);
        ((TextView) findViewById(R.id.consumed_unit_txt)).setText("" + UnitConsumptiondto.getUnitsConsumed());
        ((TextView) findViewById(R.id.consumed_daystxt)).setText("" + UnitConsumptiondto.getConsumedDays());
        ((TextView) findViewById(R.id.consumed_billcycle_txt)).setText("" + UnitConsumptiondto.getApproxUnitconsumed());
        ((TextView) findViewById(R.id.slab_txt)).setText("" + UnitConsumptiondto.getSlabCategory());
        ((TextView) findViewById(R.id.total_unit)).setText("" + UnitConsumptiondto.getUnitsConsumed());
        ((TextView) findViewById(R.id.total_amt)).setText("" + UnitConsumptiondto.getTotalAmount());
        List<SlabCategoryRangeDto> rangeDto;
        rangeDto = UnitConsumptiondto.getSlabCategoryRange();
        int rangeSize = rangeDto.size();
        Log.e("ragngesize",""+rangeSize);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
        LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.charge100lay);
        Log.i("Detail", ""+UnitConsumptiondto.getSlabCategoryRange());
        fpsInwardLinearLayout.removeAllViews();
        for (int position = 0; position < UnitConsumptiondto.getSlabCategoryRange().size(); position++) {
            LayoutInflater lin = LayoutInflater.from(context);
            fpsInwardLinearLayout.addView(returnView(lin, UnitConsumptiondto.getSlabCategoryRange().get(position), position));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                dismiss();
        }
    }

    private View returnView(LayoutInflater entitle, SlabCategoryRangeDto data, final int itemPosition) {
        View convertView = entitle.inflate(R.layout.billdetailrow, null);
        TextView slabText = (TextView) convertView.findViewById(R.id.change_100unit);
        TextView slabUnit = (TextView) convertView.findViewById(R.id.unit_100);
        TextView slabAmount = (TextView) convertView.findViewById(R.id.amount_100);
        slabText.setText(data.getSlabText());
        slabAmount.setText(data.getSlabAmount());
        slabUnit.setText(data.getSlabUnit());
        return convertView;
    }
}
