package com.vladml.hamtools.models;

import lombok.Builder;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
public class QsoRecord {

    private int lineNumber;

    private Date dateTime;

    private String callsign;

    private int mode;

    private String rstSent;

    private String numSent;

    private String rstRcvd;

    private String numRcvd;

    private String locator;

    public void setCallsign(String callsign) {
        this.callsign = (callsign.contains("\\")) ? callsign.replace("\\", "/") : callsign;
    }


    public static QsoRecord create(String[] qsoLine, int lineNumber) throws ParseException {
        String callsign = qsoLine[2];
        callsign = (callsign.contains("\\")) ? callsign.replace("\\", "/") : callsign;
        Date qsoDateTime = new SimpleDateFormat("yyMMddHHmm").parse(qsoLine[0]+qsoLine[1]);
        return QsoRecord.builder()
                .dateTime(qsoDateTime)
                .callsign(callsign)
                .mode(Integer.parseInt(qsoLine[3]))
                .rstSent(qsoLine[4])
                .numSent(qsoLine[5])
                .rstRcvd(qsoLine[6])
                .numRcvd(qsoLine[7])
                .locator(qsoLine[9])
                .lineNumber(lineNumber)
                .build();
    }

}
