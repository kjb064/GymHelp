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
import android.text.Editable;
import android.text.TextWatcher;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DialogFragment for the "Add Exercise" dialog.
 */
public class AddExerciseDialogFragment extends DialogFragment {
    protected EditText nameEditText;
    protected EditText setsRepsEditText;

    protected final int REQUEST_IMAGE_CAPTURE = 1;
    protected final int STORAGE_PERMISSION_CODE = 2;
    protected final int PICK_IMAGE = 100;

    private String imageFileName;

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

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = nameEditText.getText().toString().trim();
                String setsReps = setsRepsEditText.getText().toString().trim();
                okButton.setEnabled(!name.isEmpty() && !setsReps.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        };

        this.nameEditText = dialogLayout.findViewById(R.id.name_edit_text);
        this.nameEditText.addTextChangedListener(textWatcher);
        this.setsRepsEditText = dialogLayout.findViewById(R.id.sets_reps_edit_text);
        this.setsRepsEditText.addTextChangedListener(textWatcher);

        Button addPhotoButton = dialogLayout.findViewById(R.id.add_photo_button);
        addPhotoButton.setOnClickListener(this::onClickAddPhotoButton);

        Button takePhotoButton = dialogLayout.findViewById(R.id.take_photo_button);
        takePhotoButton.setOnClickListener(this::onClickTakePhotoButton);

        return dialogLayout;
    }

    private void onClickPositiveButton(View dialogLayout) {
        TextView imageNameTextView = dialogLayout.findViewById(R.id.image_name_text_view);

        final String name = nameEditText.getText().toString().trim();
        final String setsReps = setsRepsEditText.getText().toString().trim();
        final String photoPath = imageNameTextView.getText().toString().isEmpty() ?
                null
                : imageNameTextView.getText().toString();

        submitExercise(name, setsReps, photoPath);
        getDialog().dismiss();
    }

    /**
     * Uses the given parameters to add a new Exercise to the database.
     *
     * @param name the name of the Exercise
     * @param setsReps the sets and reps for the Exercise
     * @param imageFileName the file name of the photo associated with the Exercise
     */
    protected void submitExercise(String name, String setsReps, String imageFileName) {
        TargetFragment parentFragment = (TargetFragment) getParentFragment();
        if (parentFragment != null) {
            parentFragment.addNewExerciseToDatabaseAndRefresh(name, setsReps, imageFileName);
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
        if (activity != null && takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                // Create the File where the photo should go
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(activity, "Something went wrong. Could not create file.", Toast.LENGTH_SHORT).show();
                Log.e("Error creating File", Log.getStackTraceString(ex));
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.example.android.gymhelp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
        Activity activity = getActivity();
        if (activity != null) {
            File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String name = "JPEG_" + timeStamp + "_";
            File imageFile = File.createTempFile(
                    name,  /* prefix */
                    ".jpg",   /* suffix */
                    storageDir     /* directory */
            );
            this.imageFileName = imageFile.getName();
            return imageFile;
        }
        return null;
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
        Activity activity = getActivity();
        if (activity != null) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {

                // Save a selected photo to app...
                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setDataAndType(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(gallery, PICK_IMAGE);
            } else {
                requestStoragePermission(activity);
            }
        }
    }

    /**
     * Called the first time the user ever tries to click the "Add Photo" button.
     * A dialog asking the user for permission to access their device's files will be displayed with the
     * options to agree or decline.
     *
     * @param activity the current Activity
     */
    private void requestStoragePermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new android.support.v7.app.AlertDialog.Builder(activity)
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
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                handleImageCapture(resultCode);
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
     */
    private void handleImageCapture(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            View dialogLayout = getView();
            if (dialogLayout != null) {
                // Store the image file name in the textView for later retrieval
                TextView fileNameTextView = dialogLayout.findViewById(R.id.image_name_text_view);
                fileNameTextView.setText(this.imageFileName);

                CheckBox checkBox = dialogLayout.findViewById(R.id.photo_check_box);
                checkBox.setClickable(true);
                checkBox.setText(R.string.photo_selected);
                checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!isChecked) {
                        if (this.imageFileName != null && !this.imageFileName.isEmpty()) {
                            File deleteFile = new File(this.imageFileName);
                            if (deleteFile.delete()) {
                                Log.d("Image deletion", "Successfully deleted file at " + this.imageFileName);
                            } else {
                                Log.e("Image deletion", "Could not delete file at " + this.imageFileName);
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

        this.imageFileName = null;
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
            boolean success = false;
            String path = getPath(data.getData());
            if (path != null) {
                File selectedFile = new File(path);
                try {
                    // Create File within external directory; will set value of imageFileName
                    File copiedFile = createImageFile();
                    // copy the selected file to external directory
                    try (FileInputStream inputStream = new FileInputStream(selectedFile);
                         FileOutputStream outputStream = new FileOutputStream(copiedFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    success = true;
                } catch (IOException e) {
                    // Error occurred while creating the File
                    Toast.makeText(getActivity(), "Something went wrong. Could not create file.", Toast.LENGTH_SHORT).show();
                    Log.e("Error creating File", Log.getStackTraceString(e));
                }
            }

            View dialogLayout = getView();
            if (dialogLayout != null) {
                CheckBox checkBox = dialogLayout.findViewById(R.id.photo_check_box);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!isChecked) {
                        checkBox.setClickable(false);
                        checkBox.setText(R.string.no_photo_selected);
                    }
                });

                TextView imageNameTextView = dialogLayout.findViewById(R.id.image_name_text_view);
                if (success) {
                    imageNameTextView.setText(this.imageFileName);
                    checkBox.setText(R.string.photo_selected);
                    checkBox.setClickable(true);
                    checkBox.setChecked(true);
                }
            }

            this.imageFileName = null;
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

        Activity activity = getActivity();
        if (activity != null) {
            // TODO DATA is deprecated...
            String[] projection = {MediaStore.Images.Media.DATA};
            try (Cursor cursor = activity.managedQuery(uri, projection, null,
                    null, null)) {
                activity.startManagingCursor(cursor);
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(columnIndex);
            }
        }
        return null;
    }
}
