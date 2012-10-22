package com.mosaic.lang.math;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class AverageAggregatorTest {
    @Test
    public void appendNoValues() throws Exception {
        AverageAggregator<Quantity> aggregator = new AverageAggregator<Quantity>(Quantity.ZERO);

        assertEquals( "0", aggregator.getResult().toString() );
    }

    @Test
    public void appendOneValue() throws Exception {
        AverageAggregator<Quantity> aggregator = new AverageAggregator<Quantity>(Quantity.ZERO);

        aggregator.append( new Quantity(10) );

        assertEquals( "10", aggregator.getResult().toString() );
    }

    @Test
    public void appendNullValue() throws Exception {
        AverageAggregator<Quantity> aggregator = new AverageAggregator<Quantity>(Quantity.ZERO);

        aggregator.append( null );

        assertEquals( "0", aggregator.getResult().toString() );
    }

    @Test
    public void appendTwoValues() throws Exception {
        AverageAggregator<Quantity> aggregator = new AverageAggregator<Quantity>(Quantity.ZERO);

        aggregator.append( new Quantity(10) );
        aggregator.append( new Quantity(2) );

        assertEquals( "6", aggregator.getResult().toString() );
    }

    @Test
    public void appendThreeValuesIncNull() throws Exception {
        AverageAggregator<Quantity> aggregator = new AverageAggregator<Quantity>(Quantity.ZERO);

        aggregator.append( new Quantity(10) );
        aggregator.append( null );
        aggregator.append( new Quantity(2) );

        assertEquals( "4", aggregator.getResult().toString() );
    }

    @Test
    public void appendIterable() throws Exception {
        AverageAggregator<Quantity> aggregator = new AverageAggregator<Quantity>(Quantity.ZERO);

        aggregator.appendAll( Arrays.asList(new Quantity(10),new Quantity(2)) );

        assertEquals( "6", aggregator.getResult().toString() );
    }
}
