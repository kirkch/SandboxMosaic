package com.mosaic.parsers.matchers;

import com.mosaic.parsers.Matcher;
import com.softwaremosaic.junit.annotations.Benchmark;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import org.junit.runner.RunWith;

import java.nio.CharBuffer;

/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class WhitespaceMatcherBenchmark {

    private Matcher spaceMatcher   = WhitespaceMatcher.tabOrSpaceMatcher();
    private Matcher newLineMatcher = WhitespaceMatcher.whitespaceMatcher();

    private CharBuffer buf1 = CharBuffer.wrap("    \t \t \n\r   ");
    private CharBuffer buf2 = CharBuffer.wrap("Hello");

/*
    16.09ns per call
    16.85ns per call
    14.42ns per call
    18.75ns per call
    14.65ns per call
    14.41ns per call
*/

    @Benchmark( durationResultMultiplier = 1.0/2 )
    public void tabOrSpaceMatcherBenchmark( int count ) {
        for ( int i=0; i<count; i++ ) {
            buf1.position(0);
            buf2.position(0);

            spaceMatcher.match(buf1,true);
            spaceMatcher.match(buf2,true);
        }
    }

/*
    14.56ns per call
    14.98ns per call
    17.99ns per call
    13.60ns per call
    13.13ns per call
    12.88ns per call
*/
    @Benchmark( durationResultMultiplier = 1.0/2 )
    public void newLineMatcherBenchmark( int count ) {
        for ( int i=0; i<count; i++ ) {
            buf1.position(0);
            buf2.position(0);

            newLineMatcher.match(buf1,true);
            newLineMatcher.match(buf2,true);
        }
    }

}
