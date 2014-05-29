package com.mosaic.lang.functional;

/**
 *
 */
public abstract class FloatFunction1<T> {

    public static final FloatFunction1<String> DEFAULT_FLOAT_FORMATTER = new FloatFunction1<String>() {
        public String invoke( float v ) {
            return Float.toString(v);
        }
    };




    public abstract T invoke( float v );


    public Function1<Float,T> toFunction1() {
        return new Function1<Float, T>() {
            public T invoke( Float arg ) {
                return arg == null ? null : FloatFunction1.this.invoke( arg.floatValue() );
            }
        };
    }
}
