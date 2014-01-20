package com.mosaic.parser.graph;

import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.CharacterPredicates;
import com.mosaic.lang.functional.Function1;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unchecked")
public class Nodes<T> extends AbstractList<Node<T>> {

    private List<Node<T>> nodes;


    public Nodes() {
        this( new ArrayList() );
    }

    public Nodes( List<Node<T>> nodes ) {
        this.nodes = nodes;
    }

    public Nodes( Node newNode ) {
        this.nodes = new ArrayList();
        
        this.nodes.add( newNode );
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



    public Nodes<T> fetch( char c ) {
        List<Node<T>> endNodes = new ArrayList();

        for ( Node<T> n : nodes ) {
            n.fetchInto( endNodes, c );
        }

        return new Nodes( endNodes );
    }

//    public Nodes walk( T...path ) {
//        return walk( Arrays.asList( path ) );
//    }

//    public Nodes walk( Iterable <T> path ) {
//        List<CharacterNode<T>> endNodes = new ArrayList();
//
//        for ( CharacterNode<T> n : nodes ) {
//            endNodes.addAll( n.walk( path ) );
//        }
//
//        return new Nodes( endNodes );
//    }

    public Nodes<T> append( char c ) {
        return append( CharacterPredicates.constant( c ) );
    }

    public Nodes<T> append( CharacterPredicate predicate ) {
        List<Node<T>> endNodes = new ArrayList();

        for ( Node<T> n : nodes ) {
            endNodes.addAll( n.append(predicate) );
        }

        return new Nodes( endNodes );
    }

    public void append( CharacterPredicate predicate, Node<T> next ) {
        append( predicate, next );
    }

    public void append( CharacterPredicate predicate, Nodes<T> next ) {
        for ( Node<T> n : nodes ) {
            n.append( predicate, next );
        }
    }

    public void setPayloads( T payload ) {
        for ( Node<T> n : nodes ) {
            n.setPayload( payload );
        }
    }

    public void mapPayloads( Function1<T,T> mappingFunction) {
        for ( Node<T> n : nodes ) {
            T newPayload = mappingFunction.invoke( n.getPayload() );

            n.setPayload( newPayload );
        }
    }

    public void isEndNode( boolean isEndNode ) {
        for ( Node<T> n : nodes ) {
            n.isEndNode( isEndNode );
        }
    }

    public boolean hasContents() {
        return !isEmpty();
    }

//    /**
//     * Returns all of the nodes that can be transitioned to from this set of nodes.
//     */
//    public CharacterNodes<T> getOutNodes() {
//        List<CharacterNode<T>> outNodes = new ArrayList();
//
//        for ( CharacterNode<T> n : nodes ) {
//            outNodes.addAll( n.getOutNodes() );
//        }
//
//        return new Nodes( outNodes );
//    }

}
