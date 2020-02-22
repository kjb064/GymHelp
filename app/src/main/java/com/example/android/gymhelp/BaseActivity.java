package com.example.android.gymhelp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseActivity extends AppCompatActivity {

    protected DatabaseHelper myDb;
    protected TabLayout tabLayout;
    protected TargetAdapter adapter;
    protected ViewPager viewPager;
    protected CheckBox checkBox;
    protected View dialogLayout;
    Exercise newExercise;
    public static String currentPhotoPath = "";    // name of file saved by camera
    protected final int REQUEST_IMAGE_CAPTURE = 1;
    protected final int PICK_IMAGE = 100;
    protected final int STORAGE_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);
    } // end onCreate

    /*
     * This method is called when the FloatingActionButton is clicked to add a new exercise to the currently
     * displayed TargetFragment.
     */
    public void onClickAddButton(View view){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int tabPosition = tabLayout.getSelectedTabPosition();
        String tabName = (String) adapter.getPageTitle(tabPosition);
        builder.setTitle("Add new " + tabName + " exercise");

        dialogLayout = View.inflate(getApplicationContext(), R.layout.add_exercise_dialog, null);
        checkBox = dialogLayout.findViewById(R.id.photo_check_box);

        builder.setView(dialogLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText nameEditText = dialogLayout.findViewById(R.id.name_edit_text);
                EditText setsRepsEditText = dialogLayout.findViewById(R.id.sets_reps_edit_text);

                // Check that both fields have been filled in
                if(nameEditText.getText().toString().trim().length() == 0
                        || setsRepsEditText.getText().toString().trim().length() == 0){
                    Toast.makeText(getApplicationContext(), "Empty texts fields are not allowed.", Toast.LENGTH_SHORT).show();
                }
                else{
                    newExercise = new Exercise(nameEditText.getText().toString(),
                            setsRepsEditText.getText().toString(),
                            tabPosition);

                    if(!currentPhotoPath.isEmpty()){
                        newExercise.setImageResourcePath(currentPhotoPath);
                        currentPhotoPath = "";
                    }
                    else{
                        newExercise.setImageResourcePath(Constants.NO_IMAGE_PROVIDED);
                    }

                    myDb.addExercise(newExercise);

                    // Reset the current Fragment's data
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(Integer.toString(tabPosition));

                    if(fragment != null){
                        ((TargetFragment) fragment).resetFragmentData();
                    }
                    else{
                        Log.d("Null Fragment", "fragment is null");
                    }
                }
            }
        });

        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    } // end onClickAddButton

    /*
     * This method is called when the "Add Photo" button of add_exercise_dialog layout is clicked.
     * It creates an intent to bring the user to the gallery (assuming permission to do so has already
     * been granted). Only images are selectable from the gallery.
     */
    public void onClickAddPhotoButton(View view){

        // Verify that permission to read external storage has been granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){

            // Save a selected photo to app...
            Intent gallery =
                    new Intent(Intent.ACTION_PICK);
            gallery.setDataAndType(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI,"image/*");
            startActivityForResult(gallery, PICK_IMAGE);
        }
        else {
            requestStoragePermission();
        }

    } // end onClickAddPhotoButton


    /*
     * Given a file's URI, returns the file's path on the device.
     */
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    } // end getPath

    /*
     * This method is called when the "Take Photo" button of the add_exercise_dialog layout is clicked.
     * It creates an intent to load the device's camera, assuming it exists.
     */
    public void onClickTakePhotoButton(View view){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Something went wrong. Could not create file.", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.gymhelp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    } // end onClickTakePhotoButton

    /*
     * This method is called by onClickTakePhotoButton to create a File for a photo taken by the camera.
     * The files created by this method have unique names due to the use of a timestamp.
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;

    } // end createImageFile

    /*
     * This method will be called the first time the user ever tries to click the "Add Photo" button.
     * A dialog asking the user for permission to access their device's files will be displayed with the
     * options to agree or decline.
     */
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to access images saved to this device.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BaseActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    } // end requestStoragePermission
}
