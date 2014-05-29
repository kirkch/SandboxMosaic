package com.mosaic.lang.functional;

/**
 *
 */
public abstract class IntFunction1<T> {

    public static final IntFunction1<String> DEFAULT_INT_FORMATTER = new IntFunction1<String>() {
        public String invoke( int v ) {
            return Integer.toString(v);
        }
    };




    public abstract T invoke( int v );


    public Function1<Integer,T> toFunction1() {
        return new Function1<Integer, T>() {
            public T invoke( Integer arg ) {
                return arg == null ? null : IntFunction1.this.invoke( arg.intValue() );
            }
        };
    }
}
