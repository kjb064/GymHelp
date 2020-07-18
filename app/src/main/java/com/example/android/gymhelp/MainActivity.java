package com.example.android.gymhelp;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private String searchText = "";
    private String[] navigationDrawerItemTitles;
    private String currentProgram;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    DrawerItemCustomAdapter drawerAdapter;
    private ActionBarDrawerToggle actionBarDrawerToggle;

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

        setUpActionBar();

        // Set up navigation drawer and toggle (home/hamburger icon)
        navigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);

        List<DataModel> programsForDrawer = getProgramsForDrawer();
        drawerAdapter = new DrawerItemCustomAdapter(this, R.layout.drawer_item_layout, programsForDrawer);
        drawerList.addFooterView(createAddNewProgramButton());
        drawerList.setAdapter(drawerAdapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setOnItemLongClickListener(new DrawerItemLongClickListener());
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

    } // end onCreate

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            currentProgram = myDb.getCurrentTableName();
            actionBar.setTitle(currentProgram);
        }
    }

    private void refreshProgramsInDrawer() {
        if (!drawerAdapter.isEmpty()) {
            drawerAdapter.clear();
        }
        List<DataModel> programsForDrawer = getProgramsForDrawer();
        drawerAdapter.addAll(programsForDrawer);
        drawerAdapter.notifyDataSetChanged();
    }

    private List<DataModel> getProgramsForDrawer() {
        List<DataModel> dataModels = new ArrayList<>();
        ArrayList<String> tableNames = myDb.getTableNames();
        for (String name : tableNames) {
            dataModels.add(new DataModel(name));
        }
        return dataModels;
    }

    private Button createAddNewProgramButton() {
        Button addNewProgram = new Button(this);
        addNewProgram.setText("Add new Program");
        addNewProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dialog that takes program name
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View addProgramLayout = View.inflate(getApplicationContext(), R.layout.add_program_dialog, null);
                builder.setView(addProgramLayout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ok validates program name and then:
                        // (OPTION 1): opens new program after successful creation (closes drawer)
                        // (OPTION 2): Adds new program to list only (does not close drawer; does not open program)
                        EditText programNameEditText = addProgramLayout.findViewById(R.id.program_name_edit_text);
                        /*TODO Add new program. Will need to determine how to handle brackets around table name when
                         * creating and retrieving (will likely need to disallow names that have brackets).
                         */
                        myDb.addTable(programNameEditText.getText().toString());
                        refreshProgramsInDrawer();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });
        return addNewProgram;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshMainActivity();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(parent, view, position);
        }
    }
    private class DrawerItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            TextView textView = view.findViewById(R.id.program_name);
            final String name = textView.getText().toString();
            String dialogTitle = "Are you sure you would like to delete " + name + "?";
            builder.setTitle(dialogTitle);
            builder.setMessage("(This cannot be undone)");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    myDb.deleteTable(name);
                    refreshProgramsInDrawer();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return true;
        }
    }

    private void selectItem(AdapterView<?> parent, View view, int position) {
        TextView textView = view.findViewById(R.id.program_name);
        boolean refreshRequired = myDb.setCurrentTableName(textView.getText().toString());

        if (refreshRequired) {
            // Update action bar title to match selected program and
            // refresh to fetch the program's table data.
            currentProgram = textView.getText().toString();
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                setTitle(currentProgram);
            }
            refreshMainActivity();
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int tabPosition = tabLayout.getSelectedTabPosition();
        TargetFragment targetFragment = (TargetFragment) getSupportFragmentManager()
                .findFragmentByTag(Integer.toString(tabPosition));

        switch (item.getItemId()) {
            case R.id.sort_ascending:
                targetFragment.resetFragmentDataSorted(Constants.SORT_ASCENDING);
                break;
            case R.id.sort_descending:
                targetFragment.resetFragmentDataSorted(Constants.SORT_DESCENDING);
                break;
            case R.id.unsorted:
                targetFragment.resetFragmentData();
                break;
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(drawerList)) {
                    drawerLayout.closeDrawer(drawerList);
                }
                else {
                    drawerLayout.openDrawer(drawerList);
                }
        }
        return super.onOptionsItemSelected(item);
    }

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
