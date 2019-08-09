package com.example.android.gymhelp;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    private ExerciseAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //use the query to search
            DatabaseHelper db = new DatabaseHelper(this);
            ArrayList<Exercise> exercises = db.getQueryResults(query);

            adapter = new ExerciseAdapter(this, exercises, R.color.title_color);
            listView = (ListView) findViewById(R.id.list);

            // registerForContextMenu(listView);

            listView.setAdapter(adapter);

            // Notify if no results??

            // TODO: Possibly abstract the onClickListener for the list items into a class so it can be used here


        }
    }
}
