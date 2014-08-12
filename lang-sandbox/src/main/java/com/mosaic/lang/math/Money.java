package com.mosaic.lang.math;

import java.math.BigDecimal;

/**
 *
 */
@Deprecated
public class Money extends AbstractLongNumber<Money> {
    private static final long serialVersionUID = 3069432892432894004L;

    public static final Money ZERO = new Money(0);

    // Stores internally in tenths of the minor currency

    public static Money gbp( int pounds ) {
        return new Money( pounds * 1000L );
    }

    public static Money pence( int pence ) {
        return new Money( pence * 10L );
    }

    /**
     * @param v major currency
     */
    public Money( double v ) {
        this( new BigDecimal( v ).movePointRight( 3 ).longValue() );
    }

    /**
     * @param v major currency
     */
    public Money( String v ) {
        this( new BigDecimal( v ).movePointRight( 3 ).longValue() );
    }

    /**
     * @param v tenths of minor currency
     */
    private Money(long v) {
        super(ZERO, v);
    }

    @Override
    protected Money newInstance(long v) {
        return new Money(v);
    }

    public int asInt() {
        return (int) (super.asLong()/1000);
    }

    public double asDouble() {
        return super.asDouble() / 1000.0;
    }

    @Override
    public String toString() {
        return String.format("%.2f", asDouble());
    }

    @Override
    public Money divideBy(int b) {
        return new Money( v/b );
    }

    @Override
    public Money multiplyBy(double b) {
        return new Money( (int) (v*b) );
    }
}
