package com.vladml.hamtools.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        map.put("^G[0-9].*$", 223);
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


}
