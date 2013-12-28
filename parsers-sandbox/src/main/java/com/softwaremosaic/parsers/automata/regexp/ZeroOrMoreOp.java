package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.lang.functional.VoidFunction2;
import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

import java.util.Set;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ZeroOrMoreOp<T extends Comparable<T>> extends GraphBuilder<T> {

    private GraphBuilder<T> opToRepeat;

    /**
     *
     * @param op the op to repeat
     */
    public ZeroOrMoreOp( GraphBuilder<T> op ) {
        opToRepeat = op;
    }



    public Nodes<T> appendTo( final Node<T> startNode ) {
        final Nodes endNodes = opToRepeat.appendTo( startNode );

        startNode.depthFirstPrefixTraversal(new VoidFunction2<ConsList<KV<Set<Label<T>>,Node<T>>>, Boolean>() {
            public void invoke( ConsList<KV<Set<Label<T>>, Node<T>>> path, Boolean isEndOfPath ) {
                Node<T> visiting = path.head().getValue();
                if ( isEndOfPath && endNodes.contains(visiting) ) {
                    for ( Label<T> c : path.head().getKey() ) {
                        Node<T> sourceNode = path.tail().head().getValue();

                        sourceNode.replace( c, visiting, startNode );
                    }
                }
            }
        });


        return new Nodes(startNode);
    }


    public String toString() {
        return "(" + opToRepeat.toString() + ")*";
    }

}
