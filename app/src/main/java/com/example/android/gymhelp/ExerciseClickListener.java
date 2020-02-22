package com.example.android.gymhelp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;

public class ExerciseClickListener implements AdapterView.OnItemClickListener {

    private ListView listView;
    private Context context;
    private String tag;

    ExerciseClickListener(ListView listView, Context context, String tag){
        super();
        this.listView = listView;
        this.context = context;
        this.tag = tag;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Exercise exercise = (Exercise) listView.getAdapter().getItem(position);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(exercise.getExerciseName());

        final View dialogLayout = View.inflate(context, R.layout.custom_alertdialog, null);
        builder.setView(dialogLayout);

        final NumberPicker picker = dialogLayout.findViewById(R.id.weight_picker);
        picker.setMaxValue(350);
        picker.setMinValue(0);
        if( (int) exercise.getRecentWeight() > 0){
            picker.setValue((int) exercise.getRecentWeight());
        }

        final NumberPicker decimalPicker = dialogLayout.findViewById(R.id.decimal_picker);
        decimalPicker.setMaxValue(9);
        decimalPicker.setMinValue(0);

        int decimalOnly = (int) ((exercise.getRecentWeight() - (float) Math.floor(exercise.getRecentWeight())) * 10);
        if(decimalOnly > 0){
            decimalPicker.setValue(decimalOnly);
        }

        decimalPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return value + " lbs.";
            }
        });

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int weight = picker.getValue();
                int decimal = decimalPicker.getValue();
                float finalWeight = Float.parseFloat(weight + "." + decimal);
                DatabaseHelper db = new DatabaseHelper(context);
                db.updateExerciseWeight(exercise.getExerciseID(), finalWeight);
                if(tag != null && !tag.equals(Constants.NO_FRAGMENT_ID)){
                    TargetFragment currentFragment =
                            (TargetFragment)((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag(tag);
                    currentFragment.refreshFragment();
                }
                else if(tag != null){
                    ((SearchResultsActivity) context).refreshSearchResults();
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

    } // end onItemClick
}
