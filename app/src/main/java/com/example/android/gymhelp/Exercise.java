package com.example.android.gymhelp;

import java.util.Date;

public class Exercise {

    private int exerciseID;
    private String exerciseName = "";
    private String setsAndReps = "";
    private int NO_IMAGE_PROVIDED = -1;
    private int imageResourceID = NO_IMAGE_PROVIDED;
    private int recentWeight = 0;
    private int exerciseTarget;
    private Date date;

    // MAY HAVE TO CHANGE HOW recentWeight is initialized
    public Exercise(String exerciseName, String setsAndReps, int imageID, int recentWeight, int exerciseTarget){
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.imageResourceID = imageID;
        this.recentWeight = recentWeight;
        this.exerciseTarget = exerciseTarget;
        date = new Date();
    }

    /*public Exercise(String exerciseName, String setsAndReps, int recentWeight){
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
    }*/

    public Exercise(int exerciseID, String exerciseName, String setsAndReps, int recentWeight){
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        date = new Date();
    }

    public Exercise(int exerciseID, String exerciseName, String setsAndReps, int recentWeight, int imageResourceID){
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        this.imageResourceID = imageResourceID;
        date = new Date();
    }

    public int getExerciseID(){
        return this.exerciseID;
    }

    public void setExerciseID(int exID){
        this.exerciseID = exID;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getSetsAndReps() {
        return setsAndReps;
    }

    public int getImageResourceID() {
        return imageResourceID;
    }

    public int getRecentWeight() {
        return recentWeight;
    }

    public void setImageResourceID(int id){
        this.imageResourceID = id;
    }

    public boolean hasImage() {
        return imageResourceID != NO_IMAGE_PROVIDED;
    }

    public Date getDate() {
        return date;
    }

    public int getExerciseTarget(){ return this.exerciseTarget; }

    public void setExerciseTarget(int target){ this.exerciseTarget = target; }
}
