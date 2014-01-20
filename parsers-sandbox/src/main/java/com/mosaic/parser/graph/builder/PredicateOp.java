package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.lang.CharacterPredicate;

/**
 *
 */
public class PredicateOp extends TrieBuilderOp {

    private CharacterPredicate predicate;


    public PredicateOp( CharacterPredicate predicate ) {
        this.predicate = predicate;
    }

    public Nodes appendTo( Node startNode ) {
        return startNode.append( predicate );
    }

    public String toString() {
        return predicate.toString();
    }

    public CharacterPredicate getPredicate() {
        return predicate;
    }

}
