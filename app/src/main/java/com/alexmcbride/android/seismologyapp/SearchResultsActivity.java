package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SearchResultsActivity extends AppCompatActivity implements SearchResultsFragment.OnFragmentInteractionListener {
    public static Intent newInstance(Context context) {
        return new Intent(context, SearchResultsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SearchResultsFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public void onBack() {
        finish();
    }
}
