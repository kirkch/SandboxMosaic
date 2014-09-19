package com.mosaic.lang.math;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 *
 */
public class RollingAverageDoubleTest {

    @Test
    public void cannotHaveNegativeSize() {
        try {
            new RollingAverageDouble(-1);
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'maxSize' (-1) must be > 0", e.getMessage() );
        }
    }

    @Test
    public void cannotHaveZeroSize() {
        try {
            new RollingAverageDouble(0);
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'maxSize' (0) must be > 0", e.getMessage() );
        }
    }

    @Test
    public void appendValuesPassRolloverOf3() {
        RollingAverageDouble a = new RollingAverageDouble(3);

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
        RollingAverageDouble a = new RollingAverageDouble(2);

        assertEquals( 0.0, a.getAverage(), 0.0001 );

        a.append(4);
        assertEquals( 4.0, a.getAverage(), 0.0001 );

        a.append(2);
        assertEquals( (4.0+2.0)/2, a.getAverage(), 0.0001 );

        a.append(3);
        assertEquals( (2.0+3.0)/2, a.getAverage(), 0.0001 );

        a.append(1);
        assertEquals( (3.0+1.0)/2, a.getAverage(), 0.0001 );

        a.append(-1);
        assertEquals( (1.0-1.0)/2, a.getAverage(), 0.0001 );
    }

//    @Test
//    public void performanceTest() {   // 280ms; dropped to 14-19ms after refactor
//        RollingAverage avg = new RollingAverage(100);
//
//        StopWatch sw = new StopWatch();
//        sw.start();
//
//        double c = 0;
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
