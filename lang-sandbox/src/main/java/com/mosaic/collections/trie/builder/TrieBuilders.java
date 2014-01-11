package com.mosaic.collections.trie.builder;

import com.mosaic.lang.CaseSensitivity;
import com.mosaic.lang.CharacterPredicate;

/**
 *
 */
@SuppressWarnings("unchecked")
public class TrieBuilders {

    public static <T> TrieBuilderOp<T> and( Iterable<TrieBuilderOp<T>> childOps ) {
        return new AndOp( childOps );
    }

    public static <T> TrieBuilderOp<T> and( TrieBuilderOp<T>...childOps ) {
        return new AndOp( childOps );
    }

    public static <T> TrieBuilderOp<T> or( Iterable<TrieBuilderOp<T>> childOps ) {
        return new OrOp( childOps );
    }

    public static <T> TrieBuilderOp<T> or( TrieBuilderOp<T>...childOps ) {
        return new OrOp( childOps );
    }

    public static <T> TrieBuilderOp<T> oneOrMore( TrieBuilderOp<T> childOp ) {
        return new OneOrMoreOp( childOp );
    }

    public static <T> TrieBuilderOp<T> zeroOrMore( TrieBuilderOp<T> childOp ) {
        return new ZeroOrMoreOp( childOp );
    }

    public static <T> TrieBuilderOp<T> optional( TrieBuilderOp<T> childOp ) {
        return new OptionalOp( childOp );
    }

    public static <T> TrieBuilderOp<T> predicate( CharacterPredicate predicate ) {
        return new PredicateOp( predicate );
    }

    public static <T> TrieBuilderOp<T> regexp( String regexp ) {
        return new RegexpParser().parse( regexp );
    }

    public static <T> TrieBuilderOp<T> constant( String str, CaseSensitivity caseSensitivity ) {
        return new StringOp( str, caseSensitivity );
    }

}
