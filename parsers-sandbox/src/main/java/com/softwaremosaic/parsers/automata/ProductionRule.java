package com.softwaremosaic.parsers.automata;

import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.reflect.MethodRef;

import java.util.List;


/**
 * Describes how to map a stream of inputs to an output.
 */
@SuppressWarnings("unchecked")
public class ProductionRule<I extends Comparable<I>, O extends Comparable<O>> {

    private Node<I>              startingNode;
    private Function1<I,I>       prefilter   = Function1.PASSTHROUGH;
    private Function1<List<I>,O> postProcess = Function1.PASSTHROUGH;

    private MethodRef listenerCallback;


    public ProductionRule( Node<I> startingNode ) {
        this.startingNode = startingNode;
    }

    public Node<I> getStartingNode() {
        return startingNode;
    }

    public void setStartingNode( Node<I> startingNode ) {
        this.startingNode = startingNode;
    }

    public void setPrefilter( Function1<I, I> prefilter ) {
        this.prefilter = prefilter;
    }

    public void setListenerCallback( MethodRef listenerCallback ) {
        this.listenerCallback = listenerCallback;
    }

}
