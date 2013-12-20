package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;

/**
 * Grow an automata in a defined way.  Appends a series of transitions to
 * a specified starting node.  For example, compose multiple ops together
 * to represent a regular expression that can be appended to any graph on
 * demand.
 */
public abstract class AutomataOp {

    public static enum CaseSensitivity {
        CaseSensitive, CaseInsensitive;

        public boolean ignoreCase() {
            return this == CaseInsensitive;
        }
    }


    public void appendTo( Node startNode ) {
        appendTo( startNode.getLabel(), startNode );
    }

    public abstract void appendTo( String label, Node startNode );

}
