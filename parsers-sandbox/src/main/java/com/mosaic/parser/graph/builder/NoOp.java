package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;

/**
 *
 */
public class NoOp extends NodeBuilder {

    public static NodeBuilder INSTANCE = new NoOp();

    private NoOp() {}


    protected Nodes doAppendTo( Node startNode ) {
        return new Nodes(startNode);
    }

    protected NodeBuilder and( NodeBuilder b ) {
        return b;
    }

    protected NodeBuilder or( NodeBuilder b ) {
        return b;
    }

}
