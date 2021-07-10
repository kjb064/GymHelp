package com.example.android.gymhelp;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends BaseActivity {

    // TODO add ability to sort exercises
    // TODO add ability to delete multiple exercises
    // TODO for loading default exercises into tables, read a file and parse out exercises?

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
