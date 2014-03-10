package com.mosaic.lang.math;

import com.mosaic.lang.QA;
import com.mosaic.lang.QA;

/**
 *
 */
public class Quantity extends AbstractLongNumber<Quantity> {
    public static final Quantity ZERO = new Quantity(0);

    public Quantity() {
        this(0);
    }
    
    public Quantity( long v ) {
        super(ZERO, v);

        QA.isGTEZero( v, "quantity" );
    }

    public Quantity( String v ) {
        super(ZERO, v);

        QA.isTrue( isGTE( ZERO ), v + "" );
    }

    @Override
    protected Quantity newInstance(long v) {
        return new Quantity(v);
    }

    @Override
    public Quantity divideBy(int b) {
        return new Quantity( v/b );
    }

    @Override
    public Quantity multiplyBy(double b) {
        return new Quantity( (long) (v*b) );
    }

    @Override
    public Quantity abs() {
        return isLTZero() ? newInstance(this.v * -1) : this;
    }
}
