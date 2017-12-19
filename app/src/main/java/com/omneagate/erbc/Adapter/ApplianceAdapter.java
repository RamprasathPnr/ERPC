package com.omneagate.erbc.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omneagate.erbc.Dto.ApplianceDto;
import com.omneagate.erbc.R;

import java.util.List;

/**
 * Created by root on 3/12/16.
 */
public class ApplianceAdapter extends RecyclerView.Adapter<ApplianceAdapter.ApplianceHolder> {

    private Context context;
    public List<ApplianceDto> appliance_List;
    private LayoutInflater mLayoutInflater;


    public ApplianceAdapter(Context context, List<ApplianceDto> appliance_List) {
        this.context = context;
        this.appliance_List = appliance_List;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ApplianceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                mLayoutInflater.inflate(R.layout.appliance__adapter, parent, false);

        return new ApplianceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ApplianceHolder holder, final int position) {

        holder.appliance_name.setText(appliance_List.get(position).getApplianceName());
        holder.mTxtApplianceUnit.setVisibility(View.INVISIBLE);
        holder.appliance_image.setImageResource(appliance_List.get(position).getImageDrawableId());
        holder.ll_aplliance.setVisibility(View.VISIBLE);
        if (appliance_List.get(position).isIsselected()) {
            holder.ll_aplliance.setBackgroundColor(Color.GRAY);

        } else {
            holder.ll_aplliance.setBackgroundColor(android.R.color.white);
        }
        /*holder.ll_aplliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                *//*if(DBConstants.applianceDetailsDtoArrayList!=null && !DBConstants.applianceDetailsDtoArrayList.isEmpty()) {
                   for(int i=0 ; i<DBConstants.applianceDetailsDtoArrayList.size();i++){

                       if (DBConstants.applianceDetailsDtoArrayList.get(i).getPosition() == position) {

                           Toast.makeText(context, " check", Toast.LENGTH_SHORT).show();
                           ConfigureApplianceDialog configureApplianceDialog = new ConfigureApplianceDialog(context, appliance_List.get(position).getApplianceName(), appliance_List.get(position).isStarRated(), position, DBConstants.applianceDetailsDtoArrayList.get(i));
                           configureApplianceDialog.setCanceledOnTouchOutside(false);
                           configureApplianceDialog.show();
                           appliance_List.get(position).setIsselected(true);

                       }else{
                           appliance_List.get(position).setIsselected(true);
                           holder.ll_aplliance.setBackgroundColor(context.getResources().getColor(R.color.gray_1));
                           ConfigureApplianceDialog configureApplianceDialog = new ConfigureApplianceDialog(context, appliance_List.get(position).getApplianceName(), appliance_List.get(position).isStarRated(), position);
                           configureApplianceDialog.setCanceledOnTouchOutside(false);
                           configureApplianceDialog.show();
                       }

                   }
                }else {
                    holder.ll_aplliance.setBackgroundColor(context.getResources().getColor(R.color.gray_1));
                    appliance_List.get(position).setIsselected(true);
                    ConfigureApplianceDialog configureApplianceDialog = new ConfigureApplianceDialog(context, appliance_List.get(position).getApplianceName(), appliance_List.get(position).isStarRated(), position);
                    configureApplianceDialog.setCanceledOnTouchOutside(false);
                    configureApplianceDialog.show();
                }*//*

                holder.ll_aplliance.setBackgroundColor(context.getResources().getColor(R.color.gray_1));
                appliance_List.get(position).setIsselected(true);
                ConfigureApplianceDialog configureApplianceDialog = new ConfigureApplianceDialog(context, appliance_List.get(position).getApplianceName(), appliance_List.get(position).isStarRated(), position);
                configureApplianceDialog.setCanceledOnTouchOutside(false);
                configureApplianceDialog.show();

                notifyDataSetChanged();
//                holder.ll_aplliance.setBackgroundColor(Color.GRAY);

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return appliance_List.size();
    }

    public class ApplianceHolder extends RecyclerView.ViewHolder {
        private TextView appliance_name, mTxtApplianceUnit;
        private ImageView appliance_image;
        private RelativeLayout ll_aplliance;

        public ApplianceHolder(View v) {
            super(v);
            ll_aplliance = (RelativeLayout) v.findViewById(R.id.ll_appliance);
            appliance_name = (TextView) v.findViewById(R.id.appliance_name);
            appliance_image = (ImageView) v.findViewById(R.id.appliance_img);
            mTxtApplianceUnit = (TextView) v.findViewById(R.id.units_applianceTxt);
        }

    }


}