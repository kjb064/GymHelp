package com.example.android.gymhelp;

/**
 * Represents an Exercise stored in a table in the database.
 */
public class Exercise {

    private int exerciseID;
    private final String exerciseName;
    private final String setsAndReps;
    private String imageResourcePath = Constants.NO_IMAGE_PROVIDED;
    private final float recentWeight;
    private final int exerciseTarget;
    private String date = Constants.DEFAULT_DATE;

    /**
     * Constructor for a new Exercise.
     *
     * @param exerciseName the name of the Exercise
     * @param setsAndReps the sets and reps
     * @param recentWeight the recent weight
     * @param exerciseTarget the target ID
     */
    public Exercise(String exerciseName, String setsAndReps, float recentWeight, int exerciseTarget) {
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        this.exerciseTarget = exerciseTarget;
    }

    /**
     * Constructor for conveniently initializing all properties of an Exercise using data obtained from
     * a table in the database.
     *
     * @param exerciseID the ID of the Exercise
     * @param exerciseName the name of the Exercise
     * @param setsAndReps the sets and reps
     * @param recentWeight the recent weight
     * @param imageResourcePath the path to the associated image
     * @param date the date the Exercise's recent weight was last updated
     * @param exerciseTarget the target ID
     */
    public Exercise(int exerciseID, String exerciseName, String setsAndReps, float recentWeight,
                    String imageResourcePath, String date, int exerciseTarget) {
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        this.imageResourcePath = imageResourcePath;
        this.date = date;
        this.exerciseTarget = exerciseTarget;
    }

    public int getExerciseID() {
        return this.exerciseID;
    }

    public void setExerciseID(int exID) {
        this.exerciseID = exID;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getSetsAndReps() {
        return setsAndReps;
    }

    public float getRecentWeight() {
        return recentWeight;
    }

    /**
     * @return whether this Exercise has an associated image or not
     */
    public boolean hasImagePath() {
        return imageResourcePath != null && !Constants.NO_IMAGE_PROVIDED.equals(imageResourcePath);
    }

    public String getDate() {
        return date;
    }

    public int getExerciseTarget() {
        return this.exerciseTarget;
    }

    public void setImageResourcePath(String path) {
        this.imageResourcePath = path;
    }

    public String getImageResourcePath() {
        return this.imageResourcePath;
    }
}
