package com.softwaremosaic.parsers.automata;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.lang.functional.VoidFunction2;

import java.util.List;
import java.util.Set;


/**
 *
 */
@SuppressWarnings( "unchecked" )
public interface Node<T extends Comparable<T>> extends Iterable<KV<Label<T>,Node<T>>> {

    /**
     * Creates a new transition for the specified character.  If a transition
     * already exists for the character, and it shares the same label as this
     * node then the node will be reused.  Else a new node will be created.
     * Each transition will reuse the label from this node.
     */
    public Nodes<T> append( T label );

    public Nodes<T> append( Label<T> label );

    public void append( T label, Nodes<T> nodes );
    public void append( Label<T> label, Nodes<T> nodes );

    public void append( T label, Node<T> node );
    public void append( Label<T> label, Node<T> node );



    /**
     * Scan this nodes out edges for the transition with character c to a specified
     * destination node, then replace the node with a replacement.
     */
    public int replace( Label<T> label, Node<T> expectedCurrentNode, Node<T> replacementNode );

    public int replace( Label<T> label, Node<T> expectedCurrentNode, Nodes<T> replacementNodes );

    public int remove( Label<T> label, Node<T> targetNode );


    /**
     * Retrieves the nodes that can be transitioned to by consuming the specified
     * character.  If there are no edges, then an empty list will be returned.
     */
    public Nodes<T> walk( T label );

    public Nodes<T> walk( T...path );

    public Nodes<T> walk( Iterable<T> path );


    /**
     * Returns true if this graph may end on this node.  Typically a leaf node,
     * but may optionally be any node.
     */
    public boolean isValidEndNode();

    public boolean hasOutEdges();

    public void isValidEndNode( boolean flag );


    public List<Label<T>> getOutLabels();

    public List<KV<Label<T>,Node<T>>> getOutEdges();

//    public Node<T> skipWhiteSpace();


//    public Nodes appendRegexpIC( String regexp );


    public Nodes<T> getOutNodes();

    /**
     * Walks the graph calling the specified callbackFunction for each node past,
     * as they are past.  The traversal will take the edges sorted by the traversal
     * characters (lowest first) and grouped so that a traversal to the same node will
     * not be repeated.<p/>
     *
     * The call to the callback function supplies the path walked so far and
     * a boolean which will be true if the path has reached the last node in the
     * traversal before it backtracks to another path.<p/>
     *
     * The first path will have an empty set of characters, then for each traversal
     * taken a set of characters for that edge (or edges if more than one) that
     * lead to the next node.
     */
    public void depthFirstPrefixTraversal( VoidFunction2<ConsList<KV<Set<Label<T>>,Node<T>>>, Boolean> callbackFunction );




}
