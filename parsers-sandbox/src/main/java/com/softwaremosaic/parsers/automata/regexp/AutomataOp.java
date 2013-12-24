package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

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

    /**
     * Append this op to the specified node.
     *
     * @return the end nodes of this operation.  For example if node 1 was that
     * start node, and this op created and linked to node 2 and then node to node
     * 3 and then stopped.  Then node 3 would be the result, and the intermediate
     * node 2 would be silently hidden within the graph.
     */
    public abstract Nodes appendTo( String label, Node startNode );


    public Nodes appendTo( String label, Nodes startNodes ) {
        Nodes endNodes = new Nodes();

        for ( Node n : startNodes ) {
            endNodes.addAll(  this.appendTo(label, n)  );
        }

        return endNodes;
    }

}
