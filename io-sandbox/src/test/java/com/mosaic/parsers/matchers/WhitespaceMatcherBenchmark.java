package com.mosaic.parsers.matchers;

import com.mosaic.junitpro.Benchmark;
import com.mosaic.junitpro.JUnitPro;
import com.mosaic.parsers.Matcher;
import org.junit.runner.RunWith;

import java.nio.CharBuffer;

/**
 *
 */
@RunWith(JUnitPro.class)
public class WhitespaceMatcherBenchmark {

    private Matcher spaceMatcher   = WhitespaceMatcher.tabOrSpaceMatcher();
    private Matcher newLineMatcher = WhitespaceMatcher.whitespaceMatcher();

    private CharBuffer buf1 = CharBuffer.wrap("    \t \t \n\r   ");
    private CharBuffer buf2 = CharBuffer.wrap("Hello");

/*
81.55ns
21.98ns
14.89ns
14.79ns
14.80ns
14.82ns
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
79.19ns
16.46ns
15.13ns
18.04ns
14.48ns
14.46ns
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
