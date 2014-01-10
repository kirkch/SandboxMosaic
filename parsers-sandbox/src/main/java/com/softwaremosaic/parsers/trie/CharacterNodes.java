package com.softwaremosaic.parsers.trie;

import com.mosaic.io.CharPredicate;
import com.softwaremosaic.parsers.automata.Nodes;
import com.softwaremosaic.parsers.trie.regexp.CharPredicates;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unchecked")
public class CharacterNodes<T> extends AbstractList<CharacterNode<T>> {

    private List<CharacterNode<T>> nodes;

    
    public CharacterNodes( List<CharacterNode<T>> nodes ) {
        this.nodes = nodes;
    }

    public CharacterNodes( CharacterNode newNode ) {
        this.nodes = new ArrayList();
        
        this.nodes.add( newNode );
    }

    public CharacterNode<T> get( int i ) {
        return nodes.get( i );
    }

    public Iterator<CharacterNode<T>> iterator() {
        return nodes.iterator();
    }

    public int size() {
        return nodes.size();
    }

    public void add( int index, CharacterNode<T> element ) {
        nodes.add( index, element );
    }



    public Nodes fetch( char c ) {
        List<CharacterNode<T>> endNodes = new ArrayList();

        for ( CharacterNode<T> n : nodes ) {
            endNodes.addAll( n.fetch( c ) );
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

    public CharacterNodes<T> append( char c ) {
        return append( CharPredicates.constant( c ) );
    }

    public CharacterNodes<T> append( CharPredicate predicate ) {
        List<CharacterNode<T>> endNodes = new ArrayList();

        for ( CharacterNode<T> n : nodes ) {
            endNodes.addAll( n.append(predicate) );
        }

        return new CharacterNodes( endNodes );
    }

    public void append( CharPredicate predicate, CharacterNode<T> next ) {
        append( predicate, next );
    }

    public void append( CharPredicate predicate, CharacterNodes<T> next ) {
        for ( CharacterNode<T> n : nodes ) {
            n.append( predicate, next );
        }
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
