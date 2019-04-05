package com.vladml.hamtools.parsers;

import com.vladml.hamtools.models.Report;

import java.io.IOException;

public interface IParser {


    public void load();

    public void saveToJson(String filename) throws IOException;

    public void saveToJsonForMap(String filename) throws IOException;

    public Report getReport();

}
