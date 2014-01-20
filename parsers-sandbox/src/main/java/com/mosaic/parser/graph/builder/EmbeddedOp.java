package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;

/**
 *
 */
@SuppressWarnings("unchecked")
public class EmbeddedOp extends TrieBuilderOp {

    private String        refName;
    private TrieBuilderOp op;


    public EmbeddedOp( String refName, TrieBuilderOp op ) {
        this.refName = refName;
        this.op      = op;
    }



    public Nodes appendTo( final Node startNode ) {
        return op.appendTo( startNode );
    }


    public String toString() {
        return "$"+refName;
    }
}
