package com.vladml.hamtools;

import com.vladml.hamtools.parsers.EdiParser;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


import java.io.IOException;

public class ParserTest {

    @Test
    public void ediParserTest() {
        EdiParser parser = EdiParser.builder()
                .filename("d:\\!!\\1A-IK4ZHH-4702.edi")
                .bulkLoad(true)
                .build();

        parser.load();
        assertThat(parser.getErrors().size(), is(0));
        try {
            parser.saveToJsonForMap("D:/!!/output1.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
