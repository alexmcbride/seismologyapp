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

import java.util.Objects;

/*
 * Activity used to display details of a single earthquake, typically used when the device is in
 * portrait mode.
 */
public class EarthquakeDetailActivity extends AppCompatActivity {
    private static final String ARG_EARTHQUAKE_ID = "ARG_EARTHQUAKE_ID";

    public static Intent newInstance(Context context, long id) {
        Intent intent = new Intent(context, EarthquakeDetailActivity.class);
        intent.putExtra(ARG_EARTHQUAKE_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_detail);

        // Load earthquake to view.
        if (savedInstanceState == null) {
            // Get ID of earthquake to display.
            long id = getIntent().getLongExtra(ARG_EARTHQUAKE_ID, -1);

            // Load fragment.
            Fragment fragment = EarthquakeDetailFragment.newInstance(id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow();
        }

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.earthquake_detail_fragment_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
