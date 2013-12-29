package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OptionalOp <T extends Comparable<T>> extends GraphBuilder<T> {

    private GraphBuilder<T> opToRepeat;

    /**
     *
     * @param op the op to repeat
     */
    public OptionalOp( GraphBuilder<T> op ) {
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
