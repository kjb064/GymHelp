package com.example.android.gymhelp;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

    // TODO: Implement search suggestions

    private ExerciseAdapter adapter;
    private ListView listView;
    private String query;
    private ArrayList<Exercise> exercises;
    private DatabaseHelper db;
    private CheckBox checkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            //use the query to search
            db = new DatabaseHelper(this);
            exercises = db.getQueryResults(query);

            adapter = new ExerciseAdapter(this, exercises, R.color.title_color);
            listView = (ListView) findViewById(R.id.list);

            registerForContextMenu(listView);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new ExerciseClickListener(listView, this, Constants.NO_FRAGMENT_ID));

            // TODO: Notify if no results??


        }
    } // end onCreate

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
        final Exercise exercise = exercises.get((int) info.id);

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
                final View dialoglayout = inflater.inflate(R.layout.add_exercise_dialog, null);
                builder.setView(dialoglayout);

                checkBox = (CheckBox) dialoglayout.findViewById(R.id.photo_check_box);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            db.deleteExerciseImage(exercise);
                            exercise.setImageResourcePath(Constants.NO_IMAGE_PROVIDED);
                            MainActivity.currentPhotoPath = "";
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

                final EditText nameEditText = (EditText) dialoglayout.findViewById(R.id.name_edit_text);
                nameEditText.setText(exercise.getExerciseName());
                final EditText setsRepsEditText = (EditText) dialoglayout.findViewById(R.id.sets_reps_edit_text);
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
                            if (!MainActivity.currentPhotoPath.isEmpty()) {
                                newExercise.setImageResourcePath(MainActivity.currentPhotoPath);
                                MainActivity.currentPhotoPath = "";
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

    /*
    * Called when the ListView of this Activity needs to be updated to reflect a change in the data
    * displayed.
     */
    public void refreshSearchResults(){
        //DatabaseHelper db = new DatabaseHelper(this);
        exercises = db.getQueryResults(query);
        adapter.clear();
        adapter.addAll(exercises);
        adapter.notifyDataSetChanged();
        listView.invalidate();
    } // end refreshSearchResults
}
