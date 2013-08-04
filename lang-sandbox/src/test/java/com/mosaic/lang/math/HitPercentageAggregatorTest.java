package com.mosaic.lang.math;

/**
 *
 */
public class HitPercentageAggregatorTest {
//    @Test
//    public void testAppendNoValues() throws Exception {
//        HitPercentageAggregator<Quantity> aggregator = new HitPercentageAggregator<Quantity>(
//                new Predicate<Quantity>() {
//                    @Override
//                    public boolean apply(Quantity v) {
//                        return v.isGTZero();
//                    }
//                }
//        );
//
//        assertEquals( new Percentage(0), aggregator.getResult() );
//    }
//
//    @Test
//    public void testAppendOneValueThatFailsCondition() throws Exception {
//        HitPercentageAggregator<Quantity> aggregator = new HitPercentageAggregator<Quantity>(
//                new Predicate<Quantity>() {
//                    @Override
//                    public boolean apply(Quantity v) {
//                        return v.isGTZero();
//                    }
//                }
//        );
//
//        aggregator.append( new Quantity(0) );
//
//        assertEquals( new Percentage(0), aggregator.getResult() );
//    }
//
//    @Test
//    public void testAppendOneValueThatPassesCondition() throws Exception {
//        HitPercentageAggregator<Quantity> aggregator = new HitPercentageAggregator<Quantity>(
//                new Predicate<Quantity>() {
//                    @Override
//                    public boolean apply(Quantity v) {
//                        return v.isGTZero();
//                    }
//                }
//        );
//
//        aggregator.append( new Quantity(1) );
//
//        assertEquals( new Percentage(100), aggregator.getResult() );
//    }
//
//    @Test
//    public void testAppendTwoValuesOnePassesOneMisses() throws Exception {
//        HitPercentageAggregator<Quantity> aggregator = new HitPercentageAggregator<Quantity>(
//                new Predicate<Quantity>() {
//                    @Override
//                    public boolean apply(Quantity v) {
//                        return v.isGTZero();
//                    }
//                }
//        );
//
//        aggregator.append( new Quantity(1) );
//        aggregator.append( new Quantity(0) );
//
//        assertEquals( new Percentage(50), aggregator.getResult() );
//    }
//
//    @Test
//    public void testAppendingIntegers() throws Exception {
//        HitPercentageAggregator<Integer> aggregator = new HitPercentageAggregator<Integer>(
//                new Predicate<Integer>() {
//                    @Override
//                    public boolean apply(Integer v) {
//                        return v == 1;
//                    }
//                }
//        );
//
//        aggregator.append( 1 );
//        aggregator.append( 0 );
//
//        assertEquals( new Percentage(50), aggregator.getResult() );
//    }
}
