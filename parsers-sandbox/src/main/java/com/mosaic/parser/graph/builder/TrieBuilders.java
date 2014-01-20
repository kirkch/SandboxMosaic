package com.mosaic.parser.graph.builder;

import com.mosaic.lang.CaseSensitivity;
import com.mosaic.lang.CharacterPredicate;

/**
 *
 */
@SuppressWarnings("unchecked")
public class TrieBuilders {

    public static  TrieBuilderOp and( Iterable<TrieBuilderOp> childOps ) {
        return new AndOp( childOps );
    }

    public static  TrieBuilderOp and( TrieBuilderOp...childOps ) {
        return new AndOp( childOps );
    }

    public static  TrieBuilderOp or( Iterable<TrieBuilderOp> childOps ) {
        return new OrOp( childOps );
    }

    public static  TrieBuilderOp or( TrieBuilderOp...childOps ) {
        return new OrOp( childOps );
    }

    public static  TrieBuilderOp oneOrMore( TrieBuilderOp childOp ) {
        return new OneOrMoreOp( childOp );
    }

    public static  TrieBuilderOp zeroOrMore( TrieBuilderOp childOp ) {
        return new ZeroOrMoreOp( childOp );
    }

    public static  TrieBuilderOp optional( TrieBuilderOp childOp ) {
        return new OptionalOp( childOp );
    }

    public static  TrieBuilderOp predicate( CharacterPredicate predicate ) {
        return new PredicateOp( predicate );
    }

    public static  TrieBuilderOp regexp( String regexp ) {
        return new RegexpParser().parse( regexp );
    }

    public static  TrieBuilderOp constant( String str, CaseSensitivity caseSensitivity ) {
        return new StringOp( str, caseSensitivity );
    }

}
