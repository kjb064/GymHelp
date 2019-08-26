package com.example.android.gymhelp;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class SearchResultsActivity extends BaseActivity {

    private ExerciseAdapter adapter;
    private ListView listView;
    private String query;
    private ArrayList<Exercise> exercises;
    private DatabaseHelper db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list);
        listView = (ListView) findViewById(R.id.list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);  // Adds "back arrow" to top-left of ActionBar

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            query = query.trim();   // remove any leading/trailing whitespace

            // Use the query to search
            db = new DatabaseHelper(this);
            exercises = db.getQueryResults(query);

            // Link adapter to ListView if results were found. Otherwise, notify if no results were found.
            if(exercises.size() > 0){
                adapter = new ExerciseAdapter(this, exercises, R.color.title_color);

                registerForContextMenu(listView);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new ExerciseClickListener(listView, this, Constants.NO_FRAGMENT_ID));
            }
            else{
                Toast.makeText(getApplicationContext(), "No results for query '" + query + "'.", Toast.LENGTH_SHORT).show();
            }

        }
    } // end onCreate

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Finish (close) this activity once the back button (arrow) is selected from the ActionBar
        finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, Constants.READ_FULL_ID, 0, R.string.menu_read_full);
        menu.add(0, Constants.EDIT_ID, 0, R.string.menu_edit);
        menu.add(0, Constants.DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Exercise exercise = exercises.get((int) info.id);     // get the exercise selected

        switch (item.getItemId()){
            case Constants.READ_FULL_ID: {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(exercise.getExerciseName());

                LayoutInflater inflater = getLayoutInflater();
                final View readFullLayout = inflater.inflate(R.layout.read_full, null);
                builder.setView(readFullLayout);

                final TextView fullSetsReps = (TextView) readFullLayout.findViewById(R.id.full_sets_reps);
                String text = fullSetsReps.getText() + exercise.getSetsAndReps();
                fullSetsReps.setText(text);

                final TextView fullWeight = (TextView) readFullLayout.findViewById(R.id.full_weight);
                text = fullWeight.getText() + "" + exercise.getRecentWeight();
                fullWeight.setText(text);

                final TextView fullDate = (TextView) readFullLayout.findViewById(R.id.full_date);
                text = fullDate.getText() + exercise.getDate();
                fullDate.setText(text);

                builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                return true;
            }

            case Constants.EDIT_ID: {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Edit " + exercise.getExerciseName());

                LayoutInflater inflater = getLayoutInflater();
                dialogLayout = inflater.inflate(R.layout.add_exercise_dialog, null);
                builder.setView(dialogLayout);

                checkBox = (CheckBox) dialogLayout.findViewById(R.id.photo_check_box);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            db.deleteExerciseImage(exercise);
                            exercise.setImageResourcePath(Constants.NO_IMAGE_PROVIDED);
                            currentPhotoPath = "";
                            checkBox.setClickable(false);
                            checkBox.setText(R.string.no_photo_selected);
                        }
                        else {
                            checkBox.setClickable(true);
                            checkBox.setText(R.string.photo_selected);
                        }

                    }
                });

                if(exercise.hasImagePath()){
                    checkBox.setChecked(true);
                }

                final EditText nameEditText = (EditText) dialogLayout.findViewById(R.id.name_edit_text);
                nameEditText.setText(exercise.getExerciseName());
                final EditText setsRepsEditText = (EditText) dialogLayout.findViewById(R.id.sets_reps_edit_text);
                setsRepsEditText.setText(exercise.getSetsAndReps());

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Make sure fields are not blank
                        if (nameEditText.getText().toString().trim().length() == 0
                                || setsRepsEditText.getText().toString().trim().length() == 0) {
                            Toast.makeText(getApplicationContext(), "Empty texts fields are not allowed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Exercise newExercise = new Exercise(nameEditText.getText().toString(),
                                    setsRepsEditText.getText().toString(),
                                    exercise.getExerciseTarget());
                            newExercise.setExerciseID(exercise.getExerciseID());

                            // If a new photo has been selected/taken, update the path too.
                            if (!currentPhotoPath.isEmpty()) {
                                newExercise.setImageResourcePath(currentPhotoPath);
                                currentPhotoPath = "";
                            } else {
                                // Otherwise, leave the path the same.
                                newExercise.setImageResourcePath(exercise.getImageResourcePath());
                            }

                            db.updateExercise(newExercise);

                            // "Refresh" the Activity once the exercise has been updated
                            refreshSearchResults();
                        }

                    }
                });

                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                return true;
            }

            case Constants.DELETE_ID: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure you would like to delete " + exercise.getExerciseName() + "?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = exercise.getExerciseID();
                        db.deleteExercise(id);

                        Toast.makeText(getApplicationContext(),
                                "DELETED " + exercise.getExerciseName(),
                                Toast.LENGTH_SHORT).show();
                        exercises.remove((int) info.id);
                        refreshSearchResults();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    } // end onContextItemSelected


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            if(dialogLayout != null) {
                checkBox.setChecked(true);
            }
        }
        // TODO: Verify the selected file is a valid type?
        else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            /*The result returns the Uri ("address") of the selected picture. */
            Uri imageUri = data.getData();
            currentPhotoPath = getPath(imageUri);

            if(dialogLayout != null) {
                checkBox.setChecked(true);
            }
        }
    } // end onActivityResult

    /*
    * Called when the ListView of this Activity needs to be updated to reflect a change in the data
    * displayed.
     */
    public void refreshSearchResults(){
        exercises = db.getQueryResults(query);
        adapter.clear();
        adapter.addAll(exercises);
        adapter.notifyDataSetChanged();
        listView.invalidate();
    } // end refreshSearchResults
}
