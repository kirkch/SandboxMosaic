package com.mosaic.lang.math;

/**
 *
 */
public abstract class AbstractIntegerNumber<T extends AbstractIntegerNumber> extends MathematicalNumber<T> {
    private static final long serialVersionUID = -7978738958445124172L;
    protected final int v;

    protected AbstractIntegerNumber( T zero, int v ) {
        super( zero );
        
        this.v = v;
    }

    public AbstractIntegerNumber( T zero, String v ) {
        this( zero, Integer.parseInt(v) );
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

    @SuppressWarnings({"unchecked"})
    @Override
    public T abs() {
        return isLTZero() ? newInstance(this.v * -1) : (T) this;
    }

    public int asInt() {
        return v;
    }

    public long asLong() {
        return asInt();
    }

    public double asDouble() {
        return asInt();
    }


    public int compareTo(T b) {
        return this.v - b.v;
    }

    public int hashCode() {
        return v;
    }

    public String toString() {
        return Integer.toString(v);
    }

    public boolean equals( Object o ) {
        if ( o == null ) { return false; }
        if ( o == this ) { return true; }
        if ( this.getClass() != o.getClass() ) { return false; }

        AbstractIntegerNumber other = (AbstractIntegerNumber) o;
        return this.v == other.v;
    }

    protected abstract T newInstance(int v);
}
