package com.softwaremosaic.parsers.automata.regexp;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.lang.functional.VoidFunction2;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class ZeroOrMoreOp extends AutomataOp {

    private AutomataOp opToRepeat;

    /**
     *
     * @param op           the op to repeat
     */
    public ZeroOrMoreOp( AutomataOp op ) {
        opToRepeat = op;
    }



    public Nodes appendTo( String label, final Node startNode ) {
        final Nodes endNodes = opToRepeat.appendTo( label, startNode );

//        todo link endNodes back to startNodes using the characters leaving startNode

//        List<KV<Character,Node>> outEdges = startNode.getOutEdges();
//        Set<Character> characters = new HashSet();
//
//        for ( KV<Character,Node> o : outEdges ) {
//            characters.add( o.getKey() );
//        }
//
//        for ( Character c : characters ) {
//            endNodes.appendEdge( c, startNode );
//        }
//
//        return endNodes;

        startNode.depthFirstPrefixTraversal(new VoidFunction2<ConsList<KV<Set<Character>,Node>>, Boolean>() {
            public void invoke( ConsList<KV<Set<Character>, Node>> path, Boolean isEndOfPath ) {
                Node visiting = path.head().getValue();
                if ( isEndOfPath && endNodes.contains(visiting) ) {
                    for ( char c : path.head().getKey() ) {
                        Node sourceNode = path.tail().head().getValue();

                        sourceNode.replace( c, visiting, startNode );
                    }
                }
            }
        });


        return new Nodes(startNode);
    }

}
