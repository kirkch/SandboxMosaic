package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;

/**
 *
 */
@SuppressWarnings("unchecked")
public class EmbeddedOp<T> extends TrieBuilderOp<T> {

    private String           refName;
    private TrieBuilderOp<T> op;


    public EmbeddedOp( String refName, TrieBuilderOp<T> op ) {
        this.refName = refName;
        this.op      = op;
    }



    public Nodes<T> appendTo( final Node<T> startNode ) {
        return op.appendTo( startNode );
    }


    public String toString() {
        return "$"+refName;
    }
}
