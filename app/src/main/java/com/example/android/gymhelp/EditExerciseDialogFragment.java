package com.example.android.gymhelp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * DialogFragment for the "Edit Exercise" dialog.
 */
public class EditExerciseDialogFragment extends AddExerciseDialogFragment {

    /**
     * Creates a new EditExerciseDialogFragment with the given title and Exercise as arguments.
     *
     * @param title the title of the dialog
     * @param exercise the Exercise being edited
     * @return a new EditExerciseDialogFragment
     */
    public static EditExerciseDialogFragment newInstance(String title, Exercise exercise) {
        EditExerciseDialogFragment dialogFragment = new EditExerciseDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString("Title", title);
        bundle.putParcelable("Exercise", exercise);
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogLayout  = super.onCreateView(inflater, container, savedInstanceState);

        if (dialogLayout != null) {
            TextView photoPathTextView = dialogLayout.findViewById(R.id.image_name_text_view);
            CheckBox checkBox = dialogLayout.findViewById(R.id.photo_check_box);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkBox.setClickable(isChecked);
                if (!isChecked) {
                    photoPathTextView.setText(null);
                    checkBox.setText(R.string.no_photo_selected);
                } else {
                    checkBox.setText(R.string.photo_selected);
                }
            });

            // Set fields using data obtained from Exercise
            Bundle bundle = getArguments();
            if (bundle != null) {
                Exercise exercise = bundle.getParcelable("Exercise");
                if (exercise.hasImage()) {
                    checkBox.setChecked(true);
                    photoPathTextView.setText(exercise.getImageFileName());
                }

                final EditText nameEditText = dialogLayout.findViewById(R.id.name_edit_text);
                nameEditText.setText(exercise.getExerciseName());

                final EditText setsRepsEditText = dialogLayout.findViewById(R.id.sets_reps_edit_text);
                setsRepsEditText.setText(exercise.getSetsAndReps());
            }
        }
        return dialogLayout;
    }

    /**
     * Uses the given parameters to update the Exercise in the database.
     *
     * @param name the name of the Exercise
     * @param setsReps the sets and reps for the Exercise
     * @param imageFileName the file name of the photo associated with the Exercise
     */
    @Override
    protected void submitExercise(String name, String setsReps, String imageFileName) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Exercise exercise = bundle.getParcelable("Exercise");

            exercise.setExerciseName(name);
            exercise.setSetsAndReps(setsReps);

            final String oldFileName = exercise.getImageFileName();
            // Check if the image was removed or changed; may have to delete the old image from storage.
            boolean imageRemoved = false;
            if (exercise.hasImage()) {
                if (imageFileName == null) {
                    imageRemoved = true;
                } else {
                    imageRemoved = !imageFileName.equals(oldFileName);
                }
            }

            exercise.setImageFileName(imageFileName);

            TargetFragment parentFragment = (TargetFragment) getParentFragment();
            if (parentFragment != null) {
                if (imageRemoved) parentFragment.deleteExerciseImage(exercise.getExerciseID(), oldFileName);
                parentFragment.updateExerciseInDatabaseAndRefresh(exercise);
            }
        }
    }
}
