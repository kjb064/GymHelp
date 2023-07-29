package com.example.android.gymhelp;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

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
