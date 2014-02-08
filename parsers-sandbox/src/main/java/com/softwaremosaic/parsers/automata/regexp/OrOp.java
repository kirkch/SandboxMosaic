package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.utils.StringUtils;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OrOp<T extends Comparable<T>> extends GraphBuilder<T> {

    private List<GraphBuilder<T>> childOps = new ArrayList();

    public OrOp( GraphBuilder<T>...childOps ) {
        for ( GraphBuilder<T> c : childOps ) {
            this.childOps.add( c );
        }
    }

    public void add( GraphBuilder<T> childOp ) {
        childOps.add( childOp );
    }

    public Nodes<T> appendTo( Node<T> startNode ) {
        Nodes<T> endNodes = new Nodes();

        for ( GraphBuilder op : childOps ) {
            endNodes.addAll( op.appendTo(startNode) );
        }

        return endNodes;
    }

    public String toString() {
        return StringUtils.join( this.childOps, "|" );
    }

}
