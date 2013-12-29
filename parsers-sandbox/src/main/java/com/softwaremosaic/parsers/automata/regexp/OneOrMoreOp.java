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
public class OneOrMoreOp<T extends Comparable<T>> extends GraphBuilder<T> {

    private GraphBuilder<T> opToRepeat;

    /**
     *
     * @param op the op to repeat
     */
    public OneOrMoreOp( GraphBuilder<T> op ) {
        opToRepeat = op;
    }



    public Nodes<T> appendTo( Node<T> startNode ) {
        final Nodes<T> afterFirstStepNodes = opToRepeat.appendTo( startNode );


        final Nodes endNodes = opToRepeat.appendTo( afterFirstStepNodes );

        startNode.depthFirstPrefixTraversal(new VoidFunction2<ConsList<KV<Set<Label<T>>,Node<T>>>, Boolean>() {
            public void invoke( ConsList<KV<Set<Label<T>>, Node<T>>> path, Boolean isEndOfPath ) {
                Node<T> visiting = path.head().getValue();
                if ( isEndOfPath && endNodes.contains(visiting) ) {
                    for ( Label<T> label : path.head().getKey() ) {
                        Node sourceNode = path.tail().head().getValue();

                        sourceNode.replace( label, visiting, afterFirstStepNodes );
                    }
                }
            }
        });


        return new Nodes(startNode);
    }

    public String toString() {
        return "(" + opToRepeat.toString() + ")+";
    }
}
