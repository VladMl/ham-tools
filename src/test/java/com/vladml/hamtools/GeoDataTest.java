package com.vladml.hamtools;

import com.vladml.hamtools.util.Coordinates;
import com.vladml.hamtools.util.GeoData;
import org.junit.Test;

import static com.vladml.hamtools.util.GeoData.distance;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class GeoDataTest {

    @Test
    public void locator2CoordinateTest() {
        Coordinates c = GeoData.locator2Coordinate("KN59DH");
        assertThat(c.latitude, is(49.3125));
        assertThat(c.longitude, is(30.291666666666657));
    }

    @Test
    public void distanceTest() {
        try {
            assertThat(distance("KO50FM", "KN59DH"), is(135));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    }
