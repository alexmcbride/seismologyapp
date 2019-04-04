/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexmcbride.android.seismologyapp.model.Earthquake;
import com.alexmcbride.android.seismologyapp.model.EarthquakeRepository;
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
    private static final float ZOOM_LEVEL = 11; // higher is closer
    private Earthquake mEarthquake;
    private MapView mMapView;

    public static EarthquakeDetailFragment newInstance(long id) {
        EarthquakeDetailFragment fragment = new EarthquakeDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_EARTHQUAKE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_detail, container, false);

        TextView textLocation = view.findViewById(R.id.textTitle);
        textLocation.setText(mEarthquake.getLocation());

        TextView textPubDate = view.findViewById(R.id.textPubDate);
        textPubDate.setText(getString(R.string.earthquake_list_item_pub_date, DateUtil.formatDateTime(mEarthquake.getPubDate())));

        TextView textMagnitude = view.findViewById(R.id.textMagnitude);
        textMagnitude.setText(getString(R.string.earthquake_list_item_magnitude, mEarthquake.getMagnitude()));

        TextView textDepth = view.findViewById(R.id.textDepth);
        textDepth.setText(getString(R.string.earthquake_list_item_depth, mEarthquake.getDepth()));

        TextView textCategory = view.findViewById(R.id.textCategory);
        textCategory.setText(getString(R.string.earthquake_list_item_category, mEarthquake.getCategory()));

        TextView textLat = view.findViewById(R.id.textLat);
        textLat.setText(getString(R.string.earthquake_list_item_latitude, mEarthquake.getLat()));

        TextView textLon = view.findViewById(R.id.textLon);
        textLon.setText(getString(R.string.earthquake_list_item_longitude, mEarthquake.getLon()));

        // Init map view (with bundle state)
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle); // We need to manually call all of these :/
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Get location
                LatLng latLng = new LatLng(mEarthquake.getLat(), mEarthquake.getLon());

                // Add marker
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(mEarthquake.getLocation());
                Marker marker = googleMap.addMarker(options);

                // Move camera to marker.
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM_LEVEL));
            }
        });

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

        EarthquakeRepository earthquakeRepository = new EarthquakeRepository(getActivity());

        // Get earthquake we want to show details for.
        Bundle args = getArguments();
        if (args != null) {
            long id = args.getLong(ARG_EARTHQUAKE_ID, -1);
            mEarthquake = earthquakeRepository.getEarthquake(id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // We need to manually all these on the map view.
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
        return null;
    }

    @Override
    public void loadSavedState(Bundle bundle) {

    }
}
