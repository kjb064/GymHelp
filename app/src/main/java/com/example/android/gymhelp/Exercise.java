package com.example.android.gymhelp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents an Exercise stored in a table in the database.
 */
public class Exercise implements Parcelable {

    private int exerciseID;
    private String exerciseName;
    private String setsAndReps;
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

    public Exercise(Parcel source) {
        this.exerciseID = source.readInt();
        this.exerciseName = source.readString();
        this.setsAndReps = source.readString();
        this.recentWeight = source.readFloat();
        this.imageResourcePath = source.readString();
        this.date = source.readString();
        this.exerciseTarget = source.readInt();
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

    public void setExerciseName(String name) { this.exerciseName = name; }

    public String getSetsAndReps() {
        return setsAndReps;
    }

    public void setSetsAndReps(String setsReps) { this.setsAndReps = setsReps; }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.exerciseID);
        dest.writeString(this.exerciseName);
        dest.writeString(this.setsAndReps);
        dest.writeFloat(this.recentWeight);
        dest.writeString(this.imageResourcePath);
        dest.writeString(this.date);
        dest.writeInt(this.exerciseTarget);
    }

    public static final Parcelable.Creator<Exercise> CREATOR = new Parcelable.Creator<Exercise>() {

        @Override
        public Exercise createFromParcel(Parcel source) {
            return new Exercise(source);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
}
