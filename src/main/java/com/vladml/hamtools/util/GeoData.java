package com.vladml.hamtools.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Math.round;

public class GeoData {

    Map<String, Integer> prefixes = createPrefixMap();

    private static Map<String, Integer> createPrefixMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("^U[R-Z].*|E[M-O].*$", 288);
        map.put("^U[A-I].*|R.*$", 54);
        map.put("^E[U-W].*$", 27);
        map.put("^E[A-B].*$", 281);
        map.put("^E7.*$", 501);
        map.put("^U[N-P].*$", 130);
        map.put("^OE.*$", 206);
        map.put("^LZ.*$", 212);
        map.put("^S[N-Q].*$", 269);
        map.put("^Y[T-U].*$", 296);
        map.put("^ES.*$", 52);
        map.put("^YL.*$", 145);
        map.put("^LY.*$", 146);
        map.put("^L[A-N].*$", 266);
        map.put("^S[I-M].*|SF.*|7S.*$", 284);
        map.put("^O[F-I].*$", 224);
        map.put("^ER.*$", 179);
        map.put("^OZ.*|OV.*|5P.*$", 221);
        map.put("^D[A-R].*$", 230);
        map.put("^O[K-L].*$", 503);
        map.put("^OM.*$", 504);
        map.put("^O[N-T].*$", 209);
        map.put("^HA.*|HG.*$", 239);
        map.put("^Z3.*|4O.*$", 502);
        map.put("^F.*|TM.*$", 227);
        map.put("^I.*$", 248);
        map.put("^YO.*|Y[P-R].*$", 275);
        map.put("^9A.*$", 497);
        map.put("^S5[0-9].*$", 499);
        map.put("^T7.*$", 278);
        map.put("^SV.*|SX.*|SZ.*$", 236);
        map.put("^HB.*$", 287);
        map.put("^P[A-H].*$", 263);
        map.put("^G[0-9].*|G.*$", 223);
        map.put("^5B.*$", 215);
        map.put("^9H.*$", 257);
        map.put("^4Z.*|4X.*$", 336);
        map.put("^OD.*|OD.*$", 354);
        map.put("^A4.*$", 370);
        map.put("^TA.*|YM.*|TC.*$", 390);

        return map;
    }


    public Integer getCountryId(String callsign) {
        List<Integer> countryList = prefixes.entrySet()
                .stream()
                .filter((i) ->
                        callsign.matches(i.getKey())
                )
                .limit(1)
                .map((v) -> v.getValue())
                .collect(Collectors.toList());
        return (countryList.size() == 0) ? -1 : countryList.get(0);
    }


    private static int ord(String s) {
        return s.length() > 0 ? (s.getBytes(StandardCharsets.UTF_8)[0] & 0xff) : 0;
    }

    private static int ord(char c) {
        return c < 0x80 ? c : ord(Character.toString(c));
    }

    public static Coordinates locator2Coordinate(String locator) {
        String loc = locator.toUpperCase();
        double lat = (ord(loc.charAt(1)) - 65) * 10 + (ord(loc.charAt(3)) - 48) + (ord(loc.charAt(5)) - 65 + 0.5) / 24 - 90;
        double lon = (ord(loc.charAt(0)) - 65) * 20 + (ord(loc.charAt(2)) - 48) * 2 + (ord(loc.charAt(4)) - 65 + 0.5) / 12 - 180;
        return new Coordinates(lat,lon);
    }

    public static Integer distance(String locator1, String locator2) throws Exception {
        Pattern r = Pattern.compile("^[A-R]{2}[0-9]{2}[A-X]{2}$");

        if ( ! r.matcher(locator1).find())
            throw new Exception("Wrong locator " + locator1);

        if ( ! r.matcher(locator2).find())
            throw new Exception("Wrong locator " + locator2);

        Coordinates point1 = locator2Coordinate(locator1);
        Coordinates point2 = locator2Coordinate(locator2);

        if ((point1.latitude == point2.latitude) &&
           (point1.longitude == point2.longitude))
          return 0;

        double distance = Math.sin(Math.toRadians(point1.latitude)) * Math.sin(Math.toRadians(point2.latitude)) +
                Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude)) * Math.cos(Math.toRadians(point1.longitude - point2.longitude));

        return Math.toIntExact(round((Math.toDegrees(Math.acos(distance)) * 60 * 1.1515) * 1.609344));
    }


}
