package com.mosaic.parser.graph.builder;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.functional.VoidFunction2;

import java.util.Set;

/**
 *
 */
@SuppressWarnings("unchecked")
public class OneOrMoreOp extends NodeBuilder {

    private NodeBuilder opToRepeat;


    /**
     *
     * @param op the op to repeat
     */
    public OneOrMoreOp( NodeBuilder op ) {
        opToRepeat = op;
    }



    protected  Nodes doAppendTo( Node startNode ) {
        final Nodes afterFirstStepNodes = opToRepeat.appendTo( startNode );


        final Nodes endNodes = opToRepeat.appendTo( afterFirstStepNodes );

        startNode.depthFirstPrefixTraversal(new VoidFunction2<ConsList<KV<Set<CharacterPredicate>,Node>>, Boolean>() {
            public void invoke( ConsList<KV<Set<CharacterPredicate>, Node>> path, Boolean isEndOfPath ) {
                Node visiting = path.head().getValue();
                if ( isEndOfPath && endNodes.contains(visiting) ) {
                    for ( CharacterPredicate label : path.head().getKey() ) {
                        Node sourceNode = path.tail().head().getValue();

                        sourceNode.replace( label, visiting, afterFirstStepNodes );
                    }
                }
            }
        });


        return afterFirstStepNodes;
    }

    public String toString() {
        return "(" + opToRepeat.toString() + ")+";
    }

}
