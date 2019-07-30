package com.example.android.gymhelp;

public class Exercise {

    private int exerciseID;
    private String exerciseName = "";
    private String setsAndReps = "";
    private String imageResourceName = Constants.NO_IMAGE_PROVIDED;
    private String imageResourcePath = Constants.NO_IMAGE_PROVIDED;
    private float recentWeight = 0;
    private int exerciseTarget;
    //private Date date;
    private String date = Constants.DEFAULT_DATE;

    // MAY HAVE TO CHANGE HOW recentWeight is initialized
    public Exercise(String exerciseName, String setsAndReps, String imageResourceName, float recentWeight, int exerciseTarget){
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.imageResourceName = imageResourceName;
        this.recentWeight = recentWeight;
        this.exerciseTarget = exerciseTarget;
        //date = new Date();
    }

    public Exercise(String exerciseName, String setsAndReps, int exerciseTarget){
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        //this.recentWeight = recentWeight;
        this.exerciseTarget = exerciseTarget;
        //date = new Date();
    }

    /*
    *   Used within DatabaseHelper to add the default exercises; lacks any image-related parameters
     */
    public Exercise(String exerciseName, String setsAndReps, float recentWeight, int exerciseTarget){
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        this.exerciseTarget = exerciseTarget;
        this.imageResourceName = Constants.NO_IMAGE_PROVIDED;
    }


    /*
    * This constructor is used by the DatabaseHelper to create Exercise objects for an ArrayList that is passed
    * to the corresponding "target" fragment.
    * */
    public Exercise(int exerciseID, String exerciseName, String setsAndReps, float recentWeight,
                    String imageResourcePath, String date){
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.setsAndReps = setsAndReps;
        this.recentWeight = recentWeight;
        //this.imageResourceName = imageResourceName;
        this.imageResourcePath = imageResourcePath;
        //date = new Date();
        this.date = date;
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

    public float getRecentWeight() {
        return recentWeight;
    }

    public void setImageResourceName(String name){ this.imageResourceName = name; }

    public boolean hasImage() {
        return ! (imageResourceName != null && Constants.NO_IMAGE_PROVIDED.equals(imageResourceName));
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
}
