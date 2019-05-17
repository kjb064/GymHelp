package com.example.android.gymhelp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TargetAdapter extends FragmentPagerAdapter {
    /** Context of the app */
    private Context mContext;

    /**
     * Create a new {@link TargetAdapter} object.
     *
     * @param context is the context of the app
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public TargetAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        if (position == Constants.CHEST) {
            return new ChestFragment();
        } else if (position == Constants.LEGS) {
            return new LegsFragment();
        } else if (position == Constants.BACK) {
            return new BackFragment();
        } else if (position == Constants.SHOULDERS) {
            return new ShouldersFragment();
        } else {    // ADD THIS WHEN ABS READY:  if (position == Constants.ABS)
            return new ArmsFragment();
        } // else { return new AbsFragment(); }


    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return Constants.NUM_TARGETS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == Constants.CHEST) {
            return mContext.getString(R.string.target_chest);
        } else if (position == Constants.LEGS) {
            return mContext.getString(R.string.target_legs);
        } else if (position == Constants.BACK) {
            return mContext.getString(R.string.target_back);
        } else if (position == Constants.SHOULDERS) {
            return mContext.getString(R.string.target_shoulders);
        } else {    // ADD THIS WHEN ABS READY:  if (position == Constants.ABS)
            return mContext.getString(R.string.target_arms);
        } //else { return mContext.getString(R.string.target_abs); }
    }
}
