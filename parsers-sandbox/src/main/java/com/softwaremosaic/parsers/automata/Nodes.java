package com.softwaremosaic.parsers.automata;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A wrapper class for multiple nodes.  Essentially the same as List&lt;Nodes>
 * except that it is more friendly to work with.
 */
@SuppressWarnings("unchecked")
public class Nodes<T extends Comparable<T>> extends AbstractList<Node<T>> {
    public static final Nodes EMPTY = new Nodes( Collections.EMPTY_LIST );


    private List<Node<T>> nodes;

    public Nodes() {
        this( new ArrayList() );
    }

    public Nodes( Node<T> node ) {
        this( Arrays.asList( node ) );
    }

    public Nodes( List<Node<T>> nodes ) {
        this.nodes = nodes;
    }

    public Node<T> get( int i ) {
        return nodes.get( i );
    }

    public Iterator<Node<T>> iterator() {
        return nodes.iterator();
    }

    public int size() {
        return nodes.size();
    }

    public void add( int index, Node<T> element ) {
        nodes.add( index, element );
    }



    public Nodes walk( T label ) {
        List<Node<T>> endNodes = new ArrayList();

        for ( Node<T> n : nodes ) {
            endNodes.addAll( n.walk(label) );
        }

        return new Nodes( endNodes );
    }

    public Nodes walk( T...path ) {
        return walk( Arrays.asList(path) );
    }

    public Nodes walk( Iterable <T> path ) {
        List<Node<T>> endNodes = new ArrayList();

        for ( Node<T> n : nodes ) {
            endNodes.addAll( n.walk( path ) );
        }

        return new Nodes( endNodes );
    }

    public Nodes append( T label ) {
        return append( Labels.singleValue(label) );
    }

    public Nodes append( Label<T> label ) {
        List<Node<T>> endNodes = new ArrayList();

        for ( Node<T> n : nodes ) {
            endNodes.addAll( n.append(label) );
        }

        return new Nodes( endNodes );
    }

    public void append( T label, Node<T> next ) {
        append( Labels.singleValue(label), next );
    }

    public void append( Label<T> label, Node<T> next ) {
        for ( Node<T> n : nodes ) {
            n.append( label, next );
        }
    }

    /**
     * Returns all of the nodes that can be transitioned to from this set of nodes.
     */
    public Nodes<T> getOutNodes() {
        List<Node<T>> outNodes = new ArrayList();

        for ( Node<T> n : nodes ) {
            outNodes.addAll( n.getOutNodes() );
        }

        return new Nodes( outNodes );
    }

    public void isValidEndNode( boolean flag ) {
        for ( Node n : this ) {
            n.isValidEndNode( flag );
        }
    }
}
