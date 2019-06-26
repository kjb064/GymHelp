package com.example.android.gymhelp;

import java.util.Date;

public class Exercise {

    private int exerciseID;
    private String exerciseName = "";
    private String setsAndReps = "";
    //private int NO_IMAGE_PROVIDED = -1;
    //private int imageResourceID = NO_IMAGE_PROVIDED;

    private String NO_IMAGE_PROVIDED = "NONE";
    private String imageResourceName = NO_IMAGE_PROVIDED;

    private int recentWeight = 0;
    private int exerciseTarget;
    private Date date;

    // MAY HAVE TO CHANGE HOW recentWeight is initialized
    public Exercise(String exerciseName, String setsAndReps, String imageResourceName, int recentWeight, int exerciseTarget){
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.imageResourceName = imageResourceName;
        this.recentWeight = recentWeight;
        this.exerciseTarget = exerciseTarget;
        date = new Date();
    }

    public Exercise(String exerciseName, String setsAndReps, int exerciseTarget){
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        //this.recentWeight = recentWeight;
        this.exerciseTarget = exerciseTarget;
        date = new Date();
    }



    /*
    * This constructor is used by the DatabaseHelper to create Exercise objects for an ArrayList that is passed
    * to the corresponding "target" fragment.
    * */
    public Exercise(int exerciseID, String exerciseName, String setsAndReps, int recentWeight, String imageResourceName){
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        this.imageResourceName = imageResourceName;
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

    //public int getImageResourceID() {
    //    return imageResourceID;
    //}

    public String getImageResourceName(){ return this.imageResourceName; }

    public int getRecentWeight() {
        return recentWeight;
    }

    //public void setImageResourceID(int id){
    //    this.imageResourceID = id;
    //}

    public void setImageResourceName(String name){ this.imageResourceName = name; }

    //public boolean hasImage() {
    //    return imageResourceID != NO_IMAGE_PROVIDED;
    //}

    public boolean hasImage() {
        return ! (imageResourceName != null && NO_IMAGE_PROVIDED.equals(imageResourceName));
    }

    public Date getDate() {
        return date;
    }

    public int getExerciseTarget(){ return this.exerciseTarget; }

    public void setExerciseTarget(int target){ this.exerciseTarget = target; }
}
