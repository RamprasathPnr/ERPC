package com.omneagate.erbc.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.erbc.Activity.ComplaintsRegisterActivity;
import com.omneagate.erbc.Activity.Dialog.FeedbackDialog;
import com.omneagate.erbc.Activity.GlobalAppState;
import com.omneagate.erbc.Dto.EnumDto.GrievanceStatus;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.GrievanceDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Dto.grievanceListDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.CustomProgressDialog;
import com.omneagate.erbc.Util.DBConstants;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.NetworkConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Shanthakumar on 18-07-2016.
 */
public class ComplaintsFragment extends Fragment {

    private AppAdapter mAdapter = new AppAdapter();
    CustomProgressDialog progressBar;
    private static final String TAG = ComplaintsFragment.class.getName();
    ListView list;
    List<GrievanceDto> grievanceDtoList;
    private NetworkConnection networkConnection;
    private HttpClientWrapper httpConnection;
    View v;

    public ComplaintsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Complaint");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_complaints, container, false);
        list = (ListView) v.findViewById(R.id.listView);
        TextView nodatefound = (TextView) v.findViewById(R.id.textView5);
        nodatefound.setText(getString(R.string.nocomplaint_found));
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        networkConnection = new NetworkConnection(getActivity());
        httpConnection = new HttpClientWrapper();
        complaintList();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ComplaintsRegisterActivity.class));
                getActivity().finish();
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (grievanceDtoList.get(position).getGrievanceStatus() == GrievanceStatus.RESOLVED) {
                    FeedbackDialog feedbackdialog = new FeedbackDialog(getActivity(),
                            grievanceDtoList.get(position), getActivity());
                    feedbackdialog.show();
                }
            }
        });
        return v;

    }

    private void complaintList() {
        try {
            progressBar = new CustomProgressDialog(getActivity());
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                JSONObject requestObject = new JSONObject();
                requestObject.put("id", DBHelper.getInstance(getActivity()).getCustomerId());
                String login = new Gson().toJson(requestObject);
                Log.e("id", "" + login);
                StringEntity se = new StringEntity(requestObject.toString(), HTTP.UTF_8);
                String url = "/grievance/list";
                httpConnection.sendRequest(url, null, ServiceListenerType.GRIEVANCE_LIST, SyncHandler, RequestType.POST, se, getActivity());
            } else {
                dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }

    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
        }
    }

    /*Handler used to get response from server*/
    protected final Handler SyncHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("Sham Enter the handleMessage method :::");

            ServiceListenerType type = (ServiceListenerType) msg.obj;
            String response = msg.getData().getString(DBConstants.RESPONSE_DATA);

            switch (type) {
                case GRIEVANCE_LIST:
                    dismissProgress();
                    complaintResponse(response);
                    break;

                case GRIEVANCE_FEEDBACK:
                    dismissProgress();
                    grievanceFeedbackResponse(response);
                    break;
                default:
                    dismissProgress();
                    Toast.makeText(getActivity(), getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void complaintResponse(String response) {
        try {
            //   String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            grievanceListDto complaintListResponse = gson.fromJson(response, grievanceListDto.class);

            if (complaintListResponse != null) {
                if (complaintListResponse.getStatusCode() == 0) {
                    ((ImageView) v.findViewById(R.id.noconnection)).setVisibility(View.INVISIBLE);
                    ((TextView) v.findViewById(R.id.textView5)).setVisibility(View.INVISIBLE);
                    grievanceDtoList = complaintListResponse.getGrievanceListDto();
                    list.setAdapter(mAdapter);
                } else {
                    Toast.makeText(getActivity(), "" + complaintListResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }


    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return grievanceDtoList.size();
        }

        @Override
        public GrievanceDto getItem(int position) {
            return grievanceDtoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public int getViewTypeCount() {
            // menu type count
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                if (convertView == null) {
                    convertView = View.inflate(getActivity(), R.layout.complaint_row, null);
                    new ViewHolder(convertView);
                }
                ViewHolder holder = (ViewHolder) convertView.getTag();
                holder.laycolor = (LinearLayout) convertView.findViewById(R.id.cdays);
                holder.status_icon = (ImageView) convertView.findViewById(R.id.image_id);
                holder.complaint_number = (TextView) convertView.findViewById(R.id.cl_number);
                holder.compalint_date = (TextView) convertView.findViewById(R.id.dateid);
                holder.compalint_category = (TextView) convertView.findViewById(R.id.category_name);
                holder.complaint_description = (TextView) convertView.findViewById(R.id.connectionname);
                holder.compalint_status = (TextView) convertView.findViewById(R.id.month_year);
                holder.compalint_category.setText("" + grievanceDtoList.get(position).getGrievanceCategory().getName());
                holder.compalint_date.setText("" + grievanceDtoList.get(position).getCreatedDate());
                holder.complaint_number.setText("#CL " + grievanceDtoList.get(position).getGrievanceNumber());
                holder.complaint_description.setText("" + grievanceDtoList.get(position).getGrievanceCategory().getName());
                holder.compalint_status.setText("" + grievanceDtoList.get(position).getGrievanceStatus());

                if (grievanceDtoList.get(position).getGrievanceStatus() == GrievanceStatus.PENDING) {
                    holder.status_icon.setImageResource(R.drawable.pending);
                    holder.laycolor.setBackgroundResource(R.color.cancelButtonColor);
                    holder.compalint_status.setBackground(getResources().getDrawable(R.drawable.bluecolor_border));
                } else if (grievanceDtoList.get(position).getGrievanceStatus() == GrievanceStatus.INPROGRESS) {
                    holder.status_icon.setImageResource(R.drawable.inprogress);
                    holder.laycolor.setBackgroundResource(R.color.login_posAppName);
                    holder.compalint_status.setBackground(getResources().getDrawable(R.drawable.border_color));

                } else if (grievanceDtoList.get(position).getGrievanceStatus() == GrievanceStatus.RESOLVED) {
                    holder.status_icon.setImageResource(R.drawable.completed);
                    holder.laycolor.setBackgroundResource(R.color.greencolor);
                    holder.compalint_status.setBackground(getResources().getDrawable(R.drawable.greencolor_border));

                } else if (grievanceDtoList.get(position).getGrievanceStatus() == GrievanceStatus.CANCELLED) {
                    holder.status_icon.setImageResource(R.drawable.completed);
                    holder.laycolor.setBackgroundResource(R.color.red);
                    holder.compalint_status.setBackground(getResources().getDrawable(R.drawable.redcolor_border));
                }

            } catch (Exception e) {
                GlobalAppState.getInstance().trackException(e);
                Log.e("listviewException", e.toString(), e);
            }
            return convertView;
        }

        class ViewHolder {

            public TextView complaint_number, compalint_date, compalint_category, complaint_description, compalint_status;
            public ImageView status_icon;
            public LinearLayout laycolor;

            public ViewHolder(View view) {
                view.setTag(this);
            }
        }
    }


    private void grievanceFeedbackResponse(String response) {
        try {

            // String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            grievanceListDto feedbackResponse = gson.fromJson(response, grievanceListDto.class);
            if (feedbackResponse != null) {
                if (feedbackResponse.getStatusCode() == 0) {

                    Toast.makeText(getActivity(), "" + feedbackResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "" + feedbackResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString(), e);
        }

    }

    public void feedbackSend(String feedbackData) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            GrievanceDto feedbacksendData = gson.fromJson(feedbackData, GrievanceDto.class);
            Log.e("feedbackdto", "" + feedbacksendData);
            progressBar = new CustomProgressDialog(getActivity());
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                progressBar.show();
                String login = new Gson().toJson(feedbacksendData);
                StringEntity se = new StringEntity(login.toString(), HTTP.UTF_8);
                String url = "/grievance/feedback";
                httpConnection.sendRequest(url, null, ServiceListenerType.GRIEVANCE_FEEDBACK, SyncHandler, RequestType.POST, se, getActivity());
            } else {
                dismissProgress();
                Toast.makeText(getActivity(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG, e.toString(), e);
        }
    }

    private void grievanceFeedbackResponse(Bundle message) {
        try {

            String response = message.getString(DBConstants.RESPONSE_DATA);
            Log.e("response_data", "" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            grievanceListDto feedbackResponse = gson.fromJson(response, grievanceListDto.class);
            if (feedbackResponse != null) {
                if (feedbackResponse.getStatusCode() == 0) {

                    Toast.makeText(getActivity(), "" + feedbackResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "" + feedbackResponse.getErrorDescription(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.connectionError), Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("error", e.toString(), e);
        }

    }
    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        GlobalAppState.getInstance().trackScreenView("Complaints Fragment");
    }


}
