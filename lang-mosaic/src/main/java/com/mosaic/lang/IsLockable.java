package com.mosaic.lang;

/**
 *
 */
public interface IsLockable<T extends IsLockable<T>> {
    public IsLockable<T> lock();
    public boolean isLocked();
    public boolean isUnlocked();

    public T unlock();
    public boolean isUnlockable();
}
