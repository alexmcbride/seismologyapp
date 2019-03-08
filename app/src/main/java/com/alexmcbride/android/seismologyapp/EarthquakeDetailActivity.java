package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EarthquakeDetailActivity extends AppCompatActivity implements EarthquakeDetailFragment.OnFragmentInteractionListener {
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

        Intent intent = getIntent();
        long id = intent.getLongExtra(ARG_EARTHQUAKE_ID, -1);

        if (savedInstanceState == null) {
            Fragment fragment = EarthquakeDetailFragment.newInstance(id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Earthquake Detail");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
