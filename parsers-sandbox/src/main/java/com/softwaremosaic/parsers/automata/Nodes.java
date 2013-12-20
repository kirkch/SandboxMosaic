package com.softwaremosaic.parsers.automata;

import java.util.*;

/**
 * A wrapper class for multiple nodes.  Essentially the same as List&lt;Nodes>
 * except that it is more friendly to work with.
 */
@SuppressWarnings("unchecked")
public class Nodes extends AbstractList<Node> {
    public static final Nodes EMPTY = new Nodes(Collections.EMPTY_LIST);



    private List<Node> nodes;

    public Nodes( Node node ) {
        this( Arrays.asList(node) );
    }

    public Nodes( List<Node> nodes ) {
        this.nodes = nodes;
    }

    public Node get( int i ) {
        return nodes.get(i);
    }

    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public int size() {
        return nodes.size();
    }

    public Nodes walk( char c ) {
        List<Node> endNodes = new ArrayList();

        for ( Node n : nodes ) {
            endNodes.addAll( n.walk(c) );
        }

        return new Nodes(endNodes);
    }

    public Nodes walk( String path ) {
        List<Node> endNodes = new ArrayList();

        for ( Node n : nodes ) {
            endNodes.addAll( n.walk(path) );
        }

        return new Nodes(endNodes);
    }

    public Nodes skipWhiteSpace() {
        for ( Node n : nodes ) {
            n.skipWhiteSpace();
        }

        return this;
    }

    public Nodes appendCharacter( char c ) {
        List<Node> endNodes = new ArrayList();

        for ( Node n : nodes ) {
            endNodes.addAll( n.appendCharacter(c) );
        }

        return new Nodes(endNodes);
    }

    public Nodes appendCharacter( String label, char c ) {
        List<Node> endNodes = new ArrayList();

        for ( Node n : nodes ) {
            endNodes.addAll( n.appendCharacter(label, c) );
        }

        return new Nodes(endNodes);
    }

    /**
     * Returns all of the nodes that can be transitioned to from this set of nodes.
     */
    public Nodes getOutNodes() {
        List<Node> outNodes = new ArrayList();

        for ( Node n : nodes ) {
            outNodes.addAll( n.getOutNodes() );
        }

        return new Nodes(outNodes);
    }

    public void appendEdge( char c, Node next ) {
        for ( Node n : nodes ) {
            n.appendEdge( c, next );
        }
    }
}
