package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OptionalOp<T> extends TrieBuilderOp<T> {

    private TrieBuilderOp<T> opToRepeat;


    /**
     *
     * @param op the op to repeat
     */
    public OptionalOp( TrieBuilderOp<T> op ) {
        opToRepeat = op;
    }



    public Nodes<T> appendTo( final Node<T> startNode ) {
        final Nodes endNodes = opToRepeat.appendTo( startNode );

        endNodes.add( startNode );

        return endNodes;
    }


    public String toString() {
        return "(" + opToRepeat.toString() + ")?";
    }

}
