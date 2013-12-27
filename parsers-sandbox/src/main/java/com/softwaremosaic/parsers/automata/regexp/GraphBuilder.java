package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

/**
 * Grow an automata in a defined way.  Appends a series of transitions to
 * a specified starting node.  For example, compose multiple ops together
 * to represent a regular expression that can be appended to any graph on
 * demand.
 */
public abstract class GraphBuilder<T extends Comparable<T>> {

    public static enum CaseSensitivity {
        CaseSensitive, CaseInsensitive;

        public boolean ignoreCase() {
            return this == CaseInsensitive;
        }
    }

    /**
     * Append this op to the specified node.
     *
     * @return the last nodes appended to the graph
     */
    public abstract Nodes<T> appendTo( Node<T> startNode );


    public Nodes<T> appendTo( Nodes<T> startNodes ) {
        Nodes endNodes = new Nodes();

        for ( Node n : startNodes ) {
            endNodes.addAll(this.appendTo(n));
        }

        return endNodes;
    }

}
