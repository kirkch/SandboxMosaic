package com.softwaremosaic.parsers.automata;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.reflect.MethodRef;


/**
 * Describes how to map a stream of inputs to an output.
 */
public class ProductionRule<I, T extends Comparable<T>> {

    private Node<T>        startingNode;
    private Function1<I,I> prefilter = Function1.PASSTHROUGH;

    private MethodRef listenerCallback;


    public ProductionRule( Node<T> startingNode ) {
        this.startingNode = startingNode;
    }

    public Node<T> getStartingNode() {
        return startingNode;
    }

    public void setStartingNode( Node<T> startingNode ) {
        this.startingNode = startingNode;
    }

    public void setPrefilter( Function1<I, I> prefilter ) {
        this.prefilter = prefilter;
    }

    public void setListenerCallback( MethodRef listenerCallback ) {
        this.listenerCallback = listenerCallback;
    }

}
