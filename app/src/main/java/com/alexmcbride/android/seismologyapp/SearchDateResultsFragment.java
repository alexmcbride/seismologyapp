/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexmcbride.android.seismologyapp.model.Earthquake;
import com.alexmcbride.android.seismologyapp.model.EarthquakeRepository;
import com.alexmcbride.android.seismologyapp.model.SearchResult;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/*
 * Fragment used to show the results of a search.
 */
public class SearchDateResultsFragment extends ChildFragment {
    private static final String ARG_START_TIME = "ARG_START_TIME";
    private static final String ARG_END_TIME = "ARG_END_TIME";
    private Date mStartDate;
    private Date mEndDate;
    private EarthquakeRepository mEarthquakeRepository;
    private OnFragmentInteractionListener mListener;

    public static SearchDateResultsFragment newInstance(Date start, Date end) {
        SearchDateResultsFragment fragment = new SearchDateResultsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_START_TIME, start.getTime());
        args.putLong(ARG_END_TIME, end.getTime());
        fragment.setArguments(args);
        return fragment;
    }

    void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStartDate = new Date(getArguments().getLong(ARG_START_TIME));
            mEndDate = new Date(getArguments().getLong(ARG_END_TIME));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        RecyclerView listResults = view.findViewById(R.id.listResults);
        listResults.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listResults.setLayoutManager(layoutManager);

        mEarthquakeRepository = new EarthquakeRepository(getActivity());
        List<SearchResult> searchResults = getSearchResults();
        ResultsAdapter resultsAdapter = new ResultsAdapter(searchResults);
        listResults.setAdapter(resultsAdapter);

        return view;
    }

    private List<SearchResult> getSearchResults() {
        List<SearchResult> results = Lists.newArrayList();
        results.add(mEarthquakeRepository.getNorthernmostEarthquake(mStartDate, mEndDate));
        results.add(mEarthquakeRepository.getEasternmostEarthquake(mStartDate, mEndDate));
        results.add(mEarthquakeRepository.getSouthernmostEarthquake(mStartDate, mEndDate));
        results.add(mEarthquakeRepository.getWesternmostEarthquake(mStartDate, mEndDate));
        results.add(mEarthquakeRepository.getLargestMagnitudeEarthquake(mStartDate, mEndDate));
        results.add(mEarthquakeRepository.getLowestDepthEarthquake(mStartDate, mEndDate));
        return results;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void loadSavedState(Bundle savedInstanceState) {

    }

    @Override
    public Bundle getSavedState() {
        return new Bundle();
    }

    private class ResultsAdapter extends RecyclerView.Adapter<ResultViewHolder> {
        private final List<SearchResult> mSearchResults;

        ResultsAdapter(List<SearchResult> searchResults) {
            mSearchResults = searchResults;
        }

        @NonNull
        @Override
        public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_search_result, viewGroup, false);
            return new ResultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultViewHolder viewHolder, int position) {
            SearchResult searchResult = mSearchResults.get(position);
            final Earthquake earthquake = searchResult.getEarthquake();

            viewHolder.textTitle.setText(searchResult.getTitle());
            viewHolder.textLocation.setText(earthquake.getLocation());
            viewHolder.textPubDate.setText(DateUtil.formatDateTime(earthquake.getPubDate()));
            viewHolder.textLat.setText(getString(R.string.earthquake_list_item_latitude, earthquake.getLat()));
            viewHolder.textLon.setText(getString(R.string.earthquake_list_item_longitude, earthquake.getLon()));
            viewHolder.textDepth.setText(getString(R.string.earthquake_list_item_depth, earthquake.getDepth()));
            viewHolder.textMagnitude.setText(getString(R.string.earthquake_list_item_magnitude, earthquake.getMagnitude()));

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onEarthquakeSelected(earthquake.getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mSearchResults.size();
        }
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textLocation;
        TextView textPubDate;
        TextView textLat;
        TextView textLon;
        TextView textDepth;
        TextView textMagnitude;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textLocation = itemView.findViewById(R.id.textLocation);
            textPubDate = itemView.findViewById(R.id.textPubDate);
            textLat = itemView.findViewById(R.id.textLat);
            textLon = itemView.findViewById(R.id.textLon);
            textDepth = itemView.findViewById(R.id.textDepth);
            textMagnitude = itemView.findViewById(R.id.textMagnitude);
        }
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
