package com.vladml.hamtools;

import com.vladml.hamtools.parsers.EdiParser;
import org.junit.Test;

import java.io.IOException;

public class ParserTest {

    @Test
    public void ediParserTest() {
        EdiParser parser = EdiParser.builder()
                .filename("d:\\!!\\ut9na.145_22032.edi")
                .build();
        parser.load();
        try {
            parser.saveToJsonForMap("D:\\!!\\output.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
