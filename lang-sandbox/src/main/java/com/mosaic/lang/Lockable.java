package com.mosaic.lang;

import com.mosaic.lang.reflect.ReflectionException;

import java.util.Map;

/**
 *
 */
public class Lockable<T extends Lockable<T>> implements IsLockable<T> {
    public static <T extends IsLockable> void lockAll( Iterable<T> collection ) {
        for ( T v : collection ) {
            v.lock();
        }
    }

    public static <T extends IsLockable> void lockNbl( T o ) {
        if ( o != null ) {
            o.lock();
        }
    }

    public static <T extends IsLockable> void lockAll( Map<?,T> map) {
        for ( T v : map.values() ) {
            v.lock();
        }
    }

    public static void throwIfUnlocked( IsLockable o ) {
        if ( o.isUnlocked() ) {
            throw new IllegalStateException( o + " is unlocked" );
        }
    }

    public static void throwIfLocked( IsLockable o ) {
        if ( o.isLocked() ) {
            throw new IllegalStateException( o + " is locked" );
        }
    }


    protected boolean isLocked;

    @Override
    public IsLockable<T> lock() {
        if ( !isLocked ) {
            this.isLocked = true;
            onLock();
        }

        return this;
    }

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public boolean isUnlocked() {
        return !isLocked;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public T unlock() {
        try {
            T copy = (T) this.clone();

            copy.isLocked = false;
            copy.onUnlock();

            return copy;
        } catch ( CloneNotSupportedException e ) {
            throw ReflectionException.recast( e );
        }
    }

    @Override
    public boolean isUnlockable() {
        return this instanceof Cloneable;
    }

    protected void onLock() {}
    protected void onUnlock() {}



    protected void throwIfLocked() {
        throwIfLocked( this );
    }

    protected void throwIfUnlocked() {
        throwIfUnlocked( this );
    }
}
