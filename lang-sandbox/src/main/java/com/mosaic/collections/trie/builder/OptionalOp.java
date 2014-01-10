package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OptionalOp <T> extends TrieBuilder<T> {

    private TrieBuilder<T> opToRepeat;


    /**
     *
     * @param op the op to repeat
     */
    public OptionalOp( TrieBuilder<T> op ) {
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
