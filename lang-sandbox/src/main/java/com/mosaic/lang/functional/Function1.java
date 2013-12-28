package com.mosaic.lang.functional;

/**
 *
 */
public interface Function1<A1,R> {

    public static Function1 NO_OP = new Function1() {
        public Object invoke( Object arg ) {
            return null;
        }
    };

    public static Function1 PASSTHROUGH = new Function1() {
        public Object invoke( Object arg ) {
            return arg;
        }
    };


    public R invoke( A1 arg );
}
