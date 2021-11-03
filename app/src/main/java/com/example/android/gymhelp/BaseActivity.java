package com.example.android.gymhelp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    // TODO determine if this class is still needed for anything
    protected DatabaseHelper myDb;
    protected TabLayout tabLayout;
    protected TargetAdapter targetAdapter;
    protected ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);
    }
}
