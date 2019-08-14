package com.example.android.gymhelp;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    // TODO: Implement search suggestions

    private ExerciseAdapter adapter;
    private ListView listView;
    private String query;
    private ArrayList<Exercise> exercises;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            //use the query to search
            DatabaseHelper db = new DatabaseHelper(this);
            exercises = db.getQueryResults(query);

            adapter = new ExerciseAdapter(this, exercises, R.color.title_color);
            listView = (ListView) findViewById(R.id.list);

            // registerForContextMenu(listView);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new ExerciseClickListener(listView, this, Constants.NO_FRAGMENT_ID));

            // Notify if no results??



        }
    } // end onCreate

    /*
    * Called when the ListView of this Activity needs to be updated to reflect a change in the data
    * displayed.
     */
    public void refreshSearchResults(){
        DatabaseHelper db = new DatabaseHelper(this);
        exercises = db.getQueryResults(query);
        adapter.clear();
        adapter.addAll(exercises);
        adapter.notifyDataSetChanged();
        listView.invalidate();
    } // end refreshSearchResults
}
