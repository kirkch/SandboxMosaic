package com.mosaic.parsers.matchers;


import com.mosaic.junitpro.Benchmark;
import com.mosaic.junitpro.JUnitPro;
import com.mosaic.parsers.Matcher;
import org.junit.runner.RunWith;

import java.nio.CharBuffer;

/**
 *
 */
@RunWith( JUnitPro.class )
public class ConstantMatcherBenchmark {

    private Matcher matcher6 = ConstantMatcher.create("assert");
    private Matcher matcher1 = ConstantMatcher.create("=");


    private CharBuffer buf = CharBuffer.wrap("assert foo == bar;");

    /*
15.63ns
14.13ns
17.78ns
14.10ns
14.06ns
     */
    @Benchmark( durationResultMultiplier = 1.0/3 )
    public void benchmark6() {
        buf.position(0);

        matcher6.match(buf, true);
        matcher6.match(buf, true);

        buf.position(6);
        matcher6.match(buf, true);
    }

/*
131.30ns  // readings when multi char matcher was used
10.20ns
8.08ns
8.95ns
8.04ns
8.01ns

107.92ns  // readings when single char matcher was used
8.43ns
9.36ns
5.38ns
5.39ns
5.38ns
     */
    @Benchmark( durationResultMultiplier = 1.0/4 )
    public void benchmark1() {
        buf.position(0);

        matcher1.match(buf, true);

        buf.position(11);
        matcher1.match(buf, true);
        matcher1.match(buf, true);
        matcher1.match(buf, true);
    }

}
