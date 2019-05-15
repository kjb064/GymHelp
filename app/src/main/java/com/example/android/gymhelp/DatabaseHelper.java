package com.example.android.gymhelp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GymHelper.db";
    private static final String TABLE_NAME = "cutting";
    private static final String ID = "ID";
    private static final String EXERCISE_NAME = "name";
    private static final String WEIGHT = "weight";
    private static final String SETS_REPS = "sets";
    private static final String DATE = "date";

    private static final Date temp = new Date(946764059);
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final String DEFAULT_DATE = formatter.format(temp);


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 6);     // Most recent version: 6
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EXERCISE_NAME + " TEXT, " + WEIGHT + " INTEGER, " + SETS_REPS + " TEXT, " + DATE + " TEXT)");
        //db.execSQL("UPDATE " + TABLE_NAME + " SET date = " + DEFAULT_DATE);

        /*
        STOPPED: Tried above statement for default date, didn't work so tried setting each row to have
        default date at end of this method in for loop. Can't check since database in device viewer tool
        still has old database (???), probably try this again.
         */



        ContentValues values = new ContentValues();
        final ArrayList<Exercise> exercises = new ArrayList<Exercise>();

        /*
         * Day 1: Chest
         */
        exercises.add(new Exercise("Barbell Bench Press - Medium Grip",
                "5 sets, 15, 12, 10, 10, 10 reps ", R.drawable.bench_press_medium_grip, 1) );
        exercises.add(new Exercise("Incline Dumbbell Press",
                "4 sets, 12, 10, 10, 8 reps ", R.drawable.incline_dumbbell_press, 2) );
        exercises.add(new Exercise("Dumbbell Flyes",
                "4 sets, 10, 10, 10, 10 reps", R.drawable.dumbbell_flyes, 3));
        exercises.add(new Exercise("Straight-Arm Dumbbell Pullover",
                "3 sets, 15, 12, 10 reps ", R.drawable.straight_arm_dumbbell_pullover, 4) );
        exercises.add(new Exercise("Butterfly",
                "4 sets, 12, 12, 12, 12 reps", R.drawable.butterfly, 5) );

        /*
         * Day 2: Quads/Calves
         *  &
         * Day 6: Calves/Hamstrings
         */
        exercises.add(new Exercise("Standing Calf Raises",
                "3 sets, 60, 60, 60 reps", R.drawable.standing_calf_raises, 6) );
        exercises.add(new Exercise("Seated Calf Raise",
                "3 sets, 60, 60, 60 reps", R.drawable.seated_calf_raise, 7) );
        exercises.add(new Exercise("Leg Extensions",
                "5 sets, 15, 12, 12, 10, 10 reps", R.drawable.leg_extensions, 8) );
        exercises.add(new Exercise("Barbell Squat",
                "5 sets, 20, 15, 12, 10, 10 reps", R.drawable.barbell_squat, 9) );
        exercises.add(new Exercise("Leg Press",
                "4 sets, 15, 12, 12, 10 reps", R.drawable.leg_press, 10) );
        exercises.add(new Exercise("Smith Machine Squat",
                "3 sets, 15, 15, 15 reps", R.drawable.smith_machine_squat, 11) );
        exercises.add(new Exercise("Seated Leg Curl",
                "4 sets, 12, 10, 10, 10 reps", R.drawable.seated_leg_curl, 50) );
        exercises.add(new Exercise("Stiff-Legged Barbell Deadlift",
                "4 sets, 15, 12, 12, 10 reps", R.drawable.stiff_legged_barbell_deadlift, 51) );
        exercises.add(new Exercise("Dumbbell Lunges",
                "3 sets, 20 steps", R.drawable.dumbbell_lunges, 52) );

        /*
         * Day 3: Back
         */
        exercises.add(new Exercise("Wide-Grip Lat Pulldown",
                "4 sets, 12, 10, 10, 10 reps", R.drawable.wide_grip_lat_pulldown, 12) );
        exercises.add(new Exercise("Seated Cable Rows",
                "4 sets, 15, 12, 10, 10 reps", R.drawable.seated_cable_rows, 13) );
        exercises.add(new Exercise("Bent Over Barbell Row",
                "4 sets, 15, 12, 10, 8 reps", R.drawable.bent_over_barbell_row, 14) );
        exercises.add(new Exercise("One-Arm Dumbbell Row",
                "4 sets, 15, 10, 10, 8 reps", R.drawable.one_arm_dumbbell_row, 15) );


        /*
         * Day 4: Shoulders
         */
        exercises.add(new Exercise("Standing Military Press",
                "4 sets, 12, 10, 8, 8 reps", R.drawable.standing_military_press, 16) );
        exercises.add(new Exercise("Dumbbell Bench Press",
                "4 sets, 10, 10, 8, 8 reps", R.drawable.dumbbell_bench_press, 17) );
        exercises.add(new Exercise("Barbell Shrug",
                "4 sets, 15, 12, 12, 10 reps", R.drawable.barbell_shrug, 18) );
        exercises.add(new Exercise("Smith Machine Shrug",
                "3 sets, 12, 12, 12 reps", R.drawable.smith_machine_shrug, 19) );
        exercises.add(new Exercise("Side Lateral Raise",
                "3 sets, 12, 10, 8 reps per side", R.drawable.side_lateral_raise, 20) );
        exercises.add(new Exercise("Front Plate Raise",
                "3 sets, 12, 10, 8 reps (25, 35, 45)", R.drawable.front_plate_raise, 21) );


        /*
         * Day 5: Arms
         */

        exercises.add(new Exercise("Barbell Curl",
                "4 sets, 12, 10, 10, 8 reps", R.drawable.barbell_curl, 22) );
        exercises.add(new Exercise("Dumbbell Alternate Bicep Curl",
                "4 sets, 12, 10, 8, 8 reps", R.drawable.dumbbell_alternate_bicep_curl, 23) );
        exercises.add(new Exercise("Standing Dumbbell Reverse Curl",
                "4 sets, 12, 10, 10, 8 reps", R.drawable.standing_dumbbell_reverse_curl, 24) );
        exercises.add(new Exercise("One Arm Dumbbell Preacher Curl",
                "3 sets, 12, 12, 12 reps", R.drawable.one_arm_dumbbell_preacher_curl, 25) );
        exercises.add(new Exercise("Dumbbell One-Arm Triceps Extension",
                "4 sets, 12, 10, 10, 8 reps", R.drawable.dumbbell_one_arm_triceps_extension, 26) );
        exercises.add(new Exercise("Weighted Bench Dip",
                "4 sets, 15, 12, 12, 10 reps", R.drawable.weighted_bench_dip, 27) );
        exercises.add(new Exercise("Lying Triceps Press",
                "4 sets, 15, 10, 10, 8 reps", R.drawable.lying_triceps_press, 28) );
        exercises.add(new Exercise("Triceps Pushdown",
                "3 sets, 12, 12, 10 reps", R.drawable.triceps_pushdown, 29) );

        for(int i = 0; i < exercises.size(); i++){
            values.put("name", exercises.get(i).getExerciseName());
            values.put("weight", exercises.get(i).getRecentWeight());
            values.put("sets", exercises.get(i).getSetsAndReps());
            values.put("date", DEFAULT_DATE);
            db.insert(TABLE_NAME, null, values);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList getCuttingTableData(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cutting ", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                int weight = c.getInt(2);
                String sets = c.getString(3);

                exercises.add(new Exercise(id, name, sets, weight));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    }

    public void updateExerciseWeight(int exerciseID, int weight){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE cutting SET weight = " + weight + " WHERE ID = " + exerciseID + ";";
        db.execSQL(sql);
    }

}
