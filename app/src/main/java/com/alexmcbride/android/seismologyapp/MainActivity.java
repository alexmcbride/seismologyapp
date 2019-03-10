package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
    private static final String ARG_SELECTED_PAGE = "ARG_SELECTED_PAGE";
    private static final String TAG = "MainActivity";
    private ViewPager mViewPager;
    private EarthquakeRepository mEarthquakeRepository;
    private ListMasterDetailFragment mListMasterDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListMasterDetailFragment = ListMasterDetailFragment.newInstance();

        // Add tabs to our adapter.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addPage(SearchMasterDetailFragment.newInstance(), "Search");
        sectionsPagerAdapter.addPage(mListMasterDetailFragment, "List");
        sectionsPagerAdapter.addPage(EarthquakeMapFragment.newInstance(), "Map");

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        tabLayout.setupWithViewPager(mViewPager);

        // Load preferences
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        mViewPager.setCurrentItem(preferences.getInt(ARG_SELECTED_PAGE, 0));

        mEarthquakeRepository = new EarthquakeRepository(this);

        final String url = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
//        new DownloadEarthquakesAsyncTask().execute(url);
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

    private void updateEarthquakes(List<Earthquake> earthquakes) {
        Toast.makeText(this, "Earthquakes: " + earthquakes.size(), Toast.LENGTH_SHORT).show();
    }

    // Acts as a collection of tabs for the view pager.
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

    private class DownloadEarthquakesAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {
        private Exception mException;

        @Override
        protected List<Earthquake> doInBackground(String... strings) {
            String url = strings[0];
            EarthquakeRssReader earthquakeRssReader = new EarthquakeRssReader();
            try {
                return earthquakeRssReader.parse(url);
            } catch (Exception e) {
                mException = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            if (mException == null) {
                mEarthquakeRepository.addEarthquakes(earthquakes);
                mListMasterDetailFragment.updateEarthquakes(earthquakes);
            } else {
                Log.d(TAG, mException.toString());
                Toast.makeText(MainActivity.this, "Error: " + mException.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
