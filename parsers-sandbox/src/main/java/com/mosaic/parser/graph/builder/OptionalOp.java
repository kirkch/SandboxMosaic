package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OptionalOp extends NodeBuilder {

    private NodeBuilder opToRepeat;


    /**
     *
     * @param op the op to repeat
     */
    public OptionalOp( NodeBuilder op ) {
        opToRepeat = op;
    }



    protected  Nodes doAppendTo( final Node startNode ) {
        final Nodes endNodes = opToRepeat.appendTo( startNode );

        endNodes.add( startNode );

        return endNodes;
    }


    public String toString() {
        return "(" + opToRepeat.toString() + ")?";
    }

}
