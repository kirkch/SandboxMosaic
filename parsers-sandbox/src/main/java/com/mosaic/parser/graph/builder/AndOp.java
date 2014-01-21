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
public class AndOp extends NodeBuilder {

    private List<NodeBuilder> childOps;

    public AndOp( Iterable<NodeBuilder> childOps ) {
        this.childOps = new ArrayList();

        for ( NodeBuilder op : childOps ) {
            this.childOps.add( op );
        }
    }

    public AndOp( NodeBuilder...childOps ) {
        this.childOps = Arrays.asList(childOps);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        for ( NodeBuilder op : childOps ) {
            if ( op instanceof EmbeddedProductionRuleOp && buf.length() != 0 ) {
                buf.append(" ");
            }
            buf.append( op );
        }

        return buf.toString();
    }

    protected  Nodes doAppendTo( Node startNode ) {
        Nodes pos = new Nodes(startNode);

        for ( NodeBuilder op : childOps ) {
            pos = op.appendTo(pos);
        }

        return pos;
    }

    @Override
    protected NodeBuilder and( NodeBuilder b ) {
        childOps.add( b );

        return this;
    }
}
