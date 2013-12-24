package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

/**
 *
 */
public class AndOp extends AutomataOp {

    private AutomataOp[] childOps;

    public AndOp( AutomataOp...childOps ) {
        this.childOps = childOps;
    }

    public Nodes appendTo( String label, Node startNode ) {
        Nodes pos = new Nodes(startNode);

        for ( AutomataOp op : childOps ) {
            pos = op.appendTo(label,pos);
        }

        return pos;
    }

}
