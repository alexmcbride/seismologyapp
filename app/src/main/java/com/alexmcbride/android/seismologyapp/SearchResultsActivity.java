package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

public class SearchResultsActivity extends AppCompatActivity implements SearchResultsFragment.OnFragmentInteractionListener {
    private static final String ARG_START_DATE = "ARG_START_DATE";
    private static final String ARG_END_DATE = "ARG_END_DATE";

    public static Intent newInstance(Context context, Date start, Date end) {
        Intent intent = new Intent(context, SearchResultsActivity.class);
        intent.putExtra(ARG_START_DATE, start.getTime());
        intent.putExtra(ARG_END_DATE, end.getTime());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        Date start = new Date(intent.getLongExtra(ARG_START_DATE, 0));
        Date end = new Date(intent.getLongExtra(ARG_END_DATE, 0));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SearchResultsFragment.newInstance(start, end))
                    .commitNow();
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Search results");
        actionbar.setDisplayHomeAsUpEnabled(true);
    }
}
