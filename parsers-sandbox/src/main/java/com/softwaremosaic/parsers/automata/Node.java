package com.softwaremosaic.parsers.automata;

import java.util.*;

/**
 * A Node within a Finite State Automata.  Transitions between nodes are made
 * by traversing characters.  This particular graph supports duplicate edges to
 * both the same node, and to different nodes.
 */
@SuppressWarnings("unchecked")
public class Node {

    private String label;

    private Map<Character,List<Node>> edges = new HashMap();


    public Node() {}

    public Node( String label ) {
        this.label = label;
    }


    /**
     * Retrieve a descriptive label for this node.  A Parser will probably
     * put the name of the BNF expression that this node belongs to in it to
     * aid debugging and error messages.
     */
    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    /**
     * Creates a new transition for the specified character.  If a transition
     * already exists for the character, then a new transition to a new node
     * will be created.  They one that already exists will not be reused.
     */
    public Node appendChar( char c ) {
        Node n = newNode();

        List<Node> nextNodes = edges.get(c);
        if ( nextNodes == null ) {
            nextNodes = new ArrayList();
        }

        nextNodes.add(n);
        edges.put( c, nextNodes );

        return n;
    }

    public Node appendConstant( String a ) {
        return appendChar( a.charAt(0) );
    }

    /**
     * Retrieves the nodes that can be transitioned to by consuming the specified
     * character.  If there are no edges, then an empty list will be returned.
     */
    public List<Node> walk( char c ) {
        List<Node> nextNodes = edges.get(c);
        if ( nextNodes == null ) {
            nextNodes = Collections.EMPTY_LIST;
        }

        return nextNodes;
    }

    /**
     * A terminal node is a leaf node.  Thus it has no out going connections.
     */
    public boolean isTerminal() {
        return edges.isEmpty();
    }


    private Node newNode() {
        return new Node(this.label);
    }

}
