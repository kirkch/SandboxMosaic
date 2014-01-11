package com.mosaic.collections.trie.builder;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;
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



    public CharacterNodes<T> appendTo( final CharacterNode<T> startNode ) {
        final CharacterNodes endNodes = opToRepeat.appendTo( startNode );

        startNode.depthFirstPrefixTraversal(new VoidFunction2<ConsList<KV<Set<CharacterPredicate>,CharacterNode<T>>>, Boolean>() {
            public void invoke( ConsList<KV<Set<CharacterPredicate>, CharacterNode<T>>> path, Boolean isEndOfPath ) {
                CharacterNode<T> visiting = path.head().getValue();
                if ( isEndOfPath && endNodes.contains(visiting) ) {
                    for ( CharacterPredicate c : path.head().getKey() ) {
                        CharacterNode<T> sourceNode = path.tail().head().getValue();

                        sourceNode.replace( c, visiting, startNode );
                    }
                }
            }
        });


        return new CharacterNodes(startNode);
    }


    public String toString() {
        return "(" + opToRepeat.toString() + ")*";
    }

}
