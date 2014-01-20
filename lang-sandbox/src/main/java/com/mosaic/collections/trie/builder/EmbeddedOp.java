package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;

/**
 *
 */
@SuppressWarnings("unchecked")
public class EmbeddedOp<T> extends TrieBuilderOp<T> {

    private String           refName;
    private TrieBuilderOp<T> op;


    public EmbeddedOp( String refName, TrieBuilderOp<T> op ) {
        this.refName = refName;
        this.op      = op;
    }



    public CharacterNodes<T> appendTo( final CharacterNode<T> startNode ) {
        return op.appendTo( startNode );
    }


    public String toString() {
        return "$"+refName;
    }
}
