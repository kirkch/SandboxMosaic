package com.mosaic.lang.math;

import java.math.BigDecimal;

/**
 *
 */
public class Money extends AbstractIntegerNumber<Money> {
    private static final long serialVersionUID = 3069432892432894004L;

    public static final Money ZERO = new Money(0);

    Money(int v) {
        super(ZERO, v);
    }

    public Money( double v ) {
        this( new BigDecimal(v).movePointRight(3).intValue() );
    }

    public Money( String v ) {
        super( ZERO, new BigDecimal(v).movePointRight(3).intValue() );
    }

    @Override
    protected Money newInstance(int v) {
        return new Money(v);
    }

    public double toDouble() {
        return Double.parseDouble(this.toString());
    }

    @Override
    public String toString() {
        int major = v / 1000;
        int minor = (Math.abs((v - major*1000)) + 5)/10;

        if ( minor == 0 ) {
            return major+"";
        } else if ( minor < 10 ) {
            return major + ".0" + minor;
        } else {
            return major + "." + minor;
        }
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
