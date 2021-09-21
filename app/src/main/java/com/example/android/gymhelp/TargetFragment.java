package com.example.android.gymhelp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.android.gymhelp.Constants.DELETE_ID;
import static com.example.android.gymhelp.Constants.EDIT_ID;
import static com.example.android.gymhelp.Constants.READ_FULL_ID;
import static com.example.android.gymhelp.Constants.WEIGHT_FLAG_ID;

/**
 * Displays a ListView containing all exercises associated with a target group.
 * Allows for certain operations (such as deleting or updating) to be performed on an exercise in the ListView
 * via a context menu.
 */
public class TargetFragment extends Fragment {

    private ArrayList<Exercise> exercises;
    private ExerciseAdapter adapter;
    private DatabaseHelper db;

    private static final String GROUP_ID_KEY = "groupID";
    private static final String SORT_TYPE_KEY = "sortType";

    /**
     * Creates a TargetFragment instance with the value of <code>targetGroupId</code> passed as one
     * of its arguments. The value of <code>targetGroupId</code> should match the index of the Fragment
     * within the ViewPager.
     *
     * @param targetGroupId the ID of the Target associated with the new Fragment
     * @param sortType the sort criteria to use for displaying the Exercises
     * @return the new TargetFragment instance
     */
    public static TargetFragment createInstance(int targetGroupId, SortType sortType) {
        TargetFragment fragment = new TargetFragment();
        Bundle args = new Bundle();
        args.putInt(GROUP_ID_KEY, targetGroupId);
        args.putString(SORT_TYPE_KEY, sortType != null ? sortType.name() : null);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());
    }

    /**
     * Fetches a List of Exercises to be displayed in this TargetFragment's ListView. The List is sorted
     * by the given sortType, or unsorted if the sortType is null.
     *
     * @param sortType the sorting criteria to use when retrieving the Exercises; can be null if the
     *                 Exercises should not be sorted
     */
    private void fetchData(SortType sortType) {
        int targetId = getTargetGroupId();
        exercises = db.getSelectedExercises(targetId, sortType);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.exercise_list, container, false);
        // Fetch list of the Exercises to display, sorted according to current sort selection
        fetchData(getSelectedSortType());

        // Create an {@link ArrayAdapter}, whose data source is a list of Strings. The
        // adapter knows how to create layouts for each item in the list, using the
        // simple_list_item_1.xml layout resource defined in the Android framework.
        // This list item layout contains a single {@link TextView}, which the adapter will set to
        // display a single word.
        adapter = new ExerciseAdapter(getActivity(), exercises);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml file.
        ListView listView = rootView.findViewById(R.id.list);

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

        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> onClickAddButton());
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

    private SortType getSelectedSortType() {
        SortType sortType = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            String sortName = bundle.getString(SORT_TYPE_KEY);
            if (sortName != null) {
                if (sortName.contentEquals(SortType.ASC.name())) {
                    sortType = SortType.ASC;
                } else if (sortName.contentEquals(SortType.DESC.name())) {
                    sortType = SortType.DESC;
                }
            }
        }
        return sortType;
    }

    /**
     * Called when the FloatingActionButton is clicked to add a new exercise to the currently
     * displayed TargetFragment.
     */
    public void onClickAddButton() {
        int groupId = getTargetGroupId();
        final String targetName = TargetAdapter.lookupTitle(groupId, getActivity());
        final String title = "Add new " + targetName + " exercise";
        AddExerciseDialogFragment dialogFragment = AddExerciseDialogFragment.newInstance(title);
        dialogFragment.show(getChildFragmentManager(), "AddExerciseDialog");
    }

    /**
     * Adds a new Exercise to the appropriate table in the database and resets this fragment's data
     * so that the new Exercise can appear.
     *
     * @param name the name of the Exercise
     * @param setsReps the sets and reps for the Exercise
     * @param photoPath the path to the photo associated with the Exercise
     */
    public void addNewExerciseToDatabaseAndRefresh(String name, String setsReps, String photoPath) {
        Exercise exercise = new Exercise(name, setsReps, 0, getTargetGroupId());
        exercise.setImageFileName(photoPath);
        if (db.addExercise(exercise) == -1) {
            Toast.makeText(getContext(), "Failed to add exercise " + exercise.getExerciseName(),
                    Toast.LENGTH_SHORT).show();
        } else {
            resetFragmentData(getSelectedSortType());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int groupID = getTargetGroupId();
        menu.add(groupID, READ_FULL_ID, 0, R.string.menu_read_full);
        menu.add(groupID, EDIT_ID, 0, R.string.menu_edit);
        menu.add(groupID, DELETE_ID, 0, R.string.menu_delete);
        menu.add(groupID, WEIGHT_FLAG_ID, 0, R.string.menu_weight_flag);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO find better way to determine whether the fragment is visible? (getUserVisibleHint() may not be reliable...)
        // TODO or is it even necessary to determine if the fragment is currently visible?
        final boolean isFragmentVisibleToUser = getUserVisibleHint();
        if (isFragmentVisibleToUser) {
            Activity activity = getActivity();
            if (item.getGroupId() == getTargetGroupId() && activity != null) {
                final AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final Exercise exercise = exercises.get((int) info.id);

                switch (item.getItemId()) {
                    case READ_FULL_ID: {
                        displayReadFullDialog(exercise, activity, getLayoutInflater());
                        return true;
                    }
                    case EDIT_ID: {
                        displayEditDialog(exercise);
                        return true;
                    }
                    case DELETE_ID: {
                        displayDeleteDialog(exercise, activity);
                        return true;
                    }
                    case WEIGHT_FLAG_ID: {
                        toggleWeightFlag(exercise);
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
     * Exercise are displayed.
     *
     * @param exercise whose details to display in the dialog
     * @param activity the Activity this TargetFragment is associated with
     * @param inflater to inflate the Read Full dialog layout
     */
    public static void displayReadFullDialog(final Exercise exercise, final Activity activity, LayoutInflater inflater) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(exercise.getExerciseName());

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
     */
    private void displayEditDialog(final Exercise exercise) {
        String title = "Edit " + exercise.getExerciseName();
        EditExerciseDialogFragment dialogFragment = EditExerciseDialogFragment.newInstance(title, exercise);
        dialogFragment.show(getChildFragmentManager(), "EditExerciseDialogFragment");
    }

    /**
     * Toggles the weight flag for the given Exercise in the database and refreshes this fragment.
     *
     * @param exercise the Exercise whose weight flag should be toggled
     */
    private void toggleWeightFlag(Exercise exercise) {
        int currentFlag = exercise.getFlaggedForIncrease();
        int newFlag = currentFlag == 0 ? 1 : 0;
        exercise.setFlaggedForIncrease(newFlag);
        db.updateExerciseFlag(exercise);
        adapter.notifyDataSetChanged();
    }

    /**
     * Updates the given Exercise in the database and resets this fragment's data so the updates will appear.
     *
     * @param exercise the Exercise to update
     */
    public void updateExerciseInDatabaseAndRefresh(Exercise exercise) {
        db.updateExercise(exercise);
        adapter.notifyDataSetChanged();
    }

    /**
     * Deletes the image file at the given path and updates the path to the image in the table.
     *
     * @param exerciseId the ID of the Exercise whose image to delete
     * @param path the path to the file to delete
     */
    public void deleteExerciseImage(int exerciseId, String path) {
        db.deleteExerciseImage(exerciseId, path);
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

            adapter.remove(exercise);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * "Refreshes" the Fragment by detaching and reattaching it. This method is invoked
     *  after an exercise has been changed using the "Edit" menu option and when the weight
     *  has been updated.
     */
    private void refreshFragment() {
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
     * Fetches the Exercises for this fragment and resets the adapter.
     */
    public void resetFragmentData(SortType sortType) {
        fetchData(sortType);
        adapter.clear();
        adapter.addAll(exercises);
    }
}
