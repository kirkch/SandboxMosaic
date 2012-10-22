package com.mosaic.lang.math;


/**
 *
 */
public class Percentage extends MathematicalNumber<Percentage> {
    public static final Percentage ZERO = new Percentage(0);

    private double v;

    public Percentage() {
        super( ZERO );
    }

    public Percentage(double n, double d) {
        this( n/d*100 );
    }

    public Percentage( double v ) {
        super( ZERO );

        this.v = v;
    }


    @Override
    public Percentage add(Percentage b) {
        return new Percentage( this.v+b.v );
    }

    @Override
    public Percentage subtract(Percentage b) {
        return new Percentage( this.v-b.v );
    }

    @Override
    public Percentage multiplyBy(Percentage b) {
        return new Percentage( this.v*(b.v/100.0) );
    }

    @Override
    public Percentage divideBy(Percentage b) {
        return new Percentage( this.v/(b.v/100.0) );
    }

    @Override
    public Percentage divideBy(int b) {
        return new Percentage( v/b );
    }

    @Override
    public Percentage multiplyBy(double b) {
        return new Percentage(this.v*b);
    }

    @SuppressWarnings({"unchecked"})
    public <T extends MathematicalNumber> T multiply( T b ) {
        return (T) b.multiplyBy( this.v/100.0 );
    }
    
    @Override
    public Percentage abs() {
        return isLTZero() ? new Percentage(this.v * -1) : this;
    }

    public int compareTo(Percentage o) {
        double a = this.v;
        double b = o.v;

        if ( a < b ) {
            return -1;
        }

        return a>b ? 1 : 0;
    }

    @Override
    public String toString() {
        return v+"%";
    }

    @Override
    public int hashCode() {
        return (int) (v*1000);
    }

    public boolean equals(Object o) {
        if ( this == o ) { return true; }
        if ( !(o instanceof Percentage) ) { return false; }

        Percentage other = (Percentage) o;
        return this.v == other.v;
    }
}
