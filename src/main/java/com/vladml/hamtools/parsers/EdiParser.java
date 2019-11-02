package com.vladml.hamtools.parsers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vladml.hamtools.ReportConstants;
import com.vladml.hamtools.adapters.GsonLocalDateAdapter;
import com.vladml.hamtools.models.QsoRecord;
import com.vladml.hamtools.models.Report;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Data
@Builder
@Slf4j
public class EdiParser implements IParser {


    private static final String LOCATOR_REGEX = "^[A-R]{2}[0-9]{2}[A-X]{2}$";
    private static final String DATE_REGEX = "^[0-9]{6}$";
    private static final String TIME_REGEX = "^[0-9]{4}$";
    private static final String CALLSIGN_REGEX = "([a-zA-Z0-9][a-zA-Z0-9/].*)";
    private static final String MODE_REGEX = "^[0-9]$";
    private static final String RST_REGEX = "^[0-9a-zA-Z]{2,3}";
    private static final String NUM_REGEX = "^[0-9]{1,4}$";
    private static final String LINE_NUM = "{{ Line }} %s: ";


    @Builder.Default
    private Report report = Report.builder().build();

    @Builder.Default
    private Map<String, String> header = new HashMap<>();

    @Builder.Default
    private List<QsoRecord> qsoRecords = new ArrayList<>();

    @Builder.Default
    private List<String> errors = new ArrayList<>();

    private String filename;

    private static String removeWitespace(String str) {
        return str.replaceAll("\\s", "");
    }

    private boolean bulkLoad;

    private String getFileEncoding() {
        java.io.File file = new java.io.File(this.filename);
        String encoding = null;
        try {
            encoding = UniversalDetector.detectCharset(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "UTF-8";
    }

    private void extractDate(String date) {
          String[] dates = removeWitespace(date).split(";");
          if (dates.length == 2) {
              try {
                  report.setDateBegin(LocalDate.parse(dates[0], DateTimeFormatter.ofPattern("yyyyMMdd")));
                  report.setDateEnd(LocalDate.parse(dates[1], DateTimeFormatter.ofPattern("yyyyMMdd")));
              } catch (DateTimeParseException e) {
                  errors.add(ReportConstants.EDI_INVALID_HDR_DATE + date);
              }
          } else
              if (!bulkLoad)
                  errors.add(ReportConstants.EDI_INVALID_HDR_DATE + date);
    }

    private String normalizeBand(String band) {
        String normalizedBand = "";
        String freq = removeWitespace(band)
                .toUpperCase()
                .replace("MHZ","")
                .replace("GHZ","")
                .replace(".",",");
        if (freq.equals("144"))
            freq = "145";
        else if (freq.equals("432"))
            freq = "435";
        else if (freq.equals("430"))
            freq = "435";
        else if (freq.equals("433"))
            freq = "435";

        if (band.toUpperCase().contains("MHZ"))
            normalizedBand = freq + " MHZ";
        else if (band.toUpperCase().contains("GHZ"))
            normalizedBand = freq + " GHZ";
        try {
            if (Double.parseDouble(freq.replace(',','.')) > 100)
                normalizedBand = freq + " MHZ";
        } catch (Exception e) {
            errors.add(ReportConstants.EDI_INVALID_HDR_BAND+band);
        }
        return normalizedBand;
    }

    private static boolean isBlank(String value) {
        return (value == null || value.trim().isEmpty());
    }

    private void loadHeaderLine(String headerLine) {
        String[] tokens = headerLine.split("=");
        if (tokens.length > 1) {
            header.put(tokens[0].replaceAll("\\s+", "").toUpperCase(),
                    tokens[1].trim());
        }
    }

    private String getModeFromReport(String[] qsoLine) {
        if (isBlank(qsoLine[4]) || isBlank(qsoLine[4]))
            return "";
        else if  (qsoLine[4].length() == 2 && qsoLine[4].length() == 2) //SSB
            return "1";
        else if  (qsoLine[4].length() == 2 && qsoLine[4].length() == 3) //SSB CW
            return "3";
        else if  (qsoLine[4].length() == 3 && qsoLine[4].length() == 3) //CW
            return "2";
        else if  (qsoLine[4].length() == 3 && qsoLine[4].length() == 2) //CW SSB
            return "4";
        return "";
    }


    private String[] normalizeQsoLine(String[] qsoLine) {
        try {
            qsoLine[5] = String.format("%0" + 3 + "d", Integer.parseInt(qsoLine[5]));
            qsoLine[7] = String.format("%0" + 3 + "d", Integer.parseInt(qsoLine[7]));
            if (isBlank(qsoLine[3]))
                 qsoLine[3] = getModeFromReport(qsoLine);
        } catch(Exception e) {

        }

        return qsoLine;
    }

    private boolean validateQsoLine(String[] qsoLine, String lineLumber) {
        int errCount = errors.size();

        if (qsoLine.length < 10) {
            errors.add(String.format(LINE_NUM, lineLumber) + ReportConstants.EDI_INVALID_QSO_FIELD_COUNT);
            return false;
        }

        if (isBlank(qsoLine[0]) || !qsoLine[0].matches(DATE_REGEX))
           errors.add(String.format(LINE_NUM,lineLumber) + ReportConstants.EDI_INVALID_QSO_DATE + Optional.of(qsoLine[0]).orElse(""));
        try {
            new SimpleDateFormat("yyMMDD").parse(qsoLine[0]);
        } catch (ParseException e) {
            errors.add(String.format(LINE_NUM,lineLumber) + ReportConstants.EDI_INVALID_QSO_DATE + Optional.of(qsoLine[0]).orElse(""));
        }


        if (isBlank(qsoLine[1]) || !qsoLine[1].matches(TIME_REGEX))
            errors.add(String.format(LINE_NUM,lineLumber) + ReportConstants.EDI_INVALID_QSO_TIME + Optional.of(qsoLine[1]).orElse(""));
        try {
            new SimpleDateFormat("HHmm").parse(qsoLine[1]);
        } catch (ParseException e) {
            errors.add(String.format(LINE_NUM,lineLumber) + ReportConstants.EDI_INVALID_QSO_TIME + Optional.of(qsoLine[1]).orElse(""));
        }

        if (!bulkLoad) {
            if (isBlank(qsoLine[2]) || !qsoLine[2].matches(CALLSIGN_REGEX))
                errors.add(String.format(LINE_NUM, lineLumber) + ReportConstants.EDI_INVALID_QSO_CALLSIGN + Optional.of(qsoLine[2]).orElse(""));
            if (isBlank(qsoLine[3]) || !qsoLine[3].matches(MODE_REGEX))
                errors.add(String.format(LINE_NUM, lineLumber) + ReportConstants.EDI_INVALID_QSO_MODE + Optional.of(qsoLine[3]).orElse(""));
            if (isBlank(qsoLine[4]) || !qsoLine[4].matches(RST_REGEX))
                errors.add(String.format(LINE_NUM, lineLumber) + ReportConstants.EDI_INVALID_QSO_SNT_RST + Optional.of(qsoLine[4]).orElse(""));
            if (isBlank(qsoLine[5]) || !qsoLine[5].matches(NUM_REGEX))
                errors.add(String.format(LINE_NUM, lineLumber) + ReportConstants.EDI_INVALID_QSO_SNT_NUM + Optional.of(qsoLine[5]).orElse(""));
            if (isBlank(qsoLine[6]) || !qsoLine[6].matches(RST_REGEX))
                errors.add(String.format(LINE_NUM, lineLumber) + ReportConstants.EDI_INVALID_QSO_RVD_RST + Optional.of(qsoLine[6]).orElse(""));
            if (isBlank(qsoLine[7]) || !qsoLine[7].matches(NUM_REGEX))
                errors.add(String.format(LINE_NUM, lineLumber) + ReportConstants.EDI_INVALID_QSO_RVD_NUM + Optional.of(qsoLine[7]).orElse(""));
            if (isBlank(qsoLine[9]) || !qsoLine[9].matches(LOCATOR_REGEX))
                errors.add(String.format(LINE_NUM, lineLumber) + ReportConstants.EDI_INVALID_QSO_RVD_LOCATOR + Optional.of(qsoLine[9]).orElse(""));
        }

        return errors.size() == errCount;
    }


    private void loadQsoLine(String line, String lineNumber) {
        try {
            String[] normalizedQsoLine = normalizeQsoLine(line.replaceAll("\\s+","").toUpperCase().split(";"));
            if (validateQsoLine(normalizedQsoLine, lineNumber)) {
                qsoRecords.add(QsoRecord.create(normalizedQsoLine));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            errors.add(ReportConstants.EDI_INVALID_QSO_UNKNOWN + line);
        }
    }



    private void processHeader() {
        String date = header.get(ReportConstants.EDI_HDR_DATE);
        if (date != null)
            extractDate(date);
        else
            errors.add(ReportConstants.EDI_INVALID_HDR_DATE);

        report.setCallsign(header.get(ReportConstants.EDI_HDR_CALL).toUpperCase());
        if (isBlank(report.getCallsign()))
            errors.add(ReportConstants.EDI_INVALID_HDR_CALLSIGN);

        report.setLocator(header.get(ReportConstants.EDI_HDR_LOCATOR).toUpperCase());
        if (isBlank(report.getLocator()) || !report.getLocator().matches(LOCATOR_REGEX))
            errors.add(ReportConstants.EDI_INVALID_HDR_LOCATOR);

        report.setBand(normalizeBand(header.get(ReportConstants.EDI_HDR_BAND)));
        if (isBlank(report.getBand()))
            errors.add(ReportConstants.EDI_INVALID_HDR_BAND);

        report.setEmail(header.get(ReportConstants.EDI_HDR_EMAIL));
    }

    public void load() {

        boolean startHeader = false;
        boolean startQsoRecords = false;
        List<String> fileContent = new ArrayList<>();

        int lineNum = 0;

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(this.filename), getFileEncoding()))) {

            String str;
            while ((str = in.readLine()) != null) {
                fileContent.add(str);
            }

            for (String line : fileContent) {
                lineNum++;
                if (line.toUpperCase().contains("[REG1TEST")) {
                    startHeader = true;
                    continue;
                }

                if (line.toUpperCase().contains("[QSORECORDS")) {
                    processHeader();
                    startHeader = false;
                    startQsoRecords = true;
                    continue;
                }

                if (startQsoRecords && (isBlank(line) || line.toUpperCase().contains("[END"))) {
                    startQsoRecords = false;
                    break;
                }

                if (startHeader)
                    loadHeaderLine(line);

                if (startQsoRecords)
                    loadQsoLine(line, String.valueOf(lineNum));
            }
            if (qsoRecords.size() > 0)
                report.setQsoRecords(qsoRecords);
            else
                errors.add(ReportConstants.EDI_INVALID_QSO_COUNT);
        } catch (IOException e) {
            errors.add(e.getStackTrace().toString());
        }

    }

    public void saveToJson(String filename) throws IOException {
        try (Writer writer = new FileWriter(filename)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
                    .create();
            gson.toJson(report, writer);
        }
    }

    public void saveToJsonForMap(String filename) throws IOException {
        String content = "{\"locator\":\""+report.getLocator()+"\", " +
                         "\"callsign\":\""+ report.getCallsign()+"\", "+
                         "\"band\":\""+ report.getBand()+"\", \"qso\":[";
        for (QsoRecord qsoRecord: report.getQsoRecords()) {
            content += "[\""+new SimpleDateFormat("yyMMdd").format(qsoRecord.getDateTime())+"\", \""+new SimpleDateFormat("HHmm").format(qsoRecord.getDateTime())+
                    "\", \""+ qsoRecord.getCallsign() +
                    "\", \""+ qsoRecord.getMode() +
                    "\", \""+ qsoRecord.getRstSent() +
                    "\", \""+ qsoRecord.getNumSent() +
                    "\", \""+ qsoRecord.getRstRcvd() +
                    "\", \""+ qsoRecord.getNumRcvd() +
                    "\", \"" +
                    "\", \""+ qsoRecord.getLocator() +
                    "\"],";
        }
        content = content.substring(0, content.length()-1)+"]}";
        Files.write(Paths.get(filename), content.getBytes(),  StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      }
}
