package com.omneagate.erbc.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.omneagate.erbc.Fragment.BillHistoryFragment;
import com.omneagate.erbc.Fragment.ComplaintsFragment;
import com.omneagate.erbc.Fragment.PaymentFragment;
import com.omneagate.erbc.Fragment.TariffDetailFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shanthakumar on 18-07-2016.
 */
public class ViewPagerLandingAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public ViewPagerLandingAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(new ComplaintsFragment());
        fragments.add(new BillHistoryFragment());
        fragments.add(new TariffDetailFragment());
        fragments.add(new PaymentFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /**
     * Get the current fragment
     */
    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}

