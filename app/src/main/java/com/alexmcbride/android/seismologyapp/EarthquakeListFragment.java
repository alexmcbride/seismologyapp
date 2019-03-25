package com.alexmcbride.android.seismologyapp;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alexmcbride.android.seismologyapp.model.Earthquake;
import com.alexmcbride.android.seismologyapp.model.EarthquakeRepository;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

/*
 * Fragment to display a list of earthquakes with sort options. Most sort options involve different
 * database queries, but sorting by the nearest earthquake to you requires your current location,
 * which means a bunch of services and permissions.
 */
public class EarthquakeListFragment extends ChildFragment implements AdapterView.OnItemSelectedListener {
    private static final double DEFAULT_LATITUDE = 54.971516;
    private static final double DEFAULT_LONGITUDE = -2.462196;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String ARG_SELECTED_SORT_OPTION = "ARG_SELECTED_SORT_OPTION";
    private static final String ARG_SELECTED_SORT_DIRECTION = "ARG_SELECTED_SORT_DIRECTION";
    private static final String ARG_LAST_LATITUDE = "ARG_LAST_LATITUDE";
    private static final String ARG_LAST_LONGITUDE = "ARG_LAST_LONGITUDE";
    private static final String ARG_LOCATION_SET = "ARG_LOCATION_SET";
    private OnFragmentInteractionListener mListener;
    private EarthquakeRepository mEarthquakeRepository;
    private RecyclerView mRecyclerViewEarthquakes;
    private Spinner mSpinnerOptions;
    private Spinner mSpinnerDirection;
    private ArrayAdapter<CharSequence> mSpinnerOptionsAdapter;
    private ArrayAdapter<CharSequence> mSpinnerDirectionAdapter;
    private LocationManager mLocationManager;
    private double mLastLatitude;
    private double mLastLongitude;
    private boolean mLocationSet = false;

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
        if (checkLocationPermission()) {
            Location location = getLocationOrFallback();
            if (location != null) {
                mLastLatitude = location.getLatitude();
                mLastLongitude = location.getLongitude();
                mLocationSet = true;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private Location getLocationOrFallback() {
        // todo: rewrite to use more modern fused location provider
        // todo: https://developer.android.com/training/location/retrieve-current.html#GetLocation
        // Try first get GPS, fallback to network.
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    private boolean checkLocationPermission() {
        Activity activity = Objects.requireNonNull(getActivity());
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // todo: make this modal, so user can click yes and repeat permission stuff
                Toast.makeText(activity, R.string.location_permission_explanation, Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if permission granted.
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
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
        Activity activity = Objects.requireNonNull(getActivity());

        // Init recycler view.
        mRecyclerViewEarthquakes = view.findViewById(R.id.listEarthquakes);
        mRecyclerViewEarthquakes.setHasFixedSize(true);
        mRecyclerViewEarthquakes.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Init sort order spinner
        mSpinnerOptions = view.findViewById(R.id.spinnerSortOptions);
        mSpinnerOptionsAdapter = ArrayAdapter.createFromResource(activity, R.array.earthquake_sort_options, android.R.layout.simple_spinner_item);
        mSpinnerOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerOptions.setAdapter(mSpinnerOptionsAdapter);
        mSpinnerOptions.setOnItemSelectedListener(this);

        // Init sort direction spinner
        mSpinnerDirection = view.findViewById(R.id.spinnerSortDirection);
        mSpinnerDirectionAdapter = ArrayAdapter.createFromResource(activity, R.array.earthquake_sort_direction, android.R.layout.simple_spinner_item);
        mSpinnerDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDirection.setAdapter(mSpinnerDirectionAdapter);
        mSpinnerDirection.setOnItemSelectedListener(this);

        // Get spinner setting out of preferences.
        SharedPreferences preferences = getActivity().getPreferences(Activity.MODE_PRIVATE);
        mSpinnerOptions.setSelection(preferences.getInt(ARG_SELECTED_SORT_OPTION, 0));
        mSpinnerDirection.setSelection(preferences.getInt(ARG_SELECTED_SORT_DIRECTION, 0));
        mLastLatitude = Double.longBitsToDouble(preferences.getLong(ARG_LAST_LATITUDE, 0));
        mLastLongitude = Double.longBitsToDouble(preferences.getLong(ARG_LAST_LONGITUDE, 0));
        mLocationSet = preferences.getBoolean(ARG_LOCATION_SET, false);

        // If no location then default to centre of UK.
        if (!mLocationSet) {
            mLastLatitude = DEFAULT_LATITUDE;
            mLastLongitude = DEFAULT_LONGITUDE;
        }

        // Init DB and load earthquakes.
        mEarthquakeRepository = new EarthquakeRepository(getActivity());
        earthquakesUpdated();

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        earthquakesUpdated();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Not used.
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Activity activity = Objects.requireNonNull(getActivity());
        SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ARG_SELECTED_SORT_OPTION, mSpinnerOptions.getSelectedItemPosition());
        editor.putInt(ARG_SELECTED_SORT_DIRECTION, mSpinnerDirection.getSelectedItemPosition());
        editor.putLong(ARG_LAST_LATITUDE, Double.doubleToLongBits(mLastLatitude));
        editor.putLong(ARG_LAST_LONGITUDE, Double.doubleToLongBits(mLastLongitude));
        editor.putBoolean(ARG_LOCATION_SET, mLocationSet);
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
        int position = mSpinnerOptions.getSelectedItemPosition();
        return Objects.requireNonNull(mSpinnerOptionsAdapter.getItem(position)).toString();
    }

    private boolean getSelectedSortDirection() {
        int position = mSpinnerDirection.getSelectedItemPosition();
        String text = Objects.requireNonNull(mSpinnerDirectionAdapter.getItem(position)).toString();
        return text.equalsIgnoreCase("asc");
    }

    void earthquakesUpdated() {
        String sortOption = getSelectedSortOption();
        boolean sortDirection = getSelectedSortDirection();
        earthquakesUpdated(sortOption, sortDirection);
    }

    private void earthquakesUpdated(String sortOption, boolean sortDirection) {
        List<Earthquake> earthquakes = getSortedEarthquakes(sortOption, sortDirection);
        if (earthquakes != null) {
            // We need to reset the adapter to get it to change, as we're not reordering the list,
            // we're creating a new one each time from the database.
            // todo: look into just loading the list once and then sorting it.
            EarthquakesAdapter earthquakesAdapter = new EarthquakesAdapter(earthquakes);
            mRecyclerViewEarthquakes.setAdapter(earthquakesAdapter);
        } else {
            Toast.makeText(getActivity(), R.string.no_earthquakes_message, Toast.LENGTH_SHORT).show();
        }
    }

    private List<Earthquake> getSortedEarthquakes(String sortOption, boolean sortDirection) {
        if (sortOption.equalsIgnoreCase("nearest")) {
            return mEarthquakeRepository.getEarthquakesByNearest(mLastLatitude, mLastLongitude, sortDirection);
        } else if (sortOption.equalsIgnoreCase("date")) {
            return mEarthquakeRepository.getEarthquakesByDate(sortDirection);
        } else if (sortOption.equalsIgnoreCase("location")) {
            return mEarthquakeRepository.getEarthquakesByTitle(sortDirection);
        } else if (sortOption.equalsIgnoreCase("depth")) {
            return mEarthquakeRepository.getEarthquakesByDepth(sortDirection);
        } else if (sortOption.equalsIgnoreCase("magnitude")) {
            return mEarthquakeRepository.getEarthquakesByMagnitude(sortDirection);
        } else {
            return null; // nothing yo
        }
    }

    private class EarthquakesAdapter extends RecyclerView.Adapter<EarthquakeViewHolder> {
        private final List<Earthquake> mEarthquakes;

        EarthquakesAdapter(List<Earthquake> earthquakes) {
            super();
            mEarthquakes = earthquakes;
        }

        @NonNull
        @Override
        public EarthquakeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_earthquake, viewGroup, false);
            return new EarthquakeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EarthquakeViewHolder viewHolder, int position) {
            final Earthquake earthquake = Objects.requireNonNull(mEarthquakes.get(position));

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onEarthquakeSelected(earthquake.getId());
                }
            });

            viewHolder.textTitle.setText(earthquake.getLocation());
            viewHolder.textPubDate.setText(DateUtil.formatDateTime(earthquake.getPubDate()));
            String depth = getString(R.string.earthquake_list_item_depth, earthquake.getDepth());
            viewHolder.textDepth.setText(depth);
            String magnitude = getString(R.string.earthquake_list_item_magnitude, earthquake.getMagnitude());
            viewHolder.textMagnitude.setText(magnitude);
        }

        @Override
        public int getItemCount() {
            return mEarthquakes.size();
        }
    }

    private class EarthquakeViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textPubDate;
        TextView textDepth;
        TextView textMagnitude;

        EarthquakeViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPubDate = itemView.findViewById(R.id.textPubDate);
            textDepth = itemView.findViewById(R.id.textDepth);
            textMagnitude = itemView.findViewById(R.id.textMagnitude);
        }
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
