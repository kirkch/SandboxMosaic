package com.mosaic.lang;


/**
 * Represents a registration of some kind.  The subscription can be cancelled at any time, and
 * queried to see if the subscription is still valid.
 */
public interface Subscription {
    public boolean isActive();
    public void cancel();
}
