package com.mosaic.parser;

import com.mosaic.parser.graph.Node;
import com.mosaic.parser.graph.Nodes;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.reflect.MethodRef;
import com.mosaic.lang.reflect.ReflectionException;
import com.mosaic.parser.graph.ParserFrameOp;


/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRule {


//    public static ProductionRule terminalRegExp( String name, String regexp ) {
//        TrieBuilderOp builder = TrieBuilders.regexp( regexp );
//
//        return makeRule( name, builder, NULL_OP, END_NULL_OP );
//    }
//
//    public static ProductionRule capturingTerminalRegExp( String name, String regexp ) {
//        TrieBuilderOp builder = TrieBuilders.regexp( regexp );
//
//        return makeRule( name, builder, CAPTURE_INPUT_OP, CHARS2STRING_OP );
//    }



//    public static ProductionRule nonTerminal( String name, ProductionRule...rules ) {
//        CharacterNode<Parser.ParserFrameOp> firstNode = new CharacterNode();
//
//        CharacterNode<Parser.ParserFrameOp> lastNode = ListUtils.fold( rules, firstNode, new Function2 <CharacterNode<Parser.ParserFrameOp>,ProductionRule,CharacterNode<Parser.ParserFrameOp>>() {
//            public CharacterNode<Parser.ParserFrameOp> invoke( CharacterNode<Parser.ParserFrameOp> previousNode, ProductionRule nextRule ) {
//                assert !previousNode.hasOutEdges() : "When embedding a rule, the node that we are pushing from must have no out edges";
//
//                CharacterNode <Parser.ParserFrameOp> returnNode = new CharacterNode<>();
//
//                previousNode.setPayload( new PushRuleParserFrameOp(nextRule,previousNode.getPayload(), returnNode) );
//
//                return returnNode;
//            }
//        });
//
//
//        return new ProductionRule( name, firstNode, new CharacterNodes(lastNode) );
//    }

    private String                        name;
    private Node<ParserFrameOp> startingNode;
    private Nodes<ParserFrameOp> endNodes;


    public ProductionRule( String name, Node<ParserFrameOp> startingNode, Nodes<ParserFrameOp> endNodes ) {
        this.name         = name;
        this.startingNode = startingNode;
        this.endNodes      = endNodes;
    }


    public String name() {
        return name;
    }

    public Node<ParserFrameOp> startingNode() {
        return startingNode;
    }

    public String toString() {
        return "$"+name;
    }

    public ProductionRule withCallback( Class listenerClass, String methodName ) {
        final MethodRef action = MethodRef.create( listenerClass, methodName, Integer.TYPE, Integer.TYPE, String.class );

        endNodes.mapPayloads( new Function1<ParserFrameOp, ParserFrameOp>() {
            public ParserFrameOp invoke( ParserFrameOp currentOp ) {
                return new ProductionRuleBuilder.CallbackParserFrameOp( currentOp, action );
            }
        } );

        return this;
    }

    protected ProductionRule clone() {
        try {
            return (ProductionRule) super.clone();
        } catch ( CloneNotSupportedException e ) {
            throw ReflectionException.recast(e);
        }
    }

}
