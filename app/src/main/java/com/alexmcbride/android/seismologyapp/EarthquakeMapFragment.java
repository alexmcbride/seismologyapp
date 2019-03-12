package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexmcbride.android.seismologyapp.model.Earthquake;
import com.alexmcbride.android.seismologyapp.model.EarthquakeRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/*
 * Fragment to show all earthquakes plotted on a map.
 */
public class EarthquakeMapFragment extends Fragment {
    private static final int ZOOM_LEVEL = 6;
    private MapView mMapView;

    public EarthquakeMapFragment() {
        // Required empty public constructor
    }

    public static EarthquakeMapFragment newInstance() {
        return new EarthquakeMapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_map, container, false);

        EarthquakeRepository repository = new EarthquakeRepository(getActivity());
        final List<Earthquake> earthquakes = repository.getEarthquakes();

        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                for (Earthquake earthquake : earthquakes) {
                    // Get location
                    LatLng latLng = new LatLng(earthquake.getLat(), earthquake.getLon());

                    String title = getString(R.string.earthquake_map_marker_title,
                            earthquake.getLocation(),
                            earthquake.getDepth(),
                            earthquake.getMagnitude());

                    // Add marker
                    MarkerOptions options = new MarkerOptions();
                    options.position(latLng);
                    options.title(title);
                    googleMap.addMarker(options);
                }

                LatLng centreOfUk = new LatLng(54.971516, -2.462196);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centreOfUk, ZOOM_LEVEL));
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
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
}
