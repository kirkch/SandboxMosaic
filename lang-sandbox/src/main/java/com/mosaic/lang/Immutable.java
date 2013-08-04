package com.mosaic.lang;

/**
 * A marker interface for classes that are designed to never alter their state after the constructor
 * has completed. This indicating that the class is both thread safe, and capable of having any equivalent
 * instance swapped out for another instance. Replacing multiple equivalent copies of an object with a
 * single instance is the job of the GlobalInterner, and it is done to reduce the memory footprint of applications.
 */
public interface Immutable {
}
