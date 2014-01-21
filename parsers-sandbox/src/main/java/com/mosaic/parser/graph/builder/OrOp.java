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
public class OrOp extends NodeBuilder {

    private List<NodeBuilder> childOps = new ArrayList();


    public OrOp( NodeBuilder...childOps ) {
        Collections.addAll( this.childOps, childOps );
    }

    public OrOp( Iterable<NodeBuilder> childOps ) {
        for ( NodeBuilder op : childOps ) {
            this.childOps.add( op );
        }
    }

    public void add( NodeBuilder childOp ) {
        childOps.add( childOp );
    }

    protected  Nodes doAppendTo( Node startNode ) {
        Nodes endNodes = new Nodes();

        for ( NodeBuilder op : childOps ) {
            endNodes.addAll( op.appendTo(startNode) );
        }

        return endNodes;
    }

    public String toString() {
        return StringUtils.join( this.childOps, "|" );
    }

}
