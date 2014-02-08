package com.mosaic.lang.math;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 *
 */
public class RollingAverageIntTest {

    @Test
    public void cannotHaveNegativeSize() {
        try {
            new RollingAverageInt(-1);
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'maxSize' (-1) must be > 0", e.getMessage() );
        }
    }

    @Test
    public void cannotHaveZeroSize() {
        try {
            new RollingAverageInt(0);
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'maxSize' (0) must be > 0", e.getMessage() );
        }
    }

    @Test
    public void appendValuesPassRolloverOf3() {
        RollingAverageInt a = new RollingAverageInt(3);

        assertEquals( 0.0, a.getAverage(), 0.0001 );

        a.append(4);
        assertEquals( 4.0, a.getAverage(), 0.0001 );

        a.append(2);
        assertEquals( (4.0+2.0)/2, a.getAverage(), 0.0001 );

        a.append(3);
        assertEquals( (4.0+2.0+3.0)/3, a.getAverage(), 0.0001 );

        a.append(1);
        assertEquals( (2.0+3.0+1.0)/3, a.getAverage(), 0.0001 );

        a.append(-1);
        assertEquals( (3.0+1.0-1.0)/3, a.getAverage(), 0.0001 );
    }
    
    @Test
    public void appendValuesPassRolloverOf2() {
        RollingAverageInt a = new RollingAverageInt(2);

        assertEquals( 0.0, a.getAverage(), 0.0001 );

        a.append(4);
        assertEquals( 4.0, a.getAverage(), 0.0001 );

        a.append(2);
        assertEquals( (4.0+2.0)/2, a.getAverage(), 0.0001 );

        a.append(3);
        assertEquals( (int) ((2.0+3.0)/2), a.getAverage(), 0.0001 );

        a.append(1);
        assertEquals( (3.0+1.0)/2, a.getAverage(), 0.0001 );

        a.append(-1);
        assertEquals( (1.0-1.0)/2, a.getAverage(), 0.0001 );
    }

//    @Test
//    public void performanceTest() {   // 280ms; dropped to 15ms after refactor
//        RollingAverageInt avg = new RollingAverageInt(100);
//
//        StopWatch sw = new StopWatch();
//        sw.start();
//
//        int c = 0;
//        for ( int i=0; i<100000; i++ ) {
//            avg.append(i);
//
//            c += avg.getAverage();
//        }
//
//        sw.stop();
//        LOG.info( "Duration="+sw.getTime()+"ms" );
//        LOG.info("c = " + c);
//
//        if ( c < 0 ) {
//            System.out.println(".");
//        }
//    }
}
