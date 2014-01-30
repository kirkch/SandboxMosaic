package com.mosaic.lang;

import com.mosaic.lang.math.MathematicalNumber;
import com.mosaic.lang.reflect.ReflectionException;
import com.mosaic.utils.MathUtils;
import com.mosaic.utils.StringUtils;

/**
 *
 */
public class Validate {
// isGTE

    public static void isGTE( byte a, byte b, String fieldName ) {
        isTrue( a >= b, "%s (%s) must be >= %s", fieldName, a, b );
    }

    public static void isGTE( short a, short b, String fieldName ) {
        isTrue( a >= b, "%s (%s) must be >= %s", fieldName, a, b );
    }

    public static void isGTE( int a, int b, String fieldName ) {
        isTrue( a >= b, "%s (%s) must be >= %s", fieldName, a, b );
    }

    public static <T extends Throwable> void isGTE( int a, int b, String fieldName, Class<T> exceptionType ) {
        isTrue( a >= b, exceptionType, "%s (%s) must be >= %s", fieldName, a, b );
    }

    public static void isGTE( long a, long b, String fieldName ) {
        isTrue( a >= b, "%s (%s) must be >= %s", fieldName, a, b );
    }

    public static void isGTE( float a, float b, String fieldName, float tolerance ) {
        isTrue( a-tolerance >= b, "%s (%s) must be >= %s", fieldName, a, b );
    }

    public static void isGTE( double a, double b, String fieldName, double tolerance ) {
        isTrue( a-tolerance >= b, "%s (%s) must be >= %s", fieldName, a, b );
    }

    public static <T extends Comparable<T>> void isGTE( T a, T b, String fieldName ) {
        isTrue( a.compareTo(b) == 0, "%s (%s) must be >= %s", fieldName, a, b );
    }


// isGTZero

    public static void isGTZero( byte a, String fieldName ) {
        isGTE( a, 1, fieldName );
    }

    public static void isGTZero( short a, String fieldName ) {
        isGTE( a, 1, fieldName );
    }

    public static void isGTZero( int a, String fieldName ) {
        isGTE( a, 1, fieldName );
    }

    public static void isGTZero( long a, String fieldName ) {
        isGTE( a, 1, fieldName );
    }

    public static void isGTZero( float a, String fieldName, float tolerance ) {
        isGTE( a, 0.0f, fieldName, tolerance );
    }

    public static void isGTZero( double a, String fieldName, double tolerance ) {
        isGTE( a, 0.0, fieldName, tolerance );
    }

    public static <T extends MathematicalNumber<T>> void isGTZero( T a, String fieldName ) {
        isGTE( a, a.zero(), fieldName );
    }



// isGTEZero

    public static void isGTEZero( byte a, String fieldName ) {
        isGTE( a, 0, fieldName );
    }

    public static void isGTEZero( short a, String fieldName ) {
        isGTE( a, 0, fieldName );
    }

    public static void isGTEZero( int a, String fieldName ) {
        isGTE( a, 0, fieldName );
    }

    public static void isGTEZero( long a, String fieldName ) {
        isGTE( a, 0, fieldName );
    }

    public static void isGTEZero( float a, String fieldName, float tolerance ) {
        isGTE( a, 0f, fieldName, tolerance );
    }

    public static void isGTEZero( double a, String fieldName, double tolerance ) {
        isGTE( a, 0.0, fieldName, tolerance );
    }

    public static <T extends MathematicalNumber<T>> void isGTEZero( T a, String fieldName ) {
        isGTE( a, a.zero(), fieldName );
    }


// isGT

    public static void isGT( byte a, byte b, String fieldName ) {
        isTrue( a > b, "%s (%s) must be > %s", fieldName, a, b );
    }

    public static void isGT( short a, short b, String fieldName ) {
        isTrue( a > b, "%s (%s) must be > %s", fieldName, a, b );
    }

    public static void isGT( int a, int b, String fieldName ) {
        isTrue( a > b, "%s (%s) must be > %s", fieldName, a, b );
    }

    public static <T extends Throwable> void isGT( int a, int b, String fieldName, Class<T> exceptionType ) {
        isTrue( a > b, exceptionType, "%s (%s) must be > %s", fieldName, a, b );
    }

    public static void isGT( long a, long b, String fieldName ) {
        isTrue( a > b, "%s (%s) must be > %s", fieldName, a, b );
    }

    public static void isGT( float a, float b, String fieldName, float tolerance ) {
        isTrue( a-tolerance > b, "%s (%s) must be > %s", fieldName, a, b );
    }

    public static void isGT( double a, double b, String fieldName, double tolerance ) {
        isTrue( a-tolerance > b, "%s (%s) must be > %s", fieldName, a, b );
    }

    public static <T extends Comparable<T>> void isGT( T a, T b, String fieldName ) {
        isTrue( a.compareTo(b) > 0, "%s (%s) must be > %s", fieldName, a, b );
    }

// isLT

    public static void isLT( byte a, byte b, String fieldName ) {
        isTrue( a < b, "%s (%s) must be < %s", fieldName, a, b );
    }

    public static void isLT( short a, short b, String fieldName ) {
        isTrue( a < b, "%s (%s) must be < %s", fieldName, a, b );
    }

    public static void isLT( int a, int b, String fieldName ) {
        isTrue( a < b, "%s (%s) must be < %s", fieldName, a, b );
    }

    public static void isLT( long a, long b, String fieldName ) {
        isTrue( a < b, "%s (%s) must be < %s", fieldName, a, b );
    }

    public static void isLT( float a, float b, String fieldName, float tolerance ) {
        isTrue( a-tolerance < b, "%s (%s) must be < %s", fieldName, a, b );
    }

    public static void isLT( double a, double b, String fieldName, double tolerance ) {
        isTrue( a-tolerance < b, "%s (%s) must be < %s", fieldName, a, b );
    }

    public static <T extends Comparable<T>> void isLT( T a, T b, String fieldName ) {
        isTrue( a.compareTo(b) < 0, "%s (%s) must be < %s", fieldName, a, b );
    }

// isLTE

    public static void isLTE( byte a, byte b, String fieldName ) {
        isTrue( a <= b, "%s (%s) must be <= %s", fieldName, a, b );
    }

    public static void isLTE( short a, short b, String fieldName ) {
        isTrue( a <= b, "%s (%s) must be <= %s", fieldName, a, b );
    }

    public static void isLTE( int a, int b, String fieldName ) {
        isTrue( a <= b, "%s (%s) must be <= %s", fieldName, a, b );
    }

    public static void isLTE( long a, long b, String fieldName ) {
        isTrue( a <= b, "%s (%s) must be <= %s", fieldName, a, b );
    }

    public static void isLTE( float a, float b, String fieldName, float tolerance ) {
        isTrue( a-tolerance <= b, "%s (%s) must be <= %s", fieldName, a, b );
    }

    public static void isLTE( double a, double b, String fieldName, double tolerance ) {
        isTrue( a-tolerance <= b, "%s (%s) must be <= %s", fieldName, a, b );
    }

    public static <T extends Comparable<T>> void isLTE( T a, T b, String fieldName ) {
        isTrue( a.compareTo(b) <= 0, "%s (%s) must be <= %s", fieldName, a, b );
    }

// isLTZero

    public static void isLTZero( byte a, String fieldName ) {
        isLT( a, 0, fieldName );
    }

    public static void isLTZero( short a, String fieldName ) {
        isLT( a, 0, fieldName );
    }

    public static void isLTZero( int a, String fieldName ) {
        isLT( a, 0, fieldName );
    }

    public static void isLTZero( long a, String fieldName ) {
        isLT( a, 0, fieldName );
    }

    public static void isLTZero( float a, String fieldName, float tolerance ) {
        isLT( a, 0.0f, fieldName, tolerance );
    }

    public static void isLTZero( double a, String fieldName, double tolerance ) {
        isLT( a, 0.0, fieldName, tolerance );
    }

    public static <T extends MathematicalNumber<T>> void isLTZero( T a, String fieldName ) {
        isLT( a, a.zero(), fieldName );
    }

// isLTEZero

    public static void isLTEZero( byte a, String fieldName ) {
        isLTE( a, 0, fieldName );
    }

    public static void isLTEZero( short a, String fieldName ) {
        isLTE( a, 0, fieldName );
    }

    public static void isLTEZero( int a, String fieldName ) {
        isLTE( a, 0, fieldName );
    }

    public static void isLTEZero( long a, String fieldName ) {
        isLTE( a, 0, fieldName );
    }

    public static void isLTEZero( float a, String fieldName, float tolerance ) {
        isLTE( a, 0.0f, fieldName, tolerance );
    }

    public static void isLTEZero( double a, String fieldName, double tolerance ) {
        isLTE( a, 0.0, fieldName, tolerance );
    }

    public static <T extends MathematicalNumber<T>> void isLTEZero( T a, String fieldName ) {
        isLTE( a, a.zero(), fieldName );
    }



/////////

    public static void isMultipleOf2( int v, String fieldName ) {
        if ( !MathUtils.isPowerOf2( v ) ) {
            throwException( "%s (%s) must be a power of 2", fieldName, v);
        }
    }

    public static void notBlank( String chars, String fieldName ) {
        notBlank( chars, fieldName, "%s must not be blank (was %s)" );
    }

    public static void notBlank( String chars, String fieldName, String description ) {
        if ( StringUtils.isBlank(chars) ) {
            throwException( description, fieldName, chars );
        }
    }

    public static void notEmpty( CharSequence chars, String fieldName ) {
        notEmpty( chars, fieldName, "%s must not be empty (was %s)" );
    }

    public static void notEmpty( CharSequence chars, String fieldName, String description ) {
        if ( chars == null || chars.length() == 0 ) {
            throwException( description, fieldName, chars );
        }
    }

    public static void notNull( Object o, String fieldName ) {
        if ( o == null ) {
            throwException( "%s must not be %s", fieldName, o );
        }
    }

    public static void notNull( Object o, String fieldName, String description ) {
        if ( o == null ) {
            throwException( "%s must not be %s: %s", fieldName, o, description );
        }
    }

    public static void notNullState( Object o, String fieldName, String description ) {
        if ( o == null ) {
            throwIllegalStateException( "%s must not be %s: %s", fieldName, o, description );
        }
    }

    public static void isFalseState( boolean flag, String msg, Object...args ) {
        if ( flag ) {
            throwIllegalStateException( msg, args );
        }
    }


    public static void isNull( Object o, String fieldName ) {
        if ( o != null ) {
            throwException( "%s must be null but was %s", fieldName, o );
        }
    }

    public static void isNull( Object o, String fieldName, String description ) {
        if ( o != null ) {
            throwException( "%s must be null but was %s: %s", fieldName, o, description );
        }
    }

    public static void isNullState( Object o, String fieldName, String description ) {
        if ( o != null ) {
            throwIllegalStateException( "%s must be null but was %s: %s", fieldName, o, description );
        }
    }


    public static <T extends Throwable> void isTrue( boolean condition, String msg, Object...values ) {
        isTrue( condition, IllegalArgumentException.class, msg, values );
    }

    public static <T extends Throwable> void isTrue( boolean condition, Class<T> exceptionType, String msg, Object...values ) {
        if ( !condition ) {
            throwException( exceptionType, msg, values );
        }
    }

    public static void isFalse( boolean condition, String msg, Object...values ) {
        if ( condition ) {
            throwException( msg, values );
        }
    }

    public static void inclusiveBetween( int minInc, int v, int maxInc, String fieldName ) {
        if ( v < minInc || v > maxInc ) {
            throwException( "%s is not within the specified bounds of %s <= %s <= %s", fieldName, minInc, v, maxInc );
        }
    }

    public static void noNullElements( Object[] array, String fieldName ) {
        if ( array == null ) {
            throwException( "%s is not allowed to be null", fieldName );
        }

        for ( int i=0; i<array.length; i++ ) {
            Object e = array[i];

            notNull( e, String.format("%s[%s]",fieldName,i) );
        }
    }


    public static void isTrueState( boolean flag, String msg, Object...msgArgs ) {
        if ( !flag ) {
            throwIllegalStateException( msg, msgArgs );
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

//
//    public static void notNull(Object a) {
//        Validate.notNull( a, "arg was null" );
//    }
//
//    public static void notNull(Object a, String msg) {
//        if ( a == null ) {
//            throw new NullArgumentException(msg);
//        }
//    }
//
//    public static void isNull(Object a, String msg) {
//        isTrue( a == null, msg );
//    }
//
//    public static void isBetween(int v, int minInc, int maxInc, String varName) {
//        if ( v < minInc || v > maxInc ) {
//            throw new IllegalArgumentException( varName + " must be between " + minInc + " and " + maxInc + ", its actual value is " + v );
//        }
//    }

// isEqualTo

    public static void isEqualTo( byte a, byte b, String fieldName ) {
        isTrue( a == b, "%s (%s) must be == %s", fieldName, a, b );
    }

    public static void isEqualTo( short a, short b, String fieldName ) {
        isTrue( a == b, "%s (%s) must be == %s", fieldName, a, b );
    }

    public static void isEqualTo( int a, int b, String fieldName ) {
        isTrue( a == b, "%s (%s) must be == %s", fieldName, a, b );
    }

    public static void isEqualTo( long a, long b, String fieldName ) {
        isTrue( a == b, "%s (%s) must be == %s", fieldName, a, b );
    }

    public static void isEqualTo( float a, float b, String fieldName, float tolerance ) {
        isTrue( Math.abs(a-b) <= tolerance, "%s (%s) must be == %s (within a tolerance of %s)", fieldName, a, b, tolerance );
    }

    public static void isEqualTo( double a, double b, String fieldName, double tolerance ) {
        isTrue( Math.abs(a-b) < tolerance, "%s (%s) must be == %s (within a tolerance of %s)", fieldName, a, b, tolerance );
    }

    public static <T extends Comparable<T>> void isEqualTo( T a, T b, String fieldName ) {
        isTrue( a.compareTo(b) == 0, "%s (%s) must be == %s", fieldName, a, b );
    }

    /**
     * Validates that minInc <= n < maxExc and throws IndexOutOfBoundsException if it fails.
     */
    public static void indexBounds( int minInc, int n, int maxExc, String fieldName ) {
        if ( !(minInc <= n && n < maxExc) ) {
            throwIndexOutOfBoundsException( "%s (%d) must be >= %d and < %d", fieldName, n, minInc, maxExc );
        }
    }

    /**
     * The specified range must equal or be within specified range.
     */
    public static void withinRange( int minInc, int minValue, int maxValue, int maxExc, String minValueName, String maxValueName ) {
        indexBounds( minInc, minValue, maxExc, minValueName );
        indexBounds( minInc, maxValue, maxExc+1, maxValueName );

        isTrue( minValue < maxValue, "%s (%s) < %s (%s)", minValueName,minValue, maxValueName,maxValue );
    }

}
