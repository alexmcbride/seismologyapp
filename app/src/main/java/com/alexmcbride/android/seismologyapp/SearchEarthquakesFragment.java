package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Date;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchEarthquakesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchEarthquakesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchEarthquakesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchEarthquakesFragment() {
        // Required empty public constructor
    }

    public static SearchEarthquakesFragment newInstance() {
        SearchEarthquakesFragment fragment = new SearchEarthquakesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "");
        args.putString(ARG_PARAM2, "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_earthquakes, container, false);

        final FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        final FrameLayout searchResultsContainer = view.findViewById(R.id.searchResultsContainer);
        if (searchResultsContainer != null) {
            // In landscape mode
        }

        Button buttonSearch = view.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date start = new Date();
                Date end = new Date();

                // If contain doesn't exist tell main activity to launch activity, otherwise we
                // should it ourselves in a fragment.
                if (searchResultsContainer == null) {
                    mListener.onSearchEarthquakes(start, end);
                } else {
                    Fragment fragment = SearchResultsFragment.newInstance(start, end);
                    fm.beginTransaction().replace(R.id.searchResultsContainer, fragment).commitNow();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onSearchEarthquakes(Date start, Date end);
    }
}
