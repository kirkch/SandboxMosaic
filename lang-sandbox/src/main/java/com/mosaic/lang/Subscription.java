package com.mosaic.lang;


import com.mosaic.lang.functional.TryNow;
import com.mosaic.lang.functional.VoidFunction0;


/**
 * Represents a registration of some kind.  The subscription can be cancelled at any time, and
 * queried to see if the subscription is still valid.
 */
@ThreadSafe
public class Subscription {

    private boolean isActive = true;
    private VoidFunction0 cancelSubscriptionFunc;


    public Subscription() {
        this( VoidFunction0.NO_OP );
    }

    public Subscription( VoidFunction0 cancelSubscriptionFunc ) {
        this.cancelSubscriptionFunc = cancelSubscriptionFunc;
    }


    /**
     * Returns true if this subscription is currently still subscribed.  That is, cancel() has
     * not yet been called.
     */
    public synchronized boolean isActive() {
        return isActive;
    }

    /**
     * Cancels this subscription.  This operation may not be undone.
     */
    public synchronized void cancel() {
        if ( this.isActive ) {
            try {
                cancelSubscriptionFunc.invoke();
            } finally {
                isActive = false;
            }
        }
    }


    /**
     * Chains two subscriptions together.  isActive returns true when all of the chained
     * subscriptions return true and cancelling this subscription will cancel each of the child
     * subscriptions.
     */
    public Subscription and( Subscription b ) {
        if ( b == null ) {
            return this;
        }

        Subscription a = this;
        return new Subscription() {
            public boolean isActive() {
                return a.isActive() || b.isActive();
            }

            public void cancel() {
                TryNow.tryAll(a::cancel, b::cancel);
            }
        };
    }

}
