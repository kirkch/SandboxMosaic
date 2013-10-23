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



    public static StringCodec<Boolean> BOOLEAN_CODEC = new StringCodec<Boolean>() {
        private final Set<String> TRUE_VALUES = SetUtils.asImmutableSet("true", "t", "y", "yes","on","1");

        public Try<String> encode( Boolean v ) {
            return TryNow.successful( v ? "true" : "false" );
        }

        public Try<Boolean> decode( String v ) {
            return TryNow.successful( !StringUtils.isBlank(v) && TRUE_VALUES.contains( v.toLowerCase() ) );
        }
    };

    public static StringCodec<Integer> INTEGER_CODEC = new StringCodec<Integer>() {
        public Try<String> encode( Integer v ) {
            return TryNow.successful(Integer.toString(v));
        }

        public Try<Integer> decode( String v ) {
            try {
                return TryNow.successful( Integer.parseInt(v) );
            } catch ( NumberFormatException ex ) {
                return TryNow.failed( new Failure(this.getClass(), "'" + v + "' is not a valid number") );
            }
        }
    };

    public static StringCodec<Long> LONG_CODEC = new StringCodec<Long>() {
        public Try<String> encode( Long v ) {
            return TryNow.successful(Long.toString(v));
        }

        public Try<Long> decode( String v ) {
            try {
                return TryNow.successful( Long.parseLong(v) );
            } catch ( NumberFormatException ex ) {
                return TryNow.failed( new Failure(this.getClass(), "'" + v + "' is not a valid number") );
            }
        }
    };

    public static StringCodec<Float> FLOAT_CODEC = new StringCodec<Float>() {
        public Try<String> encode( Float v ) {
            return TryNow.successful(Float.toString(v));
        }

        public Try<Float> decode( String v ) {
            try {
                return TryNow.successful( Float.parseFloat(v) );
            } catch ( NumberFormatException ex ) {
                return TryNow.failed( new Failure(this.getClass(), "'" + v + "' is not a valid number") );
            }
        }
    };

    public static StringCodec<Double> DOUBLE_CODEC = new StringCodec<Double>() {
        public Try<String> encode( Double v ) {
            return TryNow.successful(Double.toString(v));
        }

        public Try<Double> decode( String v ) {
            try {
                return TryNow.successful( Double.parseDouble(v) );
            } catch ( NumberFormatException ex ) {
                return TryNow.failed( new Failure(this.getClass(), "'" + v + "' is not a valid number") );
            }
        }
    };

}
