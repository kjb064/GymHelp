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
import android.widget.CompoundButton;
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

    } // end onCreate

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshMainActivity();
    }

    /*
     * Resets the data contained within the current TargetFragment (i.e. the one currently visible in the
     * Main Activity). This method is called after the Main Activity is restarted, which is required to
     * reflect any changes made to the data from the SearchResultsActivity.
     */
    private void refreshMainActivity(){
        TargetFragment targetFragment = (TargetFragment) getSupportFragmentManager()
                .findFragmentByTag(Integer.toString(tabLayout.getSelectedTabPosition()));
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
        if(searchManager != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));
        }
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        final CursorAdapter suggestionsAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.hint_row,
                null,
                new String[]{"name"},
                new int[]{R.id.row_text_view},
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
            public boolean onQueryTextChange(String newText) {
                // Avoid searching for exact query twice (specifically, the below if statement
                // prevents the issue of submitting the query twice when a keyboard autocomplete suggestion
                // is selected while input is being provided to the SearchView.
                if(!searchText.equals(newText.trim()) && !newText.isEmpty()) {
                    newText = newText.trim();   // Remove leading/trailing whitespace
                    searchText = newText;
                    Cursor c = myDb.getQuerySuggestions(newText);

                    if (c.getCount() > 0) {
                        suggestionsAdapter.swapCursor(c);
                        suggestionsAdapter.notifyDataSetChanged();
                        return true;
                    } else {
                        return false;
                    }
                }
                else{
                    return false;
                }
            }
        });
        return true;
    } // end onCreateOptionsMenu


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int tabPosition = tabLayout.getSelectedTabPosition();

        // Below is for a picture being successfully taken by the camera on the device.
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            if(dialogLayout != null) {
                checkBox = dialogLayout.findViewById(R.id.photo_check_box);
                checkBox.setClickable(true);
                checkBox.setText(R.string.photo_selected);
                checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            if (!currentPhotoPath.isEmpty()) {
                                File deleteFile = new File(currentPhotoPath);
                                if (deleteFile.delete()) {
                                    Log.d("MA: Delete", "Successfully deleted file at " + currentPhotoPath);
                                } else {
                                    Log.d("MA: Delete", "Could not delete file at " + currentPhotoPath);
                                }

                                currentPhotoPath = "";
                            }
                            checkBox.setClickable(false);
                            checkBox.setText(R.string.no_photo_selected);
                        }
                    }
                });
            }

            TargetFragment targetFragment = (TargetFragment) getSupportFragmentManager()
                    .findFragmentByTag(Integer.toString(tabPosition));

            if(targetFragment != null && targetFragment.isVisible()){
                if(targetFragment.checkBox != null){
                    targetFragment.checkBox.setChecked(true);
                }
            }
        }
        // TODO: Verify the selected file is a valid type?
        // Below is for a picture successfully being selected from the device's gallery.
        else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            /*The result returns the Uri ("address") of the selected picture. */
            Uri imageUri = data.getData();
            currentPhotoPath = getPath(imageUri);
            if(dialogLayout != null) {
                checkBox = dialogLayout.findViewById(R.id.photo_check_box);
                checkBox.setClickable(true);
                checkBox.setText(R.string.photo_selected);
                checkBox.setChecked(true);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            if (!currentPhotoPath.isEmpty()) {
                                currentPhotoPath = "";
                            }
                            checkBox.setClickable(false);
                            checkBox.setText(R.string.no_photo_selected);
                        }
                    }
                });
            }

            TargetFragment targetFragment = (TargetFragment) getSupportFragmentManager()
                    .findFragmentByTag(Integer.toString(tabPosition));

            if(targetFragment != null && targetFragment.isVisible()){
                if(targetFragment.checkBox != null){
                    targetFragment.checkBox.setChecked(true);
                }
            }
        }
    } // end onActivityResult

}
