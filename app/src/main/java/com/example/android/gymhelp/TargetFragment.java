package com.example.android.gymhelp;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.android.gymhelp.Constants.DELETE_ID;
import static com.example.android.gymhelp.Constants.EDIT_ID;
import static com.example.android.gymhelp.Constants.READ_FULL_ID;

/**
 * Displays a ListView containing all exercises associated with a target group.
 * Allows for certain operations (such as deleting or updating) to be performed on an exercise in the ListView
 * via a context menu.
 */
public class TargetFragment extends Fragment {

    private ArrayList<Exercise> exercises;
    private ExerciseAdapter adapter;
    private DatabaseHelper db;
    private ListView listView;
    /** TODO make checkBox private/local to relevant method(s) */
    public CheckBox checkBox;
    private static final String GROUP_ID_KEY = "groupID";

    /**
     * Creates a TargetFragment instance with the value of <code>targetGroupId</code> passed as one
     * of its arguments. The value of <code>targetGroupId</code> should match the index of the Fragment
     * within the ViewPager.
     *
     * @param targetGroupId the ID of the Target associated with the new Fragment
     * @return the new TargetFragment instance
     */
    public static TargetFragment createInstance(int targetGroupId) {
        TargetFragment fragment = new TargetFragment();
        Bundle args = new Bundle();
        args.putInt(GROUP_ID_KEY, targetGroupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());
    }

    /**
     * Fetches the exercises to be displayed in this TargetFragment's ListView.
     */
    private void fetchData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int targetID = bundle.getInt(GROUP_ID_KEY);
            exercises = db.getSelectedExercises(targetID);
        }
    }

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
        adapter = new ExerciseAdapter(getActivity(), exercises, R.color.title_color);

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
        listView.setOnItemClickListener(new ExerciseClickListener(getContext()) {
            @Override
            protected void refresh() {
                refreshFragment();
            }
        });
        return rootView;
    }

    /**
     * Checks this TargetFragment's arguments for the value of the target group ID and returns it.
     *
     * @return the value of the target group ID obtained from the arguments or -1 if there were no
     *         arguments
     */
    private int getTargetGroupId() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle.getInt(GROUP_ID_KEY);
        } else {
            Log.e("getGroupId()", "TargetFragment did not have arguments; " +
                    "returning -1.");
            // TODO add -1 to Constants?
            return -1;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int groupID = getTargetGroupId();
        menu.add(groupID, READ_FULL_ID, 0, R.string.menu_read_full);
        menu.add(groupID, EDIT_ID, 0, R.string.menu_edit);
        menu.add(groupID, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO find better way to determine whether the fragment is visible? (getUserVisibleHint() may not be reliable...)
        final boolean isFragmentVisibleToUser = getUserVisibleHint();
        if (isFragmentVisibleToUser) {
            Activity activity = getActivity();
            if (item.getGroupId() == getTargetGroupId() && activity != null) {
                final AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final Exercise exercise = exercises.get((int) info.id);

                switch (item.getItemId()) {
                    case READ_FULL_ID: {
                        displayReadFullDialog(exercise, activity);
                        return true;
                    }
                    case EDIT_ID: {
                        displayEditDialog(exercise, activity);
                        return true;
                    }
                    case DELETE_ID: {
                        displayDeleteDialog(exercise, activity);
                        return true;
                    }
                    default:
                        return super.onContextItemSelected(item);
                }
            }
        }

        return false;
    }

    /**
     * Creates and displays the "Read Full" dialog where all of the relevant details of the selected
     * exercise are displayed.
     *
     * @param exercise whose details to display in the dialog
     * @param activity the Activity this TargetFragment is associated with
     */
    private void displayReadFullDialog(final Exercise exercise, final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(exercise.getExerciseName());

        LayoutInflater inflater = getLayoutInflater();
        final View readFullLayout = inflater.inflate(R.layout.read_full, null);
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

        builder.setPositiveButton("Dismiss", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    /**
     * Creates and displays the "Edit" dialog where the user can update the details of the selected
     * exercise.
     *
     * @param exercise the exercise whose details are displayed for editing in the dialog
     * @param activity the Activity this TargetFragment is associated with
     */
    private void displayEditDialog(final Exercise exercise, Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Edit " + exercise.getExerciseName());

        LayoutInflater inflater = getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.add_exercise_dialog, null);
        builder.setView(dialogLayout);

        checkBox = dialogLayout.findViewById(R.id.photo_check_box);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkBox.setClickable(isChecked);
            if (!isChecked) {
                db.deleteExerciseImage(exercise);
                exercise.setImageResourcePath(Constants.NO_IMAGE_PROVIDED);
                MainActivity.currentPhotoPath = "";
                checkBox.setText(R.string.no_photo_selected);
            } else {
                checkBox.setText(R.string.photo_selected);
            }

        });

        if (exercise.hasImagePath()) {
            checkBox.setChecked(true);
        }

        final EditText nameEditText = dialogLayout.findViewById(R.id.name_edit_text);
        nameEditText.setText(exercise.getExerciseName());
        final EditText setsRepsEditText = dialogLayout.findViewById(R.id.sets_reps_edit_text);
        setsRepsEditText.setText(exercise.getSetsAndReps());

        builder.setPositiveButton("OK", (dialog, which) -> {
            final String name = nameEditText.getText().toString().trim();
            final String setsReps = setsRepsEditText.getText().toString().trim();

            // Make sure fields are not blank
            if (name.isEmpty() || setsReps.isEmpty()) {
                Toast.makeText(getContext(), "Empty texts fields are not allowed.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Exercise newExercise = new Exercise(name, setsReps,
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
        });

        builder.setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    /**
     * Creates and displays the "Delete" dialog where the user can confirm or deny the deletion
     * of the selected exercise.
     *
     * @param exercise the exercise that may be deleted
     * @param activity the Activity this TargetFragment is associated with
     */
    private void displayDeleteDialog(final Exercise exercise, Activity activity) {
        final String exerciseName = exercise.getExerciseName();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Are you sure you would like to delete " + exerciseName + "?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            int id = exercise.getExerciseID();
            db.deleteExercise(id);

            Toast.makeText(getActivity(),
                    "DELETED " + exerciseName,
                    Toast.LENGTH_SHORT).show();

            exercises.remove(exercise);
            resetFragmentData();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * "Refreshes" the Fragment by detaching and reattaching it. This method is invoked
     *  after an exercise has been changed using the "Edit" menu option and when the weight
     *  has been updated.
     */
    public void refreshFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.detach(this);
            fragmentTransaction.attach(this);
            fragmentTransaction.commit();
        } else {
            Log.e("refreshFragment()", "FragmentManager is null");
        }
    }

    /**
     * Retrieves the appropriate data from the database using fetchData and then resets the
     * listView to reflect the changes that have been made. Used after adding a new exercise
     * (from the MainActivity) or selecting the "Delete" menu option (this method of resetting
     * the data has proved to be the most reliable, as other methods sometimes fail).
     */
    public void resetFragmentData() {
        // TODO find better way to determine whether the fragment is visible? (getUserVisibleHint() may not be reliable...)
        if (getUserVisibleHint()) {
            fetchData();
            adapter.clear();
            adapter.addAll(exercises);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
    }
}
