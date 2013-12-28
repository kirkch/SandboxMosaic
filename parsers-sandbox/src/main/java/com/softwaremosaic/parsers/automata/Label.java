package com.softwaremosaic.parsers.automata;

/**
 *
 */
public interface Label<T> extends Comparable<Label<T>> {

    public boolean matches( T input );

}
