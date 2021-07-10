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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.android.gymhelp.Constants.DELETE_ID;
import static com.example.android.gymhelp.Constants.EDIT_ID;
import static com.example.android.gymhelp.Constants.READ_FULL_ID;

/**
 * Displays a ListView containing all exercises associated with a target group.
 * Allows for certain operations (such as deleting or updating) to be performed on an exercise in the ListView
 * via a context menu.
 */
public class TargetFragment extends Fragment {

    private ArrayList<Exercise> exercises;
    private ExerciseAdapter adapter;
    private DatabaseHelper db;
    private ListView listView;

    /** TODO make checkBox private/local to relevant method(s) */
    public CheckBox checkBox;
    protected View dialogLayout;
    //private String currentPhotoPath;

    private static final String GROUP_ID_KEY = "groupID";

    protected final int PICK_IMAGE = 100;
    protected final int REQUEST_IMAGE_CAPTURE = 1;
    protected final int STORAGE_PERMISSION_CODE = 2;

    /**
     * Creates a TargetFragment instance with the value of <code>targetGroupId</code> passed as one
     * of its arguments. The value of <code>targetGroupId</code> should match the index of the Fragment
     * within the ViewPager.
     *
     * @param targetGroupId the ID of the Target associated with the new Fragment
     * @return the new TargetFragment instance
     */
    public static TargetFragment createInstance(int targetGroupId) {
        TargetFragment fragment = new TargetFragment();
        Bundle args = new Bundle();
        args.putInt(GROUP_ID_KEY, targetGroupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());
    }

    /**
     * Fetches the exercises to be displayed in this TargetFragment's ListView.
     */
    private void fetchData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int targetID = bundle.getInt(GROUP_ID_KEY);
            exercises = db.getSelectedExercises(targetID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.exercise_list, container, false);
        fetchData();

        // Create an {@link ArrayAdapter}, whose data source is a list of Strings. The
        // adapter knows how to create layouts for each item in the list, using the
        // simple_list_item_1.xml layout resource defined in the Android framework.
        // This list item layout contains a single {@link TextView}, which the adapter will set to
        // display a single word.
        adapter = new ExerciseAdapter(getActivity(), exercises);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml file.
        listView = rootView.findViewById(R.id.list);

        registerForContextMenu(listView);

        // Make the {@link ListView} use the {@link ArrayAdapter} we created above, so that the
        // {@link ListView} will display list items for each word in the list of words.
        // Do this by calling the setAdapter method on the {@link ListView} object and pass in
        // 1 argument, which is the {@link ArrayAdapter} with the variable name itemsAdapter.
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ExerciseClickListener(getContext()) {
            @Override
            protected void refresh() {
                refreshFragment();
            }
        });

        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> onClickAddButton());
        return rootView;
    }

    /**
     * Checks this TargetFragment's arguments for the value of the target group ID and returns it.
     *
     * @return the value of the target group ID obtained from the arguments or -1 if there were no
     *         arguments
     */
    private int getTargetGroupId() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle.getInt(GROUP_ID_KEY);
        } else {
            Log.e("getGroupId()", "TargetFragment did not have arguments; " +
                    "returning -1.");
            // TODO add -1 to Constants?
            return -1;
        }
    }

    private View getAddExerciseDialogLayout() {
        LayoutInflater inflater = getLayoutInflater();
        // TODO STOPPED: figure out root to pass here
        View dialogLayout = inflater.inflate(R.layout.add_exercise_dialog, null);

        Button addPhotoButton = dialogLayout.findViewById(R.id.add_photo_button);
        addPhotoButton.setOnClickListener(this::onClickAddPhotoButton);
        Button takePhotoButton = dialogLayout.findViewById(R.id.take_photo_button);
        takePhotoButton.setOnClickListener(this::onClickTakePhotoButton);
        return dialogLayout;
    }

    /**
     * Called when the FloatingActionButton is clicked to add a new exercise to the currently
     * displayed TargetFragment.
     */
    public void onClickAddButton() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int groupId = getTargetGroupId();
        final String targetName = TargetAdapter.lookupTitle(groupId, getActivity());
        builder.setTitle("Add new " + targetName + " exercise");

        dialogLayout = getAddExerciseDialogLayout();

        checkBox = dialogLayout.findViewById(R.id.photo_check_box);
        builder.setView(dialogLayout);

        // TODO investigate if possible to disable OK button until all text fields have text.
        // TODO would need to ensure the same restriction is applied for the dialog after selecting the "Edit" option
        builder.setPositiveButton("OK", (dialog, which) -> {
            EditText nameEditText = dialogLayout.findViewById(R.id.name_edit_text);
            EditText setsRepsEditText = dialogLayout.findViewById(R.id.sets_reps_edit_text);
            TextView photoPathTextView = dialogLayout.findViewById(R.id.photo_path_text_view);

            final String name = nameEditText.getText().toString().trim();
            final String setsReps = setsRepsEditText.getText().toString().trim();
            final String photoPath = photoPathTextView.getText().toString();

            // Check that both fields have been filled in
            if (name.isEmpty() || setsReps.isEmpty()) {
                Toast.makeText(getActivity(), "Empty texts fields are not allowed.", Toast.LENGTH_SHORT).show();
            } else {
                Exercise newExercise = new Exercise(name, setsReps, 0, groupId);
                if (!photoPath.isEmpty()) {
                    newExercise.setImageResourcePath(photoPath);
                } else {
                    newExercise.setImageResourcePath(Constants.NO_IMAGE_PROVIDED);
                }

                db.addExercise(newExercise);
                resetFragmentData();
            }
        });

        builder.setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss());

        builder.show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO for image selection, should create a copy of the image and save it within a directory only accessible by the application.
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                handleImageCapture(resultCode, this, data);
                break;
            case PICK_IMAGE:
                handlePickImage(resultCode, this, data);
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
     * @param targetFragment the currently active TargetFragment
     */
    private void handleImageCapture(int resultCode, TargetFragment targetFragment, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String imagePath = getPath(data.getData());
            if (dialogLayout != null) {
                TextView photoPathTextView = dialogLayout.findViewById(R.id.photo_path_text_view);
                photoPathTextView.setText(imagePath);

                checkBox = dialogLayout.findViewById(R.id.photo_check_box);
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

            if (targetFragment != null && targetFragment.isVisible()
                    && targetFragment.checkBox != null) {
                targetFragment.checkBox.setChecked(true);
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
     * @param targetFragment the currently active TargetFragment
     * @param data the result data
     */
    private void handlePickImage(int resultCode, TargetFragment targetFragment, Intent data) {
        // TODO: Verify the selected file is a valid type?
        if (resultCode == Activity.RESULT_OK) {
            final String photoPath = getPath(data.getData());
            if (dialogLayout != null) {
                TextView photoPathTextView = dialogLayout.findViewById(R.id.photo_path_text_view);
                photoPathTextView.setText(photoPath);

                checkBox = dialogLayout.findViewById(R.id.photo_check_box);
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

            if (targetFragment != null && targetFragment.isVisible()
                    && targetFragment.checkBox != null) {
                targetFragment.checkBox.setChecked(true);
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

    /**
     * Called when the "Take Photo" button of the add_exercise_dialog layout is clicked.
     * Creates an Intent to load the device's camera, assuming it exists.
     */
    public void onClickTakePhotoButton(View view) {
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
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir     /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = image.getAbsolutePath();
        return image;
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

            new AlertDialog.Builder(getActivity())
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int groupID = getTargetGroupId();
        menu.add(groupID, READ_FULL_ID, 0, R.string.menu_read_full);
        menu.add(groupID, EDIT_ID, 0, R.string.menu_edit);
        menu.add(groupID, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO find better way to determine whether the fragment is visible? (getUserVisibleHint() may not be reliable...)
        final boolean isFragmentVisibleToUser = getUserVisibleHint();
        if (isFragmentVisibleToUser) {
            Activity activity = getActivity();
            if (item.getGroupId() == getTargetGroupId() && activity != null) {
                final AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final Exercise exercise = exercises.get((int) info.id);

                switch (item.getItemId()) {
                    case READ_FULL_ID: {
                        displayReadFullDialog(exercise, activity, getLayoutInflater());
                        return true;
                    }
                    case EDIT_ID: {
                        displayEditDialog(exercise, activity);
                        return true;
                    }
                    case DELETE_ID: {
                        displayDeleteDialog(exercise, activity);
                        return true;
                    }
                    default:
                        return super.onContextItemSelected(item);
                }
            }
        }

        return false;
    }

    /**
     * Creates and displays the "Read Full" dialog where all of the relevant details of the selected
     * Exercise are displayed.
     *
     * @param exercise whose details to display in the dialog
     * @param activity the Activity this TargetFragment is associated with
     * @param inflater to inflate the Read Full dialog layout
     */
    public static void displayReadFullDialog(final Exercise exercise, final Activity activity, LayoutInflater inflater) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(exercise.getExerciseName());

        final View readFullLayout = inflater.inflate(R.layout.read_full, null);
        builder.setView(readFullLayout);

        final TextView fullSetsReps = readFullLayout.findViewById(R.id.full_sets_reps);
        String text = fullSetsReps.getText() + exercise.getSetsAndReps();
        fullSetsReps.setText(text);

        final TextView fullWeight = readFullLayout.findViewById(R.id.full_weight);
        text = fullWeight.getText() + "" + exercise.getRecentWeight();
        fullWeight.setText(text);

        final TextView fullDate = readFullLayout.findViewById(R.id.full_date);
        text = fullDate.getText() + exercise.getDate();
        fullDate.setText(text);

        builder.setPositiveButton("Dismiss", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    /**
     * Creates and displays the "Edit" dialog where the user can update the details of the selected
     * exercise.
     *
     * @param exercise the exercise whose details are displayed for editing in the dialog
     * @param activity the Activity this TargetFragment is associated with
     */
    private void displayEditDialog(final Exercise exercise, Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Edit " + exercise.getExerciseName());

        dialogLayout = getAddExerciseDialogLayout();
        builder.setView(dialogLayout);

        checkBox = dialogLayout.findViewById(R.id.photo_check_box);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO confirm whether this immediately deletes file upon un-check; if so, should wait until positive button is pressed
            checkBox.setClickable(isChecked);
            if (!isChecked) {
                db.deleteExerciseImage(exercise);
                exercise.setImageResourcePath(Constants.NO_IMAGE_PROVIDED);
                checkBox.setText(R.string.no_photo_selected);
            } else {
                checkBox.setText(R.string.photo_selected);
            }
        });

        final TextView photoPathTextView = dialogLayout.findViewById(R.id.photo_path_text_view);

        if (exercise.hasImagePath()) {
            checkBox.setChecked(true);
            photoPathTextView.setText(exercise.getImageResourcePath());
        }

        final EditText nameEditText = dialogLayout.findViewById(R.id.name_edit_text);
        nameEditText.setText(exercise.getExerciseName());

        final EditText setsRepsEditText = dialogLayout.findViewById(R.id.sets_reps_edit_text);
        setsRepsEditText.setText(exercise.getSetsAndReps());

        builder.setPositiveButton("OK", (dialog, which) -> {
            final String name = nameEditText.getText().toString().trim();
            final String setsReps = setsRepsEditText.getText().toString().trim();
            final String photoPath = photoPathTextView.getText().toString();

            // Make sure fields are not blank
            if (name.isEmpty() || setsReps.isEmpty()) {
                Toast.makeText(getContext(), "Empty texts fields are not allowed.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Exercise newExercise = new Exercise(name, setsReps,
                        exercise.getRecentWeight(),
                        exercise.getExerciseTarget());
                newExercise.setExerciseID(exercise.getExerciseID());

                // TODO confirm logic here; check if path has actually changed first?
                if (!photoPath.isEmpty()) {
                    // If a new photo has been selected/taken, update the path too.
                    newExercise.setImageResourcePath(photoPath);
                } else {
                    // Otherwise, leave the path the same.
                    newExercise.setImageResourcePath(exercise.getImageResourcePath());
                }

                db.updateExercise(newExercise);

                // "Refresh" the Fragment once the exercise has been updated
                refreshFragment();
            }
        });

        builder.setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    /**
     * Creates and displays the "Delete" dialog where the user can confirm or deny the deletion
     * of the selected exercise.
     *
     * @param exercise the exercise that may be deleted
     * @param activity the Activity this TargetFragment is associated with
     */
    private void displayDeleteDialog(final Exercise exercise, Activity activity) {
        final String exerciseName = exercise.getExerciseName();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Are you sure you would like to delete " + exerciseName + "?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            int id = exercise.getExerciseID();
            db.deleteExercise(id);

            Toast.makeText(getActivity(),
                    "DELETED " + exerciseName,
                    Toast.LENGTH_SHORT).show();

            exercises.remove(exercise);
            resetFragmentData();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * "Refreshes" the Fragment by detaching and reattaching it. This method is invoked
     *  after an exercise has been changed using the "Edit" menu option and when the weight
     *  has been updated.
     */
    public void refreshFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.detach(this);
            fragmentTransaction.attach(this);
            fragmentTransaction.commit();
        } else {
            Log.e("refreshFragment()", "FragmentManager is null");
        }
    }

    /**
     * Retrieves the appropriate data from the database using fetchData and then resets the
     * listView to reflect the changes that have been made. Used after adding a new exercise
     * (from the MainActivity) or selecting the "Delete" menu option (this method of resetting
     * the data has proved to be the most reliable, as other methods sometimes fail).
     */
    public void resetFragmentData() {
        // TODO find better way to determine whether the fragment is visible? (getUserVisibleHint() may not be reliable...)
        if (getUserVisibleHint()) {
            fetchData();
            adapter.clear();
            adapter.addAll(exercises);
            adapter.notifyDataSetChanged();
            listView.invalidate();
        }
    }
}
