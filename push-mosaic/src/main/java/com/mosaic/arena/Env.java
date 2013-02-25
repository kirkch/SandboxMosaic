package com.mosaic.arena;

/**
 *
 */
public interface Env {

//    public void dispatchVoid( String nodeId, String methodName, Object...args );

    public <T> Future<T> fetchNode( String nodeId );

    public <T> Future<T> createNode( String nodeId, Class<T> interfaceClass, Class<T> implementationClass );

    public Future removeNode( String nodeId );


    // future ideas: node indexes? node relationships? groupings?
    // life cycle events
    // subscriptions

}
