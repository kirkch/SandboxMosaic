package com.mosaic.push;

/**
 * A collection of processing nodes. The nodes know what to do and how, but not how they are connected together. The
 * processing graph is responsible for the life cycle of the processing node. From ensuring that it is available,
 * to passing signals along the connections within the graph like neurons firing within a brain.<p/>
 *
 * Keeping the connections between processing nodes separate from the nodes themselves makes it possible for different
 * implementations of processing graph to be created that offer different services and trade offs. Some may be lower
 * latency but use more memory, others may use multiple cpu cores while others may recover safely from machine crashes.
 * All without having to change the implementation of the problem being solved, only the container that holds the
 * processing units.
 */
public interface ProcessingGraph<I,O> extends Node<I,O> {

    public void registerNode( String nodeIdentifier, Node node );
    public void unregisterNode( String nodeIdentifier );

    public void dispatchSignalTo( String nodeIdentifier, Object signal );
    public void getCurrentSignalOf( String nodeIdentifier );


    public void registerNodeSignalCallback( String nodeIdentifier, NodeSignalCallback callback );
    public void unregisterNodeSignalCallback( NodeSignalCallback callback );

//    public void registerNodeLifecycleCallback( String nodeIdentifier, NodeLifecycleCallback callback );
//    public void unregisterNodeLifecycleCallback( NodeLifecycleCallback callback );
//
//    public void registerGraphLifecycleCallback( GraphLifecycleCallback callback );
//    public void unregisterGraphLifecycleCallback( GraphLifecycleCallback callback );



}
