package com.alexmcbride.android.seismologyapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexmcbride.android.seismologyapp.model.Earthquake;
import com.alexmcbride.android.seismologyapp.model.EarthquakeRepository;

import java.util.List;

public class SearchLocationResultsFragment extends ChildFragment {
    private static final String ARG_LOCATION = "ARG_LOCATION";

    private String mLocation;
    private EarthquakeRepository mEarthquakeRepository;
    private OnFragmentInteractionListener mListener;

    public SearchLocationResultsFragment() {
        // Required empty public constructor
    }

    void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    public static SearchLocationResultsFragment newInstance(String location) {
        SearchLocationResultsFragment fragment = new SearchLocationResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mLocation = arguments.getString(ARG_LOCATION);
        }
        mEarthquakeRepository = new EarthquakeRepository(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_location_results, container, false);

        List<Earthquake> earthquakes = mEarthquakeRepository.getEarthquakesByLocation(mLocation, true);
        LocationResultsAdapter adapter = new LocationResultsAdapter(earthquakes);

        RecyclerView listEarthquakes = view.findViewById(R.id.listResults);
        listEarthquakes.setHasFixedSize(true);
        listEarthquakes.setLayoutManager(new LinearLayoutManager(getActivity()));
        listEarthquakes.setAdapter(adapter);

        return view;
    }

    @Override
    public Bundle getSavedState() {
        return null;
    }

    @Override
    public void loadSavedState(Bundle bundle) {

    }

    private class LocationResultsAdapter extends RecyclerView.Adapter<ResultViewHolder> {
        private final List<Earthquake> mEarthquakes;

        LocationResultsAdapter(List<Earthquake> earthquakes) {
            super();
            mEarthquakes = earthquakes;
        }

        @NonNull
        @Override
        public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_earthquake, viewGroup, false);
            return new ResultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultViewHolder resultViewHolder, int position) {
            final Earthquake earthquake = mEarthquakes.get(position);
            resultViewHolder.textTitle.setText(earthquake.getLocation());
            resultViewHolder.textPubDate.setText(Util.formatPretty(earthquake.getPubDate()));
            String depth = getString(R.string.earthquake_list_item_depth, earthquake.getDepth());
            resultViewHolder.textDepth.setText(depth);
            String magnitude = getString(R.string.earthquake_list_item_magnitude, earthquake.getMagnitude());
            resultViewHolder.textMagnitude.setText(magnitude);

            resultViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onEarthquakeSelected(earthquake.getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEarthquakes.size();
        }
    }

    private static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textPubDate;
        TextView textDepth;
        TextView textMagnitude;

        ResultViewHolder(@NonNull View itemView) {
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
