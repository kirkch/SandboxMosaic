package com.mosaic.lang;

import com.mosaic.lang.math.MathematicalNumber;
import com.mosaic.lang.reflect.ReflectionException;
import com.mosaic.lang.system.SystemX;
import com.mosaic.utils.MathUtils;
import com.mosaic.utils.StringUtils;


/**
 * A suite of argument and state validations.  Use to enforce api contracts
 * with only one line per contract.  QA is short for Quality Assurance and
 * the checks on this class are only invoked when -ea is passed to the JVM.
 */
@SuppressWarnings("UnusedDeclaration")
public class QA {
// isNotZero

    public static void isNotZero( byte v, String message, Object...args ) {
        if ( v == 0 ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void isNotZero( char v, String message, Object...args ) {
        if ( v == 0 ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void isNotZero( short v, String message, Object...args ) {
        if ( v == 0 ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void isNotZero( int v, String message, Object...args ) {
        if ( v == 0 ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void isNotZero( long v, String message, Object...args ) {
        if ( v == 0 ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void isNotZero( float v, float tolerance, String message, Object...args ) {
        if ( Math.abs(v) <= tolerance ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void isNotZero( double v, double tolerance, String message, Object...args ) {
        if ( Math.abs(v) <= tolerance ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

// isGTE

    public static void argIsGTE( byte a, byte b, String argName ) {
        if ( a < b ) {
            throwException( "%s (%s) must be >= %s", argName, a, b );
        }
    }

    public static void argIsGTE( short a, short b, String argName ) {
        if ( a < b ) {
            throwException( "%s (%s) must be >= %s", argName, a, b );
        }
    }

    public static void argIsGTE( int a, int b, String argName ) {
        if ( a < b ) {
            throwException( "%s (%s) must be >= %s", argName, a, b );
        }
    }

    public static void argIsGTE( long a, long b, String argName ) {
        if ( a < b ) {
            throwException( "%s (%s) must be >= %s", argName, a, b );
        }
    }

    public static void argIsGTE( float a, float b, String argName, float tolerance ) {
        if ( a < b ) {
            throwException( "%s (%s) must be >= %s", argName, a, b );
        }
    }

    public static void argIsGTE( double a, double b, String argName, double tolerance ) {
        if ( a < b ) {
            throwException( "%s (%s) must be >= %s", argName, a, b );
        }
    }

    public static <T extends Comparable<T>> void argIsGTE( T a, T b, String argName ) {
        if ( a.compareTo(b) < 0 ) {
            throwException( "%s (%s) must be >= %s", argName, a, b );
        }
    }

    public static void isGTE( byte a, byte b, String message, Object...args ) {
        isTrue( a >= b, IllegalStateException.class, message, args );
    }

    public static void isGTE( short a, short b, String message, Object...args ) {
        isTrue( a >= b, IllegalStateException.class, message, args );
    }

    public static void isGTE( int a, int b, String message, Object...args ) {
        isTrue( a >= b, IllegalStateException.class, message, args );
    }

    public static void isGTE( long a, long b, String message, Object...args ) {
        isTrue( a >= b, IllegalStateException.class, message, args );
    }

    public static void isGTE( float a, float b, float tolerance, String message, Object...args ) {
        isTrue( a-tolerance >= b, IllegalStateException.class, message, args );
    }

    public static void isGTE( double a, double b, double tolerance, String message, Object...args ) {
        isTrue( a-tolerance >= b, IllegalStateException.class, message, args );
    }

    public static <T extends Comparable<T>> void GTE( T a, T b, String message, Object...args ) {
        isTrue( a.compareTo(b) >= 0, IllegalStateException.class, message, args );
    }


// isGTZero

    public static void argIsGTZero( byte a, String argName ) {
        if ( a <= 0 ) {
            throwException( "%s (%s) must be > 0", argName, a );
        }
    }

    public static void argIsGTZero( short a, String argName ) {
        if ( a <= 0 ) {
            throwException( "%s (%s) must be > 0", argName, a );
        }
    }

    public static void argIsGTZero( int a, String argName ) {
        if ( a <= 0 ) {
            throwException( "%s (%s) must be > 0", argName, a );
        }
    }

    public static void argIsGTZero( long a, String argName ) {
        if ( a <= 0 ) {
            throwException( "%s (%s) must be > 0", argName, a );
        }
    }

    public static void argIsGTZero( float a, String argName, float tolerance ) {
        if ( a+tolerance <= 0 ) {
            throwException( "%s (%s) must be > 0", argName, a );
        }
    }

    public static void argIsGTZero( double a, String argName, double tolerance ) {
        if ( a+tolerance <= 0 ) {
            throwException( "%s (%s) must be > 0", argName, a );
        }
    }


    public static void isGTZero( byte a, String message, Object...args ) {
        isTrue( a > 0, IllegalStateException.class, message, args );
    }

    public static void isGTZero( short a, String message, Object...args ) {
        isTrue( a > 0, IllegalStateException.class, message, args );
    }

    public static void isGTZero( int a, String message, Object...args ) {
        isTrue( a > 0, IllegalStateException.class, message, args );
    }

    public static void isGTZero( long a, String message, Object...args ) {
        isTrue( a > 0, IllegalStateException.class, message, args );
    }

    public static void isGTZero( float a, float tolerance, String message, Object...args ) {
        isTrue( a-tolerance > 0, IllegalStateException.class, message, args );
    }

    public static void isGTZero( double a, double tolerance, String message, Object...args ) {
        isTrue( a-tolerance > 0, IllegalStateException.class, message, args );
    }



// isGTEZero

    public static void argIsGTEZero( byte a, String argName ) {
        if ( a < 0 ) {
            throwException( "%s (%s) must be >= 0", argName, a );
        }
    }

    public static void argIsGTEZero( short a, String argName ) {
        if ( a < 0 ) {
            throwException( "%s (%s) must be >= 0", argName, a );
        }
    }

    public static void argIsGTEZero( int a, String argName ) {
        if ( a < 0 ) {
            throwException( "%s (%s) must be >= 0", argName, a );
        }
    }

    public static void argIsGTEZero( long a, String argName ) {
        if ( a < 0 ) {
            throwException( "%s (%s) must be >= 0", argName, a );
        }
    }

    public static void argIsGTEZero( float a, String argName, float tolerance ) {
        if ( a+tolerance < 0 ) {
            throwException( "%s (%s) must be >= 0", argName, a );
        }
    }

    public static void argIsGTEZero( double a, String argName, double tolerance ) {
        if ( a+tolerance < 0 ) {
            throwException( "%s (%s) must be >= 0", argName, a );
        }
    }

    public static void isGTEZero( byte a, String message, Object...args ) {
        isTrue( a >= 0, IllegalStateException.class, message, args );
    }

    public static void isGTEZero( short a, String message, Object...args ) {
        isTrue( a >= 0, IllegalStateException.class, message, args );
    }

    public static void isGTEZero( int a, String message, Object...args ) {
        isTrue( a >= 0, IllegalStateException.class, message, args );
    }

    public static void isGTEZero( long a, String message, Object...args ) {
        isTrue( a >= 0, IllegalStateException.class, message, args );
    }

    public static void isGTEZero( float a, float tolerance, String message, Object...args ) {
        isTrue( a+tolerance >= 0, IllegalStateException.class, message, args );
    }

    public static void isGTEZero( double a, double tolerance, String message, Object...args ) {
        isTrue( a+tolerance >= 0, IllegalStateException.class, message, args );
    }



// isGT

    public static void argIsGT( byte a, byte b, String argName ) {
        if ( a <= b ) {
            throwException( "%s (%s) must be > %s", argName, a, b );
        }
    }

    public static void argIsGT( byte a, byte b, String argNameA, String argNameB ) {
        if ( a <= b ) {
            throwException( "%s (%s) must be > %s (%s)", argNameA, a, argNameB, b );
        }
    }

    public static void argIsGT( short a, short b, String argName ) {
        if ( a <= b ) {
            throwException( "%s (%s) must be > %s", argName, a, b );
        }
    }

    public static void argIsGT( int a, int b, String argName ) {
        if ( a <= b ) {
            throwException( "%s (%s) must be > %s", argName, a, b );
        }
    }

    public static void argIsGT( int a, int b, String argNameA, String argNameB ) {
        if ( a <= b ) {
            throwException( "%s (%s) must be > %s (%s)", argNameA, a, argNameB, b );
        }
    }

    public static <T extends Throwable> void argIsGT( int a, int b, String argName, Class<T> exceptionType ) {
        if ( a <= b ) {
            throwException( "%s (%s) must be > %s", argName, a, b );
        }
    }

    public static void argIsGT( long a, long b, String argName ) {
        if ( a <= b ) {
            throwException( "%s (%s) must be > %s", argName, a, b );
        }
    }

    public static void argIsGT( long a, long b, String argNameA, String argNameB ) {
        if ( a <= b ) {
            throwException( "%s (%s) must be > %s (%s)", argNameA, a, argNameB, b );
        }
    }

    public static void argIsGT( float a, float b, String argName, float tolerance ) {
        if ( a <= b ) {
            throwException( "%s (%s) must be > %s", argName, a, b );
        }
    }

    public static void argIsGT( double a, double b, String argName, double tolerance ) {
        if ( a+tolerance <= b ) {
            throwException( "%s (%s) must be > %s", argName, a, b );
        }
    }

    public static <T extends Comparable<T>> void argIsGT( T a, T b, String argName ) {
        if ( a.compareTo(b) <= 0 ) {
            throwException( "%s (%s) must be > %s", argName, a, b );
        }
    }

    public static void isGT( byte a, byte b, String message, Object...args ) {
        isTrue( a > b, IllegalStateException.class, message, args );
    }

    public static void isGT( short a, short b, String message, Object...args ) {
        isTrue( a > b, IllegalStateException.class, message, args );
    }

    public static void isGT( int a, int b, String message, Object...args ) {
        isTrue( a > b, IllegalStateException.class, message, args );
    }

    public static void isGT( long a, long b, String message, Object...args ) {
        isTrue( a > b, IllegalStateException.class, message, args );
    }

    public static void isGT( float a, float b, float tolerance, String message, Object...args ) {
        isTrue( a-tolerance > b, IllegalStateException.class, message, args );
    }

    public static void isGT( double a, double b, double tolerance, String message, Object...args ) {
        isTrue( a-tolerance > b, IllegalStateException.class, message, args );
    }

    public static <T extends Comparable<T>> void isGT( T a, T b, String message, Object...args ) {
        isTrue( a.compareTo(b) > 0, IllegalStateException.class, message, args );
    }

// isLT

    public static void argIsLT( byte a, byte b, String argName ) {
        if ( a >= b ) {
            throwException( "%s (%s) must be < %s", argName, a, b );
        }
    }

    public static void argIsLT( short a, short b, String argName ) {
        if ( a >= b ) {
            throwException( "%s (%s) must be < %s", argName, a, b );
        }
    }

    public static void argIsLT( int a, int b, String argName ) {
        if ( a >= b ) {
            throwException( "%s (%s) must be < %s", argName, a, b );
        }
    }

    public static void argIsLT( long a, long b, String argName ) {
        if ( a >= b ) {
            throwException( "%s (%s) must be < %s", argName, a, b );
        }
    }

    public static void argIsLT( long a, long b, String argName1, String argName2 ) {
        if ( a >= b ) {
            throwException( "%s (%s) must be < %s (%s)", argName1, a, argName2, b );
        }
    }

    public static void argAIsLTArgB( long a, long b, String argNameA, String argNameB ) {
        if ( a >= b ) {
            throwException( "%s (%s) must be < %s (%s)", argNameA, a, argNameB, b );
        }
    }

    public static void argIsLT( float a, float b, String argName, float tolerance ) {
        if ( a+tolerance >= b ) {
            throwException( "%s (%s) must be < %s", argName, a, b );
        }
    }

    public static void argIsLT( double a, double b, String argName, double tolerance ) {
        if ( a-tolerance >= b ) {
            throwException( "%s (%s) must be < %s", argName, a, b );
        }
    }

    public static <T extends Comparable<T>> void argIsLT( T a, T b, String argName ) {
        if ( a.compareTo(b) >= 0 ) {
            throwException( "%s (%s) must be < %s", argName, a, b );
        }
    }

    public static void isLT( byte a, byte b, String message, Object...args ) {
        isTrue( a < b, IllegalStateException.class, message, args );
    }

    public static void isLT( short a, short b, String message, Object...args ) {
        isTrue( a < b, IllegalStateException.class, message, args );
    }

    public static void isLT( int a, int b, String message, Object...args ) {
        isTrue( a < b, IllegalStateException.class, message, args );
    }

    public static void isLT( long a, long b, String message, Object...args ) {
        if ( a - b >= 0 ) { // condition added here as this method was shown to be slow without it in benchmarks
            isTrue( a < b, IllegalStateException.class, message, args );
        }
    }

    public static void isLT( float a, float b, float tolerance, String message, Object...args ) {
        isTrue( a-tolerance < b, IllegalStateException.class, message, args );
    }

    public static void isLT( double a, double b, double tolerance, String message, Object...args ) {
        isTrue( a-tolerance < b, IllegalStateException.class, message, args );
    }

    public static <T extends Comparable<T>> void LT( T a, T b, String message, Object...args ) {
        isTrue( a.compareTo(b) < 0, message, args );
    }

// isLTE

    public static void argIsLTE( byte a, byte b, String argName ) {
        if ( a > b ) {
            throwException( "%s (%s) must be <= %s", argName, a, b );
        }
    }

    public static void argIsLTE( short a, short b, String argName ) {
        if ( a > b ) {
            throwException( "%s (%s) must be <= %s", argName, a, b );
        }
    }

    public static void argIsLTE( int a, int b, String argName ) {
        if ( a > b ) {
            throwException( "%s (%s) must be <= %s", argName, a, b );
        }
    }

    public static void argIsLTE( long a, long b, String argName ) {
        if ( a > b ) {
            throwException( "%s (%s) must be <= %s", argName, a, b );
        }
    }

    public static void argIsLTE( float a, float b, String argName, float tolerance ) {
        if ( a > b-tolerance ) {
            throwException( "%s (%s) must be <= %s", argName, a, b );
        }
    }

    public static void argIsLTE( double a, double b, String argName, double tolerance ) {
        if ( a-tolerance > b ) {
            throwException( "%s (%s) must be <= %s", argName, a, b );
        }
    }

    public static <T extends Comparable<T>> void argIsLTE( T a, T b, String argName ) {
        if ( a.compareTo(b) > 0 ) {
            throwException( "%s (%s) must be <= %s", argName, a, b );
        }
    }

    public static void isLTE( byte a, byte b, String message, Object...args ) {
        isTrue( a <= b, IllegalStateException.class, message, args );
    }

    public static void isLTE( short a, short b, String message, Object...args ) {
        isTrue( a <= b, IllegalStateException.class, message, args );
    }

    public static void isLTE( int a, int b, String message, Object...args ) {
        isTrue( a <= b, IllegalStateException.class, message, args );
    }

    public static void isLTE( long a, long b, String message, Object...args ) {
        isTrue( a <= b, IllegalStateException.class, message, args );
    }

    public static void isLTE( float a, float b, float tolerance, String message, Object...args ) {
        isTrue( a-tolerance <= b, IllegalStateException.class, message, args );
    }

    public static void isLTE( double a, double b, double tolerance, String message, Object...args ) {
        isTrue( a-tolerance <= b, IllegalStateException.class, message, args );
    }

    public static <T extends Comparable<T>> void isLTEObjects( T a, T b, String message, Object...args ) {
        isTrue( a.compareTo(b) <= 0, IllegalStateException.class, message, args );
    }

// isLTZero

    public static void argIsLTZero( byte a, String argName ) {
        if ( a >= 0 ) {
            throwException( "%s (%s) must be < 0", argName, a );
        }
    }

    public static void argIsLTZero( short a, String argName ) {
        if ( a >= 0 ) {
            throwException( "%s (%s) must be < 0", argName, a );
        }
    }

    public static void argIsLTZero( int a, String argName ) {
        if ( a >= 0 ) {
            throwException( "%s (%s) must be < 0", argName, a );
        }
    }

    public static void argIsLTZero( long a, String argName ) {
        if ( a >= 0 ) {
            throwException( "%s (%s) must be < 0", argName, a );
        }
    }

    public static void argIsLTZero( float a, String argName, float tolerance ) {
        if ( a >= 0 ) {
            throwException( "%s (%s) must be < 0", argName, a );
        }
    }

    public static void argIsLTZero( double a, String argName, double tolerance ) {
        if ( a >= 0 ) {
            throwException( "%s (%s) must be < 0", argName, a );
        }
    }

    public static <T extends MathematicalNumber<T>> void argIsLTZero( T a, String argName ) {
        if ( a.isGTZero() ) {
            throwException( "%s (%s) must be < 0", argName, a );
        }
    }

    public static void isLTZero( byte a, String message, Object...args ) {
        isTrue( a < 0, IllegalStateException.class, message, args );
    }

    public static void isLTZero( short a, String message, Object...args ) {
        isTrue( a < 0, IllegalStateException.class, message, args );
    }

    public static void isLTZero( int a, String message, Object...args ) {
        isTrue( a < 0, IllegalStateException.class, message, args );
    }

    public static void isLTZero( long a, String message, Object...args ) {
        isTrue( a < 0, IllegalStateException.class, message, args );
    }

    public static void isLTZero( float a, float tolerance, String message, Object...args ) {
        isTrue( a < 0f, IllegalStateException.class, message, args, tolerance );
    }

    public static void isLTZero( double a, double tolerance, String message, Object...args ) {
        isTrue( a < 0.0, IllegalStateException.class, message, args, tolerance );
    }

    public static <T extends MathematicalNumber<T>> void isLTZero( T a, String message, Object...args ) {
        isTrue( a.isLTEZero(), IllegalStateException.class, message, args );
    }

// isLTEZero

    public static void argIsLTEZero( byte a, String argName ) {
        if ( a > 0 ) {
            throwException( "%s (%s) must be <= 0", argName, a );
        }
    }

    public static void argIsLTEZero( short a, String argName ) {
        if ( a > 0 ) {
            throwException( "%s (%s) must be <= 0", argName, a );
        }
    }

    public static void argIsLTEZero( int a, String argName ) {
        if ( a > 0 ) {
            throwException( "%s (%s) must be <= 0", argName, a );
        }
    }

    public static void argIsLTEZero( long a, String argName ) {
        if ( a > 0 ) {
            throwException( "%s (%s) must be <= 0", argName, a );
        }
    }

    public static void argIsLTEZero( float a, String argName, float tolerance ) {
        if ( a > 0 ) {
            throwException( "%s (%s) must be <= 0", argName, a );
        }
    }

    public static void argIsLTEZero( double a, String argName, double tolerance ) {
        if ( a > 0 ) {
            throwException( "%s (%s) must be <= 0", argName, a );
        }
    }

    public static <T extends MathematicalNumber<T>> void argIsLTEZero( T a, String argName ) {
        if ( a.isGTZero() ) {
            throwException( "%s (%s) must be <= 0", argName, a );
        }
    }

    public static void isLTEZero( byte a, String message, Object...args ) {
        isTrue( a <= 0, IllegalStateException.class, message, args );
    }

    public static void isLTEZero( short a, String message, Object...args ) {
        isTrue( a <= 0, IllegalStateException.class, message, args );
    }

    public static void isLTEZero( int a, String message, Object...args ) {
        isTrue( a <= 0, IllegalStateException.class, message, args );
    }

    public static void isLTEZero( long a, String message, Object...args ) {
        isTrue( a <= 0, IllegalStateException.class, message, args );
    }

    public static void isLTEZero( float a, float tolerance, String message, Object...args ) {
        isTrue( a - tolerance <= 0, IllegalStateException.class, message, args );
    }

    public static void isLTEZero( double a, double tolerance, String message, Object...args ) {
        isTrue( a - tolerance <= 0, IllegalStateException.class, message, args );
    }

    public static <T extends MathematicalNumber<T>> void isLTEZero( T a, String message, Object...args ) {
        isTrue( a.isLTEZero(), IllegalStateException.class, message, args );
    }



/////////

    public static void isMultipleOf2( int v, String argName ) {
        if ( !MathUtils.isPowerOf2( v ) ) {
            throwException( "%s (%s) must be a power of 2", argName, v);
        }
    }

    public static void argNotBlank( String chars, String argName ) {
        if ( StringUtils.isBlank(chars) ) {
            throwException( "%s must not be blank (was %s)", argName, chars );
        }
    }

    public static void argNotBlank( UTF8 chars, String argName ) {
        if ( StringUtils.isBlank(chars) ) {
            throwException( "%s must not be blank (was %s)", argName, chars );
        }
    }

    public static void notBlank( String chars, String message, Object...args) {
        if ( StringUtils.isBlank(chars) ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void argNotEmpty( CharSequence chars, String argName ) {
        if ( chars == null || chars.length() == 0 ) {
            throwException( "%s must not be empty (was %s)", argName );
        }
    }

    public static void notEmpty( CharSequence chars, String message, Object...args ) {
        if ( chars == null || chars.length() == 0 ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void argNotNull( Object o, String argName ) {
        if ( o == null ) {
            throwException( "%s must not be %s", argName, o );
        }
    }

    public static void notNull( Object o, String message, Object...args ) {
        if ( o == null ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void argIsNull( Object o, String argName ) {
        if ( o != null ) {
            throwException( "%s must be null but was %s", argName, o );
        }
    }

    public static void isNull( Object o, String message, Object...args ) {
        if ( o != null ) {
            throwException( IllegalStateException.class, message, args );
        }
    }

    public static void argIsTrue( boolean v, String argName ) {
        if ( !v ) {
            throwException( "'%s' (%s) must be true", argName, v );
        }
    }

    public static void argIsFalse( boolean v, String argName ) {
        if ( v ) {
            throwException( "'%s' (%s) must be false", argName, v );
        }
    }

    public static void isTrue( boolean condition, String msg, Object...values ) {
        isTrue( condition, IllegalStateException.class, msg, values );
    }

    public static <T extends Throwable> void isTrue( boolean condition, Class<T> exceptionType, String msg, Object...values ) {
        if ( !condition ) {
            throwException( exceptionType, msg, values );
        }
    }

    public static void isFalse( boolean condition, String msg, Object...values ) {
        if ( condition ) {
            throwException( IllegalStateException.class, msg, values );
        }
    }

    public static void argInclusiveBetween( int minInc, int v, int maxInc, String argName ) {
        if ( v < minInc || v > maxInc ) {
            throwException( "%s is not within the specified bounds of %s <= %s <= %s", argName, minInc, v, maxInc );
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void argHasNoNullElements( Object[] array, String argName ) {
        if ( array == null ) {
            throwException( "%s is not allowed to be null", argName );
        }

        for ( int i=0; i<array.length; i++ ) {
            Object e = array[i];

            if ( e == null ) {
                throwException( "%s must not be null",argName+"["+i+"]" );
            }
        }
    }


    private static void throwException( String msg, Object...values ) {
        throwException( IllegalArgumentException.class, msg, values );
    }

    private static <T extends Throwable> void throwException( Class<T> exceptionType, String msg, Object...values ) {
        String formattedMessage = String.format( msg, formatValues(values) );

        try {
            throw exceptionType.getConstructor( String.class ).newInstance( formattedMessage );
        } catch ( Throwable ex ) {
            throw ReflectionException.recast( ex );
        }
    }

    private static void throwIllegalStateException( String msg, Object...values ) {
        String formattedMessage = String.format( msg, formatValues(values) );

        throw new IllegalStateException( formattedMessage );
    }

    private static void throwIndexOutOfBoundsException( String msg, Object...values ) {
        String formattedMessage = String.format( msg, formatValues(values) );

        throw new IndexOutOfBoundsException( formattedMessage );
    }

    private static Object[] formatValues( Object[] values ) {
        int      numValues       = values.length;
        Object[] formattedValues = new Object[numValues];

        for ( int i=0; i<numValues; i++ ) {
            formattedValues[i] = formatValue(values[i]);
        }

        return formattedValues;
    }

    private static Object formatValue( Object value ) {
        if ( value instanceof String ) {
            return "'"+value+"'";
        }

        return value;
    }


// isEqualTo

    public static void argIsEqualTo( byte a, byte b, String argName ) {
        if ( a != b ) {
            throwException(  "%s (%s) must be == %s", argName, a, b );
        }
    }

    public static void argIsEqualTo( short a, short b, String argName ) {
        if ( a != b ) {
            throwException(  "%s (%s) must be == %s", argName, a, b );
        }
    }

    public static void argIsEqualTo( int a, int b, String argName ) {
        if ( a != b ) {
            throwException(  "%s (%s) must be == %s", argName, a, b );
        }
    }

    public static void argIsEqualTo( long a, long b, String argName ) {
        if ( a != b ) {
            throwException(  "%s (%s) must be == %s", argName, a, b );
        }
    }

    public static void argIsEqualTo( float a, float b, String argName, float tolerance ) {
        if ( Math.abs(a-b) <= tolerance ) {
            throwException(  "%s (%s) must be == %s", argName, a, b );
        }
    }

    public static void argIsEqualTo( double a, double b, String argName, double tolerance ) {
        if ( Math.abs(a-b) <= tolerance ) {
            throwException(  "%s (%s) must be == %s", argName, a, b );
        }
    }

    public static <T extends Comparable<T>> void argIsEqualTo( T a, T b, String argName ) {
        if ( a.compareTo(b) != 0 ) {
            throwException(  "%s (%s) must be == %s", argName, a, b );
        }
    }



    /**
     * Validates that minInc <= n < maxExc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetween( byte minInc, byte n, byte maxExc, String argName ) {
        if ( !(minInc <= n && n < maxExc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and < %d", argName, n, minInc, maxExc );
        }
    }

    /**
     * Validates that minInc <= n < maxExc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetween( short minInc, short n, short maxExc, String argName ) {
        if ( !(minInc <= n && n < maxExc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and < %d", argName, n, minInc, maxExc );
        }
    }

    /**
     * Validates that minInc <= n < maxExc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetween( char minInc, char n, char maxExc, String argName ) {
        if ( !(minInc <= n && n < maxExc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and < %d", argName, n, minInc, maxExc );
        }
    }

    /**
     * Validates that minInc <= n < maxExc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetween( int minInc, int n, int maxExc, String argName ) {
        if ( !(minInc <= n && n < maxExc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and < %d", argName, n, minInc, maxExc );
        }
    }

    /**
     * Validates that minInc <= n < maxExc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetween( long minInc, long n, long maxExc, String argName ) {
        if ( !(minInc <= n && n < maxExc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and < %d", argName, n, minInc, maxExc );
        }
    }

    /**
     * Validates that minInc <= n <= maxExc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetweenInc( byte minInc, byte n, byte maxInc, String argName ) {
        if ( !(minInc <= n && n <= maxInc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and <= %d", argName, n, minInc, maxInc );
        }
    }

    /**
     * Validates that minInc <= n <= maxInc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetweenInc( short minInc, short n, short maxInc, String argName ) {
        if ( !(minInc <= n && n <= maxInc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and <= %d", argName, n, minInc, maxInc );
        }
    }

    /**
     * Validates that minInc <= n <= maxInc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetweenInc( char minInc, char n, char maxInc, String argName ) {
        if ( !(minInc <= n && n <= maxInc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and <= %d", argName, n, minInc, maxInc );
        }
    }

    /**
     * Validates that minInc <= n <= maxInc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetweenInc( int minInc, int n, int maxInc, String argName ) {
        if ( !(minInc <= n && n <= maxInc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and <= %d", argName, n, minInc, maxInc );
        }
    }

    /**
     * Validates that minInc <= n <= maxInc and throws IndexOutOfBoundsException if it fails.
     */
    public static void argIsBetweenInc( long minInc, long n, long maxInc, String argName ) {
        if ( !(minInc <= n && n <= maxInc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and <= %d", argName, n, minInc, maxInc );
        }
    }

    /**
     * The specified range must equal or be within specified range.
     */
    public static void argIsWithinRange( int minInc, int minValue, int maxValue, int maxExc, String minValueName, String maxValueName ) {
        argIsBetween( minInc, minValue, maxExc, minValueName );
        argIsBetween( minInc, maxValue, maxExc + 1, maxValueName );

        if ( !(minValue < maxValue) ) {
            throwException( "%s (%s) < %s (%s)", minValueName, minValue, maxValueName, maxValue );
        }
    }

    public static void argIsUnsignedByte( int v, String name ) {
        if ( SystemX.isDebugRun() ) {
            if ( v < 0 || v > 255 ) {
                throwException( "%s (%s) is outside the bounds of an unsigned byte (0-255)", name, v );
            }
        }
    }

    public static void isInt( long v, String argName ) {
        if ( SystemX.isDebugRun() ) {
            if ( (v & 0x7FFFFFFF) != v ) {
                throwException( "%s (%s) is out of bounds of an int", argName, v );
            }
        }
    }

    public static void isUnsignedInt( long v, String argName ) {
        if ( SystemX.isDebugRun() ) {
            if ( (v & 0xFFFFFFFF) != v ) {
                throwException( "%s (%s) is out of bounds of an unsigned int", argName, v );
            }
        }
    }

    public static void isUnsignedShort( int v, String argName ) {
        if ( SystemX.isDebugRun() ) {
            if ( (v & 0xFFFF) != v ) {
                throwException( "%s (%s) is out of bounds of an unsigned short", argName, v );
            }
        }
    }
}
