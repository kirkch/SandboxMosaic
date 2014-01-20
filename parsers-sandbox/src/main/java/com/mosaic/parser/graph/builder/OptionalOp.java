package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OptionalOp extends TrieBuilderOp {

    private TrieBuilderOp opToRepeat;


    /**
     *
     * @param op the op to repeat
     */
    public OptionalOp( TrieBuilderOp op ) {
        opToRepeat = op;
    }



    public Nodes appendTo( final Node startNode ) {
        final Nodes endNodes = opToRepeat.appendTo( startNode );

        endNodes.add( startNode );

        return endNodes;
    }


    public String toString() {
        return "(" + opToRepeat.toString() + ")?";
    }

}
