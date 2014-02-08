package com.mosaic.lang.math;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 *
 */
public class MovingAverageAggregatorTest {
//    private static final Logger LOG = LoggerFactory.getLogger( MovingAverageAggregatorTest.class );

    @Test
    public void cannotHaveNegativeSize() {
        try {
            new MovingAverageAggregator<Money>(Money.ZERO, -1);
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'maxSize' (-1) must be > 0", e.getMessage() );
        }
    }

    @Test
    public void cannotHaveZeroSize() {
        try {
            new MovingAverageAggregator<Money>(Money.ZERO, 0);
            fail( "expected IAE" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'maxSize' (0) must be > 0", e.getMessage() );
        }
    }

    @Test
    public void appendValuesPassRolloverOf3() {
        MovingAverageAggregator<Money> a = new MovingAverageAggregator<Money>(Money.ZERO, 3);

        assertEquals( Money.ZERO, a.getResult() );

        a.append( new Money(4.0) );
        assertEquals( new Money(4.0), a.getResult() );

        a.append( new Money(2.0) );
        assertEquals( new Money((4.0+2.0)/2), a.getResult() );

        a.append( new Money(3.0) );
        assertEquals( new Money((4.0+2.0+3.0)/3), a.getResult() );

        a.append( new Money(1.0) );
        assertEquals( new Money((2.0+3.0+1.0)/3), a.getResult() );

        a.append( new Money(-1.0) );
        assertEquals( new Money((3.0+1.0-1.0)/3), a.getResult() );
    }

    @Test
    public void appendValuesPassRolloverOf2() {
        MovingAverageAggregator<Money> a = new MovingAverageAggregator<Money>(Money.ZERO, 2);

        assertEquals( Money.ZERO, a.getResult() );

        a.append( new Money(4.0) );
        assertEquals( new Money(4.0), a.getResult() );

        a.append( new Money(2.0) );
        assertEquals( new Money((4.0+2.0)/2), a.getResult() );

        a.append( new Money(3.0) );
        assertEquals( new Money((2.0+3.0)/2), a.getResult() );

        a.append( new Money(1.0) );
        assertEquals( new Money((3.0+1.0)/2), a.getResult() );

        a.append( new Money(-1.0) );
        assertEquals( new Money((1.0-1.0)/2), a.getResult() );
    }

//    @Test
//    public void performanceTest() { // 170ms
//        MovingAverageAggregator<Money> avg = new MovingAverageAggregator<Money>( Money.ZERO, 100 );
//
//        StopWatch sw = new StopWatch();
//        sw.start();
//
//        Money c = Money.ZERO;
//        for ( int i=0; i<100000; i++ ) {
//            avg.append( new Money((double) i) );
//
//            c = c.add( avg.getResult() );
//        }
//
//        sw.stop();
//        LOG.info( "Duration="+sw.getTime()+"ms" );
//        LOG.info( "c = " + c );
//    }
}
