package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OptionalOp <T> extends TrieBuilderOp<T> {

    private TrieBuilderOp<T> opToRepeat;


    /**
     *
     * @param op the op to repeat
     */
    public OptionalOp( TrieBuilderOp<T> op ) {
        opToRepeat = op;
    }



    public CharacterNodes<T> appendTo( final CharacterNode<T> startNode ) {
        final CharacterNodes endNodes = opToRepeat.appendTo( startNode );

        endNodes.add( startNode );

        return endNodes;
    }


    public String toString() {
        return "(" + opToRepeat.toString() + ")?";
    }

}