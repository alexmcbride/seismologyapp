package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexmcbride.android.seismologyapp.models.Earthquake;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/*
 * Fragment used to display the details of a single earthquake.
 */
public class EarthquakeDetailFragment extends ChildFragment {
    private static final String ARG_EARTHQUAKE_ID = "ARG_EARTHQUAKE_ID";
    private static final String MAP_VIEW_BUNDLE_KEY = "MAP_VIEW_BUNDLE_KEY";
    private static final float ZOOM_LEVEL = 14; // higher is closer
    private EarthquakeRepository mEarthquakeRepository;
    private Earthquake mEarthquake;
    private MapView mMapView;
    private TextView mTextLocation;
    private TextView mTextPubDate;
    private TextView mTextMagnitude;
    private TextView mTextDepth;
    private TextView mTextCategory;
    private TextView mTextLat;
    private TextView mTextLon;

    public static EarthquakeDetailFragment newInstance() {
        return new EarthquakeDetailFragment();
    }

    public static EarthquakeDetailFragment newInstance(long id) {
        EarthquakeDetailFragment fragment = newInstance();
        Bundle args = new Bundle();
        args.putLong(ARG_EARTHQUAKE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_detail, container, false);

        mTextLocation = view.findViewById(R.id.textLocation);
        mTextPubDate = view.findViewById(R.id.textPubDate);
        mTextMagnitude = view.findViewById(R.id.textMagnitude);
        mTextDepth = view.findViewById(R.id.textDepth);
        mTextCategory = view.findViewById(R.id.textCategory);
        mTextLat = view.findViewById(R.id.textLat);
        mTextLon = view.findViewById(R.id.textLon);

        // Init map (with state)
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);

        updateEarthquake();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save state of map view.
        // todo: this might not save state due to child fragment state issues.
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEarthquakeRepository = new EarthquakeRepository(getActivity());

        // Load the earthquake we want to show details for.
        Bundle args = getArguments();
        if (args != null) {
            long id = args.getLong(ARG_EARTHQUAKE_ID, -1);
            mEarthquake = mEarthquakeRepository.getEarthquake(id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // We need to pass all these on to the map view.
        mMapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public Bundle getSavedState() {
        return new Bundle();
    }

    @Override
    public void loadSavedState(Bundle bundle) {

    }

    void updateEarthquake(long id) {
        mEarthquake = mEarthquakeRepository.getEarthquake(id);
        updateEarthquake();
    }

    private void updateEarthquake() {
        if (mEarthquake == null) {
            return;
        }
        mTextLocation.setText(mEarthquake.getLocation());
        mTextPubDate.setText("Date: " + Util.formatPretty(mEarthquake.getPubDate()));
        mTextMagnitude.setText(getString(R.string.earthquake_list_item_magnitude, mEarthquake.getMagnitude()));
        mTextDepth.setText(getString(R.string.earthquake_list_item_magnitude, mEarthquake.getDepth()));
        mTextCategory.setText("Category: " + mEarthquake.getCategory());
        mTextLat.setText("Latitude: " + mEarthquake.getLat());
        mTextLon.setText("Longitude: " + mEarthquake.getLon());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = new LatLng(mEarthquake.getLat(), mEarthquake.getLon());

                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(mEarthquake.getLocation());
                Marker marker = googleMap.addMarker(options);

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM_LEVEL));
            }
        });
    }
}
