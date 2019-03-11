package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alexmcbride.android.seismologyapp.models.CloseableCursor;
import com.alexmcbride.android.seismologyapp.models.Earthquake;
import com.alexmcbride.android.seismologyapp.models.EarthquakeCursorWrapper;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRepository;

import java.util.Locale;

/*
 * Fragment used to select a list of earthquakes.
 */
public class EarthquakeListFragment extends ChildFragment {
    private static final String TAG = "EarthquakeListFragment";
    private OnFragmentInteractionListener mListener;
    private EarthquakeRepository mEarthquakeRepository;
    private ListView mListEarthquakes;
    private CloseableCursor mEarthquakeCursor;


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

        Spinner spinnerSortOptions = view.findViewById(R.id.spinnerSortOptions);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.earthquake_sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortOptions.setAdapter(adapter);
        spinnerSortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mEarthquakeRepository = new EarthquakeRepository(getActivity());
        earthquakesUpdated();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        closeDatabase();
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

    void earthquakesUpdated() {
        closeDatabase();

        // We do it this way so we can close the resources later.
        mEarthquakeCursor = mEarthquakeRepository.getEarthquakesCursor();
        EarthquakeCursorAdapter adapter = new EarthquakeCursorAdapter(getActivity(), mEarthquakeCursor.getCursor());
        mListEarthquakes.setAdapter(adapter);
    }

    private void closeDatabase() {
        if (mEarthquakeCursor != null) {
            mEarthquakeCursor.close();
        }
    }

    private class EarthquakeCursorAdapter extends CursorAdapter {
        private EarthquakeCursorWrapper mCursorWrapper;

        EarthquakeCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
            mCursorWrapper = new EarthquakeCursorWrapper(cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.list_item_earthquake, viewGroup, false);
            bindView(view, context, cursor);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Earthquake earthquake = mCursorWrapper.getEarthquake();

            ((TextView) view.findViewById(R.id.textLocation)).setText(earthquake.getLocation());
            ((TextView) view.findViewById(R.id.textDate)).setText(Util.formatPretty(earthquake.getPubDate()));

            String depth = "Depth: " +  String.format(Locale.ENGLISH, "%.0f", earthquake.getDepth()) + " km";
            ((TextView) view.findViewById(R.id.textDepth)).setText(depth);

            String magnitude = "Magnitude: " +  String.format(Locale.ENGLISH, "%.2f", earthquake.getMagnitude());
            ((TextView) view.findViewById(R.id.textMagnitude)).setText(magnitude);
        }
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
