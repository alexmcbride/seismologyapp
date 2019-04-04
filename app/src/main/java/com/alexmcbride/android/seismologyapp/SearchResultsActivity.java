/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;
import java.util.Objects;

/*
 * Activity used to display a single set of search results, typically used when the device is in
 * portrait mode.
 */
public class SearchResultsActivity extends AppCompatActivity implements SearchLocationResultsFragment.OnFragmentInteractionListener, SearchDateResultsFragment.OnFragmentInteractionListener {
    private static final String ARG_START_DATE = "ARG_START_DATE";
    private static final String ARG_END_DATE = "ARG_END_DATE";
    private static final String ARG_LOCATION = "ARG_LOCATION";

    public static Intent newInstance(Context context, Date start, Date end) {
        Intent intent = new Intent(context, SearchResultsActivity.class);
        intent.putExtra(ARG_START_DATE, start.getTime());
        intent.putExtra(ARG_END_DATE, end.getTime());
        return intent;
    }

    static Intent newInstance(Context context, String location) {
        Intent intent = new Intent(context, SearchResultsActivity.class);
        intent.putExtra(ARG_LOCATION, location);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Date start = new Date(intent.getLongExtra(ARG_START_DATE, 0));
            Date end = new Date(intent.getLongExtra(ARG_END_DATE, 0));
            String location = intent.getStringExtra(ARG_LOCATION);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, getFragment(start, end, location))
                    .commitNow();
        }

        ActionBar actionbar = getSupportActionBar();
        Objects.requireNonNull(actionbar).setTitle("Search results");
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private Fragment getFragment(Date start, Date end, String location) {
        if (location != null) {
            SearchLocationResultsFragment fragment = SearchLocationResultsFragment.newInstance(location);
            fragment.setListener(this);
            return fragment;
        } else {
            SearchDateResultsFragment fragment = SearchDateResultsFragment.newInstance(start, end);
            fragment.setListener(this);
            return fragment;
        }
    }

    @Override
    public void onEarthquakeSelected(long id) {
        startActivity(EarthquakeDetailActivity.newInstance(this, id));
    }
}
