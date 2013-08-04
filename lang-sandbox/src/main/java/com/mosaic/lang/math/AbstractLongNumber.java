package com.mosaic.lang.math;

/**
 *
 */
public abstract class AbstractLongNumber<T extends AbstractLongNumber> extends MathematicalNumber<T> {
    protected final long v;

    protected AbstractLongNumber( T zero, long v ) {
        super( zero );
        
        this.v = v;
    }

    public AbstractLongNumber( T zero, String v ) {
        this( zero, Long.parseLong(v) );
    }

    @Override
    public T add(T b) {
        return newInstance( this.v + b.v );
    }

    @Override
    public T subtract(T b) {
        return newInstance( this.v - b.v );
    }

    @Override
    public T multiplyBy(T b) {
        return newInstance( this.v * b.v );
    }

    @Override
    public T divideBy(T b) {
        return newInstance( this.v / b.v );
    }

    @Override
    public T abs() {
        return newInstance( Math.abs(this.v) );
    }

    public int asInt() {
        return (int) v;
    }

    public long asLong() {
        return v;
    }

    public double asDouble() {
        return v;
    }

    public int compareTo(T b) {
        return new Long(this.v).compareTo(new Long(b.v));
    }

    public int hashCode() {
        return (int) v;
    }

    public String toString() {
        return Long.toString(v);
    }

    public boolean equals( Object o ) {
        if ( o == null ) { return false; }
        if ( o == this ) { return true; }
        if ( this.getClass() != o.getClass() ) { return false; }

        AbstractLongNumber other = (AbstractLongNumber) o;
        return this.v == other.v;
    }

    protected abstract T newInstance(long v);
}
