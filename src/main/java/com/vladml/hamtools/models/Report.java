package com.vladml.hamtools.models;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Report {

    private LocalDate dateBegin;

    private LocalDate dateEnd;

    @SerializedName("callsign")
    private String callsign;

    @SerializedName("locator")
    private String locator;

    private String band;

    private String email;

    private String operatorName;

    private String operatorCallsign;

    public void setCallsign(String callsign) {
        this.callsign = (callsign.contains("\\")) ? callsign.replace("\\", "/") : callsign;
    }

    @Builder.Default
    private List<QsoRecord> qsoRecords = new ArrayList<>();

}
