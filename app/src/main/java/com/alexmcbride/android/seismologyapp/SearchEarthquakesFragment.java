package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Date;

public class SearchEarthquakesFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public SearchEarthquakesFragment() {
        // Required empty public constructor
    }

    public static SearchEarthquakesFragment newInstance() {
        return new SearchEarthquakesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_earthquakes, container, false);

        Button buttonSearch = view.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date start = new Date();
                Date end = new Date();
                if (mListener != null) {
                    mListener.onSearchEarthquakes(start, end);
                }
            }
        });

        return view;
    }

    void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    public interface OnFragmentInteractionListener {
        void onSearchEarthquakes(Date start, Date end);
    }
}
