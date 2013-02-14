package com.mosaic.lang.math;

/**
 *
 */
@SuppressWarnings({"unchecked"})
public abstract class MathematicalNumber<T extends MathematicalNumber> extends Orderable<T> {
    private static final long serialVersionUID = 1929563657928729440L;
    
    private T zero;

    protected MathematicalNumber( T zero ) {
        this.zero = zero;
    }

    public abstract T add( T b );
    public abstract T subtract( T b );
    public abstract T multiplyBy( T b );
    public abstract T divideBy( T b );

    public abstract T divideBy( int b );
    public abstract T multiplyBy( double b );

    public abstract T abs();


    public abstract int asInt();
    public abstract long asLong();
    public abstract double asDouble();

    public T zero() {
        return zero;
    }

    public boolean isZero() {
        return this.equals( zero );
    }

    public boolean isGTZero() {
        return this.isGT( zero );
    }

    public boolean isLTZero() {
        return this.isLT( zero );
    }

    public boolean isGTEZero() {
        return this.isGTE( zero );
    }

    public boolean isLTEZero() {
        return this.isLTE( zero );
    }
}
