package com.mosaic.lang.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 */
public abstract class AbstractDecimalNumber <T extends AbstractDecimalNumber> extends MathematicalNumber<T> {
    protected final BigDecimal v;

    protected AbstractDecimalNumber( T zero, double v, int precision ) {
        this( zero, new BigDecimal(v), precision );
    }

    public AbstractDecimalNumber(T zero, String v, int precision) {
        this( zero, new BigDecimal(v), precision );
    }

    public AbstractDecimalNumber(T zero, BigDecimal v, int precision) {
        super( zero );

        this.v = v.setScale( precision, RoundingMode.HALF_UP );
    }

    @Override
    public T add(T b) {
        return newInstance( this.v.add(b.v), v.scale() );
    }

    @Override
    public T subtract(T b) {
        return newInstance( this.v.subtract(b.v), v.scale() );
    }

    @Override
    public T multiplyBy(T b) {
        return newInstance( this.v.multiply(b.v), v.scale() );
    }

    @Override
    public T divideBy(T b) {
        return newInstance( this.v.divide(b.v), v.scale() );
    }

    public int compareTo(T b) {
        return this.v.compareTo(b.v);
    }

    public int hashCode() {
        return v.intValue();
    }

    public String toString() {
        return v.toString();
    }

    public boolean equals( Object o ) {
        if ( o == null ) { return false; }
        if ( o == this ) { return true; }
        if ( this.getClass() != o.getClass() ) { return false; }

        AbstractDecimalNumber other = (AbstractDecimalNumber) o;
        return this.v.compareTo(other.v) == 0;
    }

    protected abstract T newInstance(BigDecimal v, int precision);
}
