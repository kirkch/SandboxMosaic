package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.lang.functional.VoidFunction2;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

import java.util.Set;

/**
 *
 */
public class OneOrMoreOp extends AutomataOp {

    private AutomataOp opToRepeat;

    /**
     *
     * @param op the op to repeat
     */
    public OneOrMoreOp( AutomataOp op ) {
        opToRepeat = op;
    }



    public Nodes appendTo( String label, Node startNode ) {
        final Nodes afterFirstStepNodes = opToRepeat.appendTo( label, startNode );


        final Nodes endNodes = opToRepeat.appendTo( label, afterFirstStepNodes );

        startNode.depthFirstPrefixTraversal(new VoidFunction2<ConsList<KV<Set<Character>,Node>>, Boolean>() {
            public void invoke( ConsList<KV<Set<Character>, Node>> path, Boolean isEndOfPath ) {
                Node visiting = path.head().getValue();
                if ( isEndOfPath && endNodes.contains(visiting) ) {
                    for ( char c : path.head().getKey() ) {
                        Node sourceNode = path.tail().head().getValue();

                        sourceNode.replace( c, visiting, afterFirstStepNodes );
                    }
                }
            }
        });


        return new Nodes(startNode);
    }
}
