package com.mosaic.lang;

import com.mosaic.lang.reflect.ReflectionException;

import java.util.Map;


/**
 * Convenience base class for implementing IsLockable.
 */
@SuppressWarnings("unchecked")
public class Lockable<T extends Lockable<T>> implements IsLockable<T> {

    /**
     * Lock every object within the specified collection.
     */
    public static <T extends IsLockable> void lockAll( Iterable<T> collection ) {
        for ( T v : collection ) {
            v.lock();
        }
    }

    /**
     * Conditionally lock the specified object, handling the null case gracefully.
     */
    public static <T extends IsLockable> void lockNbl( T o ) {
        if ( o != null ) {
            o.lock();
        }
    }

    /**
     * Lock every value in the specified map.
     */
    public static <T extends IsLockable> void lockAll( Map<?,T> map) {
        for ( T v : map.values() ) {
            v.lock();
        }
    }

    /**
     * Throw an exception if the specified object is mutable.
     */
    public static void throwIfUnlocked( IsLockable o ) {
        if ( o.isUnlocked() ) {
            throw new IllegalStateException( o + " is unlocked" );
        }
    }

    /**
     * Throw an exception if the specified object is immutable.
     */
    public static void throwIfLocked( IsLockable o ) {
        if ( o.isLocked() ) {
            throw new IllegalStateException( o + " is locked" );
        }
    }


    protected boolean isLocked;



    public T lock() {
        if ( !isLocked ) {
            this.isLocked = true;
            onLock();
        }

        return (T) this;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isUnlocked() {
        return !isLocked;
    }

    @SuppressWarnings({"unchecked"})
    public T copy() {
        try {
            T copy = (T) this.clone();

            copy.isLocked = false;
            copy.onUnlock();

            return copy;
        } catch ( CloneNotSupportedException e ) {
            throw ReflectionException.recast( e );
        }
    }

    public boolean isCopyable() {
        return this instanceof Cloneable;
    }

    protected void onLock() {}
    protected void onUnlock() {}



    protected void throwIfLocked() {
        throwIfLocked( this );
    }

    /**
     * Call from every mutating method.  This will prevent mutations when the
     * object has been locked.
     */
    protected void throwIfUnlocked() {
        throwIfUnlocked( this );
    }

}
