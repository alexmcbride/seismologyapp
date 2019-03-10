package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

/*
 * Fragment used to show the results of a search.
 */
public class SearchResultsFragment extends Fragment implements ChildFragment {
    private static final String ARG_START_TIME = "ARG_START_TIME";
    private static final String ARG_END_TIME = "ARG_END_TIME";
    private Date mStartDate;
    private Date mEndDate;
    private TextView textView;

    public static SearchResultsFragment newInstance() {
        return new SearchResultsFragment();
    }

    public static SearchResultsFragment newInstance(Date start, Date end) {
        SearchResultsFragment fragment = newInstance();
        Bundle args = new Bundle();
        args.putLong(ARG_START_TIME, start.getTime());
        args.putLong(ARG_END_TIME, end.getTime());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStartDate = new Date(getArguments().getLong(ARG_START_TIME));
            mEndDate = new Date(getArguments().getLong(ARG_START_TIME));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        textView = view.findViewById(R.id.textView);
        if (mStartDate != null && mEndDate != null) {
            textView.setText("Start: " + mStartDate.toString() + " End: " + mEndDate.toString());
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void updateSearchResults(Date start, Date end) {
        // update ui
        textView.setText("Updated");
    }

    @Override
    public void setSavedState(Bundle savedInstanceState) {

    }

    @Override
    public Bundle getSavedState() {
        return new Bundle();
    }
}
