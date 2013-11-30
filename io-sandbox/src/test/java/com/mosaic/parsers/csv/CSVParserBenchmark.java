package com.mosaic.parsers.csv;

import com.mosaic.parsers.PushParser;
import com.softwaremosaic.junit.JUnitMosaicRunner;
import com.softwaremosaic.junit.annotations.Benchmark;
import org.junit.runner.RunWith;

import java.nio.CharBuffer;
import java.util.List;

/**
 *
 */
@RunWith(JUnitMosaicRunner.class)
public class CSVParserBenchmark {

    private CSVParserCallbackFake delegate = new CSVParserCallbackFake();
    private PushParser            parser   = new CSVPushParser( delegate );

    CharBuffer inputBuffer = CharBuffer.wrap("name, symbol, stock exchange, ask, ask size, bid, bid size, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
            "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
            "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
            "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n");


/*
12712.80ns
8140.03ns
9464.38ns
4902.50ns
4344.58ns
4304.85ns

    3919.95ns per call
    3236.53ns per call
    3336.48ns per call
    3181.40ns per call
    3140.58ns per call
    3303.33ns per call
*/

    @Benchmark( value=10000, durationResultMultiplier=0.25, units="row" )
    public void csvBenchmark( int iterationCount ) {
        for ( int i=0; i<iterationCount; i++ ) {
            inputBuffer.position(0);

            parser.push(inputBuffer, true);
        }
    }


    class CSVParserCallbackFake implements CSVParserCallback {

        public void start() {
        }

        public void headers(int line, List<String> headers) {
        }

        public void row(int line, List<String> columns) {
        }

        public void end() {
        }

        public void malformedRow(int line, int column, String message) {
        }
    }
}
