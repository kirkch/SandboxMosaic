package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.utils.StringUtils;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class OrOp<T extends Comparable<T>> extends GraphBuilder<T> {

    private List<GraphBuilder<T>> childOps;

    public OrOp( GraphBuilder<T>...childOps ) {
        this.childOps = Arrays.asList(childOps);
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
