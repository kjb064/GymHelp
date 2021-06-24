package com.example.android.gymhelp;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

// TODO add class javadoc
public class TargetAdapter extends FragmentStatePagerAdapter {
    /** Context of the app */
    private final Context mContext;

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
     *
     * @param position the position of the item
     */
    @Override
    public Fragment getItem(int position) {
        return TargetFragment.createInstance(position);
    }

    /**
     * Return the total number of pages (num pages == num targets).
     */
    @Override
    public int getCount() {
        return Constants.NUM_TARGETS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case Constants.CHEST:
                title = mContext.getString(R.string.target_chest);
                break;
            case Constants.LEGS:
                title = mContext.getString(R.string.target_legs);
                break;
            case Constants.BACK:
                title = mContext.getString(R.string.target_back);
                break;
            case Constants.SHOULDERS:
                title = mContext.getString(R.string.target_shoulders);
                break;
            case Constants.ARMS:
                title = mContext.getString(R.string.target_arms);
                break;
            case Constants.ABS:
                title = mContext.getString(R.string.target_abs);
                break;
            case Constants.COMPOUND:
                title = mContext.getString(R.string.target_compound);
                break;
            default:
                title = "";
                Log.e("getItem()", "Value of 'position' did not match a valid " +
                        "value; returning empty string.");
                break;
        }
        return title;
    }
}
