package com.mosaic.lang.functional;

/**
 *
 */
public interface VoidFunction0 {

    public static final VoidFunction0 NO_OP = () -> {};


    public void invoke();


    public default VoidFunction0 and( VoidFunction0 b ) {
        if ( b == null ) {
            return this;
        }

        VoidFunction0 a = this;

        return new VoidFunction0() {
            public void invoke() {
                a.invoke();
                b.invoke();
            }
        };
    }
    
}
