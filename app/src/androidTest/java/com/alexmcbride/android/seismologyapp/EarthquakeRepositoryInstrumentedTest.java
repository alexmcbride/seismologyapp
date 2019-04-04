package com.alexmcbride.android.seismologyapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.alexmcbride.android.seismologyapp.model.Earthquake;
import com.alexmcbride.android.seismologyapp.model.EarthquakeDbHelper;
import com.alexmcbride.android.seismologyapp.model.EarthquakeRepository;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EarthquakeRepositoryInstrumentedTest {
    private static final String DB_NAME = "test-db";
    private Context mAppContext;
    private EarthquakeRepository mRepository;
    private EarthquakeDbHelper mDbHelper;

    @Before
    public void setup() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        mDbHelper = new EarthquakeDbHelper(mAppContext, DB_NAME);
        mDbHelper.createTables();
        mRepository = new EarthquakeRepository(mDbHelper);
    }

    @After
    public void tearDown() {
        mDbHelper.dropTables();
    }

    @Test
    public void useAppContext() {
        assertEquals("com.alexmcbride.android.seismologyapp", mAppContext.getPackageName());
    }

    private Earthquake createEarthquake(String title) {
        return createEarthquake(title, new Date());
    }

    private Earthquake createEarthquake(String title, Date pubDate) {
        return createEarthquake(title, pubDate, 0, 0);
    }

    private Earthquake createEarthquake(String title, double depth, double magnitude) {
        return createEarthquake(title, new Date(), depth, magnitude);
    }

    private Earthquake createEarthquake(String title, Date pubDate, double depth, double magnitude) {
        Earthquake earthquake = new Earthquake();
        earthquake.setTitle(title);
        earthquake.setPubDate(pubDate); // date can't be null
        earthquake.setDepth(depth);
        earthquake.setMagnitude(magnitude);
        return earthquake;
    }

    private Earthquake createEarthquake(String title, String location) {
        Earthquake earthquake = new Earthquake();
        earthquake.setTitle(title);
        earthquake.setPubDate(new Date()); // date can't be null
        earthquake.setLocation(location);
        return earthquake;
    }

    private Earthquake createEarthquakeWithLatLon(String title, double lat, double lon) {
        Earthquake earthquake = new Earthquake();
        earthquake.setPubDate(new Date());
        earthquake.setTitle(title);
        earthquake.setLat(lat);
        earthquake.setLon(lon);
        return earthquake;
    }

    private Date createPubDate(int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 4, 4, hour, min);
        return calendar.getTime();
    }

    @Test
    public void testAddEarthquakes() {
        Earthquake earthquake1 = createEarthquake("Earthquake 1");
        Earthquake earthquake2 = createEarthquake("Earthquake 2");
        addEarthquakes(earthquake1, earthquake2);

        List<Earthquake> result = mRepository.getEarthquakes();
        assertEquals(2, result.size());
        assertEquals(earthquake1.getTitle(), result.get(0).getTitle());
        assertEquals(earthquake2.getTitle(), result.get(1).getTitle());
    }

    @Test
    public void testGetEarthquakes() {
        Earthquake earthquake1 = createEarthquake("Earthquake 1", createPubDate(14, 19));
        Earthquake earthquake2 = createEarthquake("Earthquake 2", createPubDate(14, 19));
        addEarthquakes(earthquake1, earthquake2);

        List<Earthquake> result = mRepository.getEarthquakes();
        assertEquals(2, result.size());
        assertEquals(earthquake1.getTitle(), result.get(0).getTitle());
        assertEquals(earthquake2.getTitle(), result.get(1).getTitle());
    }

    @Test
    public void testGetEarthquakesByDate() {
        Earthquake earthquake1 = createEarthquake("Earthquake 1", createPubDate(14, 19));
        Earthquake earthquake2 = createEarthquake("Earthquake 2", createPubDate(14, 20));
        addEarthquakes(earthquake1, earthquake2);

        assertEarthquakesEqual(mRepository.getEarthquakesByDate(true),  earthquake1, earthquake2);
        assertEarthquakesEqual(mRepository.getEarthquakesByDate(false),  earthquake2, earthquake1);
    }

    @Test
    public void testGetEarthquakesByTitle() {
        Earthquake earthquake1 = createEarthquake("Earthquake 1");
        Earthquake earthquake2 = createEarthquake("Earthquake 2");
        addEarthquakes(earthquake1, earthquake2);

        assertEarthquakesEqual(mRepository.getEarthquakesByTitle(true), earthquake1, earthquake2);
        assertEarthquakesEqual(mRepository.getEarthquakesByTitle(false), earthquake2, earthquake1);
    }

    @Test
    public void testGetEarthquakesByDepth() {
        Earthquake earthquake1 = createEarthquake("Earthquake 1", 1.0, 0);
        Earthquake earthquake2 = createEarthquake("Earthquake 2", 2.0, 0);
        addEarthquakes(earthquake1, earthquake2);

        assertEarthquakesEqual(mRepository.getEarthquakesByDepth(true), earthquake1, earthquake2);
        assertEarthquakesEqual(mRepository.getEarthquakesByDepth(false), earthquake2, earthquake1);
    }

    @Test
    public void testGetEarthquakesByMagnitude() {
        Earthquake earthquake1 = createEarthquake("Earthquake 1", 0, 1.0);
        Earthquake earthquake2 = createEarthquake("Earthquake 2", 0, 2.0);
        addEarthquakes(earthquake1, earthquake2);

        assertEarthquakesEqual(mRepository.getEarthquakesByMagnitude(true), earthquake1, earthquake2);
        assertEarthquakesEqual(mRepository.getEarthquakesByMagnitude(false), earthquake2, earthquake1);
    }

    @Test
    public void testGetEarthquakesByNearest() {
        Earthquake earthquake1 = createEarthquakeWithLatLon("Earthquake 1", 2.0, 2.0);
        Earthquake earthquake2 = createEarthquakeWithLatLon("Earthquake 2", 3, 3.0);
        addEarthquakes(earthquake1, earthquake2);

        assertEarthquakesEqual(mRepository.getEarthquakesByNearest(1.0, 1.0, true), earthquake1, earthquake2);
        assertEarthquakesEqual(mRepository.getEarthquakesByNearest(1.0, 1.0, false), earthquake2, earthquake1);
    }

    @Test
    public void testGetEarthquakesByLocation() {
        Earthquake earthquake1 = createEarthquake("Earthquake 1", "abcd");
        Earthquake earthquake2 = createEarthquake("Earthquake 2", "bbcd");
        Earthquake earthquake3 = createEarthquake("Earthquake 3", "efgh");
        List<Earthquake> earthquakes = Lists.newArrayList(earthquake1, earthquake2, earthquake3);
        mRepository.addEarthquakes(earthquakes);

        assertEarthquakesEqual(mRepository.getEarthquakesByLocation("bc", true), earthquake1, earthquake2);
        assertEarthquakesEqual(mRepository.getEarthquakesByLocation("bc", false), earthquake2, earthquake1);
    }

    private void addEarthquakes(Earthquake earthquake1, Earthquake earthquake2) {
        List<Earthquake> earthquakes = Lists.newArrayList(earthquake1, earthquake2);
        mRepository.addEarthquakes(earthquakes);
    }

    private void assertEarthquakesEqual(List<Earthquake> result1, Earthquake earthquake1, Earthquake earthquake2) {
        assertEquals(2, result1.size());
        assertEquals(earthquake1.getTitle(), result1.get(0).getTitle());
        assertEquals(earthquake2.getTitle(), result1.get(1).getTitle());
    }
}
