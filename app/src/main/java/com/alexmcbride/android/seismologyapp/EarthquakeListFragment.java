package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alexmcbride.android.seismologyapp.models.Earthquake;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRepository;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nullable;

/*
 * Fragment used to select a list of earthquakes.
 */
public class EarthquakeListFragment extends ChildFragment {
    private static final String TAG = "EarthquakeListFragment";
    private OnFragmentInteractionListener mListener;
    private EarthquakeRepository mEarthquakeRepository;
    private ListView mListEarthquakes;
    private Spinner mSpinnerSortOptions;
    private ArrayAdapter<CharSequence> mSpinnerSortOptionsAdapter;

    public EarthquakeListFragment() {
        // Required empty public constructor
    }

    public static EarthquakeListFragment newInstance() {
        return new EarthquakeListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);

        mListEarthquakes = view.findViewById(R.id.listEarthquakes);
        mListEarthquakes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mListener.onEarthquakeSelected(id);
            }
        });

        mSpinnerSortOptions = view.findViewById(R.id.spinnerSortOptions);
        mSpinnerSortOptionsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.earthquake_sort_options, android.R.layout.simple_spinner_item);
        mSpinnerSortOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSortOptions.setAdapter(mSpinnerSortOptionsAdapter);
        mSpinnerSortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String sortOption = mSpinnerSortOptionsAdapter.getItem(position).toString();
                earthquakesUpdated(sortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mEarthquakeRepository = new EarthquakeRepository(getActivity());
        earthquakesUpdated();

        return view;
    }

    void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public Bundle getSavedState() {
        Bundle state = new Bundle();
        return state;
    }

    @Override
    public void loadSavedState(Bundle bundle) {

    }

    private String getSelectedSortOption() {
        int position = mSpinnerSortOptions.getSelectedItemPosition();
        return Objects.requireNonNull(mSpinnerSortOptionsAdapter.getItem(position)).toString();
    }

    void earthquakesUpdated() {
        String sortOption = getSelectedSortOption();
        earthquakesUpdated(sortOption);
    }

    private void earthquakesUpdated(String sortOption) {
        List<Earthquake> earthquakes = sortEarthquakeList(sortOption);
        EarthquakesAdapter adapter = new EarthquakesAdapter(Objects.requireNonNull(getActivity()), earthquakes);
        mListEarthquakes.setAdapter(adapter);
    }

    private List<Earthquake> sortEarthquakeList(String sortOption) {
        if (sortOption.equalsIgnoreCase("nearest")) {
            return mEarthquakeRepository.getEarthquakesByNearest(55.746310, -4.182994);
        } else if (sortOption.equalsIgnoreCase("date")) {
            return mEarthquakeRepository.getEarthquakesByDate();
        } else if (sortOption.equalsIgnoreCase("title")) {
            return mEarthquakeRepository.getEarthquakesByTitle();
        } else if (sortOption.equalsIgnoreCase("depth")) {
            return mEarthquakeRepository.getEarthquakesByDepth();
        }else if (sortOption.equalsIgnoreCase("magnitude")) {
            return mEarthquakeRepository.getEarthquakesByMagnitude();
        }else {
            return Lists.newArrayList();
        }
    }

    private class EarthquakesAdapter extends ArrayAdapter<Earthquake> {
        EarthquakesAdapter(@NonNull Context context, List<Earthquake> earthquakes) {
            super(context, -1);
            addAll(earthquakes);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_earthquake, parent, false);
            }

            Earthquake earthquake = Objects.requireNonNull(getItem(position));

            ((TextView) convertView.findViewById(R.id.textLocation)).setText(earthquake.getLocation());
            ((TextView) convertView.findViewById(R.id.textDate)).setText(Util.formatPretty(earthquake.getPubDate()));

            String depth = "Depth: " + String.format(Locale.ENGLISH, "%.0f", earthquake.getDepth()) + " km";
            ((TextView) convertView.findViewById(R.id.textDepth)).setText(depth);

            String magnitude = "Magnitude: " + String.format(Locale.ENGLISH, "%.2f", earthquake.getMagnitude());
            ((TextView) convertView.findViewById(R.id.textMagnitude)).setText(magnitude);

            return convertView;
        }
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
