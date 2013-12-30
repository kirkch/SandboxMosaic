package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unchecked")
public class AndOp<T extends Comparable<T>> extends GraphBuilder<T> {

    private List<GraphBuilder<T>> childOps;

    public AndOp( Iterable<GraphBuilder<T>> childOps ) {
        this.childOps = new ArrayList();

        for ( GraphBuilder<T> op : childOps ) {
            this.childOps.add( op );
        }
    }

    public AndOp( GraphBuilder <T>...childOps ) {
        this.childOps = Arrays.asList(childOps);
    }

    public Nodes<T> appendTo( Node<T> startNode ) {
        Nodes<T> pos = new Nodes(startNode);

        for ( GraphBuilder op : childOps ) {
            pos = op.appendTo(pos);
        }

        return pos;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        for ( GraphBuilder<T> op : childOps ) {
            buf.append( op );
        }

        return buf.toString();
    }
}
