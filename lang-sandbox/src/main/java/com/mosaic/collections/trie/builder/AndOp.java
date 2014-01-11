package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 */
@SuppressWarnings("unchecked")
public class AndOp<T> extends TrieBuilderOp<T> {

    private List<TrieBuilderOp<T>> childOps;

    public AndOp( Iterable<TrieBuilderOp<T>> childOps ) {
        this.childOps = new ArrayList();

        for ( TrieBuilderOp<T> op : childOps ) {
            this.childOps.add( op );
        }
    }

    public AndOp( TrieBuilderOp<T>...childOps ) {
        this.childOps = Arrays.asList(childOps);
    }

    public CharacterNodes<T> appendTo( CharacterNode<T> startNode ) {
        CharacterNodes<T> pos = new CharacterNodes(startNode);

        for ( TrieBuilderOp op : childOps ) {
            pos = op.appendTo(pos);
        }

        return pos;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        for ( TrieBuilderOp<T> op : childOps ) {
            buf.append( op );
        }

        return buf.toString();
    }

}
