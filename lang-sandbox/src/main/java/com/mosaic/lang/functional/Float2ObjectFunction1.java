package com.mosaic.lang.functional;

/**
 *
 */
public abstract class Float2ObjectFunction1<T> {

    public static final Float2ObjectFunction1<String> DEFAULT_FLOAT_FORMATTER = new Float2ObjectFunction1<String>() {
        public String invoke( float v ) {
            return Float.toString(v);
        }
    };




    public abstract T invoke( float v );


    public Function1<Float,T> toFunction1() {
        return new Function1<Float, T>() {
            public T invoke( Float arg ) {
                return arg == null ? null : Float2ObjectFunction1.this.invoke( arg.floatValue() );
            }
        };
    }
}
