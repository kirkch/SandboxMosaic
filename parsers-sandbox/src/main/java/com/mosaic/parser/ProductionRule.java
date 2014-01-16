package com.mosaic.parser;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.KV;
import com.mosaic.collections.trie.CharacterNode;
import com.mosaic.collections.trie.CharacterNodes;
import com.mosaic.collections.trie.builder.TrieBuilderOp;
import com.mosaic.collections.trie.builder.TrieBuilders;
import com.mosaic.lang.CaseSensitivity;
import com.mosaic.lang.CharacterPredicate;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Function2;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.lang.reflect.MethodRef;
import com.mosaic.lang.reflect.ReflectionException;
import com.mosaic.utils.ListUtils;

import java.util.Set;

import static com.mosaic.collections.ConsList.Nil;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static com.mosaic.parser.Parser.ParserContext;
import static com.mosaic.parser.Parser.ParserContextOp;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRule {

    private static ParserContextOp NULL_OP = new ParserContextOp() {
        public ParserContext justArrived( ParserContext nextState ) {
            return nextState;
        }

        public ParserContext consumed( char c, ParserContext nextState ) {
            return nextState;
        }

        public ParserContext productionRuleFinished( ParserContext nextState ) {
            return null;
        }
    };

    private static ParserContextOp END_NULL_OP = new ParserContextOp() {
        public ParserContext justArrived( ParserContext nextState ) {
            return nextState;
        }

        public ParserContext consumed( char c, ParserContext nextState ) {
            return nextState;
        }

        public ParserContext productionRuleFinished( ParserContext nextState ) {
            return nextState;
        }
    };

    private static ParserContextOp CAPTURE_INPUT_OP = new ParserContextOp() {
        public ParserContext justArrived( ParserContext nextState ) {
            return nextState;
        }

        public ParserContext consumed( char c, ParserContext nextState ) {
            return nextState.appendInputValue( c );
        }

        public ParserContext productionRuleFinished( ParserContext nextState ) {
            return null;
        }

        public String toString() {
            return "CAPTURE_INPUT_OP";
        }
    };

    private static ParserContextOp CHARS2STRING_OP = new ParserContextOp() {
        public ParserContext justArrived( ParserContext nextState ) {
            return nextState;
        }

        public ParserContext consumed( char c, ParserContext nextState ) {
            return nextState.appendInputValue( c );
        }

        public ParserContext productionRuleFinished( ParserContext nextState ) {
            String str = nextState.getValue().reverse().join( "" );

            return nextState.setValue( Nil.cons(str) );
        }

        public String toString() {
            return "CHARS2STRING_OP";
        }
    };


    public static ProductionRule terminalConstant( String name, String constant ) {
        return terminalConstant( name, constant, CaseSensitive );
    }

    public static ProductionRule terminalConstant( String name, String constant, CaseSensitivity caseSensitivity ) {
        TrieBuilderOp builder = TrieBuilders.constant( constant, caseSensitivity );

        return makeRule( name, builder, NULL_OP, END_NULL_OP );
    }

    public static ProductionRule capturingTerminalConstant( String name, String constant ) {
        return capturingTerminalConstant( name, constant, CaseSensitive );
    }

    public static ProductionRule capturingTerminalConstant( String name, String constant, CaseSensitivity caseSensitivity ) {
        TrieBuilderOp builder = TrieBuilders.constant( constant, caseSensitivity );

        return makeRule( name, builder, CAPTURE_INPUT_OP, CHARS2STRING_OP );
    }

    public static ProductionRule terminalRegExp( String name, String regexp ) {
        TrieBuilderOp builder = TrieBuilders.regexp( regexp );

        return makeRule( name, builder, NULL_OP, END_NULL_OP );
    }

    public static ProductionRule capturingTerminalRegExp( String name, String regexp ) {
        TrieBuilderOp builder = TrieBuilders.regexp( regexp );

        return makeRule( name, builder, CAPTURE_INPUT_OP, CHARS2STRING_OP );
    }



    public static ProductionRule nonTerminal( String name, ProductionRule...rules ) {
        CharacterNode<ParserContextOp> firstNode = new CharacterNode();

        CharacterNode<ParserContextOp> lastNode = ListUtils.fold( rules, firstNode, new Function2 <CharacterNode<ParserContextOp>,ProductionRule,CharacterNode<ParserContextOp>>() {
            public CharacterNode<ParserContextOp> invoke( CharacterNode<ParserContextOp> previousNode, ProductionRule nextRule ) {
                assert !previousNode.hasOutEdges() : "When embedding a rule, the node that we are pushing from must have no out edges";

                CharacterNode <ParserContextOp> returnNode = new CharacterNode<>();

                previousNode.setPayload( new PushRuleParserContextOp(nextRule,previousNode.getPayload(), returnNode) );

                return returnNode;
            }
        });


        return new ProductionRule( name, firstNode, new CharacterNodes(lastNode) );
    }

//    public static ProductionRule capturingNonTerminal( ProductionRule...rules ) {
//        return null;
//    }
//
//    public static ProductionRule nonTerminalOr( ProductionRule...rules ) {
//        return null;
//    }
//
//    public static ProductionRule capturingNonTerminalOr( ProductionRule...rules ) {
//        return null;
//    }
//
//    public static ProductionRule nonTerminalOptional( ProductionRule...rules ) {
//        return null;
//    }
//
//    public static ProductionRule capturingNonTerminalOptional( ProductionRule...rules ) {
//        return null;
//    }



//    private static ProductionRule makeRule( String name, TrieBuilderOp builder, ParserContextOp op ) {
//        return makeRule( name, builder, op, op, op );
//    }

    private static ProductionRule makeRule( String name, TrieBuilderOp builder, ParserContextOp innerNodeOp, ParserContextOp endNodeOp ) {
        return makeRule( name, builder, innerNodeOp, innerNodeOp, endNodeOp );
    }

    private static ProductionRule makeRule( String name, TrieBuilderOp builder, ParserContextOp firstNodeOp, ParserContextOp innerNodeOp, ParserContextOp endNodeOp ) {
        CharacterNode<ParserContextOp>  firstNode = new CharacterNode();
        CharacterNodes<ParserContextOp> endNodes  = builder.appendTo( firstNode );


        setOp( firstNode, innerNodeOp );
        endNodes.setPayloads( endNodeOp );
        firstNode.setPayload( firstNodeOp );
        endNodes.isEndNode( true );

        return new ProductionRule( name, firstNode, endNodes );
    }

    private static void setOp( CharacterNode<ParserContextOp> firstNode, final ParserContextOp op ) {
        firstNode.depthFirstPrefixTraversal( new VoidFunction2<ConsList<KV<Set<CharacterPredicate>, CharacterNode<ParserContextOp>>>, Boolean>() {
            public void invoke( ConsList<KV<Set<CharacterPredicate>, CharacterNode<ParserContextOp>>> path, Boolean isEndOfPath ) {
                CharacterNode<ParserContextOp> node = path.head().getValue();

                node.setPayload( op );
            }
        } );
    }



    private String                          name;
    private CharacterNode<ParserContextOp>  startingNode;
    private CharacterNodes<ParserContextOp> endNodes;


    public ProductionRule( String name, CharacterNode<ParserContextOp> startingNode, CharacterNodes<ParserContextOp> endNodes ) {
        this.name         = name;
        this.startingNode = startingNode;
        this.endNodes      = endNodes;
    }


    public String name() {
        return name;
    }

    public CharacterNode<ParserContextOp> startingNode() {
        return startingNode;
    }

    public String toString() {
        return "$"+name;
    }

    public ProductionRule withCallback( Class listenerClass, String methodName ) {
        final MethodRef action = MethodRef.create( listenerClass, methodName, Integer.TYPE, Integer.TYPE, String.class );

        endNodes.mapPayloads( new Function1<ParserContextOp, ParserContextOp>() {
            public ParserContextOp invoke( ParserContextOp currentOp ) {
                return new CallbackParserContextOp( currentOp, action );
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


    private static class CallbackParserContextOp extends WrappedParserContextOp {
        private MethodRef       callbackMethodRef;

        public CallbackParserContextOp( ParserContextOp wrappedOp, MethodRef action ) {
            super( wrappedOp );

            this.callbackMethodRef = action;
        }

        public ParserContext productionRuleFinished( ParserContext nextState ) {
            nextState = super.productionRuleFinished( nextState );

            if ( nextState != null ) {
                nextState = nextState.appendAction( callbackMethodRef );
            }

            return nextState;
        }
    }

    private static class WrappedParserContextOp implements ParserContextOp {
        private ParserContextOp wrappedOpNbl;

        public WrappedParserContextOp( ParserContextOp wrappedOpNbl ) {
            this.wrappedOpNbl = wrappedOpNbl;
        }

        public ParserContext justArrived( ParserContext nextState ) {
            return wrappedOpNbl == null ? nextState : wrappedOpNbl.justArrived( nextState );
        }

        public ParserContext consumed( char c, ParserContext nextState ) {
            return wrappedOpNbl == null ? nextState : wrappedOpNbl.consumed( c, nextState );
        }

        public ParserContext productionRuleFinished( ParserContext nextState ) {
            return wrappedOpNbl == null ? nextState : wrappedOpNbl.productionRuleFinished( nextState );
        }

    }

    private static class PushRuleParserContextOp extends WrappedParserContextOp {
        private ProductionRule                 nextRule;
        private CharacterNode<ParserContextOp> returnNode;

        public PushRuleParserContextOp( ProductionRule nextRule, ParserContextOp wrappedOp, CharacterNode<ParserContextOp> returnNode ) {
            super( wrappedOp );

            Validate.notNull( nextRule,   "nextRule" );
            Validate.notNull( returnNode, "returnNode" );

            this.nextRule   = nextRule;
            this.returnNode = returnNode;
        }

        public ParserContext justArrived( ParserContext nextState ) {
            return super.justArrived(nextState).push( nextRule.name(), nextRule.startingNode(), returnNode );
        }
    }

    private static class PopFrameOp extends WrappedParserContextOp {
        private ProductionRule                 nextRule;
        private CharacterNode<ParserContextOp> returnNode;

        public PopFrameOp(ParserContextOp wrappedOp ) {
            super( wrappedOp );
        }

        public ParserContext justArrived( ParserContext nextState ) {
            return super.justArrived(nextState).pop();
        }
    }
}
