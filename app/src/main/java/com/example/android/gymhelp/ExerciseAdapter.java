package com.example.android.gymhelp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;

public class ExerciseAdapter extends ArrayAdapter<Exercise> {

    private int backgroundColor;
    private Context context;
    private Resources resources;
    private int imageID;
    private String defaultImageName = "baseline_image_black_48dp";

    public ExerciseAdapter(Context context, ArrayList<Exercise> exercises, int color) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, exercises);
        backgroundColor = color;
        this.context = context;
        this.resources = context.getResources();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the object located at this position in the list
        Exercise currentExercise = getItem(position);

        TextView idTextView = (TextView) listItemView.findViewById(R.id.exercise_ID);
        idTextView.setText(Integer.toString(currentExercise.getExerciseID()));

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView exerciseTextView = (TextView) listItemView.findViewById(R.id.exercise_text_view);

        // Get the name from the current object and
        // set this text on the name TextView
        exerciseTextView.setText(currentExercise.getExerciseName());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView setTextView = (TextView) listItemView.findViewById(R.id.set_text_view);

        setTextView.setText(currentExercise.getSetsAndReps());

        TextView weightTextView = (TextView) listItemView.findViewById(R.id.weight_text_view);
        weightTextView.setText("Weight: " + Float.toString(currentExercise.getRecentWeight()) + " lbs.");

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);
        String date = context.getResources().getString(R.string.weight_updated) + " " + currentExercise.getDate();
        dateTextView.setText(date);

        // Find the ImageView in the list_item.xml layout with the ID "image"
        ImageView iconView = (ImageView) listItemView.findViewById(R.id.image);

        if(currentExercise.hasImagePath()){

            // Get the dimensions of the View
            int targetW = (int) resources.getDimension(R.dimen.list_item_height);
            int targetH = targetW;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentExercise.getImageResourcePath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            // Determine the orientation, adjust if necessary
            String orientString;
            int orientation;
            int rotationAngle = 0;
            try {
                ExifInterface exifInterface = new ExifInterface(currentExercise.getImageResourcePath());
                orientString = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

                switch (orientation){
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotationAngle = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotationAngle = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                         rotationAngle = 270;
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeFile(currentExercise.getImageResourcePath(), bmOptions);
            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bmOptions.outWidth, bmOptions.outHeight, matrix, true);

            if(bitmap != null) {
                iconView.setImageBitmap(bitmap);
            }
            else{
                // Failed to get image bitmap, use default image
                imageID = resources.getIdentifier(defaultImageName, "drawable", context.getPackageName());
                iconView.setImageResource(imageID);
                Toast.makeText(context, "Could not load image for " + currentExercise.getExerciseName()
                + " from " + currentExercise.getImageResourcePath(), Toast.LENGTH_LONG).show();
            }

        }
        else {

            // otherwise, use default image for iconView
            imageID = resources.getIdentifier(defaultImageName, "drawable", context.getPackageName());
            iconView.setImageResource(imageID);
        }

        // Set the theme color for the list item
        View textContainer = listItemView.findViewById(R.id.text_container);

        // Find the color the resource ID maps to
        int color = ContextCompat.getColor(getContext(), backgroundColor);

        // Set the background color of the text_container view
        textContainer.setBackgroundColor(color);

        // Return the whole list item layout
        // so that it can be shown in the ListView
        return listItemView;
    } // end getView

}
