package com.example.android.gymhelp;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
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
    private static final String IMAGE_PATH = "imagePath";
    private static final String IMAGE_NAME = "image";
    private static final String EXERCISE_TARGET = "target";

    private static final Date temp = new Date(946764059);
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final String DEFAULT_DATE = formatter.format(temp);
    private Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 14);     // Most recent version: 14
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE " + TABLE_NAME
                + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXERCISE_NAME + " TEXT, "
                + WEIGHT + " INTEGER, "
                + SETS_REPS + " TEXT, "
                + DATE + " TEXT, "
                + IMAGE_NAME + " TEXT, "
                + IMAGE_PATH + " TEXT, "
                + EXERCISE_TARGET + " INTEGER" + ")" );
        //db.execSQL("UPDATE " + TABLE_NAME + " SET date = " + DEFAULT_DATE);

        /*
        STOPPED: Tried above statement for default date, didn't work so tried setting each row to have
        default date at end of this method in for loop. Can't check since database in device viewer tool
        still has old database (???), probably try this again.
         */



        ContentValues values = new ContentValues();
        final ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        Resources resources = context.getResources();

        /*
         * Day 1: Chest
         */
        exercises.add(new Exercise("Barbell Bench Press - Medium Grip",
                "5 sets, 15, 12, 10, 10, 10 reps ",
                resources.getResourceName(R.drawable.bench_press_medium_grip),
                0,
                Constants.CHEST) );
        exercises.add(new Exercise("Incline Dumbbell Press",
                "4 sets, 12, 10, 10, 8 reps ",
                resources.getResourceEntryName(R.drawable.incline_dumbbell_press),
                0,
                Constants.CHEST) );
        exercises.add(new Exercise("Dumbbell Flyes",
                "4 sets, 10, 10, 10, 10 reps",
                resources.getResourceEntryName(R.drawable.dumbbell_flyes),
                0,
                Constants.CHEST));
        exercises.add(new Exercise("Straight-Arm Dumbbell Pullover",
                "3 sets, 15, 12, 10 reps ",
                resources.getResourceEntryName(R.drawable.straight_arm_dumbbell_pullover),
                0,
                Constants.CHEST) );
        exercises.add(new Exercise("Butterfly",
                "4 sets, 12, 12, 12, 12 reps",
                resources.getResourceEntryName(R.drawable.butterfly),
                0,
                Constants.CHEST) );

        /*
         * Day 2: Quads/Calves
         *  &
         * Day 6: Calves/Hamstrings
         */
        exercises.add(new Exercise("Standing Calf Raises",
                "3 sets, 60, 60, 60 reps",
                resources.getResourceEntryName(R.drawable.standing_calf_raises),
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Seated Calf Raise",
                "3 sets, 60, 60, 60 reps",
                resources.getResourceEntryName(R.drawable.seated_calf_raise),
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Leg Extensions",
                "5 sets, 15, 12, 12, 10, 10 reps",
                resources.getResourceEntryName(R.drawable.leg_extensions),
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Barbell Squat",
                "5 sets, 20, 15, 12, 10, 10 reps",
                resources.getResourceEntryName(R.drawable.barbell_squat),
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Leg Press",
                "4 sets, 15, 12, 12, 10 reps",
                resources.getResourceEntryName(R.drawable.leg_press),
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Smith Machine Squat",
                "3 sets, 15, 15, 15 reps",
                resources.getResourceEntryName(R.drawable.smith_machine_squat),
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Seated Leg Curl",
                "4 sets, 12, 10, 10, 10 reps",
                resources.getResourceEntryName(R.drawable.seated_leg_curl),
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Stiff-Legged Barbell Deadlift",
                "4 sets, 15, 12, 12, 10 reps",
                resources.getResourceEntryName(R.drawable.stiff_legged_barbell_deadlift),
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Dumbbell Lunges",
                "3 sets, 20 steps",
                resources.getResourceEntryName(R.drawable.dumbbell_lunges),
                0,
                Constants.LEGS) );

        /*
         * Day 3: Back
         */
        exercises.add(new Exercise("Wide-Grip Lat Pulldown",
                "4 sets, 12, 10, 10, 10 reps",
                resources.getResourceEntryName(R.drawable.wide_grip_lat_pulldown),
                0,
                Constants.BACK) );
        exercises.add(new Exercise("Seated Cable Rows",
                "4 sets, 15, 12, 10, 10 reps",
                resources.getResourceEntryName(R.drawable.seated_cable_rows),
                0,
                Constants.BACK) );
        exercises.add(new Exercise("Bent Over Barbell Row",
                "4 sets, 15, 12, 10, 8 reps",
                resources.getResourceEntryName(R.drawable.bent_over_barbell_row),
                0,
                Constants.BACK) );
        exercises.add(new Exercise("One-Arm Dumbbell Row",
                "4 sets, 15, 10, 10, 8 reps",
                resources.getResourceEntryName(R.drawable.one_arm_dumbbell_row),
                0,
                Constants.BACK) );


        /*
         * Day 4: Shoulders
         */
        exercises.add(new Exercise("Standing Military Press",
                "4 sets, 12, 10, 8, 8 reps",
                resources.getResourceEntryName(R.drawable.standing_military_press),
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Dumbbell Bench Press",
                "4 sets, 10, 10, 8, 8 reps",
                resources.getResourceEntryName(R.drawable.dumbbell_bench_press),
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Barbell Shrug",
                "4 sets, 15, 12, 12, 10 reps",
                resources.getResourceEntryName(R.drawable.barbell_shrug),
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Smith Machine Shrug",
                "3 sets, 12, 12, 12 reps",
                resources.getResourceEntryName(R.drawable.smith_machine_shrug),
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Side Lateral Raise",
                "3 sets, 12, 10, 8 reps per side",
                resources.getResourceEntryName(R.drawable.side_lateral_raise),
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Front Plate Raise",
                "3 sets, 12, 10, 8 reps (25, 35, 45)",
                resources.getResourceEntryName(R.drawable.front_plate_raise),
                0,
                Constants.SHOULDERS) );


        /*
         * Day 5: Arms
         */
        exercises.add(new Exercise("Barbell Curl",
                "4 sets, 12, 10, 10, 8 reps",
                resources.getResourceEntryName(R.drawable.barbell_curl),
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Dumbbell Alternate Bicep Curl",
                "4 sets, 12, 10, 8, 8 reps",
                resources.getResourceEntryName(R.drawable.dumbbell_alternate_bicep_curl),
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Standing Dumbbell Reverse Curl",
                "4 sets, 12, 10, 10, 8 reps",
                resources.getResourceEntryName(R.drawable.standing_dumbbell_reverse_curl),
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("One Arm Dumbbell Preacher Curl",
                "3 sets, 12, 12, 12 reps",
                resources.getResourceEntryName(R.drawable.one_arm_dumbbell_preacher_curl),
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Dumbbell One-Arm Triceps Extension",
                "4 sets, 12, 10, 10, 8 reps",
                resources.getResourceEntryName(R.drawable.dumbbell_one_arm_triceps_extension),
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Weighted Bench Dip",
                "4 sets, 15, 12, 12, 10 reps",
                resources.getResourceEntryName(R.drawable.weighted_bench_dip),
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Lying Triceps Press",
                "4 sets, 15, 10, 10, 8 reps",
                resources.getResourceEntryName(R.drawable.lying_triceps_press),
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Triceps Pushdown",
                "3 sets, 12, 12, 10 reps",
                resources.getResourceEntryName(R.drawable.triceps_pushdown),
                0,
                Constants.ARMS) );

        for(int i = 0; i < exercises.size(); i++){
            values.put("name", exercises.get(i).getExerciseName());
            values.put("weight", exercises.get(i).getRecentWeight());
            values.put("sets", exercises.get(i).getSetsAndReps());
            values.put("date", DEFAULT_DATE);
            values.put("image", exercises.get(i).getImageResourceName());
            values.put("target", exercises.get(i).getExerciseTarget());
            db.insert(TABLE_NAME, null, values);
        }

    } // end onCreate

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*
        (DONE) Will require modification (or new methods) for each fragment under "Cutting"
     */
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
                // String date;
                String imageName = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imageName));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    }

    public ArrayList getChestExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cutting " +
                                   "WHERE " + EXERCISE_TARGET + " = " + Constants.CHEST + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                int weight = c.getInt(2);
                String sets = c.getString(3);
                // String date;
                String imageName = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imageName));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getChestExercises

    public ArrayList getLegExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cutting " +
                                   "WHERE " + EXERCISE_TARGET + " = " + Constants.LEGS + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                int weight = c.getInt(2);
                String sets = c.getString(3);
                // String date;
                String imageName = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imageName));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;

    } // end getLegExercises

    public ArrayList getBackExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cutting " +
                                   "WHERE " + EXERCISE_TARGET + " = " + Constants.BACK + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                int weight = c.getInt(2);
                String sets = c.getString(3);
                // String date;
                String imageName = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imageName));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getBackExercises

    public ArrayList getShoulderExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cutting " +
                                   "WHERE " + EXERCISE_TARGET + " = " + Constants.SHOULDERS + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                int weight = c.getInt(2);
                String sets = c.getString(3);
                // String date;
                String imageName = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imageName));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getShoulderExercises

    public ArrayList getArmExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cutting " +
                                   "WHERE " + EXERCISE_TARGET + " = " + Constants.ARMS + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                int weight = c.getInt(2);
                String sets = c.getString(3);
                // String date;
                String imageName = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imageName));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getArmExercises

    /*
        !!! Will need testing once abs exercises have been added !!!
     */
    public ArrayList getAbsExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cutting " +
                                   "WHERE " + EXERCISE_TARGET + " = " + Constants.ABS + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                int weight = c.getInt(2);
                String sets = c.getString(3);
                // String date;
                String imageName = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imageName));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getAbsExercises

    public void updateExerciseWeight(int exerciseID, int weight){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE cutting SET weight = " + weight + " WHERE ID = " + exerciseID + ";";
        db.execSQL(sql);
    }

    public void addExercise(Exercise newExercise){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newExercise.getExerciseName());
        values.put("weight", 0);
        values.put("sets", newExercise.getSetsAndReps());

        values.put("date", DEFAULT_DATE);   // Needs fix

        //if(newExercise.hasImage()){
            values.put("image", newExercise.getImageResourceName());
        //}

        values.put("target", newExercise.getExerciseTarget());
        db.insert(TABLE_NAME, null, values);
    }

}
