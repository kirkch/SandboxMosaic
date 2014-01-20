package com.mosaic.parser.graph.builder;


import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;


/**
 * Grow a Trie in a defined way.  Appends a series of transitions to
 * a specified starting node.  For example, compose multiple ops together
 * to represent a regular expression that can be appended to any graph on
 * demand.
 */
@SuppressWarnings("unchecked")
public abstract class TrieBuilderOp {

    /**
     * Append this op to the specified node.
     *
     * @return the last nodes appended to the graph
     */
    public abstract Nodes appendTo( Node startNode );


    public Nodes appendTo( Nodes startNodes ) {
        Nodes endNodes = new Nodes();

        for ( Node n : startNodes ) {
            endNodes.addAll(this.appendTo(n));
        }

        return endNodes;
    }

    /**
     * Override this method to enable/disable case sensitivity as appropriate.
     */
    public void insensitive( boolean b ) {

    }

}
