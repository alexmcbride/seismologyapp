package com.alexmcbride.android.seismologyapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.alexmcbride.android.seismologyapp.model.EarthquakeRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/*
 * Fragment used to search for earthquakes.
 */
public class SearchEarthquakesFragment extends ChildFragment {
    private static final String ARG_START_DATE = "SEARCH_START_DATE";
    private static final String ARG_END_DATE = "SEARCH_END_DATE";
    private OnFragmentInteractionListener mListener;

    private Calendar mStartDate = Calendar.getInstance();
    private Calendar mEndDate = Calendar.getInstance();

    public SearchEarthquakesFragment() {
        // Required empty public constructor
    }

    public static SearchEarthquakesFragment newInstance() {
        return new SearchEarthquakesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EarthquakeRepository earthquakeRepository = new EarthquakeRepository(getActivity());
        mStartDate.setTime(earthquakeRepository.getLowestDate());
        mStartDate.set(Calendar.HOUR_OF_DAY, 0);
        mStartDate.set(Calendar.MINUTE, 0);
        mEndDate.setTime(earthquakeRepository.getHighestDate());
        mEndDate.set(Calendar.HOUR_OF_DAY, 23);
        mEndDate.set(Calendar.MINUTE, 59);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_earthquakes, container, false);

        final FragmentActivity context = Objects.requireNonNull(getActivity());
        final TextView textStartDate = view.findViewById(R.id.textStartDate);
        textStartDate.setText(Util.formatDate(mStartDate.getTime()));
        final TextView textEndDate = view.findViewById(R.id.textEndDate);
        textEndDate.setText(Util.formatDate(mEndDate.getTime()));

        Button chooseStartDate = view.findViewById(R.id.buttonStartDate);
        setDateListener(context, textStartDate, chooseStartDate, mStartDate);

        Button chooseEndDate = view.findViewById(R.id.buttonEndDate);
        setDateListener(context, textEndDate, chooseEndDate, mEndDate);

        Button buttonSearch = view.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onSearchEarthquakes(mStartDate.getTime(), mEndDate.getTime());
                }
            }
        });

        return view;
    }

    private void setDateListener(final FragmentActivity context, final TextView textView, final Button button, final Calendar calendar) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        textView.setText(Util.formatDate(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public void loadSavedState(Bundle bundle) {
        mStartDate = (Calendar) bundle.getSerializable(ARG_START_DATE);
        mEndDate = (Calendar) bundle.getSerializable(ARG_END_DATE);
    }

    @Override
    public Bundle getSavedState() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_START_DATE, mStartDate);
        bundle.putSerializable(ARG_END_DATE, mEndDate);
        return bundle;
    }

    public interface OnFragmentInteractionListener {
        void onSearchEarthquakes(Date start, Date end);
    }
}
