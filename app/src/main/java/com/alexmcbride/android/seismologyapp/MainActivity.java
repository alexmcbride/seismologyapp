package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alexmcbride.android.seismologyapp.models.Earthquake;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRepository;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRssReader;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SearchMasterDetailFragment.OnFragmentInteractionListener,
        ListMasterDetailFragment.OnFragmentInteractionListener,
        EarthquakeMapFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final String ARG_SELECTED_PAGE = "ARG_SELECTED_PAGE";
    private static final String UPDATE_URL = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private static final int UPDATE_DELAY_MILLIS = 1000 * 60;//1 min

    private ViewPager mViewPager;
    private SearchMasterDetailFragment mSearchMasterDetailFragment;
    private ListMasterDetailFragment mListMasterDetailFragment;
    private EarthquakeMapFragment mEarthquakeMapFragment;
    private EarthquakeRepository mEarthquakeRepository;
    private EarthquakeRssReader mEarthquakeRssReader;
    private Handler mUpdateHandler = new Handler();
    private DownloadEarthquakesRunnable mDownloadEarthquakesRunnable = new DownloadEarthquakesRunnable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create out parent fragments.
        mSearchMasterDetailFragment = SearchMasterDetailFragment.newInstance();
        mListMasterDetailFragment = ListMasterDetailFragment.newInstance();
        mEarthquakeMapFragment = EarthquakeMapFragment.newInstance();

        // Add tabs to our adapter.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addPage(mSearchMasterDetailFragment, "Search");
        sectionsPagerAdapter.addPage(mListMasterDetailFragment, "List");
        sectionsPagerAdapter.addPage(mEarthquakeMapFragment, "Map");

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.setupWithViewPager(mViewPager);

        // Load activity preferences
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        mViewPager.setCurrentItem(preferences.getInt(ARG_SELECTED_PAGE, 0));

        // Download and DB stuff
        mEarthquakeRssReader = new EarthquakeRssReader();
        mEarthquakeRepository = new EarthquakeRepository(this);

        startDownloadTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopDownloadTask();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save preferences
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ARG_SELECTED_PAGE, mViewPager.getCurrentItem());
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchEarthquakes(Date start, Date end) {
        Intent intent = SearchResultsActivity.newInstance(this, start, end);
        startActivity(intent);
    }

    @Override
    public void onEarthquakeSelected(long id) {
        Intent intent = EarthquakeDetailActivity.newInstance(this, id);
        startActivity(intent);
    }

    private void earthquakesUpdated(List<Earthquake> earthquakes) {
        mListMasterDetailFragment.earthquakesUpdated();
    }

    private void startDownloadTask() {
        mUpdateHandler.postDelayed(mDownloadEarthquakesRunnable, 0);
    }

    private void startDownloadTaskWithDelay() {
        mUpdateHandler.postDelayed(mDownloadEarthquakesRunnable, UPDATE_DELAY_MILLIS);
    }

    private void stopDownloadTask() {
        mUpdateHandler.removeCallbacks(mDownloadEarthquakesRunnable);
    }

    /*
     * Class to handle downloading updates on a timer. We only do this in main activity, as the
     * other activities are only used for showing detail on a specific earthquake, so don't need
     * to update as frequently.
     */
    private class DownloadEarthquakesRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Log.d(TAG, "Downloading earthquakes...");
                // Download earthquakes and add to repository.
                List<Earthquake> earthquakes = mEarthquakeRssReader.parse(UPDATE_URL);
                boolean updated = mEarthquakeRepository.addEarthquakes(earthquakes);
                if (updated) {
                    Log.d(TAG, "Earthquakes updated");
                    earthquakesUpdated(earthquakes);
                } else {
                    Log.d(TAG, "No new earthquakes found");
                }
                startDownloadTaskWithDelay();
            } catch (Exception e) {
                Log.d(TAG, "Task error: " + e.toString());
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Manages out collection of tab pages.
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Page> mPageList = Lists.newArrayList();

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void addPage(Fragment fragment, String title) {
            mPageList.add(new Page(fragment, title.toUpperCase()));
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mPageList.get(position).title;
        }

        @Override
        public Fragment getItem(int position) {
            return mPageList.get(position).fragment;
        }

        @Override
        public int getCount() {
            return mPageList.size();
        }

        // Internal class to represent a page of the view pager.
        private class Page {
            Fragment fragment;
            String title;

            Page(Fragment fragment, String title) {
                this.fragment = fragment;
                this.title = title;
            }
        }
    }
}
