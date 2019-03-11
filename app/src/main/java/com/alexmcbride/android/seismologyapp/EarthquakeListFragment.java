package com.alexmcbride.android.seismologyapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alexmcbride.android.seismologyapp.models.Earthquake;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRepository;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nullable;

/*
 * Fragment used to select a list of earthquakes.
 */
public class EarthquakeListFragment extends ChildFragment {
    private static final String TAG = "EarthquakeListFragment";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private OnFragmentInteractionListener mListener;
    private EarthquakeRepository mEarthquakeRepository;
    private ListView mListEarthquakes;
    private Spinner mSpinnerSortOptions;
    private ArrayAdapter<CharSequence> mSpinnerSortOptionsAdapter;
    private LocationManager mLocationManager;
    private Location mLastKnownLocation;

    public EarthquakeListFragment() {
        // Required empty public constructor
    }

    public static EarthquakeListFragment newInstance() {
        return new EarthquakeListFragment();
    }

    @Override
    public void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        updateLastKnownLocation();
    }

    private void updateLastKnownLocation() {
        // We don't need better than coarse grained, as the earthquakes are quite spread out, that
        // level of accuracy isn't needed. Also we don't both to update the location for the same
        // reason.
        Activity activity = Objects.requireNonNull(getActivity());
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(activity, getString(R.string.location_permission_explanation), Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        } else {
            mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastKnownLocation();
            } else {
                Toast.makeText(getActivity(), getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);

        mListEarthquakes = view.findViewById(R.id.listEarthquakes);
        mListEarthquakes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mListener.onEarthquakeSelected(id);
            }
        });

        mSpinnerSortOptions = view.findViewById(R.id.spinnerSortOptions);
        mSpinnerSortOptionsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.earthquake_sort_options, android.R.layout.simple_spinner_item);
        mSpinnerSortOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSortOptions.setAdapter(mSpinnerSortOptionsAdapter);
        mSpinnerSortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String sortOption = mSpinnerSortOptionsAdapter.getItem(position).toString();
                earthquakesUpdated(sortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mEarthquakeRepository = new EarthquakeRepository(getActivity());
        earthquakesUpdated();

        return view;
    }

    void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public Bundle getSavedState() {
        return new Bundle();
    }

    @Override
    public void loadSavedState(Bundle bundle) {

    }

    private String getSelectedSortOption() {
        int position = mSpinnerSortOptions.getSelectedItemPosition();
        return Objects.requireNonNull(mSpinnerSortOptionsAdapter.getItem(position)).toString();
    }

    void earthquakesUpdated() {
        String sortOption = getSelectedSortOption();
        earthquakesUpdated(sortOption);
    }

    private void earthquakesUpdated(String sortOption) {
        List<Earthquake> earthquakes = sortEarthquakeList(sortOption);
        EarthquakesAdapter adapter = new EarthquakesAdapter(Objects.requireNonNull(getActivity()), earthquakes);
        mListEarthquakes.setAdapter(adapter);
    }

    private List<Earthquake> sortEarthquakeList(String sortOption) {
        if (sortOption.equalsIgnoreCase("nearest") && mLastKnownLocation != null) {
            return mEarthquakeRepository.getEarthquakesByNearest(
                    mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude());
        } else if (sortOption.equalsIgnoreCase("date")) {
            return mEarthquakeRepository.getEarthquakesByDate();
        } else if (sortOption.equalsIgnoreCase("title")) {
            return mEarthquakeRepository.getEarthquakesByTitle();
        } else if (sortOption.equalsIgnoreCase("depth")) {
            return mEarthquakeRepository.getEarthquakesByDepth();
        } else if (sortOption.equalsIgnoreCase("magnitude")) {
            return mEarthquakeRepository.getEarthquakesByMagnitude();
        } else {
            return Lists.newArrayList(); // nothing
        }
    }

    private class EarthquakesAdapter extends ArrayAdapter<Earthquake> {
        EarthquakesAdapter(@NonNull Context context, List<Earthquake> earthquakes) {
            super(context, -1);
            addAll(earthquakes);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_earthquake, parent, false);
            }

            Earthquake earthquake = Objects.requireNonNull(getItem(position));

            ((TextView) convertView.findViewById(R.id.textLocation)).setText(earthquake.getLocation());
            ((TextView) convertView.findViewById(R.id.textDate)).setText(Util.formatPretty(earthquake.getPubDate()));

            String depth = getString(R.string.earthquake_list_item_depth, earthquake.getDepth());
            ((TextView) convertView.findViewById(R.id.textDepth)).setText(depth);

            String magnitude = getString(R.string.earthquake_list_item_manitude, earthquake.getMagnitude());
            ((TextView) convertView.findViewById(R.id.textMagnitude)).setText(magnitude);

            return convertView;
        }
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
