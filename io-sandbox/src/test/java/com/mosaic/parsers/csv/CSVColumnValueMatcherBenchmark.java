package com.mosaic.parsers.csv;


import com.mosaic.parsers.MatchResult;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.runner.RunWith;

import java.nio.CharBuffer;

/**
 *
 */
//@RunWith(JUnitMosaicRunner.class)
public class CSVColumnValueMatcherBenchmark {

    private CSVColumnValueMatcher matcher = new CSVColumnValueMatcher(',');
    private String csv = "name, symbol, stock exchange, ask, ask sizeInBytes, bid, bid sizeInBytes, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
            "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
            "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
            "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n";

    private CharBuffer buf = CharBuffer.wrap(csv);


/*  timings
1421.61ns
1157.66ns
1061.96ns
1067.24ns
1060.62ns
1085.38ns

1449.67ns
1105.68ns
1151.19ns
1147.22ns
1085.67ns
1091.28ns
 */

//    @Benchmark( durationResultMultiplier=0.25 )
    public void benchmark() {
        buf.position(0);

        while ( doMatch().isMatch() ) {
            buf.position(buf.position() + 1);
        }
    }

    private MatchResult doMatch() {
        return matcher.match(buf, false);
    }

}
