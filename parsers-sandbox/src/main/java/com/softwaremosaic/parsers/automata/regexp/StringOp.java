package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;
import com.softwaremosaic.parsers.automata.ObjectNode;

/**
 * Append the transitions required to match a constant onto the automata.
 */
@SuppressWarnings("unchecked")
public class StringOp extends GraphBuilder<Character> {

    private String          constant;
    private CaseSensitivity caseSensitivity;


    public StringOp( String constant, CaseSensitivity caseSensitivity ) {
        this.constant        = constant;
        this.caseSensitivity = caseSensitivity;
    }

    public Nodes<Character> appendTo( Node<Character> startNode ) {
        int max = constant.length();

        Nodes n = new Nodes(startNode);
        for ( int i=0; i<max; i++ ) {
            char c = constant.charAt(i);

            if ( caseSensitivity.ignoreCase() ) {
                char lc = Character.toLowerCase(c);
                char uc = Character.toUpperCase(c);

                Node<Character> dest = new ObjectNode();  // todo use CharacterNode
                n.append(lc, dest);
                n.append(uc, dest);

                n = new Nodes(dest);
            } else {
                n = n.append( c );
            }
        }

        return n;
    }

    public String toString() {
        if ( caseSensitivity.ignoreCase() ) {
            return "~" + constant.toLowerCase();
        } else {
            return constant;
        }
    }

    public boolean isCaseSensitive() {
        return !caseSensitivity.ignoreCase();
    }

    public String getConstant() {
        return constant;
    }

}
