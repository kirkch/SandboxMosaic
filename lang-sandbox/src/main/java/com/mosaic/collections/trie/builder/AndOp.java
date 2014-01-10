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
public class AndOp<T> extends TrieBuilder<T> {

    private List<TrieBuilder<T>> childOps;

    public AndOp( Iterable<TrieBuilder<T>> childOps ) {
        this.childOps = new ArrayList();

        for ( TrieBuilder<T> op : childOps ) {
            this.childOps.add( op );
        }
    }

    public AndOp( TrieBuilder<T>...childOps ) {
        this.childOps = Arrays.asList(childOps);
    }

    public CharacterNodes<T> appendTo( CharacterNode<T> startNode ) {
        CharacterNodes<T> pos = new CharacterNodes(startNode);

        for ( TrieBuilder op : childOps ) {
            pos = op.appendTo(pos);
        }

        return pos;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        for ( TrieBuilder<T> op : childOps ) {
            buf.append( op );
        }

        return buf.toString();
    }

}
