package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alexmcbride.android.seismologyapp.models.Earthquake;
import com.alexmcbride.android.seismologyapp.models.EarthquakeCursorWrapper;
import com.alexmcbride.android.seismologyapp.models.EarthquakeRepository;

/*
 * Fragment used to select a list of earthquakes.
 */
public class EarthquakeListFragment extends Fragment implements ChildFragment {
    private static final String TAG = "EarthquakeListFragment";
    private OnFragmentInteractionListener mListener;
    private EarthquakeRepository mEarthquakeRepository;
    private ListView mListEarthquakes;
    private Cursor mEarthquakeCursor;
    private SQLiteDatabase mDatabase;

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
        updateEarthquakes();

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
        return new Bundle();
    }

    @Override
    public void loadSavedState(Bundle bundle) {

    }

    void updateEarthquakes() {
        closeDatabase();

        // We do it this way so we can close the resources later.
        mDatabase = mEarthquakeRepository.getReadableDatabase();
        mEarthquakeCursor = mEarthquakeRepository.getEarthquakesCursor(mDatabase);

        EarthquakeCursorAdapter adapter = new EarthquakeCursorAdapter(getActivity(), mEarthquakeCursor);
        mListEarthquakes.setAdapter(adapter);
    }

    private void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
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
            TextView textTitle = view.findViewById(R.id.textTitle);
            textTitle.setText(earthquake.getTitle());
        }
    }

    public interface OnFragmentInteractionListener {
        void onEarthquakeSelected(long id);
    }
}
