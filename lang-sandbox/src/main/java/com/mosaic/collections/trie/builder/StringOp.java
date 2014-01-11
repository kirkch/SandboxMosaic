package com.mosaic.collections.trie.builder;

import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;
import com.mosaic.lang.CaseSensitivity;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.CharacterPredicates;
import com.mosaic.lang.functional.Function1;
import com.mosaic.utils.ListUtils;

import java.util.List;

import static com.mosaic.collections.trie.builder.RegExpCharacterUtils.escape;
import static com.mosaic.lang.CaseSensitivity.CaseInsensitive;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;


/**
 * Append the transitions required to match a constant onto the automata.
 */
@SuppressWarnings("unchecked")
public class StringOp<T> extends TrieBuilderOp<T> {

    private String          constant;
    private CaseSensitivity caseSensitivity;

    private List<CharacterPredicate> perCharacterPredicates;


    public StringOp( String constant, final CaseSensitivity caseSensitivity ) {
        this.constant        = constant;
        this.caseSensitivity = caseSensitivity;

        this.perCharacterPredicates = ListUtils.map( constant, new Function1<Character, CharacterPredicate>() {
            public CharacterPredicate invoke( Character c ) {
                return CharacterPredicates.characterPredicate( c, caseSensitivity );
            }
        });
    }

    public CharacterNodes<T> appendTo( CharacterNode<T> startNode ) {
        CharacterNodes<T> n = new CharacterNodes(startNode);

        for ( CharacterPredicate nextLabel : perCharacterPredicates ) {
            n = n.append( nextLabel );
        }

        return n;
    }

    public String toString() {
        if ( caseSensitivity.ignoreCase() ) {
            return "~" + escape( constant.toLowerCase() );
        } else {
            return escape(constant);
        }
    }

    public boolean isCaseSensitive() {
        return !caseSensitivity.ignoreCase();
    }

    public String getConstant() {
        return constant;
    }

    public void insensitive( boolean b ) {
        caseSensitivity = b ? CaseInsensitive : CaseSensitive;
    }

}
