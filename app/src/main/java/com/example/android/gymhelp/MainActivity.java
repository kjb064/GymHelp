package com.example.android.gymhelp;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends BaseActivity {

    // TODO add ability to delete multiple exercises
    // TODO for loading default exercises into tables, read a file and parse out exercises?
    // TODO remember selected program and load it upon starting app

    private String searchText = "";
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
        targetAdapter = new TargetAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(targetAdapter);

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
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.program_drawer_list_view);

        drawerAdapter = new DrawerItemCustomAdapter(this, R.layout.drawer_item_layout, getProgramsForDrawer());
        drawerList.addHeaderView(createAddNewProgramButton());
        drawerList.setAdapter(drawerAdapter);
        drawerList.setOnItemClickListener((parent, view, position, id) -> this.selectProgram(view));
        drawerList.setOnItemLongClickListener((parent, view, position, id) -> this.onItemLongClick(view));
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(myDb.getCurrentTableName());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int tabPosition = tabLayout.getSelectedTabPosition();
        TargetFragment targetFragment = (TargetFragment) getSupportFragmentManager()
                .findFragmentByTag(Integer.toString(tabPosition));

        switch (item.getItemId()) {
            // TODO add ability to sort exercises
//            case R.id.sort_ascending:
//                targetFragment.resetFragmentDataSorted(Constants.SORT_ASCENDING);
//                break;
//            case R.id.sort_descending:
//                targetFragment.resetFragmentDataSorted(Constants.SORT_DESCENDING);
//                break;
//            case R.id.unsorted:
//                targetFragment.resetFragmentData();
//                break;
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(drawerList)) {
                    drawerLayout.closeDrawer(drawerList);
                } else {
                    drawerLayout.openDrawer(drawerList);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Retrieves the list of names of the users' exercise programs and updates the drawer adapter.
     */
    private void refreshProgramsInDrawer() {
        drawerAdapter.clear();
        List<String> programsForDrawer = getProgramsForDrawer();
        drawerAdapter.addAll(programsForDrawer);
        drawerAdapter.notifyDataSetChanged();
    }

    /**
     * Gets a list of the names of the users' exercise programs (tables) from the database.
     *
     * @return a List of exercise program names
     */
    private List<String> getProgramsForDrawer() {
        return myDb.getTableNames();
    }

    private Button createAddNewProgramButton() {
        Button addNewProgram = new Button(this);
        addNewProgram.setText(R.string.add_new_program_label);
        addNewProgram.setOnClickListener(view -> {
            // dialog that takes program name
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final View addProgramLayout = View.inflate(getApplicationContext(), R.layout.add_program_dialog, null);
            builder.setView(addProgramLayout);

            builder.setPositiveButton("OK", (dialog, which) -> {
                // TODO ok validates program name and then:
                // (OPTION 1): opens new program after successful creation (closes drawer)
                // (OPTION 2): Adds new program to list only (does not close drawer; does not open program)
                EditText programNameEditText = addProgramLayout.findViewById(R.id.program_name_edit_text);
                myDb.addTable(programNameEditText.getText().toString());
                refreshProgramsInDrawer();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.show();
        });
        return addNewProgram;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private boolean onItemLongClick(View view) {
        // TODO present context menu with options (e.g. Delete program, rename...)
        // TODO upon deleting the program, either clear the activity or automatically switch to a different program
            // - Default empty activity could be useful if we allow the user to delete all their programs (could
            //   also be the default state of the app after initial installation?)
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        TextView textView = view.findViewById(R.id.program_name);
        final String name = textView.getText().toString();
        String dialogTitle = "Are you sure you would like to delete " + name + "?";
        builder.setTitle(dialogTitle);
        builder.setMessage("(This cannot be undone)");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            myDb.deleteTable(name);
            refreshProgramsInDrawer();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
        return true;
    }

    /**
     * Sets the selected program as the current program, updates the program name in the ActionBar,
     * and triggers a refresh of the TargetFragments.
     *
     * @param view the view associated with the selected program
     */
    private void selectProgram(View view) {
        TextView textView = view.findViewById(R.id.program_name);
        String selectedProgram = textView.getText().toString();
        String currentProgram = myDb.getCurrentTableName();

        // Switch to the selected program if it's not the current program and it's table exists
        if (!currentProgram.contentEquals(selectedProgram) && myDb.doesTableExist(selectedProgram)) {
            myDb.setCurrentTableName(selectedProgram);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                // Update action bar title to match selected program
                actionBar.setTitle(selectedProgram);
            }
            // Tell targetAdapter the dataset changed so the TargetFragments can be re-created with
            // the new data associated with the selected program.
            targetAdapter.notifyDataSetChanged();
        }
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
}
