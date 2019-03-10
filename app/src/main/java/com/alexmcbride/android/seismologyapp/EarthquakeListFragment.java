package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alexmcbride.android.seismologyapp.models.Earthquake;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRepository;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

/*
 * Fragment used to select a list of earthquakes.
 */
public class EarthquakeListFragment extends Fragment implements ChildFragment {
    private static final String TAG = "EarthquakeListFragment";
    private OnFragmentInteractionListener mListener;
    private EarthquakeRepository mEarthquakeRepository;
    private ListView mListEarthquakes;

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
                Earthquake earthquake = (Earthquake) adapterView.getItemAtPosition(position);
                mListener.onEarthquakeSelected(earthquake.getId());
            }
        });

        mEarthquakeRepository = new EarthquakeRepository(getActivity());
        List<Earthquake> earthquakes = mEarthquakeRepository.getEarthquakes();
        updateEarthquakes(earthquakes);

        return view;
    }

    void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public Bundle getSavedState() {
        return new Bundle();
    }

    @Override
    public void loadSavedState(Bundle bundle) {

    }

    void updateEarthquakes(List<Earthquake> earthquakes) {
        Log.d(TAG, "Earthquakes: " + earthquakes.size());
        EarthquakeAdapter adapter = new EarthquakeAdapter(getActivity(), earthquakes);
        mListEarthquakes.setAdapter(adapter);
    }

    private class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
        EarthquakeAdapter(@NonNull Context context, List<Earthquake> earthquakes) {
            super(context, -1);
            addAll(earthquakes);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_item_earthquake, parent, false);
            }

            Earthquake earthquake = Objects.requireNonNull(getItem(position));
            TextView textTitle = convertView.findViewById(R.id.textTitle);
            textTitle.setText(earthquake.getTitle());

            return convertView;
        }
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
