package com.mosaic.parser.graph.builder;

import com.mosaic.lang.CharacterPredicate;
import com.mosaic.parser.ProductionRule;
import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.parser.graph.ParserFrameOp;
import com.mosaic.parser.graph.ParserFrameOps;

/**
 *
 */
@SuppressWarnings("unchecked")
public class EmbeddedProductionRuleOp extends NodeBuilder {

    private ProductionRule embeddedRule;


    public EmbeddedProductionRuleOp( ProductionRule embeddedRule ) {
        this.embeddedRule = embeddedRule;
    }



    protected Nodes doAppendTo( final Node startNode ) {
        Node returnNode = new Node();


        ParserFrameOp op = ParserFrameOps.pushOp( embeddedRule.name(), embeddedRule.startingNode(), startNode.getActions(), startNode );
        startNode.setActions( op );

        startNode.append( new EmbeddedRuleLink(embeddedRule.name()), returnNode );

        return new Nodes(returnNode);
    }


    public String toString() {
        return "$"+embeddedRule.name();
    }


    private static class EmbeddedRuleLink implements CharacterPredicate {

        private String ruleName;

        private EmbeddedRuleLink( String ruleName ) {
            this.ruleName = ruleName;
        }

        public boolean matches( char input ) {
            return false;
        }

        public String toString() {
            return "$"+ruleName;
        }

        public int compareTo( CharacterPredicate o ) {
            return o == this ? 0 : -1;
        }
    }
}
