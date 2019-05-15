package com.example.android.gymhelp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class CuttingActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list);

        final DatabaseHelper db = new DatabaseHelper(this);
        int imageIDs[] = new int[32];
        imageIDs[0] = R.drawable.bench_press_medium_grip;
        imageIDs[1] = R.drawable.incline_dumbbell_press;
        imageIDs[2] = R.drawable.dumbbell_flyes;
        imageIDs[3] = R.drawable.straight_arm_dumbbell_pullover;
        imageIDs[4] = R.drawable.butterfly;
        imageIDs[5] = R.drawable.standing_calf_raises;
        imageIDs[6] = R.drawable.seated_calf_raise;
        imageIDs[7] = R.drawable.leg_extensions;
        imageIDs[8] = R.drawable.barbell_squat;
        imageIDs[9] = R.drawable.leg_press;
        imageIDs[10] = R.drawable.smith_machine_squat;
        imageIDs[11] = R.drawable.seated_leg_curl;
        imageIDs[12] = R.drawable.stiff_legged_barbell_deadlift;
        imageIDs[13] = R.drawable.dumbbell_lunges;
        imageIDs[14] = R.drawable.wide_grip_lat_pulldown;
        imageIDs[15] = R.drawable.seated_cable_rows;
        imageIDs[16] = R.drawable.bent_over_barbell_row;
        imageIDs[17] = R.drawable.one_arm_dumbbell_row;
        imageIDs[18] = R.drawable.standing_military_press;
        imageIDs[19] = R.drawable.dumbbell_bench_press;
        imageIDs[20] = R.drawable.barbell_shrug;
        imageIDs[21] = R.drawable.smith_machine_shrug;
        imageIDs[22] = R.drawable.side_lateral_raise;
        imageIDs[23] = R.drawable.front_plate_raise;
        imageIDs[24] = R.drawable.barbell_curl;
        imageIDs[25] = R.drawable.dumbbell_alternate_bicep_curl;
        imageIDs[26] = R.drawable.standing_dumbbell_reverse_curl;
        imageIDs[27] = R.drawable.one_arm_dumbbell_preacher_curl;
        imageIDs[28] = R.drawable.dumbbell_one_arm_triceps_extension;
        imageIDs[29] = R.drawable.weighted_bench_dip;
        imageIDs[30] = R.drawable.lying_triceps_press;
        imageIDs[31] = R.drawable.triceps_pushdown;

        ArrayList<Exercise> ex = db.getCuttingTableData();
        for(int i = 0; i < ex.size(); i++){
            ex.get(i).setImageResourceID(imageIDs[i]);
        }


        // Create an {@link ArrayAdapter}, whose data source is a list of Strings. The
        // adapter knows how to create layouts for each item in the list, using the
        // simple_list_item_1.xml layout resource defined in the Android framework.
        // This list item layout contains a single {@link TextView}, which the adapter will set to
        // display a single word.
        final ExerciseAdapter adapter = new ExerciseAdapter(this, ex, R.color.cutting_color);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml file.
        final ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link ArrayAdapter} we created above, so that the
        // {@link ListView} will display list items for each word in the list of words.
        // Do this by calling the setAdapter method on the {@link ListView} object and pass in
        // 1 argument, which is the {@link ArrayAdapter} with the variable name itemsAdapter.
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final View view2 = null;
                LayoutInflater inflater = getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.custom_alertdialog, null);


                final Exercise ex = (Exercise) listView.getAdapter().getItem(position);
                final AlertDialog.Builder builder = new AlertDialog.Builder(CuttingActivity.this);
                builder.setView(dialoglayout);
                builder.setTitle(ex.getExerciseName());
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText editText = (EditText) dialoglayout.findViewById(R.id.weight_field);
                        int weight = Integer.parseInt(editText.getText().toString());
                        db.updateExerciseWeight(ex.getExerciseID(), weight);
                        finish();
                        startActivity(getIntent());
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
        });

    }
}
