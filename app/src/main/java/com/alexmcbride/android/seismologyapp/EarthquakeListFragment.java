package com.alexmcbride.android.seismologyapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

/*
 * Fragment to display a list of earthquakes with sort options. Most sort options involve different
 * database queries, but sorting by the nearest earthquake to you requires your current location,
 * which means a bunch of services and permissions.
 */
public class EarthquakeListFragment extends ChildFragment {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String ARG_SELECTED_SORT_OPTION = "ARG_SELECTED_SORT_OPTION";
    private OnFragmentInteractionListener mListener;
    private EarthquakeRepository mEarthquakeRepository;
    private ListView mListEarthquakes;
    private Spinner mSpinnerSortOptions;
    private ArrayAdapter<CharSequence> mSpinnerSortOptionsAdapter;
    private LocationManager mLocationManager;
    private Location mLastLocation;
    private EarthquakesAdapter mEarthquakesAdapter;

    public EarthquakeListFragment() {
        // Required empty public constructor
    }

    public static EarthquakeListFragment newInstance() {
        return new EarthquakeListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to know the user's location for the sort by nearest option.
        Activity activity = Objects.requireNonNull(getActivity());
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        updateLastLocation();
    }

    private void updateLastLocation() {
        // As the earthquakes are quite spread out a high level of accuracy isn't needed. Also we
        // don't bother to update the location again for the same reason.
        Activity activity = Objects.requireNonNull(getActivity());
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(activity, R.string.location_permission_explanation, Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        } else {
            mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if permission granted.
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Try and get the location again.
                updateLastLocation();
            } else {
                Toast.makeText(getActivity(), R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);

        // Init list.
        mListEarthquakes = view.findViewById(R.id.listEarthquakes);
        mListEarthquakes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Earthquake earthquake = Objects.requireNonNull(mEarthquakesAdapter.getItem(position));
                mListener.onEarthquakeSelected(earthquake.getId());
            }
        });

        // Init spinner (dropdown for sort option)
        Activity activity = Objects.requireNonNull(getActivity());
        mSpinnerSortOptions = view.findViewById(R.id.spinnerSortOptions);
        mSpinnerSortOptionsAdapter = ArrayAdapter.createFromResource(activity, R.array.earthquake_sort_options, android.R.layout.simple_spinner_item);
        mSpinnerSortOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSortOptions.setAdapter(mSpinnerSortOptionsAdapter);
        mSpinnerSortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                CharSequence text = Objects.toString(mSpinnerSortOptionsAdapter.getItem(position));
                String sortOption = text.toString();
                earthquakesUpdated(sortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Get spinner setting out of preferences.
        SharedPreferences preferences = getActivity().getPreferences(Activity.MODE_PRIVATE);
        mSpinnerSortOptions.setSelection(preferences.getInt(ARG_SELECTED_SORT_OPTION, 0));

        // Init DB and load earthquakes.
        mEarthquakeRepository = new EarthquakeRepository(getActivity());
        earthquakesUpdated();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save spinner state.
        Activity activity = Objects.requireNonNull(getActivity());
        SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ARG_SELECTED_SORT_OPTION, mSpinnerSortOptions.getSelectedItemPosition());
        editor.apply();
    }

    void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public Bundle getSavedState() {
        return null;
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
        List<Earthquake> earthquakes = getSortedEarthquakes(sortOption);
        if (earthquakes != null) {
            mEarthquakesAdapter = new EarthquakesAdapter(Objects.requireNonNull(getActivity()), earthquakes);
            mListEarthquakes.setAdapter(mEarthquakesAdapter);
        } else {
            Toast.makeText(getActivity(), R.string.no_earthquakes_message, Toast.LENGTH_SHORT).show();
        }
    }

    private List<Earthquake> getSortedEarthquakes(String sortOption) {
        if (sortOption.equalsIgnoreCase("nearest") && mLastLocation != null) {
            return mEarthquakeRepository.getEarthquakesByNearest(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(), true);
        } else if (sortOption.equalsIgnoreCase("date")) {
            return mEarthquakeRepository.getEarthquakesByDate(true);
        } else if (sortOption.equalsIgnoreCase("title")) {
            return mEarthquakeRepository.getEarthquakesByTitle(true);
        } else if (sortOption.equalsIgnoreCase("depth")) {
            return mEarthquakeRepository.getEarthquakesByDepth(true);
        } else if (sortOption.equalsIgnoreCase("magnitude")) {
            return mEarthquakeRepository.getEarthquakesByMagnitude(true);
        } else {
            return null; // nothing
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

            String magnitude = getString(R.string.earthquake_list_item_magnitude, earthquake.getMagnitude());
            ((TextView) convertView.findViewById(R.id.textMagnitude)).setText(magnitude);

            return convertView;
        }
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
