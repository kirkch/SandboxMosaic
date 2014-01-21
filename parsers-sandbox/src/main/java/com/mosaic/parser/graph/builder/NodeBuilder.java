package com.mosaic.parser.graph.builder;


import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.parser.graph.ParserFrameOp;
import com.mosaic.parser.graph.ParserFrameOps;

import java.util.Set;


/**
 * Grow a Trie in a defined way.  Appends a series of transitions to
 * a specified starting node.  For example, compose multiple ops together
 * to represent a regular expression that can be appended to any graph on
 * demand.
 */
@SuppressWarnings("unchecked")
public abstract class NodeBuilder {


    private boolean isCapturing;

    public Node build() {
        Node n = new Node();

        appendTo( n );

        return n;
    }


    /**
     * Append this op to the specified node.
     *
     * @return the last nodes appended to the graph
     */
    public Nodes appendTo( Node startNode ) {
        Nodes endNodes = doAppendTo( startNode );

        if ( isCapturing ) {
            walkGraphAndSetNodeAction( startNode, ParserFrameOps.captureInputOp() );

            endNodes.setActions( ParserFrameOps.captureEndOp() );
        }

        return endNodes;
    }

    protected abstract Nodes doAppendTo( Node startNode );


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

    public NodeBuilder isCapturing( boolean isCapturing ) {
        this.isCapturing = isCapturing;

        return this;
    }


    protected NodeBuilder and( NodeBuilder b ) {
        return new AndOp( this, b );
    }

    protected NodeBuilder or( NodeBuilder b ) {
        return new OrOp( this, b );
    }


    private void walkGraphAndSetNodeAction( Node firstNode, final ParserFrameOp op ) {
        firstNode.depthFirstPrefixTraversal( new VoidFunction2<ConsList<KV<Set<CharacterPredicate>, Node>>, Boolean>() {
            public void invoke( ConsList<KV<Set<CharacterPredicate>, Node>> path, Boolean isEndOfPath ) {
                Node node = path.head().getValue();

                node.setActions( op );
            }
        } );
    }
}
