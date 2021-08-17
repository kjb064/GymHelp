package com.example.android.gymhelp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // TODO refactor to use SQLiteDatabase methods where applicable
    // TODO refactor to call getWriteable()/getReadableDatabase() within try with resource blocks

    // TODO: Check for duplicates when inserting new exercise (do this before passing to DB helper...?)

    private static final String DATABASE_NAME = "GymHelper.db";
    private static final String ID = "ID";
    private static final String EXERCISE_NAME = "name";
    private static final String WEIGHT = "weight";
    private static final String SETS_REPS = "sets";
    private static final String DATE = "date";
    private static final String IMAGE_PATH = "imagePath";
    private static final String EXERCISE_TARGET = "target";
    private static final String WEIGHT_FLAG = "flag";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int WEIGHT_INDEX = 2;
    private static final int SETS_REPS_INDEX = 3;
    private static final int DATE_INDEX = 4;
    private static final int IMAGE_PATH_INDEX = 5;
    private static final int TARGET_INDEX = 6;
    private static final int WEIGHT_FLAG_INDEX = 7;

    /**
     * The table being operated on by this DatabaseHelper.
     *
     * TODO consider making non-static; Currently is static so that changing the table being operated
     * on via setCurrentTableName() syncs with each TargetFragment's instance of DatabaseHelper.
     *
     * Alternatively, fragments could delegate to MainActivity to handle all database operations?
     *
     * Alternatively, fragments could keep track of the table their data comes from and pass the table name
     * to the appropriate functions?
     */
    private static String TABLE_NAME = "defaultWorkout";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 22);     // Most recent version: 22
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createExerciseTable(db, TABLE_NAME);

        createDefaultPPLTable(db);
    }

    /**
     * Surrounds the given text with quotes (") and returns the new String.
     *
     * <p>
     * Convenience to surround a table's name with quotes for SQLite to accept a name that may
     * include spaces.
     * </p>
     *
     * @param text the text to surround with quotes
     * @return the text surrounded by quotes
     */
    private String quotes(String text) {
        return "\"" + text + "\"";
    }

    /**
     * Creates a table with the given name in the database for holding data on different exercises.
     *
     * @param db the database manager
     * @param tableName the name of the table to be created (name does not need to include quotes)
     */
    private void createExerciseTable(SQLiteDatabase db, String tableName) {
        // Surround the tableName in quotes to allow for names with spaces.
        db.execSQL("CREATE TABLE " + quotes(tableName)
                + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXERCISE_NAME + " TEXT, "
                + WEIGHT + " FLOAT, "
                + SETS_REPS + " TEXT, "
                + DATE + " TEXT, "
                + IMAGE_PATH + " TEXT, "
                + EXERCISE_TARGET + " INTEGER, "
                + WEIGHT_FLAG + " INTEGER)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO ensure this upgrades all tables once multiple tables feature is implemented

        // Re-create the existing table as tempTable
        final String tempTableName = "tempTable";
        createExerciseTable(db, tempTableName);

        db.execSQL("INSERT INTO " + tempTableName
        + " ( " + ID + ", " + EXERCISE_NAME + ", " + WEIGHT + ", " + SETS_REPS + ", " + DATE +
                ", " + IMAGE_PATH + ", " + EXERCISE_TARGET + ")"
        + " SELECT " + ID + ", " + EXERCISE_NAME + ", " + WEIGHT + ", " +SETS_REPS + ", "
                + DATE + ", " + IMAGE_PATH+ ", " + EXERCISE_TARGET + " FROM " + internalGetCurrentTable());

        // Delete the old table and rename tempTable to the old table's name
        final String tableNameWithQuotes = internalGetCurrentTable();
        db.execSQL("DROP TABLE " + tableNameWithQuotes);
        db.execSQL("ALTER TABLE tempTable RENAME TO " + tableNameWithQuotes);
        db.execSQL("UPDATE " + tableNameWithQuotes + " SET " + WEIGHT_FLAG + " = 0");
    }

    /**
     * Gets a list of all tables currently in the database.
     *
     * @return the list of tables
     */
    ArrayList<String> getTableNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> tableNames = new ArrayList<>();

        try (Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)) {
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    String name = c.getString(c.getColumnIndex("name"));
                    if (!name.contentEquals("android_metadata") && !name.contentEquals("sqlite_sequence")) {
                        tableNames.add(name);
                    }
                    c.moveToNext();
                }
            }
        }
        return tableNames;
    }

    /**
     * @return the name of the current table being operated on
     */
    String getCurrentTableName() {
        return TABLE_NAME;
    }

    /**
     * Sets the given tableName as the current table to operate on.
     *
     * @param tableName the table to set
     */
    void setCurrentTableName(String tableName) {
        TABLE_NAME = tableName;
    }

    /**
     * Checks whether a table with the given name exists in the database.
     *
     * @param tableName the name of the table whose existence to verify
     * @return whether the table exists
     */
    boolean doesTableExist(String tableName) {
        return getTableNames().contains(tableName);
    }

    /**
     * Creates a table in the database with the given name.
     *
     * @param tableName the name of the table to create
     */
    void addTable(String tableName) {
        // TODO Validate/sanitize tableName
        SQLiteDatabase db = getWritableDatabase();
        createExerciseTable(db, tableName);
    }

    /**
     * Deletes the table in the database with the given name.
     *
     * @param tableName the name of the table to delete
     */
    void deleteTable(String tableName) {
        // TODO confirm table exists in database before attempting to delete OR catch exception
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE " + quotes(tableName));
    }

    private String internalGetCurrentTable() {
        return quotes(getCurrentTableName());
    }

    /**
     * Returns a list of all Exercises in the table with a name similar to the given search text.
     *
     * @param searchText to find Exercises
     * @return a list of Exercises with names similar to the given searchText
     */
    public ArrayList<Exercise> getQueryResults(String searchText) {
        ArrayList<Exercise> exercises = new ArrayList<>();
        try (Cursor cursor = getQuerySuggestions(searchText)) {
            if (cursor.moveToFirst()) {
                do {
                    exercises.add(createExerciseFromQueryResults(cursor));
                } while (cursor.moveToNext());
            }
        }
        return exercises;
    }

    /**
     * Given a query, returns a Cursor of the rows in the table with a "name" field
     * similar to the query.
     *
     * @param searchText to lookup an exercise by name
     * @return Cursor to access the query results
     */
    public Cursor getQuerySuggestions(String searchText) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT rowid _id, " + EXERCISE_NAME + " FROM " + internalGetCurrentTable() +
                " WHERE " + EXERCISE_NAME + " LIKE '%" + searchText + "%';", null);
    }

    private Exercise createExerciseFromQueryResults(Cursor cursor) {
        int id = cursor.getInt(ID_INDEX);
        String name = cursor.getString(NAME_INDEX);
        float weight = cursor.getFloat(WEIGHT_INDEX);
        String sets = cursor.getString(SETS_REPS_INDEX);
        String date = cursor.getString(DATE_INDEX);
        String imagePath = cursor.getString(IMAGE_PATH_INDEX);
        int targetID = cursor.getInt(TARGET_INDEX);
        int weightFlag = cursor.getInt(WEIGHT_FLAG_INDEX);

        return new Exercise(id, name, sets, weight, imagePath, date, targetID, weightFlag);
    }

    /**
     *  Given the ID of the desired target (e.g. Chest, Arms, Abs, etc.), returns an
     *  ArrayList of all the Exercises in the table associated with that ID, sorted
     *  alphabetically by name (if a sortType was provided).
     *
     * @param targetID the target ID
     * @param sortType the sorting criteria to use in the query (i.e. ascending/descending alphabetical order)
     *                 or null if the Exercises should not be sorted
     * @return a list of all Exercises associated with the given target ID
     */
    public ArrayList<Exercise> getSelectedExercises(int targetID, SortType sortType) {
        ArrayList<Exercise> exercises = new ArrayList<>();
        if (isValidTargetID(targetID)) {
            SQLiteDatabase db = this.getReadableDatabase();
            String orderBy = sortType != null ? EXERCISE_NAME + " COLLATE NOCASE " + sortType.toString() : null;
            try (Cursor cursor = db.query(internalGetCurrentTable(), null, EXERCISE_TARGET + " = ?",
                    new String[]{Integer.toString(targetID)}, null, null, orderBy)) {
                if (cursor.moveToFirst()) {
                    do {
                        exercises.add(createExerciseFromQueryResults(cursor));
                    } while (cursor.moveToNext());
                }
            }
        } else {
            Log.w("Invalid targetId", "The value of targetID ( " + targetID + ") did not match " +
                    "a valid target ID value; returning an empty list");
        }
        return exercises;
    }

    private boolean isValidTargetID(int targetID) {
        switch (targetID) {
            case Constants.CHEST:
            case Constants.LEGS:
            case Constants.BACK:
            case Constants.SHOULDERS:
            case Constants.ARMS:
            case Constants.ABS:
            case Constants.COMPOUND:
                return true;
            default:
                return false;
        }
    }

    /**
     * Updates an exercise's weight in the table.
     *
     * @param exerciseID the ID of the Exercise to update
     * @param weight the new weight to set for the Exercise
     */
    public void updateExerciseWeight(int exerciseID, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        String currentDate = formatter.format(calendar.getTime());

        String sql = "UPDATE " + internalGetCurrentTable() +
                " SET " + WEIGHT + " = " + weight + ", " +
                DATE + " = '" + currentDate + "'" +
                " WHERE ID = " + exerciseID + ";";
        db.execSQL(sql);
    }

    /**
     * Adds an exercise to the table.
     *
     * @param newExercise to add
     * @return the ID of the newly added row (i.e. the new Exercise's ID); an ID of -1
     *         indicates an error occurred when adding the Exercise to the table.
     */
    public int addExercise(Exercise newExercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        return addExercise(newExercise, db);
    }

    private int addExercise(Exercise newExercise, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(EXERCISE_NAME, newExercise.getExerciseName());
        values.put(WEIGHT, 0);
        values.put(SETS_REPS, newExercise.getSetsAndReps());
        values.put(DATE, Constants.DEFAULT_DATE);
        values.put(IMAGE_PATH, newExercise.getImageResourcePath());
        values.put(EXERCISE_TARGET, newExercise.getExerciseTarget());
        values.put(WEIGHT_FLAG, newExercise.getFlaggedForIncrease());
        long id = db.insert(internalGetCurrentTable(), null, values);
        if (id == -1) {
            Log.w("Add exercise failed", "Failed to add exercise " + newExercise.getExerciseName());
        } else {
            Log.d("Add exercise success", "Successfully added exercise " + newExercise.getExerciseName());
        }
        // TODO consider changing id attribute of Exercise to long instead of int so this cast isn't necessary
        return (int) id;
    }

    /**
     * Deletes an exercise from the table.
     *
     * @param exerciseID the ID of the Exercise to delete
     */
    public void deleteExercise(int exerciseID) {
        // Check if there's an image associated with the item to delete. If so, delete the image.
        // Note: This will ONLY delete an image taken using the "Take Image" button when adding a new
        // exercise. An image that was already on the device and was added using the "Add Image"
        // button will remain on the device.
        // TODO above note will become irrelevant once images that are selected on the device are copied to a directory known to the app

        String path = null;
        SQLiteDatabase db = this.getWritableDatabase();
        final String query = "SELECT " + IMAGE_PATH + " FROM " + internalGetCurrentTable() +
                " WHERE " + ID + " = " + exerciseID + ";";

        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {    // Check if cursor is empty
                path = cursor.getString(0);
            }

            if (FileUtil.deleteFile(path)) {
                Log.d("Delete",
                        "Successfully deleted file at " + path);
            } else {
                Log.d("Delete",
                        "Could not delete file at " + path);
            }

            // Then delete the exercise from the table
            if (db.delete(internalGetCurrentTable(), ID + "=" + exerciseID, null) > 0) {
                Log.d("Delete", "Successfully deleted exercise #" + exerciseID);
            } else {
                Log.d("Delete", "Could not delete exercise #" + exerciseID);
            }
        }
    }

    /**
     * Deletes images that were saved to the app's internal storage (i.e. taken
     * by the camera upon selecting the "Take Photo" button).
     *
     * @param exerciseId the ID of the Exercise whose image should be deleted
     * @param path the path to file to delete
     */
    public void deleteExerciseImage(int exerciseId, String path) {
        // TODO call this method within deleteExercise() above?

        // Delete the image if possible
        if (FileUtil.deleteFile(path)) {
            Log.d("Delete",
                "Successfully deleted file at " + path);
        } else {
            Log.d("Delete",
                "Could not delete file at " + path);
        }

        // Delete the path from the table
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.putNull(IMAGE_PATH);
        db.update(internalGetCurrentTable(), args, ID + " = " + exerciseId, null);
    }

    /**
     * Updates the Exercise's data within the table.
     *
     * @param exercise the Exercise to update
     */
    public void updateExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        final String query = "SELECT * FROM " + internalGetCurrentTable() +
                " WHERE " + ID + " = " + exercise.getExerciseID() + ";";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(1);
                String setsAndReps = cursor.getString(3);
                String imagePath = cursor.getString(5);

                String sql = "UPDATE " + internalGetCurrentTable() + " SET ";
                boolean updateNeeded = false;

                if (!name.equals(exercise.getExerciseName())) {
                    sql += EXERCISE_NAME + " = '" + exercise.getExerciseName() + "'";
                    updateNeeded = true;
                }

                if (!setsAndReps.equals(exercise.getSetsAndReps())) {
                    if (updateNeeded){
                        sql += ", " + SETS_REPS + " = '" + exercise.getSetsAndReps() + "'";
                    } else{
                        sql += SETS_REPS + " = '" + exercise.getSetsAndReps() + "'";
                        updateNeeded = true;
                    }
                }

                if (imagePath != null && exercise.getImageResourcePath() != null) {
                    if (!imagePath.equals(exercise.getImageResourcePath())) {
                        if (updateNeeded) {
                            sql += ", " + IMAGE_PATH + " = '" + exercise.getImageResourcePath() + "'";
                        } else{
                            sql += IMAGE_PATH + " = '" + exercise.getImageResourcePath() + "'";
                            updateNeeded = true;
                        }
                    }
                } else if (imagePath == null && exercise.getImageResourcePath() != null) {
                    if (updateNeeded) {
                        sql += ", " + IMAGE_PATH + " = '" + exercise.getImageResourcePath() + "'";
                    } else{
                        sql += IMAGE_PATH + " = '" + exercise.getImageResourcePath() + "'";
                        updateNeeded = true;
                    }
                }

                if (updateNeeded) {
                    sql += " WHERE " + ID + " = " + exercise.getExerciseID() + ";";
                    Log.d("SQL", "" + sql);
                    db.execSQL(sql);
                }
            }
        }
    }

    /**
     * Updates the given Exercise's weight flag in the table.
     *
     * @param exercise the Exercise to update
     */
    void updateExerciseFlag(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEIGHT_FLAG, exercise.getFlaggedForIncrease());
        db.update(internalGetCurrentTable(), contentValues, ID + " = ?", new String[] { Integer.toString(exercise.getExerciseID()) });
    }

    /*
     * The PPL routine below was retrieved from:
     * https://www.reddit.com/r/Fitness/comments/37ylk5/a_linear_progression_based_ppl_program_for/
     */
    private void createDefaultPPLTable(SQLiteDatabase db) {
        final ArrayList<Exercise> exercises = new ArrayList<>();

        /*
         * Pull
         */
        exercises.add(new Exercise("Deadlifts",
                "1x5+",
                0,
                Constants.COMPOUND));
        exercises.add(new Exercise("Barbell Rows",
                "4x5, 1x5+",
                0,
                Constants.BACK));
        exercises.add(new Exercise("Pulldowns",
                "3x8-12",
                0,
                Constants.BACK));
        exercises.add(new Exercise("Pullups",
                "3x8-12",
                0,
                Constants.COMPOUND));
        exercises.add(new Exercise("Chin-ups",
                "3x8-12",
                0,
                Constants.COMPOUND));
        exercises.add(new Exercise("Seated Cable Rows",
                "3x8-12",
                0,
                Constants.BACK));
        exercises.add(new Exercise("Chest Supported Rows",
                "3x8-12",
                0,
                Constants.BACK));
        exercises.add(new Exercise("Face Pulls",
                "5x15-20",
                0,
                Constants.SHOULDERS));
        exercises.add(new Exercise("Hammer Curls",
                "4x8-12",
                0,
                Constants.ARMS));
        exercises.add(new Exercise("Dumbbell Curls",
                "4x8-12",
                0,
                Constants.ARMS));

        /*
         * Push
         */
        exercises.add(new Exercise("Bench Press",
                "4x5, 1x5+ OR 3x8-12",
                0,
                Constants.CHEST));
        exercises.add(new Exercise("Overhead Press",
                "4x5, 1x5+ OR 3x8-12",
                0,
                Constants.COMPOUND));
        exercises.add(new Exercise("Incline Dumbbell Press",
                "3x8-12",
                0,
                Constants.CHEST));
        exercises.add(new Exercise("Triceps Pushdowns",
                "3x8-12, SS Lateral Raises",
                0,
                Constants.ARMS));
        exercises.add(new Exercise("Lateral Raises",
                "3x15-20",
                0,
                Constants.SHOULDERS));
        exercises.add(new Exercise("Overhead Triceps Extensions",
                "3x8-12, SS Lateral Raises",
                0,
                Constants.ARMS));

        /*
         * Legs
         */
        exercises.add(new Exercise("Squats",
                "2x5, 1x5+",
                0,
                Constants.LEGS));
        exercises.add(new Exercise("Romanian Deadlifts",
                "3x8-12",
                0,
                Constants.COMPOUND));
        exercises.add(new Exercise("Leg Press",
                "3x8-12",
                0,
                Constants.LEGS));
        exercises.add(new Exercise("Leg Curls",
                "3x8-12",
                0,
                Constants.LEGS));
        exercises.add(new Exercise("Calf Raises",
                "5x8-12",
                0,
                Constants.LEGS));


        for (Exercise exercise : exercises) {
            addExercise(exercise, db);
        }
    }

    /*
     * The cutting routine below was retrieved from:
     * https://www.bodybuilding.com/content/ryan-hughes-cutting-program.html
     */
    private void createDefaultCuttingTable() {
        final ArrayList<Exercise> exercises = new ArrayList<>();

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

        for (Exercise exercise : exercises) {
            addExercise(exercise);
        }
    }
}
