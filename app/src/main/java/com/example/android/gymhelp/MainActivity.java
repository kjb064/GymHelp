package com.example.android.gymhelp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        TextView cutting = (TextView) findViewById(R.id.cutting_btn);
        cutting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cuttingIntent = new Intent(MainActivity.this, CuttingActivity.class);
                startActivity(cuttingIntent);
            }
        });
    }
}
