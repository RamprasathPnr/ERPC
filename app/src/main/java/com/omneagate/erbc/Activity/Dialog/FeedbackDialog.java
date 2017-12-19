package com.omneagate.erbc.Activity.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.omneagate.erbc.Activity.ComplaintListActivity;
import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Dto.EnumDto.FeedBackType;
import com.omneagate.erbc.Dto.GrievanceDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.NetworkConnection;

/**
 * Created by user1 on 21/7/16.
 */

public class FeedbackDialog extends Dialog implements View.OnClickListener {

    public final Context context;
    String rating, command;
    CustomProgressDialog progressBar;
    NetworkConnection networkConnection;
    private static final String TAG = FeedbackDialog.class.getName();
    HttpClientWrapper httpConnection;
    GrievanceDto grievencerecord;
    EditText commands_txt;
    RatingBar rattings;
    private Activity activity;

    public FeedbackDialog(Context context, GrievanceDto grivancedata, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.grievencerecord = grivancedata;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            setContentView(R.layout.feedbackdialog);
            networkConnection = new NetworkConnection(context);
            httpConnection = new HttpClientWrapper();
            setCancelable(false);
            ((TextView) findViewById(R.id.textViewNwText)).setText
                    (context.getString(R.string.your_complaint) + grievencerecord.getGrievanceNumber()
                            + " "+context.getString(R.string.resolved));
            Button okButton = (Button) findViewById(R.id.buttonNwOk);
            Button cancelButton = (Button) findViewById(R.id.buttoncancel);
            commands_txt = (EditText) findViewById(R.id.commands);
            rattings = (RatingBar) findViewById(R.id.ratingBar);
            cancelButton.setOnClickListener(this);
            okButton.setOnClickListener(this);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("Error", e.toString(), e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                feedbackSendServer();
                break;
            case R.id.buttoncancel:
                dismiss();
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void feedbackSendServer() {
        rating = String.valueOf(rattings.getRating());
        command = commands_txt.getText().toString();

        if (rating.equalsIgnoreCase("0.0")) {
            Toast.makeText(context, "" + context.getString(R.string.rating_error), Toast.LENGTH_SHORT).show();
            return;
        }
        if (rating.equalsIgnoreCase("")) {
            Toast.makeText(context, "" + context.getString(R.string.comments_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("command", "" + command);
        Log.e("rating", "" + rating);
        dismiss();
        grievencerecord.setFeedbackComments(command);
        if (rating.equalsIgnoreCase("1.0")) {
            grievencerecord.setFeedbackType(FeedBackType.BAD);
        } else if (rating.equalsIgnoreCase("2.0")) {
            grievencerecord.setFeedbackType(FeedBackType.AVERAGE);
        } else if (rating.equalsIgnoreCase("3.0")) {
            grievencerecord.setFeedbackType(FeedBackType.GOOD);
        } else if (rating.equalsIgnoreCase("4.0")) {
            grievencerecord.setFeedbackType(FeedBackType.VERYGOOD);
        } else {
            grievencerecord.setFeedbackType(FeedBackType.EXCELLENT);
        }
        String feedbackData = new Gson().toJson(grievencerecord);
        Log.e("Sham Feedback value:::", feedbackData);
        if (activity instanceof ComplaintListActivity) {
            ((ComplaintListActivity) activity).feedbackSend(feedbackData);
        }
        //context.feedbackSend(feedbackData);
    }
}
