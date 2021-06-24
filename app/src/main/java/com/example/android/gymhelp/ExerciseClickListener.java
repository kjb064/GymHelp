package com.example.android.gymhelp;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.NumberPicker;

// TODO add class javadoc
public abstract class ExerciseClickListener implements AdapterView.OnItemClickListener {

    private final Context context;

    /**
     * Constructs a new ExerciseClickListener.
     *
     * @param context the parent Context
     */
    ExerciseClickListener(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Exercise exercise = (Exercise) parent.getAdapter().getItem(position);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(exercise.getExerciseName());

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogLayout = inflater.inflate(R.layout.custom_alertdialog, null);

        builder.setView(dialogLayout);
        final NumberPicker picker = dialogLayout.findViewById(R.id.weight_picker);
        picker.setMaxValue(1000);
        picker.setMinValue(0);
        if ((int) exercise.getRecentWeight() > 0) {
            picker.setValue((int) exercise.getRecentWeight());
        }

        final NumberPicker decimalPicker = dialogLayout.findViewById(R.id.decimal_picker);
        decimalPicker.setMaxValue(9);
        decimalPicker.setMinValue(0);

        int decimalOnly = (int) ((exercise.getRecentWeight() - (float) Math.floor(exercise.getRecentWeight())) * 10);
        if (decimalOnly > 0) {
            decimalPicker.setValue(decimalOnly);
        }

        decimalPicker.setFormatter(value -> value + " lbs.");

        builder.setPositiveButton("Update", (dialog, which) -> {
            int weight = picker.getValue();
            int decimal = decimalPicker.getValue();
            float finalWeight = Float.parseFloat(weight + "." + decimal);
            DatabaseHelper db = new DatabaseHelper(context);
            db.updateExerciseWeight(exercise.getExerciseID(), finalWeight);
            refresh();
        });

        builder.setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Refreshes the ListView associated with this listener.
     */
    protected abstract void refresh();
}
