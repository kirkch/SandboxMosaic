package com.mosaic.lang;

/**
 *
 */
public enum ComparisonResult {
    LT, EQ, GT;


    public boolean isLT() {
        return this == LT;
    }

    public boolean isLTE() {
        return this == LT || this == EQ;
    }

    public boolean isEQ() {
        return this == EQ;
    }

    public boolean isGT() {
        return this == GT;
    }

    public boolean isGTE() {
        return this == GT || this == EQ;
    }

    public static ComparisonResult compare( int a, int b ) {
        if ( a == b ) {
            return EQ;
        } else if ( a < b ) {
            return LT;
        } else {
            return GT;
        }
    }
}
