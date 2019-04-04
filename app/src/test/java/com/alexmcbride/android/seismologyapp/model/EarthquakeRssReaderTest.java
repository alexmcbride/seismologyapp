/*
 * Name: Alex McBride
 * Student ID: S1715224
 */
package com.alexmcbride.android.seismologyapp.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class EarthquakeRssReaderTest {
    @Test
    public void testParseXml() throws Exception {
        EarthquakeRssReader earthquakeRssReader = new TestableEarthquakeRssReader();

        List<Earthquake> earthquakes = earthquakeRssReader.parse("http://www.example.com/earthquakes.xml");

        assertEquals(2, earthquakes.size());
    }

    /*
     * Test implementation that overrides the input stream source to provide our mock XML.
     */
    private class TestableEarthquakeRssReader extends EarthquakeRssReader {
        private static final String FAKE_EARTHQUAKES_XML = "<?xml version=\"1.0\"?>\n" +
                "<rss version=\"2.0\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "<channel>\n" +
                "<title>Recent UK earthquakes</title>\n" +
                "<link>http://earthquakes.bgs.ac.uk/</link>\n" +
                "<description>Recent UK seismic events recorded by the BGS Seismology team</description>\n" +
                "<language>en-gb</language>\n" +
                "<lastBuildDate>Sun, 24 Mar 2019 12:40:01</lastBuildDate>\n" +
                "<image>\n" +
                "<title>BGS Logo</title>\n" +
                "<url>http://www.bgs.ac.uk/images/logos/bgs_c_w_227x50.gif</url>\n" +
                "<link>http://earthquakes.bgs.ac.uk/</link>\n" +
                "</image>\n" +
                "<item>\n" +
                "<title>UK Earthquake alert : M  1.8 :ISLAY,ARGYLL AND BUTE, Sat, 16 Mar 2019 10:11:31</title>\n" +
                "<description>Origin date/time: Sat, 16 Mar 2019 10:11:31 ; Location: ISLAY,ARGYLL AND BUTE ; Lat/long: 55.794,-6.353 ; Depth: 7 km ; Magnitude:  1.8</description>\n" +
                "<link>http://earthquakes.bgs.ac.uk/earthquakes/recent_events/20190316101132.html</link>\n" +
                "<pubDate>Sat, 16 Mar 2019 10:11:31</pubDate>\n" +
                "<category>EQUK</category>\n" +
                "<geo:lat>55.794</geo:lat>\n" +
                "<geo:long>-6.353</geo:long>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>UK Earthquake alert : M  2.4 :BYFORD,HEREFORD, Thu, 14 Mar 2019 14:30:59</title>\n" +
                "<description>Origin date/time: Thu, 14 Mar 2019 14:30:59 ; Location: BYFORD,HEREFORD ; Lat/long: 52.085,-2.898 ; Depth: 23 km ; Magnitude:  2.4</description>\n" +
                "<link>http://earthquakes.bgs.ac.uk/earthquakes/recent_events/20190314143059.html</link>\n" +
                "<pubDate>Thu, 14 Mar 2019 14:30:59</pubDate>\n" +
                "<category>EQUK</category>\n" +
                "<geo:lat>52.085</geo:lat>\n" +
                "<geo:long>-2.898</geo:long>\n" +
                "</item>\n" +
                "</channel>\n" +
                "</rss>\n";

        @Override
        protected InputStream getInputStream(URLConnection connection) {
            byte[] bytes = FAKE_EARTHQUAKES_XML.getBytes(StandardCharsets.UTF_8);
            return new ByteArrayInputStream(bytes);
        }
    }
}