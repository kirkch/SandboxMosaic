package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

/**
 *
 */
public class LabelOp<T extends Comparable<T>> extends GraphBuilder<T> {

    private Label<T> label;


    public LabelOp( Label<T> label ) {
        this.label = label;
    }

    public Nodes<T> appendTo( Node<T> startNode ) {
        return startNode.append( label );
    }

    public String toString() {
        return label.toString();
    }

    public Label<T> getLabel() {
        return label;
    }

}
