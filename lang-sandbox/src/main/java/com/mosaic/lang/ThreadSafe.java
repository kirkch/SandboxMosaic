package com.mosaic.lang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;


/**
 *
 */
@Target({TYPE, METHOD, CONSTRUCTOR, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe {
    public String value() default "";
}
