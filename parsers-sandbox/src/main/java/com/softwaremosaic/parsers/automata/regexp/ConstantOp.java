package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

/**
 * Append the transitions required to match a constant onto the automata.
 */
public class ConstantOp extends AutomataOp {

    private String          constant;
    private CaseSensitivity caseSensitivity;


    public ConstantOp( String constant, CaseSensitivity caseSensitivity ) {
        this.constant        = constant;
        this.caseSensitivity = caseSensitivity;
    }

    public Nodes appendTo( String label, Node startNode ) {
        int max = constant.length();

        Nodes n = new Nodes(startNode);
        for ( int i=0; i<max; i++ ) {
            char c = constant.charAt(i);

            if ( caseSensitivity.ignoreCase() ) {
                char lc = Character.toLowerCase(c);
                char uc = Character.toUpperCase(c);

                Node dest = new Node(label);
                n.appendEdge(lc, dest);
                n.appendEdge(uc, dest);

                n = new Nodes(dest);
            } else {
                n = n.appendCharacter( label, c );
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
