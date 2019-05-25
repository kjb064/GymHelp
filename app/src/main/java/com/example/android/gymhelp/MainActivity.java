package com.example.android.gymhelp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private TabLayout tabLayout;
    private TargetAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        adapter = new TargetAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tabs
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()
        tabLayout.setupWithViewPager(viewPager);


        /*// Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);


        TextView cutting = (TextView) findViewById(R.id.cutting_btn);
        cutting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cuttingIntent = new Intent(MainActivity.this, CuttingActivity.class);
                startActivity(cuttingIntent);
            }
        });
        */

    } // end onCreate

    public void onClickAddButton(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String tabName = (String) adapter.getPageTitle(tabLayout.getSelectedTabPosition());
        builder.setTitle("Add new " + tabName + " exercise");
        builder.show();
    }
}
