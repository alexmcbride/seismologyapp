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
import android.widget.Toast;

import com.alexmcbride.android.seismologyapp.models.Earthquake;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRepository;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRssReader;
import com.google.common.collect.Lists;

import java.lang.ref.WeakReference;
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
    private Handler mUpdateHandler = new Handler();
    private DownloadEarthquakesRunnable mDownloadEarthquakesRunnable;
    private Snackbar mUpdateSnackbar;

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

    private void showUpdateSnackbar(int addedCount, View.OnClickListener listener) {
        // Dismiss if already showing a snackbar.
        if (mUpdateSnackbar != null && mUpdateSnackbar.isShown()) {
            mUpdateSnackbar.dismiss();
        }
        View container = findViewById(R.id.container);
        String message = getString(R.string.earthquakes_updated_snackbar_message, addedCount);
        mUpdateSnackbar = Snackbar.make(container, message, Snackbar.LENGTH_INDEFINITE);
        mUpdateSnackbar.setAction("Update?", listener);
        mUpdateSnackbar.show();
    }

    private void earthquakesUpdated(final List<Earthquake> earthquakes) {
        // Add to database.
        if (earthquakes.size() > 0) {
            Log.d(TAG, "Received earthquakes: " + earthquakes.size());
            showUpdateSnackbar(earthquakes.size(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListMasterDetailFragment.earthquakesUpdated();
                }
            });
        } else {
            Log.d(TAG, "No new earthquakes found.");
        }
    }

    private void startDownloadTask() {
        mUpdateHandler.postDelayed(mDownloadEarthquakesRunnable, 0);
    }

    private void stopDownloadTask() {
        mUpdateHandler.removeCallbacks(mDownloadEarthquakesRunnable);
    }

    private class DownloadEarthquakesRunnable implements Runnable {
        private final MainActivity mMainActivity;

        DownloadEarthquakesRunnable(MainActivity mainActivity) {
            mMainActivity = mainActivity;
        }

        @Override
        public void run() {
            new DownloadEarthquakesAsyncTask(mMainActivity).execute(UPDATE_URL);

            // Trigger next tick of timer.
            mUpdateHandler.postDelayed(mDownloadEarthquakesRunnable, UPDATE_DELAY_MILLIS);
        }
    }

    private static class DownloadEarthquakesAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {
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
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            if (mException == null) {
                MainActivity mainActivity = mActivityReference.get();
                mainActivity.earthquakesUpdated(earthquakes);
            } else {
                Log.d(TAG, mException.toString());
                Toast.makeText(mActivityReference.get(), "Error: " + mException.getMessage(), Toast.LENGTH_SHORT).show();
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
