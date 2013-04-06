package com.mosaic.io.csv;

import com.mosaic.io.CharacterStream;
import com.mosaic.parsers.push.Matcher;
import org.junit.Test;

/**
 *
 */
public class CSVPushParserTest {

    @Test
    public void f() {
        Matcher.setDebugEnabled( true );

        CharacterStream stream = new CharacterStream( "a,b,c,d" ).appendEOS();
        CSVPushParser   parser = new CSVPushParser();


        parser.appendCharacters( stream );
    }

}
