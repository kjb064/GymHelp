package com.example.android.gymhelp;

public class Exercise {

    private int exerciseID;
    private String exerciseName = "";
    private String setsAndReps = "";
    private String imageResourcePath = Constants.NO_IMAGE_PROVIDED;
    private float recentWeight = 0;
    private int exerciseTarget;
    private String date = Constants.DEFAULT_DATE;
    private int flaggedForIncrease = 0;

    /*
    *   Used within the MainActivity's "onClickAddButton" method (after which the exercise
    *   is passed to the DatabaseHelper's "addExercise" method) and TargetFragment's
    *   "onContextItemSelected" method for the "Edit" menu option (after which the exercise
    *   is passed to the DatabaseHelper's "updateExercise" method).
     */
    public Exercise(String exerciseName, String setsAndReps, int exerciseTarget){
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.exerciseTarget = exerciseTarget;
    }

    /*
     *   Used within DatabaseHelper to add the default exercises; lacks any image-related parameters
     */
    public Exercise(String exerciseName, String setsAndReps, float recentWeight, int exerciseTarget){
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        this.exerciseTarget = exerciseTarget;
    }


    /*
    * This constructor is used by the DatabaseHelper to create Exercise objects for an ArrayList that is passed
    * to the corresponding "target" fragment.
    * */
    public Exercise(int exerciseID, String exerciseName, String setsAndReps, float recentWeight,
                    String imageResourcePath, String date, int flag){
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        this.imageResourcePath = imageResourcePath;
        this.date = date;
        this.flaggedForIncrease = flag;
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

    public float getRecentWeight() {
        return recentWeight;
    }

    public boolean hasImagePath(){
        if(imageResourcePath != null){
            return (! Constants.NO_IMAGE_PROVIDED.equals(imageResourcePath));
        }
        else{
            return false;
        }
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date){ this.date = date; }

    public int getExerciseTarget(){ return this.exerciseTarget; }

    public void setExerciseTarget(int target){ this.exerciseTarget = target; }

    public void setImageResourcePath(String path){
        this.imageResourcePath = path;
    }

    public String getImageResourcePath(){
        return this.imageResourcePath;
    }

    public void setFlaggedForIncrease(int flag){
        this.flaggedForIncrease = flag;
    }

    public int getFlaggedForIncrease(){
        return this.flaggedForIncrease;
    }
}
