package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OrOp<T> extends TrieBuilder<T> {

    private List<TrieBuilder<T>> childOps = new ArrayList();


    public OrOp( TrieBuilder<T>...childOps ) {
        Collections.addAll( this.childOps, childOps );
    }

    public void add( TrieBuilder<T> childOp ) {
        childOps.add( childOp );
    }

    public CharacterNodes<T> appendTo( CharacterNode<T> startNode ) {
        CharacterNodes<T> endNodes = new CharacterNodes();

        for ( TrieBuilder op : childOps ) {
            endNodes.addAll( op.appendTo(startNode) );
        }

        return endNodes;
    }

    public String toString() {
        return StringUtils.join( this.childOps, "|" );
    }

}
