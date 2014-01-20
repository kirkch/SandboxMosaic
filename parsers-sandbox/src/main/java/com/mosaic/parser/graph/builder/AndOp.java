package com.mosaic.parser.graph.builder;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 */
@SuppressWarnings("unchecked")
public class AndOp extends TrieBuilderOp {

    private List<TrieBuilderOp> childOps;

    public AndOp( Iterable<TrieBuilderOp> childOps ) {
        this.childOps = new ArrayList();

        for ( TrieBuilderOp op : childOps ) {
            this.childOps.add( op );
        }
    }

    public AndOp( TrieBuilderOp...childOps ) {
        this.childOps = Arrays.asList(childOps);
    }

    public Nodes appendTo( Node startNode ) {
        Nodes pos = new Nodes(startNode);

        for ( TrieBuilderOp op : childOps ) {
            pos = op.appendTo(pos);
        }

        return pos;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        for ( TrieBuilderOp op : childOps ) {
            buf.append( op );
        }

        return buf.toString();
    }

}
