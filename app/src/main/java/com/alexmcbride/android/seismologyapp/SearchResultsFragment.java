package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

public class SearchResultsFragment extends Fragment {
    private static final String ARG_START_TIME = "ARG_START_TIME";
    private static final String ARG_END_TIME = "ARG_END_TIME";
    private OnFragmentInteractionListener mListener;
    private Date mStartDate;
    private Date mEndDate;

    public static SearchResultsFragment newInstance(Date start, Date end) {
        SearchResultsFragment fragment = new SearchResultsFragment();
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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchResultsFragment.OnFragmentInteractionListener) {
            mListener = (SearchResultsFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {

    }
}
