package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.Util;

public class BluetoothPrintTipsActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private float lastX;
    private Button ok_button;
    private boolean isTamil = false;
    String activityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Util.checkAppLanguage(BluetoothPrintTipsActivity.this).equalsIgnoreCase("ta"))
            isTamil = true;

        if (isTamil) {
            setContentView(R.layout.activity_bluetooth_print_tipstamil);
            //   Toast.makeText(ApplianceTipsActivity.this,"tamil",Toast.LENGTH_SHORT).show();
        } else {
            setContentView(R.layout.activity_bluetooth_print_tips);
            //  Toast.makeText(ApplianceTipsActivity.this,"english",Toast.LENGTH_SHORT).show();
        }
        Intent intent = getIntent();
        activityName = intent.getStringExtra("activityName");


        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        ok_button = (Button) findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  if(activityName.equalsIgnoreCase("BillHistroyDetailActivity")){
                    BillHistoryDetailActivity tipsactivity = new BillHistoryDetailActivity();
                    tipsactivity.callprint();
                } else if(activityName.equalsIgnoreCase("PaymentResultActivity")) {
                    PaymentResultActivity payactivity = new PaymentResultActivity();
                    payactivity.callprint();
                }else{
                    PaymentHistorydetail payhistroy =new PaymentHistorydetail();
                    payhistroy.callprint();
                }*/
                finish();
            }
        });
    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            // when user first touches the screen to swap
            case MotionEvent.ACTION_DOWN: {
                lastX = touchevent.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float currentX = touchevent.getX();

                // if left to right swipe on screen
                if (lastX < currentX) {
                    // If no more View/Child to flip
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;

                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Left and current Screen will go OUT from Right
                    viewFlipper.setInAnimation(this, R.anim.in_from_left);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_right);
                    // Show the next Screen
                    viewFlipper.showNext();
                }

                // if right to left swipe on screen
                if (lastX > currentX) {
                    if (viewFlipper.getDisplayedChild() == 1)
                        break;
                    // set the required Animation type to ViewFlipper
                    // The Next screen will come in form Right and current Screen will go OUT from Left
                    viewFlipper.setInAnimation(this, R.anim.in_from_right);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_left);
                    // Show The Previous Screen
                    viewFlipper.showPrevious();
                }
                break;
            }
        }
        return false;
    }
}
