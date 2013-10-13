package com.mosaic.lang;

/**
 * Lockable object pattern.  Objects that implement this interface start life
 * as being mutable allowing easy configuration.  However before sharing
 * the object, it can be locked.  Which prevents any further modifications
 * to the object.<p/>
 *
 * Requires every method that could mutate state to verify that the object
 * is currently unlocked before proceeding.
 */
public interface IsLockable<T extends IsLockable<T>> {

    public IsLockable<T> lock();
    public boolean isLocked();
    public boolean isUnlocked();

    /**
     * Returns true if the object supports being copied.
     *
     * @see #copy()
     */
    public boolean isCopyable();

    /**
     * Creates a copy of this object that is ready for modification.
     */
    public T copy();

}
