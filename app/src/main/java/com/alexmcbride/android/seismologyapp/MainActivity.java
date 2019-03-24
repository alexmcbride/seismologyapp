package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.view.View;

import com.alexmcbride.android.seismologyapp.model.Earthquake;
import com.alexmcbride.android.seismologyapp.model.EarthquakeRepository;
import com.alexmcbride.android.seismologyapp.model.EarthquakeRssReader;
import com.google.common.collect.Lists;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SearchMasterDetailFragment.OnFragmentInteractionListener,
        ListMasterDetailFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final String ARG_SELECTED_PAGE = "ARG_SELECTED_PAGE";
    private static final String ARG_FIRST_RUN = "ARG_FIRST_RUN";
    private static final String UPDATE_URL = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private static final int UPDATE_DELAY_MILLIS = 1000 * 60;//1 min

    private ViewPager mViewPager;
    private SearchMasterDetailFragment mSearchMasterDetailFragment;
    private ListMasterDetailFragment mListMasterDetailFragment;
    private EarthquakeMapFragment mEarthquakeMapFragment;
    private Handler mUpdateHandler = new Handler();
    private DownloadEarthquakesRunnable mDownloadEarthquakesRunnable;
    private Snackbar mUpdateSnackbar;
    private Snackbar mUpdateFailedSnackbar;
    private boolean mFirstRun;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create fragments for the view pager.
        mSearchMasterDetailFragment = SearchMasterDetailFragment.newInstance();
        mListMasterDetailFragment = ListMasterDetailFragment.newInstance();
        mEarthquakeMapFragment = EarthquakeMapFragment.newInstance();

        // Add fragments to adapter.
        FragmentsPagerAdapter fragmentsPagerAdapter = new FragmentsPagerAdapter(getSupportFragmentManager());
        fragmentsPagerAdapter.addPage(mListMasterDetailFragment, "Earthquakes");
        fragmentsPagerAdapter.addPage(mSearchMasterDetailFragment, "Search");
        fragmentsPagerAdapter.addPage(mEarthquakeMapFragment, "Map");

        // Set up the ViewPager with the fragment adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(fragmentsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.setupWithViewPager(mViewPager);

        // Load activity preferences
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        mViewPager.setCurrentItem(preferences.getInt(ARG_SELECTED_PAGE, 0));
        mFirstRun = preferences.getBoolean(ARG_FIRST_RUN, true);

        // Init timer stuff.
        mDownloadEarthquakesRunnable = new DownloadEarthquakesRunnable(this);
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
        editor.putBoolean(ARG_FIRST_RUN, false);
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
    public void onSearchEarthquakes(String location) {
        Intent intent = SearchResultsActivity.newInstance(this, location);
        startActivity(intent);
    }

    @Override
    public void onEarthquakeSelected(long id) {
        Intent intent = EarthquakeDetailActivity.newInstance(this, id);
        startActivity(intent);
    }

    private void showUpdateSnackbar(int addedCount, View.OnClickListener listener) {
        // When first running don't bother to ask the user to update.
        if (mFirstRun) {
            listener.onClick(null);
        } else {
            // Dismiss if already showing a snackbar.
            if (mUpdateSnackbar != null && mUpdateSnackbar.isShown()) {
                mUpdateSnackbar.dismiss();
            }

            // Ask user if they want to update the UI now.
            View container = findViewById(R.id.container);
            String message = getString(R.string.earthquakes_updated_snackbar_message, addedCount);
            mUpdateSnackbar = Snackbar.make(container, message, Snackbar.LENGTH_INDEFINITE);
            mUpdateSnackbar.setAction(R.string.update_snackbar_question, listener);
            mUpdateSnackbar.show();
        }
    }

    private void updateFragments() {
        mListMasterDetailFragment.earthquakesUpdated();
    }

    private void earthquakesUpdated(final List<Earthquake> earthquakes) {
        // Add to database.
        if (earthquakes.size() > 0) {
            Log.d(TAG, "Received earthquakes: " + earthquakes.size());
            showUpdateSnackbar(earthquakes.size(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateFragments();
                }
            });
        } else {
            Log.d(TAG, "No new earthquakes found.");
        }
    }

    private void startDownloadTask() {
        mUpdateHandler.postDelayed(mDownloadEarthquakesRunnable, 0);
    }

    private void startDownloadTaskDelayed() {
        mUpdateHandler.postDelayed(mDownloadEarthquakesRunnable, UPDATE_DELAY_MILLIS);
    }

    private void stopDownloadTask() {
        mUpdateHandler.removeCallbacks(mDownloadEarthquakesRunnable);
    }

    private void showUpdateFailedSnackbar() {
        // If snackbar already displayed dismiss it.
        if (mUpdateFailedSnackbar != null && mUpdateFailedSnackbar.isShown()) {
            mUpdateFailedSnackbar.dismiss();
        }

        View container = findViewById(R.id.container);
        String message = "Update earthquakes failed";
        mUpdateFailedSnackbar = Snackbar.make(container, message, Snackbar.LENGTH_INDEFINITE);
        mUpdateFailedSnackbar.setAction("Continue?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownloadTaskDelayed();
            }
        });

        mUpdateFailedSnackbar.show();
    }

    private class DownloadEarthquakesRunnable implements Runnable {
        private final MainActivity mMainActivity;

        DownloadEarthquakesRunnable(MainActivity mainActivity) {
            mMainActivity = mainActivity;
        }

        @Override
        public void run() {
            new DownloadEarthquakesAsyncTask(mMainActivity).execute(UPDATE_URL);
        }
    }

    private static class DownloadEarthquakesAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {
        // Store activity as a WeakReference to prevent memory leaks
        private WeakReference<MainActivity> mActivityReference;
        private Exception mException;

        DownloadEarthquakesAsyncTask(MainActivity activityReference) {
            mActivityReference = new WeakReference<>(activityReference);
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Starting download earthquakes task...");
        }

        @Override
        protected List<Earthquake> doInBackground(String... strings) {
            String url = strings[0];
            EarthquakeRssReader earthquakeRssReader = new EarthquakeRssReader();
            try {
                List<Earthquake> earthquakes = earthquakeRssReader.parse(url);
                EarthquakeRepository repository = new EarthquakeRepository(mActivityReference.get());
                return repository.addEarthquakes(earthquakes);
            } catch (Exception e) {
                mException = e;
                return Lists.newArrayList();
            }
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            MainActivity activity = mActivityReference.get();
            if (mException == null) {
                if (earthquakes.size() > 0) {
                    activity.earthquakesUpdated(earthquakes);
                }
                activity.startDownloadTaskDelayed();
            } else {
                Log.d(TAG, mException.toString());
                activity.showUpdateFailedSnackbar();
            }
        }
    }

    // Manages collection fragments for the view pager.
    private class FragmentsPagerAdapter extends FragmentPagerAdapter {
        private List<Page> mPageList = Lists.newArrayList();

        FragmentsPagerAdapter(FragmentManager fm) {
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
