package com.mosaic.lang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;


/**
 * A marker annotation for classes that are designed to never alter their state after the constructor
 * has completed.
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Immutable {
    public String value() default "";
}
