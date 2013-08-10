package com.mosaic.parsers.matchers;

import com.mosaic.hammer.junit.Benchmark;
import com.mosaic.hammer.junit.Hammer;
import com.mosaic.parsers.Matcher;
import org.junit.runner.RunWith;

import java.nio.CharBuffer;

/**
 *
 */
@RunWith(Hammer.class)
public class WhitespaceMatcherBenchmark {

    private Matcher spaceMatcher = WhitespaceMatcher.tabOrSpaceMatcher();

    private CharBuffer buf1 = CharBuffer.wrap("    \t \t  ");
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
    public void benchmark( int count ) {
        for ( int i=0; i<count; i++ ) {
            buf1.position(0);
            buf2.position(0);

            spaceMatcher.match(buf1,true);
            spaceMatcher.match(buf2,true);
        }
    }

}
