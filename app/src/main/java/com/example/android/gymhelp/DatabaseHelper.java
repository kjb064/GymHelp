package com.example.android.gymhelp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GymHelper.db";
    private static final String TABLE_NAME = "cutting";
    private static final String ID = "ID";
    private static final String EXERCISE_NAME = "name";
    private static final String WEIGHT = "weight";
    private static final String SETS_REPS = "sets";
    private static final String DATE = "date";
    private static final String IMAGE_PATH = "imagePath";
    private static final String EXERCISE_TARGET = "target";

    private static SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
    private Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 19);     // Most recent version: 19
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE " + TABLE_NAME
                + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXERCISE_NAME + " TEXT, "
                + WEIGHT + " FLOAT, "
                + SETS_REPS + " TEXT, "
                + DATE + " TEXT, "
                + IMAGE_PATH + " TEXT, "
                + EXERCISE_TARGET + " INTEGER" + ")" );

        ContentValues values = new ContentValues();
        final ArrayList<Exercise> exercises = new ArrayList<Exercise>();

        /*
         * Day 1: Chest
         */
        exercises.add(new Exercise("Barbell Bench Press - Medium Grip",
                "5 sets, 15, 12, 10, 10, 10 reps ",
                0,
                Constants.CHEST) );
        exercises.add(new Exercise("Incline Dumbbell Press",
                "4 sets, 12, 10, 10, 8 reps ",
                0,
                Constants.CHEST) );
        exercises.add(new Exercise("Dumbbell Flyes",
                "4 sets, 10, 10, 10, 10 reps",
                0,
                Constants.CHEST));
        exercises.add(new Exercise("Straight-Arm Dumbbell Pullover",
                "3 sets, 15, 12, 10 reps ",
                0,
                Constants.CHEST) );
        exercises.add(new Exercise("Butterfly",
                "4 sets, 12, 12, 12, 12 reps",
                0,
                Constants.CHEST) );

        /*
         * Day 2: Quads/Calves
         *  &
         * Day 6: Calves/Hamstrings
         */
        exercises.add(new Exercise("Standing Calf Raises",
                "3 sets, 60, 60, 60 reps",
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Seated Calf Raise",
                "3 sets, 60, 60, 60 reps",
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Leg Extensions",
                "5 sets, 15, 12, 12, 10, 10 reps",
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Barbell Squat",
                "5 sets, 20, 15, 12, 10, 10 reps",
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Leg Press",
                "4 sets, 15, 12, 12, 10 reps",
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Smith Machine Squat",
                "3 sets, 15, 15, 15 reps",
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Seated Leg Curl",
                "4 sets, 12, 10, 10, 10 reps",
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Stiff-Legged Barbell Deadlift",
                "4 sets, 15, 12, 12, 10 reps",
                0,
                Constants.LEGS) );
        exercises.add(new Exercise("Dumbbell Lunges",
                "3 sets, 20 steps",
                0,
                Constants.LEGS) );

        /*
         * Day 3: Back
         */
        exercises.add(new Exercise("Wide-Grip Lat Pulldown",
                "4 sets, 12, 10, 10, 10 reps",
                0,
                Constants.BACK) );
        exercises.add(new Exercise("Seated Cable Rows",
                "4 sets, 15, 12, 10, 10 reps",
                0,
                Constants.BACK) );
        exercises.add(new Exercise("Bent Over Barbell Row",
                "4 sets, 15, 12, 10, 8 reps",
                0,
                Constants.BACK) );
        exercises.add(new Exercise("One-Arm Dumbbell Row",
                "4 sets, 15, 10, 10, 8 reps",
                0,
                Constants.BACK) );


        /*
         * Day 4: Shoulders
         */
        exercises.add(new Exercise("Standing Military Press",
                "4 sets, 12, 10, 8, 8 reps",
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Dumbbell Bench Press",
                "4 sets, 10, 10, 8, 8 reps",
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Barbell Shrug",
                "4 sets, 15, 12, 12, 10 reps",
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Smith Machine Shrug",
                "3 sets, 12, 12, 12 reps",
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Side Lateral Raise",
                "3 sets, 12, 10, 8 reps per side",
                0,
                Constants.SHOULDERS) );
        exercises.add(new Exercise("Front Plate Raise",
                "3 sets, 12, 10, 8 reps (25, 35, 45)",
                0,
                Constants.SHOULDERS) );


        /*
         * Day 5: Arms
         */
        exercises.add(new Exercise("Barbell Curl",
                "4 sets, 12, 10, 10, 8 reps",
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Dumbbell Alternate Bicep Curl",
                "4 sets, 12, 10, 8, 8 reps",
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Standing Dumbbell Reverse Curl",
                "4 sets, 12, 10, 10, 8 reps",
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("One Arm Dumbbell Preacher Curl",
                "3 sets, 12, 12, 12 reps",
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Dumbbell One-Arm Triceps Extension",
                "4 sets, 12, 10, 10, 8 reps",
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Weighted Bench Dip",
                "4 sets, 15, 12, 12, 10 reps",
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Lying Triceps Press",
                "4 sets, 15, 10, 10, 8 reps",
                0,
                Constants.ARMS) );
        exercises.add(new Exercise("Triceps Pushdown",
                "3 sets, 12, 12, 10 reps",
                0,
                Constants.ARMS) );

        for(int i = 0; i < exercises.size(); i++){
            values.put(EXERCISE_NAME, exercises.get(i).getExerciseName());
            values.put(WEIGHT, exercises.get(i).getRecentWeight());
            values.put(SETS_REPS, exercises.get(i).getSetsAndReps());
            values.put(DATE, Constants.DEFAULT_DATE);
            values.put(EXERCISE_TARGET, exercises.get(i).getExerciseTarget());
            db.insert(TABLE_NAME, null, values);
        }

    } // end onCreate

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList getAllExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + ";", null);

        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                float weight = c.getFloat(2);
                String sets = c.getString(3);
                String date = c.getString(4);
                String imagePath = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imagePath, date));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getAllExercises

    public ArrayList getChestExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                                   " WHERE " + EXERCISE_TARGET + " = " + Constants.CHEST + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                float weight = c.getFloat(2);
                String sets = c.getString(3);
                String date = c.getString(4);
                String imagePath = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imagePath, date));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getChestExercises

    public ArrayList getLegExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                                   " WHERE " + EXERCISE_TARGET + " = " + Constants.LEGS + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                float weight = c.getFloat(2);
                String sets = c.getString(3);
                String date = c.getString(4);
                String imagePath = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imagePath, date));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;

    } // end getLegExercises

    public ArrayList getBackExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                                   " WHERE " + EXERCISE_TARGET + " = " + Constants.BACK + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                float weight = c.getFloat(2);
                String sets = c.getString(3);
                String date = c.getString(4);
                String imagePath = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imagePath, date));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getBackExercises

    public ArrayList getShoulderExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                                   " WHERE " + EXERCISE_TARGET + " = " + Constants.SHOULDERS + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                float weight = c.getFloat(2);
                String sets = c.getString(3);
                String date = c.getString(4);
                String imagePath = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imagePath, date));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getShoulderExercises

    public ArrayList getArmExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                                   " WHERE " + EXERCISE_TARGET + " = " + Constants.ARMS + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                float weight = c.getFloat(2);
                String sets = c.getString(3);
                String date = c.getString(4);
                String imagePath = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imagePath, date));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getArmExercises

    public ArrayList getAbsExercises(){
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                                   " WHERE " + EXERCISE_TARGET + " = " + Constants.ABS + ";", null);
        if(c.moveToFirst()){
            do{
                int id = c.getInt(0);
                String name = c.getString(1);
                float weight = c.getFloat(2);
                String sets = c.getString(3);
                String date = c.getString(4);
                String imagePath = c.getString(5);
                exercises.add(new Exercise(id, name, sets, weight, imagePath, date));
            }while(c.moveToNext());
        }

        c.close();
        return exercises;
    } // end getAbsExercises

    public void updateExerciseWeight(int exerciseID, float weight){
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        String currentDate = formatter.format(calendar.getTime());
        Log.d("Date", currentDate);

        String sql = "UPDATE " + TABLE_NAME +
                " SET " + WEIGHT + " = " + weight + ", " +
                DATE + " = '" + currentDate + "'" +
                " WHERE ID = " + exerciseID + ";";
        db.execSQL(sql);
    } // end updateExerciseWeight

    public void addExercise(Exercise newExercise){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXERCISE_NAME, newExercise.getExerciseName());
        values.put(WEIGHT, 0);
        values.put(SETS_REPS, newExercise.getSetsAndReps());
        values.put(DATE, Constants.DEFAULT_DATE);
        values.put(IMAGE_PATH, newExercise.getImageResourcePath());
        values.put(EXERCISE_TARGET, newExercise.getExerciseTarget());
        db.insert(TABLE_NAME, null, values);

    } // end addExercise

    public void deleteExercise(int exerciseID){
        Log.d("Delete", "Deleting item pos= " + exerciseID);

        // Check if there's an image associated with the item to delete. If so, delete the image.
            // Note: This will ONLY delete an image taken using the "Take Image" button when adding a new
            // exercise. An image that was already on the device and was added using the "Add Image"
            // button will remain on the device.

        String path = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + IMAGE_PATH + " FROM " + TABLE_NAME +
                " WHERE " + ID + " = " + exerciseID + ";", null);

        if(c.moveToFirst()){    // Check if cursor is empty
            path = c.getString(0);
        }

        if(path != null && !path.equals(Constants.NO_IMAGE_PROVIDED)){
            File deleteFile = new File(path);
            if(deleteFile.delete()){
                Log.d("Delete", "Successfully deleted file at " + path);
            }
            else {
                Log.d("Delete", "Could not delete file at " + path);
            }
        }

        // Then delete the exercise from the table

        if(db.delete(TABLE_NAME, ID + "=" + exerciseID, null) > 0){
            Log.d("Delete", "Successfully deleted exercise #" + exerciseID);
        }
        else{
            Log.d("Delete", "Could not delete exercise #" + exerciseID);
        }

    } // end deleteExercise

    /*
    *   Called after the user requests an edit to an exercise. Updates the exercise's data
    *   within the table.
     */
    public void updateExercise(Exercise exercise){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE " + ID + " = " + exercise.getExerciseID() + ";", null);

        if(c.moveToFirst()){

            String name = c.getString(1);
            String setsAndReps = c.getString(3);
            String imagePath = c.getString(5);

            String sql = "UPDATE " + TABLE_NAME + " SET ";
            boolean updateNeeded = false;

            if(!name.equals(exercise.getExerciseName())){
                sql += EXERCISE_NAME + " = '" + exercise.getExerciseName() + "'";
                updateNeeded = true;
            }

            if(!setsAndReps.equals(exercise.getSetsAndReps())){
                if(updateNeeded){
                    sql += ", " + SETS_REPS + " = '" + exercise.getSetsAndReps() + "'";
                }
                else{
                    sql += SETS_REPS + " = '" + exercise.getSetsAndReps() + "'";
                    updateNeeded = true;
                }
            }

            if(imagePath != null && exercise.getImageResourcePath() != null){
                if(!imagePath.equals(exercise.getImageResourcePath())){
                    if(updateNeeded){
                        sql += ", " + IMAGE_PATH + " = '" + exercise.getImageResourcePath() + "'";
                    }
                    else{
                        sql += IMAGE_PATH + " = '" + exercise.getImageResourcePath() + "'";
                        updateNeeded = true;
                    }
                }
            }
            else if(imagePath == null && exercise.getImageResourcePath() != null){
                if(updateNeeded){
                    sql += ", " + IMAGE_PATH + " = '" + exercise.getImageResourcePath() + "'";
                }
                else{
                    sql += IMAGE_PATH + " = '" + exercise.getImageResourcePath() + "'";
                    updateNeeded = true;
                }
            }

            if(updateNeeded){
                sql += " WHERE " + ID + " = " + exercise.getExerciseID() + ";";
                Log.d("SQL", "" + sql);
                db.execSQL(sql);
            }
        }

    } // end updateExercise
}
