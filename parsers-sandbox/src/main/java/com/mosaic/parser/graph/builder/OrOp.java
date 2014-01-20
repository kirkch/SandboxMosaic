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
public class OrOp extends TrieBuilderOp {

    private List<TrieBuilderOp> childOps = new ArrayList();


    public OrOp( TrieBuilderOp...childOps ) {
        Collections.addAll( this.childOps, childOps );
    }

    public OrOp( Iterable<TrieBuilderOp> childOps ) {
        for ( TrieBuilderOp op : childOps ) {
            this.childOps.add( op );
        }
    }

    public void add( TrieBuilderOp childOp ) {
        childOps.add( childOp );
    }

    public Nodes appendTo( Node startNode ) {
        Nodes endNodes = new Nodes();

        for ( TrieBuilderOp op : childOps ) {
            endNodes.addAll( op.appendTo(startNode) );
        }

        return endNodes;
    }

    public String toString() {
        return StringUtils.join( this.childOps, "|" );
    }

}
