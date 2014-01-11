package com.mosaic.collections.trie.builder;


import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;


/**
 * Grow a Trie in a defined way.  Appends a series of transitions to
 * a specified starting node.  For example, compose multiple ops together
 * to represent a regular expression that can be appended to any graph on
 * demand.
 */
@SuppressWarnings("unchecked")
public abstract class TrieBuilderOp<T> {

    /**
     * Append this op to the specified node.
     *
     * @return the last nodes appended to the graph
     */
    public abstract CharacterNodes<T> appendTo( CharacterNode<T> startNode );


    public CharacterNodes<T> appendTo( CharacterNodes<T> startNodes ) {
        CharacterNodes<T> endNodes = new CharacterNodes();

        for ( CharacterNode<T> n : startNodes ) {
            endNodes.addAll(this.appendTo(n));
        }

        return endNodes;
    }

    /**
     * Override this method to enable/disable case sensitivity as appropriate.
     */
    public void insensitive( boolean b ) {

    }

}
