package com.vladml.hamtools.models;

import lombok.Builder;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
public class QsoRecord {


    private Date dateTime;

    private String callsign;

    private int mode;

    private String rstSent;

    private String numSent;

    private String rstRcvd;

    private String numRcvd;

    private String locator;


    public static QsoRecord create(String[] qsoLine) throws ParseException {
        Date qsoDateTime = new SimpleDateFormat("yyMMddHHmm").parse(qsoLine[0]+qsoLine[1]);
        return QsoRecord.builder()
                .dateTime(qsoDateTime)
                .callsign(qsoLine[2])
                .mode(Integer.parseInt(qsoLine[3]))
                .rstSent(qsoLine[4])
                .numSent(qsoLine[5])
                .rstRcvd(qsoLine[6])
                .numRcvd(qsoLine[7])
                .locator(qsoLine[9])
                .build();
    }

}
