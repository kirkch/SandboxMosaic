package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OrOp<T> extends TrieBuilderOp<T> {

    private List<TrieBuilderOp<T>> childOps = new ArrayList();


    public OrOp( TrieBuilderOp<T>...childOps ) {
        Collections.addAll( this.childOps, childOps );
    }

    public OrOp( Iterable<TrieBuilderOp<T>> childOps ) {
        for ( TrieBuilderOp<T> op : childOps ) {
            this.childOps.add( op );
        }
    }

    public void add( TrieBuilderOp<T> childOp ) {
        childOps.add( childOp );
    }

    public Nodes<T> appendTo( Node<T> startNode ) {
        Nodes<T> endNodes = new Nodes();

        for ( TrieBuilderOp op : childOps ) {
            endNodes.addAll( op.appendTo(startNode) );
        }

        return endNodes;
    }

    public String toString() {
        return StringUtils.join( this.childOps, "|" );
    }

}
