package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;
import com.mosaic.lang.CharacterPredicate;

/**
 *
 */
public class PredicateOp<T> extends TrieBuilder<T> {

    private CharacterPredicate predicate;


    public PredicateOp( CharacterPredicate predicate ) {
        this.predicate = predicate;
    }

    public CharacterNodes<T> appendTo( CharacterNode<T> startNode ) {
        return startNode.append( predicate );
    }

    public String toString() {
        return predicate.toString();
    }

    public CharacterPredicate getPredicate() {
        return predicate;
    }

}
