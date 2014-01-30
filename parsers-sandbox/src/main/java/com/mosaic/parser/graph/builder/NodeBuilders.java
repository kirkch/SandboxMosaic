package com.mosaic.parser.graph.builder;

import com.mosaic.lang.CaseSensitivity;
import com.mosaic.lang.CharacterPredicate;


/**
 *
 */
@SuppressWarnings("unchecked")
public class NodeBuilders {

    public static NodeBuilder and( Iterable<NodeBuilder> childOps ) {
        return new AndOp( childOps );
    }

    public static NodeBuilder and( NodeBuilder...childOps ) {
        return new AndOp( childOps );
    }

    public static NodeBuilder or( Iterable<NodeBuilder> childOps ) {
        return new OrOp( childOps );
    }

    public static NodeBuilder or( NodeBuilder...childOps ) {
        return new OrOp( childOps );
    }

    public static NodeBuilder oneOrMore( NodeBuilder childOp ) {
        return new OneOrMoreOp( childOp );
    }

    public static NodeBuilder zeroOrMore( NodeBuilder childOp ) {
        return new ZeroOrMoreOp( childOp );
    }

    public static NodeBuilder optional( NodeBuilder childOp ) {
        return new OptionalOp( childOp );
    }

    public static NodeBuilder predicate( CharacterPredicate predicate ) {
        return new PredicateOp( predicate );
    }

    public static NodeBuilder regexp( String regexp ) {
        return new NodeBuilderFactory().parse( regexp );
    }

    public static NodeBuilder constant( String str, CaseSensitivity caseSensitivity ) {
        return new StringOp( str, caseSensitivity );
    }

    public static NodeBuilder noOp() {
        return NoOp.INSTANCE;
    }
}
