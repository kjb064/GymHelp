package com.example.android.gymhelp;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import static com.example.android.gymhelp.Constants.DELETE_ID;
import static com.example.android.gymhelp.Constants.EDIT_ID;
import static com.example.android.gymhelp.Constants.READ_FULL_ID;
import static com.example.android.gymhelp.Constants.FLAG_FOR_INCREASE;

public class TargetFragment extends Fragment {

    private ArrayList<Exercise> ex;
    private ExerciseAdapter adapter;
    private DatabaseHelper db;
    private ListView listView;
    public CheckBox checkBox;
    private static String key = "groupID";

    public static TargetFragment createInstance(int TARGET_GROUP_ID){
        TargetFragment fragment = new TargetFragment();
        Bundle args = new Bundle();
        args.putInt(key, TARGET_GROUP_ID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());
    }

    private int getExerciseId(){
        Bundle arguments = getArguments();
        if(arguments != null){
            return arguments.getInt(key);
        }
        else {
            return -1;
        }
    }

    private void fetchData(){ ex = db.getSelectedExercises(getExerciseId()); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.exercise_list, container, false);
        fetchData();

        // Create an {@link ArrayAdapter}, whose data source is a list of Strings. The
        // adapter knows how to create layouts for each item in the list, using the
        // simple_list_item_1.xml layout resource defined in the Android framework.
        // This list item layout contains a single {@link TextView}, which the adapter will set to
        // display a single word.
        adapter = new ExerciseAdapter(getActivity(), ex, R.color.title_color);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml file.
        listView = rootView.findViewById(R.id.list);

        registerForContextMenu(listView);

        // Make the {@link ListView} use the {@link ArrayAdapter} we created above, so that the
        // {@link ListView} will display list items for each word in the list of words.
        // Do this by calling the setAdapter method on the {@link ListView} object and pass in
        // 1 argument, which is the {@link ArrayAdapter} with the variable name itemsAdapter.
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new ExerciseClickListener(listView, getContext(), Integer.toString(getExerciseId())));
        return rootView;
    } // end onCreateView

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int exerciseId = getExerciseId();
        menu.add(exerciseId, READ_FULL_ID, 0, R.string.menu_read_full);
        menu.add(exerciseId, EDIT_ID, 0, R.string.menu_edit);
        menu.add(exerciseId, DELETE_ID, 0, R.string.menu_delete);
        menu.add(exerciseId, FLAG_FOR_INCREASE, 0, R.string.menu_flag_for_increase);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()) {
            if (item.getGroupId() == getExerciseId()) {

                final AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                final Exercise exercise = ex.get((int) info.id);

                switch (item.getItemId()) {
                    case READ_FULL_ID: {
                        Activity activity = getActivity();
                        if(activity != null){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle(exercise.getExerciseName());

                            final View readFullLayout = View.inflate(getContext(), R.layout.read_full, null);
                            builder.setView(readFullLayout);

                            final TextView fullSetsReps = readFullLayout.findViewById(R.id.full_sets_reps);
                            String text = fullSetsReps.getText() + exercise.getSetsAndReps();
                            fullSetsReps.setText(text);

                            final TextView fullWeight = readFullLayout.findViewById(R.id.full_weight);
                            text = fullWeight.getText() + "" + exercise.getRecentWeight();
                            fullWeight.setText(text);

                            final TextView fullDate = readFullLayout.findViewById(R.id.full_date);
                            text = fullDate.getText() + exercise.getDate();
                            fullDate.setText(text);

                            builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }

                        return true;
                    }

                    case EDIT_ID: {
                        Activity activity = getActivity();
                        if(activity != null){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Edit " + exercise.getExerciseName());

                            final View dialogLayout = View.inflate(getContext(), R.layout.add_exercise_dialog, null);
                            builder.setView(dialogLayout);

                            checkBox = dialogLayout.findViewById(R.id.photo_check_box);
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

                            final EditText nameEditText = dialogLayout.findViewById(R.id.name_edit_text);
                            nameEditText.setText(exercise.getExerciseName());
                            final EditText setsRepsEditText = dialogLayout.findViewById(R.id.sets_reps_edit_text);
                            setsRepsEditText.setText(exercise.getSetsAndReps());

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Make sure fields are not blank
                                    if (nameEditText.getText().toString().trim().length() == 0
                                            || setsRepsEditText.getText().toString().trim().length() == 0) {
                                        Toast.makeText(getContext(), "Empty texts fields are not allowed.",
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

                                        // "Refresh" the Fragment once the exercise has been updated
                                        refreshFragment();
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
                        }

                        return true;
                    }

                    case DELETE_ID: {
                        final Activity activity = getActivity();
                        if(activity != null){
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Are you sure you would like to delete " + exercise.getExerciseName() + "?");

                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int id = exercise.getExerciseID();
                                    db.deleteExercise(id);

                                    Toast.makeText(activity,
                                            "DELETED " + exercise.getExerciseName(),
                                            Toast.LENGTH_SHORT).show();
                                    ex.remove((int) info.id);
                                    resetFragmentData();

                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }

                        return true;
                    }
                    case FLAG_FOR_INCREASE:
                        int currentFlag = exercise.getFlaggedForIncrease();
                        int newFlag = currentFlag == 0 ? 1 : 0;
                        exercise.setFlaggedForIncrease(newFlag);
                        db.updateExerciseFlag(exercise);
                        refreshFragment();
                        return true;

                    default:
                        return super.onContextItemSelected(item);
                } // end switch
            } // end if
        } // end if

        return false;
    } // end onContextItemSelected

    /*
    *   "Refreshes" the Fragment by detaching and reattaching it. This method is invoked
    *   after an exercise has been changed using the "Edit" menu option and when the weight
    *   has been updated.
     */
    public void refreshFragment(){
        FragmentManager manager = getFragmentManager();
        if(manager != null){
            Fragment currentFragment = manager.findFragmentByTag(getTag());
            if(currentFragment != null) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.detach(currentFragment);
                fragmentTransaction.attach(currentFragment);
                fragmentTransaction.commit();
            }
            else {
                Log.d("Null fragment", "Current fragment is null");
            }
        }
    } // end refreshFragment

    /*
    *  Retrieves the appropriate data from the database using fetchData and then resets the
    *  listView to reflect the changes that have been made. Used after adding a new exercise
    *  (from the MainActivity) or selecting the "Delete" menu option (this method of resetting
    *  the data has proved to be the most reliable, as other methods sometimes fail).
     */
    public void resetFragmentData(){
        if(getUserVisibleHint()) {
            fetchData();
            adapter.clear();
            adapter.addAll(ex);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
    } // end resetFragmentData
}
