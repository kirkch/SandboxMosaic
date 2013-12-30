package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.lang.functional.Function1;
import com.mosaic.utils.ListUtils;
import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.automata.Labels;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

import java.util.List;

import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseInsensitive;
import static com.softwaremosaic.parsers.automata.regexp.GraphBuilder.CaseSensitivity.CaseSensitive;
import static com.softwaremosaic.parsers.automata.regexp.RegExpCharacterUtils.escape;

/**
 * Append the transitions required to match a constant onto the automata.
 */
@SuppressWarnings("unchecked")
public class StringOp extends GraphBuilder<Character> {

    private String                 constant;
    private CaseSensitivity        caseSensitivity;

    private List<Label<Character>> labels;


    public StringOp( String constant, final CaseSensitivity caseSensitivity ) {
        this.constant = constant;
        this.caseSensitivity = caseSensitivity;

        this.labels = ListUtils.map( constant, new Function1<Character, Label<Character>>() {
            public Label<Character> invoke( Character c ) {
                return Labels.characterLabel( c, caseSensitivity );
            }
        });
    }

    public Nodes<Character> appendTo( Node<Character> startNode ) {
        Nodes n = new Nodes(startNode);
        for ( Label<Character> nextLabel : labels ) {
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
