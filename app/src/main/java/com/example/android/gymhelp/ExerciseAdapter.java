package com.example.android.gymhelp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ExerciseAdapter extends ArrayAdapter<Exercise> {

    private final Context context;
    private final Resources resources;

    public ExerciseAdapter(Context context, ArrayList<Exercise> exercises) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, exercises);
        this.context = context;
        this.resources = context.getResources();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the object located at this position in the list
        Exercise currentExercise = getItem(position);

        final String exerciseId = Integer.toString(currentExercise.getExerciseID());
        TextView idTextView = listItemView.findViewById(R.id.exercise_ID);
        idTextView.setText(exerciseId);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView exerciseTextView = listItemView.findViewById(R.id.exercise_text_view);

        // Get the name from the current object and
        // set this text on the name TextView
        exerciseTextView.setText(currentExercise.getExerciseName());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView setTextView = listItemView.findViewById(R.id.set_text_view);

        setTextView.setText(currentExercise.getSetsAndReps());

        final String exerciseWeight = "Weight: " + currentExercise.getRecentWeight() + " lbs.";
        TextView weightTextView = listItemView.findViewById(R.id.weight_text_view);
        weightTextView.setText(exerciseWeight);

        TextView dateTextView = listItemView.findViewById(R.id.date_text_view);
        String date = context.getResources().getString(R.string.weight_updated) + " " + currentExercise.getDate();
        dateTextView.setText(date);

        TextView increaseWeightTextView = listItemView.findViewById(R.id.increase_weight_text_view);
        int visibility = currentExercise.getFlaggedForIncrease() == 1 ? View.VISIBLE : View.INVISIBLE;
        increaseWeightTextView.setVisibility(visibility);

        displayExerciseImage(listItemView, currentExercise);

        // Set the theme color for the list item
        View textContainer = listItemView.findViewById(R.id.text_container);

        // Find the color the resource ID maps to
        int color = ContextCompat.getColor(getContext(), R.color.title_color);

        // Set the background color of the text_container view
        textContainer.setBackgroundColor(color);

        // Return the whole list item layout
        // so that it can be shown in the ListView
        return listItemView;
    }

    private void displayExerciseImage(View listItemView, Exercise currentExercise) {
        // Find the ImageView in the list_item.xml layout with the ID "image"
        ImageView iconView = listItemView.findViewById(R.id.image);
        if (currentExercise.hasImage()) {
            // Get the dimensions of the View
            int dimension = (int) resources.getDimension(R.dimen.list_item_height);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            File externalStorage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(externalStorage, currentExercise.getImageFileName());
            BitmapFactory.decodeFile(imageFile.getPath(), bmOptions);
            int photoWidth = bmOptions.outWidth;
            int photoHeight = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoWidth / dimension, photoHeight / dimension);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), bmOptions);
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                int rotationAngle = getImageRotationAngle(imageFile);
                matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bmOptions.outWidth, bmOptions.outHeight, matrix, true);
                iconView.setImageBitmap(bitmap);
            } else {
                // Failed to get image bitmap, use default image
                loadDefaultImage(iconView);
                Toast.makeText(context, "Could not load image for " + currentExercise.getExerciseName()
                        + " from " + currentExercise.getImageFileName(), Toast.LENGTH_LONG).show();
            }
        } else {
            // otherwise, use default image for iconView
            loadDefaultImage(iconView);
        }
    }

    /**
     * Determines the orientation of the image and returns the degrees of rotation required to display
     * the image properly in an ImageView.
     *
     * @param imageFile the image file
     * @return the degrees of rotation to properly display the image
     */
    private int getImageRotationAngle(File imageFile) {
        int rotationAngle = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(imageFile);
            String orientString = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            switch (orientation) {
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
            Log.e("getImageRotationAngle()", "Error occurred while retrieving file " +
                    "descriptor for " + imageFile.getName() +"; Returning 0", e);
        }
        return rotationAngle;
    }

    /**
     * Loads the default image into the given ImageView.
     *
     * @param iconView to set the image for
     */
    private void loadDefaultImage(ImageView iconView) {
        int imageID = resources.getIdentifier("baseline_image_black_48dp", "drawable", context.getPackageName());
        iconView.setImageResource(imageID);
    }
}
