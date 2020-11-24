package com.vladml.hamtools;

import com.vladml.hamtools.parsers.EdiParser;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ParserTest {
/*
    @Test
    public void ediParserTest() {
        EdiParser parser = EdiParser.builder()
                .filename("d:\\!!\\1A-IK4ZHH-4702.edi")
                .bulkLoad(true)
                .build();

        parser.load();
        System.out.println(parser.getReport().getCallsign());
        assertThat(parser.getErrors().size(), is(0));
        try {
            parser.saveToJsonForMap("D:/!!/output1.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
 */
    @Test
    public void badLocatorTest() {

        Path resourceDirectory = Paths.get("src","test","resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        System.out.println(absolutePath);


        EdiParser parser = EdiParser.builder()
                .filename(absolutePath + "\\edi\\bad_locator.edi")
                .bulkLoad(false)
                .build();
        parser.load();


        List<String> errors =  parser.getErrors();


    }


}
