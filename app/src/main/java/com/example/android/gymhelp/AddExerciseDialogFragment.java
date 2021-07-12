package com.example.android.gymhelp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DialogFragment for the "Add Exercise" dialog.
 */
public class AddExerciseDialogFragment extends DialogFragment {

    protected final int REQUEST_IMAGE_CAPTURE = 1;
    protected final int STORAGE_PERMISSION_CODE = 2;
    protected final int PICK_IMAGE = 100;

    /**
     * Creates a new AddExerciseDialogFragment with the given title as an argument.
     *
     * @param title the title of the dialog
     * @return a new AddExerciseDialogFragment
     */
    public static AddExerciseDialogFragment newInstance(String title) {
        AddExerciseDialogFragment dialogFragment = new AddExerciseDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString("Title", title);
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View dialogLayout = inflater.inflate(R.layout.add_exercise_dialog, container);

        Bundle arguments = getArguments();
        if (arguments != null) {
            String title = arguments.getString("Title");
            TextView titleTextView = dialogLayout.findViewById(R.id.title_text_view);
            titleTextView.setText(title);
        }

        Button okButton = dialogLayout.findViewById(R.id.ok_button);
        okButton.setOnClickListener((view) -> onClickPositiveButton(dialogLayout));

        Button dismissButton = dialogLayout.findViewById(R.id.dismiss_button);
        dismissButton.setOnClickListener((view) -> getDialog().dismiss());

        Button addPhotoButton = dialogLayout.findViewById(R.id.add_photo_button);
        addPhotoButton.setOnClickListener(this::onClickAddPhotoButton);

        Button takePhotoButton = dialogLayout.findViewById(R.id.take_photo_button);
        takePhotoButton.setOnClickListener(this::onClickTakePhotoButton);

        return dialogLayout;
    }

    private void onClickPositiveButton(View dialogLayout) {
        EditText nameEditText = dialogLayout.findViewById(R.id.name_edit_text);
        EditText setsRepsEditText = dialogLayout.findViewById(R.id.sets_reps_edit_text);
        TextView photoPathTextView = dialogLayout.findViewById(R.id.photo_path_text_view);

        final String name = nameEditText.getText().toString().trim();
        final String setsReps = setsRepsEditText.getText().toString().trim();
        final String photoPath = photoPathTextView.getText().toString().isEmpty() ?
                Constants.NO_IMAGE_PROVIDED
                : photoPathTextView.getText().toString();

        // TODO investigate if possible to disable OK button until all text fields have text.
        // TODO would need to ensure the same restriction is applied for the dialog after selecting the "Edit" option
        // Check that both fields have been filled in
        if (name.isEmpty() || setsReps.isEmpty()) {
            Toast.makeText(getActivity(), "Empty texts fields are not allowed.", Toast.LENGTH_SHORT).show();
        } else {
            submitExercise(name, setsReps, photoPath);
        }
        getDialog().dismiss();
    }

    /**
     * Uses the given parameters to add a new Exercise to the database.
     *
     * @param name the name of the Exercise
     * @param setsReps the sets and reps for the Exercise
     * @param photoPath the path to the photo associated with the Exercise
     */
    protected void submitExercise(String name, String setsReps, String photoPath) {
        TargetFragment parentFragment = (TargetFragment) getParentFragment();
        if (parentFragment != null) {
            parentFragment.addNewExerciseToDatabaseAndRefresh(name, setsReps, photoPath);
        }
    }

    /**
     * Called when the "Take Photo" button of the add_exercise_dialog layout is clicked.
     * Creates an Intent to load the device's camera, assuming it exists.
     *
     * @param button the "Take Photo" button
     */
    public void onClickTakePhotoButton(View button) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        final Activity activity = getActivity();
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            try {
                // Create the File where the photo should go
                File photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.example.android.gymhelp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(activity, "Something went wrong. Could not create file.", Toast.LENGTH_SHORT).show();
                Log.e("Error creating File", Log.getStackTraceString(ex));
            }
        }
    }

    /**
     * Called by onClickTakePhotoButton to create a File for a photo taken by the camera.
     * The files created by this method have unique names due to the use of a timestamp.
     *
     * @return the image File
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir     /* directory */
        );
    }

    /**
     * Called when the "Add Photo" button of add_exercise_dialog layout is clicked.
     * Creates an Intent to bring the user to the gallery (assuming permission to do so has already
     * been granted). Only images are selectable from the gallery.
     *
     * @param addPhotoButton the "Add Photo" Button
     */
    public void onClickAddPhotoButton(View addPhotoButton) {
        // Verify that permission to read external storage has been granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

            // Save a selected photo to app...
            Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            gallery.setType("image/*");
            startActivityForResult(gallery, PICK_IMAGE);
            // TODO determine if this method is better than above (UPDATE: Method above works with the way the code is currently written...)
//            Intent gallery = new Intent();
//            gallery.setType("image/*");
//            gallery.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(gallery, "Select image"), PICK_IMAGE);
        } else {
            requestStoragePermission();
        }
    }

    /**
     * Called the first time the user ever tries to click the "Add Photo" button.
     * A dialog asking the user for permission to access their device's files will be displayed with the
     * options to agree or decline.
     */
    private void requestStoragePermission() {
        Activity activity = getActivity();
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new android.support.v7.app.AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed")
                    .setMessage("Permission is required to access images saved to this device.")
                    .setPositiveButton("Accept", (dialog, which) -> ActivityCompat.requestPermissions(activity,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO for image selection, should create a copy of the image and save it within a directory only accessible by the application.
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                handleImageCapture(resultCode, data);
                break;
            case PICK_IMAGE:
                handlePickImage(resultCode, data);
                break;
            default:
                Log.e("onActivityResult()", "Value of 'requestCode' did not " +
                        "match a valid value");
                break;
        }
    }

    /**
     * Handles the case where the user has just successfully taken a picture using the camera
     * on their device.
     *
     * @param resultCode the result code
     * @param data the result data
     */
    private void handleImageCapture(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String imagePath = getPath(data.getData());
            View dialogLayout = getView();
            if (dialogLayout != null) {
                // Store the imagePath in the textView for later retrieval
                TextView photoPathTextView = dialogLayout.findViewById(R.id.photo_path_text_view);
                photoPathTextView.setText(imagePath);

                CheckBox checkBox = dialogLayout.findViewById(R.id.photo_check_box);
                checkBox.setClickable(true);
                checkBox.setText(R.string.photo_selected);
                checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!isChecked) {
                        if (!imagePath.isEmpty()) {
                            File deleteFile = new File(imagePath);
                            if (deleteFile.delete()) {
                                Log.d("Image deletion", "Successfully deleted file at " + imagePath);
                            } else {
                                Log.e("Image deletion", "Could not delete file at " + imagePath);
                            }
                        }
                        checkBox.setClickable(false);
                        checkBox.setText(R.string.no_photo_selected);
                    }
                });
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("handleImageCapture()", "The user cancelled or an error occurred.");
        }
    }

    /**
     * Handles the case where the user has just selected an image file that was saved on their
     * device.
     *
     * @param resultCode the result code
     * @param data the result data
     */
    private void handlePickImage(int resultCode, Intent data) {
        // TODO: Verify the selected file is a valid type?
        if (resultCode == Activity.RESULT_OK) {
            final String photoPath = getPath(data.getData());
            View dialogLayout = getView();
            if (dialogLayout != null) {
                TextView photoPathTextView = dialogLayout.findViewById(R.id.photo_path_text_view);
                photoPathTextView.setText(photoPath);

                CheckBox checkBox = dialogLayout.findViewById(R.id.photo_check_box);
                checkBox.setClickable(true);
                checkBox.setText(R.string.photo_selected);
                checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!isChecked) {
                        checkBox.setClickable(false);
                        checkBox.setText(R.string.no_photo_selected);
                    }
                });
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("handlePickImage()", "The user cancelled or an error occurred.");
        }
    }

    /**
     * Given a file's URI, returns the file's path on the device.
     *
     * @param uri the the file's URI
     * @return the path to the file as a String
     */
    public String getPath(Uri uri) {
        // TODO verify what is being done here is necessary and if this is the correct way to do this
        // TODO DATA is deprecated...
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        getActivity().startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }
}
