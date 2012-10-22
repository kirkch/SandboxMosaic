package com.mosaic.lang;

import com.mosaic.utils.MathUtils;

/**
 *
 */
public class Validate {
    public static void isGTE( int a, int b, String fieldName ) {
        isTrue( a >= b, "%s (%s) must be >= %s", fieldName, a, b );
    }

    public static void isGTZero( int a, String fieldName ) {
        isGTE( a, 1, fieldName );
    }

    public static void isGTEZero( int a, String fieldName ) {
        isGTE( a, 0, fieldName );
    }

    public static void isGTE( long a, long b, String fieldName ) {
        isTrue( a >= b, "%s (%s) must be >= %s", fieldName, a, b );
    }

    public static void isGTZero( long a, String fieldName ) {
        isGTE( a, 1, fieldName );
    }

    public static void isGTEZero( long a, String fieldName ) {
        isGTE( a, 0, fieldName );
    }

    public static void isMultipleOf2( int v, String fieldName ) {
        if ( !MathUtils.isPowerOf2( v ) ) {
            throwException( "%s (%s) must be a power of 2", fieldName, v);
        }
    }

//    public static void notBlank( CharSequence chars, String fieldName ) {
//        if ( StringUtils.isBlank( chars )) {
//            throwException( "%s must not be %s", fieldName, chars );
//        }
//    }

    public static void notNull( Object o, String fieldName ) {
        if ( o == null ) {
            throwException( "%s must not be %s", fieldName, o );
        }
    }

    public static void isTrue( boolean condition, String msg, Object...values ) {
        if ( !condition ) {
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

    private static void throwException( String msg, Object...values ) {
        String formattedMessage = String.format( msg, formatValues(values) );

        throw new IllegalArgumentException( formattedMessage );
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

    public static void isEqualTo( int v1, int v2, String msg ) {
        if ( v1 != v2 ) {
            throw new IllegalArgumentException(String.format(msg, v1, v2));
        }
    }

    public static void isLT( int a, int b, String msg ) {
        if ( a >= b ) {
            throw new IllegalArgumentException(String.format(msg, a, b));
        }
    }
}
