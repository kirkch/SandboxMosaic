package com.mosaic.parsers.csv;

import com.mosaic.hammer.junit.Benchmark;
import com.mosaic.hammer.junit.Hammer;
import com.mosaic.io.ByteBufferUtils;
import com.mosaic.parsers.MatchResult;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

import static com.mosaic.io.ByteBufferUtils.UTF8;

/**
 *
 */
@RunWith(Hammer.class)
public class CSVColumnValueMatcherBenchmark {

    private CSVColumnValueMatcher matcher = new CSVColumnValueMatcher(',');
    private String csv = "name, symbol, stock exchange, ask, ask size, bid, bid size, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
            "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
            "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
            "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n";

    private CharBuffer buf = CharBuffer.wrap(csv);
//    private ByteBuffer buf;
    private MatchResult matchResult = new MatchResult();



    @Benchmark( durationResultMultiplier=0.25 )
    public void benchmark() {
        buf.position(0);

        while ( doMatch() >= 0 ) {
            buf.position(buf.position()+1);
        }
    }

    private int doMatch() {
        int r = matcher.match(buf, matchResult, false);
//        System.out.println("r = " + r);
        return r;
    }

}
