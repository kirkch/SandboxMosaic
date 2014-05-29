package com.mosaic.lang.functional;

/**
 *
 */
public abstract class LongFunction1<T> {

    public static final LongFunction1<String> DEFAULT_LONG_FORMATTER = new LongFunction1<String>() {
        public String invoke( long v ) {
            return Long.toString(v);
        }
    };


    public abstract T invoke( long v );


    public Function1<Long,T> toFunction1() {
        return new Function1<Long, T>() {
            public T invoke( Long arg ) {
                return arg == null ? null : LongFunction1.this.invoke( arg.intValue() );
            }
        };
    }
}
