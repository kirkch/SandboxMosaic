package com.mosaic.lang.math;

/**
 *
 */
@SuppressWarnings({"unchecked"})
public class AverageAggregator<T extends MathematicalNumber> extends Aggregator<T,T> {
    private T zero;

    private T   resultSoFar;
    private int numValues;

    public AverageAggregator( T zero ) {

        this.zero = zero;
    }

    @Override
    public void append(T v) {
        if ( v == null ) {

        } else if ( resultSoFar == null ) {
            this.resultSoFar = v;
        } else {
            this.resultSoFar = (T) resultSoFar.add( v );
        }

        this.numValues++;
    }

    @Override
    public T getResult() {
        return resultSoFar == null ? zero : (T) resultSoFar.divideBy( numValues );
    }
}
