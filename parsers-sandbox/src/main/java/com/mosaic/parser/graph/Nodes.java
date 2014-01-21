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
public class Nodes extends AbstractList<Node> {

    private List<Node> nodes;


    public Nodes() {
        this( new ArrayList() );
    }

    public Nodes( List<Node> nodes ) {
        this.nodes = nodes;
    }

    public Nodes( Node newNode ) {
        this.nodes = new ArrayList();
        
        this.nodes.add( newNode );
    }

    public Node get( int i ) {
        return nodes.get( i );
    }

    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public int size() {
        return nodes.size();
    }

    public void add( int index, Node element ) {
        nodes.add( index, element );
    }



    public Nodes fetch( char c ) {
        List<Node> endNodes = new ArrayList();

        for ( Node n : nodes ) {
            n.fetchInto( endNodes, c );
        }

        return new Nodes( endNodes );
    }

//    public Nodes walk( T...path ) {
//        return walk( Arrays.asList( path ) );
//    }

//    public Nodes walk( Iterable <T> path ) {
//        List<CharacterNode> endNodes = new ArrayList();
//
//        for ( CharacterNode n : nodes ) {
//            endNodes.addAll( n.walk( path ) );
//        }
//
//        return new Nodes( endNodes );
//    }

    public Nodes append( char c ) {
        return append( CharacterPredicates.constant( c ) );
    }

    public Nodes append( CharacterPredicate predicate ) {
        List<Node> endNodes = new ArrayList();

        for ( Node n : nodes ) {
            endNodes.addAll( n.append(predicate) );
        }

        return new Nodes( endNodes );
    }

    public void append( CharacterPredicate predicate, Node next ) {
        append( predicate, next );
    }

    public void append( CharacterPredicate predicate, Nodes next ) {
        for ( Node n : nodes ) {
            n.append( predicate, next );
        }
    }

    public void setActions( ParserFrameOp payload ) {
        for ( Node n : nodes ) {
            n.setActions( payload );
        }
    }

    public void isEndNode( boolean isEndNode ) {
        for ( Node n : nodes ) {
            n.isEndNode( isEndNode );
        }
    }

    public boolean hasContents() {
        return !isEmpty();
    }

    public void wrapActions( Function1<ParserFrameOp, ParserFrameOp> wrappingFunction ) {
        for ( Node n : nodes ) {
            ParserFrameOp newPayload = wrappingFunction.invoke( n.getActions() );

            n.setActions( newPayload );
        }
    }

//    /**
//     * Returns all of the nodes that can be transitioned to from this set of nodes.
//     */
//    public CharacterNodes getOutNodes() {
//        List<CharacterNode> outNodes = new ArrayList();
//
//        for ( CharacterNode n : nodes ) {
//            outNodes.addAll( n.getOutNodes() );
//        }
//
//        return new Nodes( outNodes );
//    }

}
