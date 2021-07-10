package com.example.android.gymhelp;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultsActivity extends BaseActivity {

    // TODO class needs to be totally reworked

    // TODO consider deleting this class and just use search feature as a filter if possible?

    private ExerciseAdapter adapter;
    private ListView listView;
    private String query;
    private ArrayList<Exercise> exercises;
    private DatabaseHelper db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list);
        listView = findViewById(R.id.list);

        ActionBar actionBar = getSupportActionBar();

        // Add "back arrow" to top-left of ActionBar
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            query = query.trim();   // remove any leading/trailing whitespace

            // Use the query to search
            db = new DatabaseHelper(this);
            exercises = db.getQueryResults(query);

            // Link adapter to ListView if results were found. Otherwise, notify if no results were found.
            if (!exercises.isEmpty()) {
                adapter = new ExerciseAdapter(this, exercises);

                registerForContextMenu(listView);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new ExerciseClickListener(this) {
                    @Override
                    protected void refresh() {
                        refreshSearchResults();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "No results for query '" + query + "'.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Finish (close) this activity once the back button (arrow) is selected from the ActionBar
        finish();
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.add(0, Constants.READ_FULL_ID, 0, R.string.menu_read_full);
//        menu.add(0, Constants.EDIT_ID, 0, R.string.menu_edit);
//        menu.add(0, Constants.DELETE_ID, 0, R.string.menu_delete);
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        final AdapterView.AdapterContextMenuInfo info =
//                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        final Exercise exercise = exercises.get((int) info.id);     // get the exercise selected
//
//        switch (item.getItemId()) {
//            case Constants.READ_FULL_ID: {
//                TargetFragment.displayReadFullDialog(exercise, this, getLayoutInflater());
//                return true;
//            }
//
//            // TODO call static method in TargetFragment once issue with checkbox is resolved
//            case Constants.EDIT_ID: {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Edit " + exercise.getExerciseName());
//
//                LayoutInflater inflater = getLayoutInflater();
//                dialogLayout = inflater.inflate(R.layout.add_exercise_dialog, null);
//                builder.setView(dialogLayout);
//
//                checkBox = dialogLayout.findViewById(R.id.photo_check_box);
//                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                    if (!isChecked) {
//                        db.deleteExerciseImage(exercise);
//                        exercise.setImageResourcePath(Constants.NO_IMAGE_PROVIDED);
//                        currentPhotoPath = "";
//                        checkBox.setClickable(false);
//                        checkBox.setText(R.string.no_photo_selected);
//                    } else {
//                        checkBox.setClickable(true);
//                        checkBox.setText(R.string.photo_selected);
//                    }
//                });
//
//                if (exercise.hasImagePath()) {
//                    checkBox.setChecked(true);
//                }
//
//                final EditText nameEditText = dialogLayout.findViewById(R.id.name_edit_text);
//                nameEditText.setText(exercise.getExerciseName());
//                final EditText setsRepsEditText = dialogLayout.findViewById(R.id.sets_reps_edit_text);
//                setsRepsEditText.setText(exercise.getSetsAndReps());
//
//                builder.setPositiveButton("OK", (dialog, which) -> {
//                    // Make sure fields are not blank
//                    if (nameEditText.getText().toString().trim().length() == 0
//                            || setsRepsEditText.getText().toString().trim().length() == 0) {
//                        Toast.makeText(getApplicationContext(), "Empty texts fields are not allowed.",
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        Exercise newExercise = new Exercise(nameEditText.getText().toString(),
//                                setsRepsEditText.getText().toString(),
//                                exercise.getRecentWeight(),
//                                exercise.getExerciseTarget());
//                        newExercise.setExerciseID(exercise.getExerciseID());
//
//                        // If a new photo has been selected/taken, update the path too.
//                        if (!currentPhotoPath.isEmpty()) {
//                            newExercise.setImageResourcePath(currentPhotoPath);
//                            currentPhotoPath = "";
//                        } else {
//                            // Otherwise, leave the path the same.
//                            newExercise.setImageResourcePath(exercise.getImageResourcePath());
//                        }
//
//                        // TODO can reduce complexity by just passing all properties to set instead of Exercise object?
//                        db.updateExercise(newExercise);
//
//                        // "Refresh" the Activity once the exercise has been updated
//                        refreshSearchResults();
//                    }
//                });
//
//                builder.setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss());
//
//                builder.show();
//                return true;
//            }
//
//            case Constants.DELETE_ID: {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Are you sure you would like to delete " + exercise.getExerciseName() + "?");
//
//                builder.setPositiveButton("Delete", (dialog, which) -> {
//                    int id = exercise.getExerciseID();
//                    db.deleteExercise(id);
//
//                    Toast.makeText(getApplicationContext(),
//                            "DELETED " + exercise.getExerciseName(),
//                            Toast.LENGTH_SHORT).show();
//                    exercises.remove((int) info.id);
//                    refreshSearchResults();
//                });
//
//                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//
//                builder.show();
//                return true;
//            }
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            if (dialogLayout != null) {
//                checkBox.setChecked(true);
//            }
//        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
//            // TODO: Verify the selected file is a valid type?
//            /*The result returns the Uri ("address") of the selected picture. */
//            Uri imageUri = data.getData();
//            currentPhotoPath = getPath(imageUri);
//
//            if (dialogLayout != null) {
//                checkBox.setChecked(true);
//            }
//        }
    }

    /**
     * Called when the ListView of this Activity needs to be updated to reflect a change in the data
     * displayed.
     */
    public void refreshSearchResults() {
        exercises = db.getQueryResults(query);
        adapter.clear();
        adapter.addAll(exercises);
        adapter.notifyDataSetChanged();
        listView.invalidate();
    }
}
