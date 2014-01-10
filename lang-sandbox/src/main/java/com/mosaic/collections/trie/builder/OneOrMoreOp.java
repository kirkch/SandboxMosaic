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
public class OneOrMoreOp<T> extends TrieBuilder<T> {

    private TrieBuilder<T> opToRepeat;


    /**
     *
     * @param op the op to repeat
     */
    public OneOrMoreOp( TrieBuilder<T> op ) {
        opToRepeat = op;
    }



    public CharacterNodes<T> appendTo( CharacterNode<T> startNode ) {
        final CharacterNodes<T> afterFirstStepNodes = opToRepeat.appendTo( startNode );


        final CharacterNodes endNodes = opToRepeat.appendTo( afterFirstStepNodes );

        startNode.depthFirstPrefixTraversal(new VoidFunction2<ConsList<KV<Set<CharacterPredicate>,CharacterNode<T>>>, Boolean>() {
            public void invoke( ConsList<KV<Set<CharacterPredicate>, CharacterNode<T>>> path, Boolean isEndOfPath ) {
                CharacterNode<T> visiting = path.head().getValue();
                if ( isEndOfPath && endNodes.contains(visiting) ) {
                    for ( CharacterPredicate label : path.head().getKey() ) {
                        CharacterNode sourceNode = path.tail().head().getValue();

                        sourceNode.replace( label, visiting, afterFirstStepNodes );
                    }
                }
            }
        });


        return afterFirstStepNodes;
    }

    public String toString() {
        return "(" + opToRepeat.toString() + ")+";
    }

}
