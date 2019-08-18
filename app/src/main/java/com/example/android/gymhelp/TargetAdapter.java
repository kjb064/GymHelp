package com.example.android.gymhelp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TargetAdapter extends FragmentStatePagerAdapter {
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
        MainActivity activity = (MainActivity) mContext;

        if (position == Constants.CHEST) {
            TargetFragment fragment = TargetFragment.createInstance(Constants.CHEST);
            activity.getSupportFragmentManager().beginTransaction().add(fragment, Integer.toString(Constants.CHEST));
            return fragment;
        } else if (position == Constants.LEGS) {
            TargetFragment fragment = TargetFragment.createInstance(Constants.LEGS);
            activity.getSupportFragmentManager().beginTransaction().add(fragment, Integer.toString(Constants.LEGS));
            return fragment;
        } else if (position == Constants.BACK) {
            TargetFragment fragment = TargetFragment.createInstance(Constants.BACK);
            activity.getSupportFragmentManager().beginTransaction().add(fragment, Integer.toString(Constants.BACK));
            return fragment;
        } else if (position == Constants.SHOULDERS) {
            TargetFragment fragment = TargetFragment.createInstance(Constants.SHOULDERS);
            activity.getSupportFragmentManager().beginTransaction().add(fragment, Integer.toString(Constants.SHOULDERS));
            return fragment;
        } else if (position == Constants.ARMS){
            TargetFragment fragment = TargetFragment.createInstance(Constants.ARMS);
            activity.getSupportFragmentManager().beginTransaction().add(fragment, Integer.toString(Constants.ARMS));
            return fragment;
        } else if (position == Constants.ABS){
            TargetFragment fragment = TargetFragment.createInstance(Constants.ABS);
            activity.getSupportFragmentManager().beginTransaction().add(fragment, Integer.toString(Constants.ABS));
            return fragment;
        }
        else {
            TargetFragment fragment = TargetFragment.createInstance(Constants.COMPOUND);
            activity.getSupportFragmentManager().beginTransaction().add(fragment, Integer.toString(Constants.COMPOUND));
            return fragment;
        }

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
        } else if (position == Constants.ARMS){
            return mContext.getString(R.string.target_arms);
        } else if (position == Constants.ABS){
            return mContext.getString(R.string.target_abs);
        }
        else { return mContext.getString(R.string.target_compound);}
    }
}
