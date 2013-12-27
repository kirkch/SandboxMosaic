package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.collections.KV;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Tuple2;
import com.mosaic.utils.ArrayUtils;
import com.mosaic.utils.ListUtils;
import com.mosaic.utils.StringUtils;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class OrOp extends AutomataOp {

    private List<AutomataOp> childOps;

    public OrOp( AutomataOp...childOps ) {
        this.childOps = Arrays.asList(childOps);
    }

    public Nodes appendTo( String label, Node startNode ) {
        Nodes endNodes = new Nodes();

        for ( AutomataOp op : childOps ) {
            endNodes.addAll( op.appendTo(label,startNode) );
        }

        return endNodes;
    }

    public String toString() {
        return StringUtils.join( this.childOps, "|" );
    }

}
