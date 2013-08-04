package com.mosaic.parsers.pull;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
//@Ignore
public class TokenizerBenchmark {

    CSVPushParserDelegateNoOp delegate = new CSVPushParserDelegateNoOp();


    @Test
    public void givenEmptyString_expectStartMatchingDelegateCall() throws IOException {
        String csv = "name, symbol, stock exchange, ask, ask size, bid, bid size, open, days low, days high, previous close, last trade time, last trade date, market capitalization, p/e ratio, holdings value, volume, divident yield, average daily volume, divident per share, earnings per share, divdent pay date, notes\n" +
                "\"ANGLO AMERICAN\",\"AAL.L\",\"London\",1672.50,1,513,1671.9999,599,1645.9999,1644.50,1678.0001,1650.00,\"6:42am\",\"4/4/2013\",21.318B,N/A,-,1152415,38.11,3031098,628.87,-1.191,\"N/A\",\"-\"\n" +
                "\"ASSOCIAT BRIT FOO\",\"ABF.L\",\"London\",1920.9999,899,1920.0001,1,569,1931.00,1898.00,1931.00,1920.9999,\"6:44am\",\"4/4/2013\",15.155B,2732.57,-,292145,N/A,777768,0.00,0.703,\"N/A\",\"-\"\n" +
                "\"AGGREKO\",\"AGK.L\",\"London\",1787.0001,1,755,1786.00,463,1800.00,1783.00,1800.00,1787.9999,\"6:44am\",\"4/4/2013\",4.754B,1720.89,-,179757,N/A,899662,0.00,1.039,\"N/A\",\"-\"\n";

        Tokenizer t = new Tokenizer( new StringReader(csv) );


        long sum=0;
        long startMillis = System.currentTimeMillis();
        sum += run( csv );   // 17us
        sum += run( csv );   //  4us
        sum += run( csv );
        sum += run( csv );
        sum += run( csv );   // 1.5us

        long durationMillis = System.currentTimeMillis() - startMillis;

        System.out.println("total duration including gc's = " + durationMillis + "ms");   // 1.8s
        System.out.println("sum = " + sum);
    }

    private long run(String csv) throws IOException {
        System.gc();

        long sum = 0;
        long startNanos = System.nanoTime();
        for ( int i=0; i<10000; i++ ) {
//            parseCSV( csv );
            sum += sumString( csv );
        }

        long durationNanos = System.nanoTime() - startNanos;
        long perRowNanos   = durationNanos/delegate.count;

        System.out.println( "per row " + perRowNanos + "ns" );
        return sum;
    }

    private Pattern column = Pattern.compile("[^,]+");
    private Pattern comma = Pattern.compile(",");
    private Pattern EOL   = Pattern.compile("[ \t]*$");

    private long sumString(String csv) throws IOException {
        long n = 0;

        byte[] bytes = csv.getBytes();

        int length = csv.length();
        for ( int i=0; i< length; i++ ) {
            n += csv.charAt(i);
        }

        delegate.count += 4;

        return n;
    }

    private void parseCSV(String csv) throws IOException {
        Tokenizer t = new Tokenizer( new StringReader(csv) );


        if ( t.walkRegexp(column) ) {
            String value = t.consume();

//            System.out.println("value = " + value);

            while ( t.walkConstant(",") ) {
                t.skip();

                if ( t.walkRegexp(column) ) {
                    value = t.consume();

//                    System.out.println("value = " + value);
                }
            }

        }

        delegate.count += 4;
    }


    private class CSVPushParserDelegateNoOp {
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
