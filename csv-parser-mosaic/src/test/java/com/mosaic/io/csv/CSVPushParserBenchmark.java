package com.mosaic.io.csv;

import com.mosaic.io.Characters;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 *
 */
@Ignore
public class CSVPushParserBenchmark {

    CSVPushParserDelegateNoOp delegate = new CSVPushParserDelegateNoOp();


    @Test
    public void givenEmptyString_expectStartMatchingDelegateCall() {
        CSVPushParser parser = new CSVPushParser( delegate );

        Characters csv = Characters.wrapString("name, symbol, stock exchange, ask, ask size, bid, bid size, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
                "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
                "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
                "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n");
Characters csv1 = Characters.wrapString("name, symbol, stock exchange, ask, ask size, bid, bid size, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
                "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
                "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
                "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n");
Characters csv2 = Characters.wrapString("name, symbol, stock exchange, ask, ask size, bid, bid size, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
                "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
                "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
                "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n");
Characters csv3 = Characters.wrapString("name, symbol, stock exchange, ask, ask size, bid, bid size, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
                "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
                "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
                "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n");

        long startMillis = System.currentTimeMillis();
        run(parser, csv);   // 17us
        run(parser, csv);   //  4us
        run(parser, csv);
        run(parser, csv);
        run(parser, csv);
        run(parser, csv);
        run(parser, csv);
        run(parser, csv);
        run(parser, csv);
        run(parser, csv);
        run(parser, csv1);
        run(parser, csv2);
        run(parser, csv3);   // 1.5us

        long durationMillis = System.currentTimeMillis() - startMillis;

        System.out.println("total duration including gc's = " + durationMillis + "ms");   // 1.8s
    }

    // runs in 6.5-7us
    private int run(CSVPushParser parser, Characters csv) {
        System.gc();

        delegate.count = 0;

        long startNanos = System.nanoTime();
        for ( int i=0; i<10000; i++ ) {
            parser.appendCharacters( csv );
        }

        long durationNanos = System.nanoTime() - startNanos;
        long perRowNanos   = durationNanos/delegate.count;

        System.out.println( "per row " + perRowNanos + "ns "+delegate.count );
//        System.out.println("delegate.columnCount = " + delegate.count);

        return delegate.count;
    }


    private class CSVPushParserDelegateNoOp implements CSVPushParserDelegate {
        public int count;
        public int columnCount;

        public void parsingStarted() {
        }

        public void headerRead( int lineNumber, List<String> headers ) {
            count++;
            columnCount = headers.size();
        }

        public void rowRead( int lineNumber, List<String> columns ) {
            count++;
        }

        public void parsingEnded() {
        }
    }

}
