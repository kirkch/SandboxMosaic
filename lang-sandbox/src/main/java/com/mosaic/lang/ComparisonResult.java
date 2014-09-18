package com.mosaic.lang;

/**
 *
 */
public enum ComparisonResult {
    LT(-1), EQ(0), GT(1);


    private int javaEncoding;

    ComparisonResult( int javaEncoding ) {
        this.javaEncoding = javaEncoding;
    }

    public int toInt() {
        return javaEncoding;
    }


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
