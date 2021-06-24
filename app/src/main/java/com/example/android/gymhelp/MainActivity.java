package com.example.android.gymhelp;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class MainActivity extends BaseActivity {

    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        viewPager = findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        adapter = new TargetAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tabs
        tabLayout = findViewById(R.id.tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshMainActivity();
    }

    /**
     * Resets the data contained within the current TargetFragment (i.e. the one currently visible in the
     * Main Activity). This method is called after the Main Activity is restarted, which is required to
     * reflect any changes made to the data from the SearchResultsActivity.
     */
    private void refreshMainActivity() {
        // TODO determine if below is correct way to get the current fragment.
        // TODO replace all calls to findFragmentByID with this method if it's valid (BaseActivity, SearchResultsActivity)
        TargetFragment targetFragment = (TargetFragment) adapter.getItem(viewPager.getCurrentItem());
        targetFragment.resetFragmentData();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem search = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) search.getActionView();

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        final CursorAdapter suggestionsAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.hint_row,
                null,
                new String[] {"name"},
                new int[] {R.id.row_text_view},
                0);

        searchView.setSuggestionsAdapter(suggestionsAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor c = searchView.getSuggestionsAdapter().getCursor();
                c.moveToPosition(position);
                searchView.setQuery(c.getString(1), true);
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                // Avoid searching for exact query twice (specifically, the below if statement
                // prevents the issue of submitting the query twice when a keyboard autocomplete suggestion
                // is selected while input is being provided to the SearchView.
                final String newQuery = newText.trim(); // Remove leading/trailing whitespace
                if (!searchText.equals(newQuery) && !newQuery.isEmpty()) {
                    searchText = newQuery;
                    Cursor c = myDb.getQuerySuggestions(newQuery);

                    if (c.getCount() > 0) {
                        suggestionsAdapter.swapCursor(c);
                        suggestionsAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
                return false;
            }
        });
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int tabPosition = tabLayout.getSelectedTabPosition();
        TargetFragment targetFragment = (TargetFragment) getSupportFragmentManager()
                .findFragmentByTag(Integer.toString(tabPosition));

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                handleImageCapture(resultCode, targetFragment);
                break;
            case PICK_IMAGE:
                handlePickImage(resultCode, targetFragment, data);
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
    private void handleImageCapture(int resultCode, TargetFragment targetFragment) {
        if (resultCode == RESULT_OK) {
            if (dialogLayout != null) {
                checkBox = dialogLayout.findViewById(R.id.photo_check_box);
                checkBox.setClickable(true);
                checkBox.setText(R.string.photo_selected);
                checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!isChecked) {
                        if (!currentPhotoPath.isEmpty()) {
                            File deleteFile = new File(currentPhotoPath);
                            if (deleteFile.delete()) {
                                Log.d("Image deletion", "Successfully deleted file at " + currentPhotoPath);
                            } else {
                                Log.e("Image deletion", "Could not delete file at " + currentPhotoPath);
                            }
                            currentPhotoPath = "";
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
        } else if (resultCode == RESULT_CANCELED) {
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
        if (resultCode == RESULT_OK) {
            /*The result returns the Uri ("address") of the selected picture. */
            Uri imageUri = data.getData();
            currentPhotoPath = getPath(imageUri);
            if (dialogLayout != null) {
                checkBox = dialogLayout.findViewById(R.id.photo_check_box);
                checkBox.setClickable(true);
                checkBox.setText(R.string.photo_selected);
                checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!isChecked) {
                        if (!currentPhotoPath.isEmpty()) {
                            currentPhotoPath = "";
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
        } else if (resultCode == RESULT_CANCELED) {
            Log.d("handlePickImage()", "The user cancelled or an error occurred.");
        }
    }

}
