package com.omneagate.erbc.Activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.NetworkConnection;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.ByteArrayOutputStream;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by user1 on 24/5/16.
 */
public class ViewConnectionFragment extends BaseActivityFragment  {

    ImageView billImg,cardImg;
    View view;
    MaterialBetterSpinner discomm,phase,connection_type,consumer_type;
    String languageCode =  GlobalAppState.language;
    private static final String TAG = ViewConnectionFragment.class.getName();
    Bitmap bitmap_bill,bitmap_card;
    public ViewConnectionFragment() {
        // Required empty public constructor
    }

    Target loadtargetbill,loadtargetcard;

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
        view = inflater.inflate(R.layout.viewconnection, container, false);
        configureInitialPage(view);
        billImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                 //   Bitmap bitmap = ((BitmapDrawable)billImg.getDrawable()).getBitmap();
                    dialog(R.layout.image_confirmation, bitmap_bill);
                /*    final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
                    mMaterialDialog.setCanceledOnTouchOutside(true);
                    mMaterialDialog.setPositiveButton("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });

                    Bitmap bitmap = ((BitmapDrawable)billImg.getDrawable()).getBitmap();
                    Bitmap  resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*10), (int)(bitmap.getHeight()*10), true);
                 // Bitmap  resized = Bitmap.createScaledBitmap(bitmap,100, 100, true);
                    Drawable drawe = new BitmapDrawable(getResources(), resized);
                    mMaterialDialog.setBackground(drawe);
                    mMaterialDialog.show();*/

                }catch(Exception e)
                {
                    GlobalAppState.getInstance().trackException(e);
                }
            }
        });


        cardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                  //  Bitmap bitmap = ((BitmapDrawable)cardImg.getDrawable()).getBitmap();
                    dialog(R.layout.image_confirmation, bitmap_card);
                   /* final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
                    mMaterialDialog.setCanceledOnTouchOutside(true);
                    mMaterialDialog.setPositiveButton("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
                    Bitmap bitmap = ((BitmapDrawable)cardImg.getDrawable()).getBitmap();
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*10), (int)(bitmap.getHeight()*10), true);
                    //Bitmap  resized = Bitmap.createScaledBitmap(bitmap,100, 100, true);
                    Drawable drawe = new BitmapDrawable(getResources(), resized);
                    mMaterialDialog.setBackground(drawe);
                    mMaterialDialog.show();*/

                }catch(Exception e)
                {
                    GlobalAppState.getInstance().trackException(e);

                }
            }
        });
       return view;
    }

    public View dialog(int cus_layout, Bitmap bm) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View layout = inflater.inflate(cus_layout,
                null);

        ImageView img = (ImageView) layout.findViewById(R.id.imageView10);
//        bm = Bitmap.createScaledBitmap(bm, 300, 500, true);
        bm = Bitmap.createBitmap(bm);
        img.setImageBitmap(bm);
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    public void loadBitmapBill(String url) {

        if (loadtargetbill == null) loadtargetbill = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // do something with the Bitmap
                bitmap_bill=bitmap;
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                if (bitmap_bill != null)
                    bitmap_bill.compress(Bitmap.CompressFormat.JPEG, 25, stream1);
               /* Bitmap scaled =Bitmap.createScaledBitmap(bitmap_bill,100,150,true);
                billImg.setImageBitmap(scaled);*/
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }


        };

        Picasso.with(getActivity()).load(url).into(loadtargetbill);
    }
    public void loadBitmapCard(String url) {

        if (loadtargetcard == null) loadtargetcard = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // do something with the Bitmap
                bitmap_card=bitmap;
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                if (bitmap_card != null)
                    bitmap_card.compress(Bitmap.CompressFormat.JPEG, 25, stream1);
               /* Bitmap scaled =Bitmap.createScaledBitmap(bitmap_card,100,150,true);
                cardImg.setImageBitmap(scaled);*/
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }


        };

        Picasso.with(getActivity()).load(url).into(loadtargetcard);
    }

    private void configureInitialPage(View view) {
        try
        {
            Button nextBtn = (Button) view.findViewById(R.id.button);
            nextBtn.setVisibility(View.INVISIBLE);
            discomm = (MaterialBetterSpinner) view.findViewById(R.id.discomm);
            phase = (MaterialBetterSpinner)view.findViewById(R.id.phase);
            connection_type = (MaterialBetterSpinner)view.findViewById(R.id.connectiontype);
            consumer_type = (MaterialBetterSpinner)view.findViewById(R.id.consumer_type);
            billImg = (ImageView)view.findViewById(R.id.upload_bill);
            cardImg = (ImageView)view.findViewById(R.id.upload_card);
            //setData
            if (languageCode.equalsIgnoreCase("ta")) {
                discomm.setText(""+ConnectionDetailActivity.ConnectionDetail.getDiscom().getRegionalName());
                phase.setText(""+ConnectionDetailActivity.ConnectionDetail.getPhase().getRegionalName());
                connection_type.setText(""+ConnectionDetailActivity.ConnectionDetail.getConnectionType().getRegionalName());
                consumer_type.setText(""+ConnectionDetailActivity.ConnectionDetail.getConsumerType().getRegionalName());
            }else{
                discomm.setText(""+ConnectionDetailActivity.ConnectionDetail.getDiscom().getName());
                phase.setText(""+ConnectionDetailActivity.ConnectionDetail.getPhase().getName());
                connection_type.setText(""+ConnectionDetailActivity.ConnectionDetail.getConnectionType().getName());
                consumer_type.setText(""+ConnectionDetailActivity.ConnectionDetail.getConsumerType().getName());
            }



            if(ConnectionDetailActivity.ConnectionDetail.getEbBillImageUrl()!="" || ConnectionDetailActivity.ConnectionDetail.getEbBillImageUrl()!="null" )
            {
                Picasso.with(getActivity())
                        .load(ConnectionDetailActivity.ConnectionDetail.getEbBillImageUrl())
                        .resize(100,150)
                        .into(billImg);
                loadBitmapBill(ConnectionDetailActivity.ConnectionDetail.getEbBillImageUrl());

            }

            if(ConnectionDetailActivity.ConnectionDetail.getEbCardImageUrl()!="" || ConnectionDetailActivity.ConnectionDetail.getEbCardImageUrl()!="null" )
            {
                Picasso.with(getActivity())
                        .load(ConnectionDetailActivity.ConnectionDetail.getEbCardImageUrl())
                        .resize(100,150)
                        .into(cardImg);
                loadBitmapCard(ConnectionDetailActivity.ConnectionDetail.getEbCardImageUrl());

            }

         /*   //billiamhgeset
            byte[] decodedString = Base64.decode(ConnectionDetailActivity.ConnectionDetail.getEbBillImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            billImg.setImageBitmap(decodedByte);
            //cardimage set
            byte[] decodedString1 = Base64.decode(ConnectionDetailActivity.ConnectionDetail.getEbCardImage(), Base64.DEFAULT);
            Bitmap decodedByte1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
            cardImg.setImageBitmap(decodedByte1);*/
        }catch(Exception e)
        {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG,""+e.toString());
        }
    }

}

