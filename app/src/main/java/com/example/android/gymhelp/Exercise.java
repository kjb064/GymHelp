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
    private String imageFileName;
    private final float recentWeight;
    private final int exerciseTarget;
    private String date = Constants.DEFAULT_DATE;
    private int flaggedForIncrease = 0;

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
     * @param imageFileName the file name of the associated image
     * @param date the date the Exercise's recent weight was last updated
     * @param exerciseTarget the target ID
     */
    public Exercise(int exerciseID, String exerciseName, String setsAndReps, float recentWeight,
                    String imageFileName, String date, int exerciseTarget, int weightFlag) {
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        this.imageFileName = imageFileName;
        this.date = date;
        this.exerciseTarget = exerciseTarget;
        this.flaggedForIncrease = weightFlag;
    }

    /**
     * Creates an Exercise from a Parcel.
     *
     * @param source the Parcel containing the data to initialize this Exercise
     */
    public Exercise(Parcel source) {
        this.exerciseID = source.readInt();
        this.exerciseName = source.readString();
        this.setsAndReps = source.readString();
        this.recentWeight = source.readFloat();
        this.imageFileName = source.readString();
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
    public boolean hasImage() {
        return imageFileName != null && !Constants.NO_IMAGE_PROVIDED.equals(imageFileName);
    }

    public String getDate() {
        return date;
    }

    public int getExerciseTarget() {
        return this.exerciseTarget;
    }

    public void setImageFileName(String name) {
        this.imageFileName = name;
    }

    public String getImageFileName() {
        return this.imageFileName;
    }

    void setFlaggedForIncrease(int flag) {
        this.flaggedForIncrease = flag;
    }

    int getFlaggedForIncrease() {
        return this.flaggedForIncrease;
    }

    // TODO determine what the return value should be
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
        dest.writeString(this.imageFileName);
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
