package com.softwaremosaic.parsers.automata.regexp;

import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

/**
 *
 */
public class OrOp extends AutomataOp {

    private AutomataOp[] childOps;

    public OrOp( AutomataOp...childOps ) {
        this.childOps = childOps;
    }

    public Nodes appendTo( String label, Node startNode ) {
        Nodes endNodes = new Nodes();

        for ( AutomataOp op : childOps ) {
            endNodes.addAll( op.appendTo(label,startNode) );
        }

        return endNodes;
    }

}
