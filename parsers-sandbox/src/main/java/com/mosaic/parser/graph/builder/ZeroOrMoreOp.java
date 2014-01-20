package com.mosaic.parser.graph.builder;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.functional.VoidFunction2;

import java.util.Set;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ZeroOrMoreOp<T> extends TrieBuilderOp<T> {

    private TrieBuilderOp<T> opToRepeat;

    
    /**
     *
     * @param op the op to repeat
     */
    public ZeroOrMoreOp( TrieBuilderOp<T> op ) {
        opToRepeat = op;
    }



    public Nodes<T> appendTo( final Node<T> startNode ) {
        final Nodes endNodes = opToRepeat.appendTo( startNode );

        startNode.depthFirstPrefixTraversal(new VoidFunction2<ConsList<KV<Set<CharacterPredicate>,Node<T>>>, Boolean>() {
            public void invoke( ConsList<KV<Set<CharacterPredicate>, Node<T>>> path, Boolean isEndOfPath ) {
                Node<T> visiting = path.head().getValue();
                if ( isEndOfPath && endNodes.contains(visiting) ) {
                    for ( CharacterPredicate c : path.head().getKey() ) {
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
