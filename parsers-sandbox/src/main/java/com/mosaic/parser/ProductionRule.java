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
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.lang.reflect.MethodRef;
import com.mosaic.lang.reflect.ReflectionException;

import java.util.Set;

import static com.mosaic.collections.ConsList.Nil;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static com.mosaic.parser.Parser.ParserContext;
import static com.mosaic.parser.Parser.ParserContextOp;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRule<T> {

    private static ParserContextOp NULL_OP = new ParserContextOp() {
        public void justConsumed( char c, ParserContext mutableNextState ) {}


        public boolean productionRuleFinished( ParserContext mutableNextState ) {
            return false;
        }
    };

    private static ParserContextOp END_NULL_OP = new ParserContextOp() {
        public void justConsumed( char c, ParserContext mutableNextState ) {}


        public boolean productionRuleFinished( ParserContext mutableNextState ) {
            return true;
        }
    };

    private static ParserContextOp CAPTURE_INPUT_OP = new ParserContextOp() {
        public void justConsumed( char c, ParserContext mutableNextState ) {
            mutableNextState.appendInputValue(c);
        }

        public boolean productionRuleFinished( ParserContext mutableNextState ) {
            return false;
        }

        public String toString() {
            return "CAPTURE_INPUT_OP";
        }
    };

    private static ParserContextOp CHARS2STRING_OP = new ParserContextOp() {
        public void justConsumed( char c, ParserContext mutableNextState ) {
            mutableNextState.appendInputValue(c);
        }

        public boolean productionRuleFinished( ParserContext mutableNextState ) {
            String str = mutableNextState.getValue().reverse().join( "" );

            mutableNextState.setValue( Nil.cons(str) );

            return true;
        }

        public String toString() {
            return "CHARS2STRING_OP";
        }
    };



    public static ProductionRule terminalConstant( String name, String constant ) {
        return terminalConstant( name, constant, CaseSensitive );
    }

    public static ProductionRule<ParserContextOp> terminalConstant( String name, String constant, CaseSensitivity caseSensitivity ) {
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



    public static ProductionRule nonTerminal( ProductionRule...rules ) {
        return null;
    }

    public static ProductionRule capturingNonTerminal( ProductionRule...rules ) {
        return null;
    }

    public static ProductionRule nonTerminalOr( ProductionRule...rules ) {
        return null;
    }

    public static ProductionRule capturingNonTerminalOr( ProductionRule...rules ) {
        return null;
    }

    public static ProductionRule nonTerminalOptional( ProductionRule...rules ) {
        return null;
    }

    public static ProductionRule capturingNonTerminalOptional( ProductionRule...rules ) {
        return null;
    }



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

        return new ProductionRule<>( name, firstNode, endNodes );
    }

    private static void setOp( CharacterNode<ParserContextOp> firstNode, final ParserContextOp op ) {
        firstNode.depthFirstPrefixTraversal( new VoidFunction2<ConsList<KV<Set<CharacterPredicate>, CharacterNode<ParserContextOp>>>, Boolean>() {
            public void invoke( ConsList<KV<Set<CharacterPredicate>, CharacterNode<ParserContextOp>>> path, Boolean isEndOfPath ) {
                CharacterNode<ParserContextOp> node = path.head().getValue();

                node.setPayload( op );
            }
        } );
    }



    private String         name;
    private CharacterNode  startingNode;
    private CharacterNodes endNodes;


    public ProductionRule( String name, CharacterNode startingNode, CharacterNodes endNodes ) {
        this.name         = name;
        this.startingNode = startingNode;
        this.endNodes     = endNodes;
    }


    public String name() {
        return name;
    }

    public CharacterNode startingNode() {
        return startingNode;
    }

    public String toString() {
        return "$"+name;
    }

    public ProductionRule withCallback( Class listenerClass, String methodName ) {
        final MethodRef action = MethodRef.create( listenerClass, methodName, Integer.TYPE, Integer.TYPE, String.class );

        endNodes.mapPayloads( new Function1<ParserContextOp,ParserContextOp>() {
            public ParserContextOp invoke( ParserContextOp currentOp ) {
                return new CallbackParserContextOp( currentOp, action );
            }
        });


        return this;
    }

    protected ProductionRule clone() {
        try {
            return (ProductionRule) super.clone();
        } catch ( CloneNotSupportedException e ) {
            throw ReflectionException.recast(e);
        }
    }


    private static class CallbackParserContextOp implements ParserContextOp {
        private ParserContextOp wrappedOp;
        private MethodRef       callbackMethodRef;

        public CallbackParserContextOp( ParserContextOp wrappedOp, MethodRef action ) {
            Validate.notNull( wrappedOp, "wrappedOp" );

            this.wrappedOp         = wrappedOp;
            this.callbackMethodRef = action;
        }

        public void justConsumed( char c, ParserContext mutableNextState ) {
            wrappedOp.justConsumed( c, mutableNextState );
        }

        public boolean productionRuleFinished( ParserContext mutableNextState ) {
            boolean isValidEndPoint = wrappedOp.productionRuleFinished( mutableNextState );

            if ( isValidEndPoint ) {
                mutableNextState.appendAction( callbackMethodRef );
            }

            return isValidEndPoint;
        }
    }
}
