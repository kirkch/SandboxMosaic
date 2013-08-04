package com.mosaic.lang.math;


/**
 *
 */
public class Ratio extends MathematicalNumber<Ratio> {
    public static final Ratio ZERO = new Ratio(0, 0);

    private double numerator;
    private double denominator;

    public Ratio( String v ) {
        this( Double.parseDouble(v) );
    }

    public Ratio( double v ) {
        this( v, 1 );
    }

    public Ratio( int numerator, int denominator) {
        this( (double) numerator, (double) denominator);
    }

    public Ratio( double numerator, double denominator) {
        super( ZERO );

        this.numerator   = numerator;
        this.denominator = denominator;
    }

    @Override
    public Ratio add(Ratio o) {
        return new Ratio( this.numerator +o.numerator, this.denominator +o.denominator);
    }

    @Override
    public Ratio subtract(Ratio b) {
        return new Ratio( this.numerator -b.numerator, this.denominator -b.denominator);
    }

    @Override
    public Ratio multiplyBy(Ratio b) {
        return new Ratio( this.numerator *b.numerator, this.denominator *b.denominator);
    }

    @Override
    public Ratio divideBy(Ratio b) {
        return new Ratio( this.numerator*b.denominator, this.denominator*b.numerator );
    }

    @Override
    public Ratio divideBy(int b) {
        return new Ratio( numerator, denominator*b );
    }

    @Override
    public Ratio multiplyBy(double b) {
        return new Ratio(numerator*(b*denominator), denominator);
    }

    @Override
    public Ratio abs() {
        return this;
    }

    public int asInt() {
        return asPercentage().asInt();
    }

    public long asLong() {
        return asPercentage().asLong();
    }

    public double asDouble() {
        return asPercentage().asDouble();
    }

    public Percentage asPercentage() {
        return new Percentage( numerator, denominator );
    }

    public int compareTo( Ratio o ) {
        double mult = o.denominator/this.denominator;

        double r = this.numerator * mult - o.numerator;
        if ( isZero(r) ) {
            return 0;
        }

        return r < 0 ? -1 : 1;
    }

    @Override
    public int hashCode() {
        return (int) (numerator+denominator) + 17;
    }

    public boolean equals(Object o) {
        if ( this == o ) { return true; }
        if ( !(o instanceof Ratio) ) { return false; }

        Ratio other = (Ratio) o;
        return this.numerator*other.denominator == other.numerator*this.denominator;
    }

    @Override
    public String toString() {
        return numerator + ":" + denominator;
    }

    private boolean isZero(double r) {
        return Math.abs(r) <= 0.000001;
    }
}
