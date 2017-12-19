package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.omneagate.erbc.Activity.Dialog.LogoutDialog;
import com.omneagate.erbc.Dto.CustomerDto;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Util.DBHelper;
import com.omneagate.erbc.Util.Util;

/**
 * Created by user1 on 28/4/16.
 */
public class DashboardActivity extends BaseActivity {

    DrawerLayout  dLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String TAG = DashboardActivity.class.getName();

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.dashboardactivity);
            Configuration config = getApplicationContext().getResources().getConfiguration();


            String languageCode =  GlobalAppState.language;
            TextView    title = (TextView) findViewById(R.id.title_toolbar);
            title.setText(R.string.print);


            setTitle("");
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.outward_size));

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setLogo(R.drawable.ic_menu_white_36dp);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
            getSupportActionBar().setHomeButtonEnabled(true);
            setNavigationDrawer();
            Fragment homepage = new HomepageActivity();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, homepage);
            transaction.commit();

        }catch(Exception e)
        {
            GlobalAppState.getInstance().trackException(e);
        }

/*
        //notification example
        try
        {
            AlarmManager alarms = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
            AlarmReceiver receiver = new AlarmReceiver();
            IntentFilter filter = new IntentFilter("ALARM_ACTION");
            registerReceiver(receiver, filter);
            Intent intent = new Intent("ALARM_ACTION");
            intent.putExtra("param", "My scheduled action");
            PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
            // I choose 3s after the launch of my application
           // alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3000, AlarmManager.INTERVAL_DAY, operation);
          //  alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3000, operation);
          //  alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3000,1000 * 60 * 5, operation);
            Calendar calendar = Calendar.getInstance();
                alarms .setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY*30,
                    AlarmManager.INTERVAL_DAY*30, operation);
        }
        catch(Exception e)
        {
            Log.e("alerm",""+e.toString(),e);
        }
*/
    }

    private void setNavigationDrawer() {

        try
        {
            dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this,dLayout,toolbar,R.string.app_name,R.string.app_name);
            dLayout.setDrawerListener(actionBarDrawerToggle);
            NavigationView navView = (NavigationView) findViewById(R.id.navigation);
            navView.setItemIconTintList(null);
            navView.setItemBackground(getResources().getDrawable(R.drawable.ic_launcher));
            View header=navView.getHeaderView(0);
            TextView profilename = (TextView)header.findViewById(R.id.profilename);
            TextView firstletter = (TextView)header.findViewById(R.id.imageView6);

            CustomerDto customerRecord = DBHelper.getInstance(getApplicationContext()).getcustomerData();
            if(customerRecord != null)
            {
                profilename.setText(""+customerRecord.getFirstName().toUpperCase() + " "+customerRecord.getLastName().toUpperCase());
                char firstLetter = customerRecord.getFirstName().toUpperCase().charAt(0);
                Log.e("firstLetter",""+firstLetter);
                firstletter.setText(""+firstLetter);
            }
            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    Fragment frag = null;
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.logout1) {
                        LogoutDialog alertdialog = new LogoutDialog(DashboardActivity.this,getString(R.string.logoutString));
                        alertdialog.show();
                    }
                    else if (itemId == R.id.profile)
                    {
                        Intent myprofile_page = new Intent(getApplicationContext(), MyProfileActivity.class);
                        myprofile_page.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myprofile_page);
                        finish();
                    }
                   else if (itemId == R.id.reminder)
                    {
                        Intent reminder_page = new Intent(getApplicationContext(), ReminderListActivity.class);
                        startActivity(reminder_page);
                        finish();
                    }
                    else if (itemId == R.id.feedback)
                    {
                        Intent complaint = new Intent(getApplicationContext(), ComplaintListActivity.class);
                        startActivity(complaint);
                        finish();
                    }
                    if (frag != null) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, frag);
                        transaction.commit();
                        dLayout.closeDrawers();
                        return true;
                    }
                    return false;
                }
            });

        }catch (Exception e)
        {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG,""+e.toString(),e);
        }
    }

    @Override
    public void onBackPressed() {
       try
        {
            LogoutDialog alertdialog = new LogoutDialog(DashboardActivity.this,getString(R.string.logoutString));
            alertdialog.show();
        }
        catch(Exception e)
        {
            GlobalAppState.getInstance().trackException(e);
            Log.e(TAG,e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();

    }
}
