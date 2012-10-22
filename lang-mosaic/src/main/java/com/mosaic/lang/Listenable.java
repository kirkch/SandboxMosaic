package com.mosaic.lang;

/**
 *
 */
public interface Listenable<L> {
    public void addListener( L listener );
    public void removeListener( L listener );
    
    public void removeAllListeners();
}
