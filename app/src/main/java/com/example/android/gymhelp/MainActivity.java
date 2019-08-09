package com.example.android.gymhelp;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private TabLayout tabLayout;
    private TargetAdapter adapter;
    private ViewPager viewPager;
    private CheckBox checkBox;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int PICK_IMAGE = 100;
    private final int STORAGE_PERMISSION_CODE = 2;

    Exercise newExercise;
    public static String currentPhotoPath = "";    // name of file saved by camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        adapter = new TargetAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tabs
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()
        tabLayout.setupWithViewPager(viewPager);

    } // end onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        return true;
    }

    public void onClickAddButton(View view){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int tabPosition = tabLayout.getSelectedTabPosition();
        String tabName = (String) adapter.getPageTitle(tabPosition);
        builder.setTitle("Add new " + tabName + " exercise");

        LayoutInflater inflater = getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.add_exercise_dialog, null);

        builder.setView(dialoglayout);
        checkBox = (CheckBox) dialoglayout.findViewById(R.id.photo_check_box);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText nameEditText = (EditText) dialoglayout.findViewById(R.id.name_edit_text);
                EditText setsRepsEditText = (EditText) dialoglayout.findViewById(R.id.sets_reps_edit_text);

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
                    if(viewPager.getCurrentItem() != tabPosition) Log.d("hello", "not tab pos");
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(Integer.toString(tabPosition));

                    if(fragment != null){
                        ((TargetFragment) fragment).resetFragmentData();
                    }
                    else{
                        Log.d("hello", "fragment is null");
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
    }

    public void onClickAddPhotoButton(View view){

        // Verify that permission to read external storage has been granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){

            // Save a selected photo to app...
            Intent gallery =
                    new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        }
        else {
            requestStoragePermission();
        }

    } // end onClickAddPhotoButton

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            checkBox.setClickable(true);
            checkBox.setText(R.string.photo_selected);
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        if(!currentPhotoPath.isEmpty()){
                            File deleteFile = new File(currentPhotoPath);
                            if(deleteFile.delete()){
                                Log.d("Delete", "Successfully deleted file at " + currentPhotoPath);
                            }
                            else {
                                Log.d("Delete", "Could not delete file at " + currentPhotoPath);
                            }

                            currentPhotoPath = "";
                        }
                        checkBox.setClickable(false);
                        checkBox.setText(R.string.no_photo_selected);
                    }
                }
            });
        }
        else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            /*The result returns the Uri ("address") of the selected picture. */
            Uri imageUri = data.getData();
            currentPhotoPath = getPath(imageUri);
            Log.d("Path", "" + currentPhotoPath);

            checkBox.setClickable(true);
            checkBox.setText(R.string.photo_selected);
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        if(!currentPhotoPath.isEmpty()){
                            currentPhotoPath = "";
                        }
                        checkBox.setClickable(false);
                        checkBox.setText(R.string.no_photo_selected);
                    }
                }
            });
        }
    } // end onActivityResult

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

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

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to access images saved to this device.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
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
