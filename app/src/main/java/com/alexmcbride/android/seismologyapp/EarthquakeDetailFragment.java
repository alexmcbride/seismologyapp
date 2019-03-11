package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
 * Fragment used to display the details of a single earthquake.
 */
public class EarthquakeDetailFragment extends ChildFragment {
    private static final String ARG_EARTHQUAKE_ID = "ARG_EARTHQUAKE_ID";
    private TextView mTextView;
    private long mCurrentId;

    public static EarthquakeDetailFragment newInstance() {
        return  new EarthquakeDetailFragment();
    }

    public static EarthquakeDetailFragment newInstance(long id) {
        EarthquakeDetailFragment fragment =  newInstance();
        Bundle args = new Bundle();
        args.putLong(ARG_EARTHQUAKE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_detail, container, false);
        mTextView = view.findViewById(R.id.textView);
        updateEarthquake();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCurrentId = args.getLong(ARG_EARTHQUAKE_ID, -1);
        }
    }

    void updateEarthquake() {
        updateEarthquake(mCurrentId);
    }

    void updateEarthquake(long id) {
        mTextView.setText("Selected " + id);
    }

    @Override
    public Bundle getSavedState() {
        return new Bundle();
    }

    @Override
    public void loadSavedState(Bundle bundle) {

    }
}
