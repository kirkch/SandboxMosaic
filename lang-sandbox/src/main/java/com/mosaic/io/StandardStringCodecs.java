package com.mosaic.io;

import com.mosaic.lang.Failure;
import com.mosaic.lang.functional.Try;
import com.mosaic.lang.functional.TryNow;
import com.mosaic.utils.SetUtils;
import com.mosaic.utils.StringUtils;

import java.util.Set;


/**
 *
 */
public class StandardStringCodecs {

    public static StringCodec<String> NO_OP_CODEC = new StringCodec<String>() {
        public String encode( String v ) {
            return v;
        }

        public String decode( String v ) {
            return v;
        }
    };

    public static StringCodec<Boolean> BOOLEAN_CODEC = new StringCodec<Boolean>() {
        private final Set<String> TRUE_VALUES = SetUtils.asImmutableSet("true", "t", "y", "yes","on","1");

        public String encode( Boolean v ) {
            return v ? "true" : "false";
        }

        public Boolean decode( String v ) {
            return !StringUtils.isBlank(v) && TRUE_VALUES.contains( v.toLowerCase() );
        }
    };

    public static StringCodec<Integer> INTEGER_CODEC = new StringCodec<Integer>() {
        public String encode( Integer v ) {
            return Integer.toString( v );
        }

        public Integer decode( String v ) {
            return Integer.parseInt(v);
        }

        public Try<Integer> tryDecode( String v ) {
            try {
                return TryNow.successful( decode( v ) );
            } catch ( NumberFormatException ex ) {
                return TryNow.failed( new Failure(this.getClass(), "'" + v + "' is not a valid number") );
            }
        }
    };

    public static StringCodec<Long> LONG_CODEC = new StringCodec<Long>() {
        public String encode( Long v ) {
            return Long.toString( v );
        }

        public Long decode( String v ) {
            return Long.parseLong(v);
        }
    };

    public static StringCodec<Float> FLOAT_CODEC = new StringCodec<Float>() {
        public String encode( Float v ) {
            return Float.toString(v);
        }

        public Float decode( String v ) {
            return Float.parseFloat(v);
        }

        public Try<Float> tryDecode( String v ) {
            try {
                return TryNow.successful( decode(v) );
            } catch ( NumberFormatException ex ) {
                return TryNow.failed( new Failure(this.getClass(), "'" + v + "' is not a valid number") );
            }
        }
    };

    public static StringCodec<Double> DOUBLE_CODEC = new StringCodec<Double>() {
        public String encode( Double v ) {
            return Double.toString(v);
        }

        public Double decode( String v ) {
            return Double.parseDouble( v );
        }

        public Try<Double> tryDecode( String v ) {
            try {
                return TryNow.successful( decode( v ) );
            } catch ( NumberFormatException ex ) {
                return TryNow.failed( new Failure(this.getClass(), "'" + v + "' is not a valid number") );
            }
        }
    };

}
