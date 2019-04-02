package com.vladml.hamtools;

import com.vladml.hamtools.parsers.EdiParser;
import org.junit.Test;

import java.io.IOException;

public class ParserTest {

    @Test
    public void ediParserTest() {
        EdiParser parser = EdiParser.builder()
                .filename("d:\\!!\\2IT IU2AQS Aprile.edi")
                .build();
        parser.load();
        try {
            parser.saveToJsonForMap("D:/!!/output1.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
