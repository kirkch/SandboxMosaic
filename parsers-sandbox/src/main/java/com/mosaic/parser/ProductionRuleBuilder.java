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
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.lang.reflect.MethodRef;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mosaic.collections.ConsList.Nil;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;
import static com.mosaic.parser.Parser.ParserFrame;
import static com.mosaic.parser.Parser.ParserFrameOp;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRuleBuilder {

    private static ParserFrameOp CAPTURE_INPUT_OP = new CaptureOp();
    private static ParserFrameOp CAPTURE_END_OP   = new PopFrameOp(new ToStringOp(CAPTURE_INPUT_OP));
    private static ParserFrameOp POP_OP           = new PopFrameOp();


    private Map<String,ProductionRule> rules = new HashMap();



    public ProductionRule constant( String name, String constant ) {
        return constant( name, constant, CaseSensitive );
    }

    public ProductionRule constant( String name, String constant, CaseSensitivity caseSensitivity ) {
        TrieBuilderOp builder = TrieBuilders.constant( constant, caseSensitivity );

        return makeRule( name, builder, NULL_OP, POP_OP );
    }

    public ProductionRule capturingConstant( String name, String constant ) {
        return capturingConstant( name, constant, CaseSensitive );
    }

    public ProductionRule capturingConstant( String name, String constant, CaseSensitivity caseSensitivity ) {
        TrieBuilderOp builder = TrieBuilders.constant( constant, caseSensitivity );

        return makeRule( name, builder, CAPTURE_INPUT_OP, CAPTURE_END_OP );
    }

    public ProductionRule regexp( String name, String regexp ) {
        TrieBuilderOp builder = TrieBuilders.regexp( regexp );

        return makeRule( name, builder, NULL_OP, POP_OP );
    }

    public ProductionRule capturingRegexp( String name, String regexp ) {
        TrieBuilderOp builder = TrieBuilders.regexp( regexp );

        return makeRule( name, builder, CAPTURE_INPUT_OP, CAPTURE_END_OP );
    }




    private ProductionRule makeRule( String name, TrieBuilderOp builder, ParserFrameOp innerNodeOp, ParserFrameOp endNodeOp ) {
        return makeRule( name, builder, innerNodeOp, innerNodeOp, endNodeOp );
    }

    private ProductionRule makeRule( String name, TrieBuilderOp builder, ParserFrameOp firstNodeOp, ParserFrameOp innerNodeOp, ParserFrameOp endNodeOp ) {
        if ( rules.containsKey(name) ) {
            throw new IllegalArgumentException( "'"+name+"' has already been declared" );
        }

        CharacterNode<ParserFrameOp> firstNode = new CharacterNode();
        CharacterNodes<ParserFrameOp> endNodes  = builder.appendTo( firstNode );


        setOp( firstNode, innerNodeOp );
        endNodes.setPayloads( endNodeOp );
        firstNode.setPayload( firstNodeOp );
        endNodes.isEndNode( true );

        ProductionRule productionRule = new ProductionRule( name, firstNode, endNodes );

        rules.put( name, productionRule );

        return productionRule;
    }

    private void setOp( CharacterNode<Parser.ParserFrameOp> firstNode, final Parser.ParserFrameOp op ) {
        firstNode.depthFirstPrefixTraversal( new VoidFunction2<ConsList<KV<Set<CharacterPredicate>, CharacterNode<ParserFrameOp>>>, Boolean>() {
            public void invoke( ConsList<KV<Set<CharacterPredicate>, CharacterNode<Parser.ParserFrameOp>>> path, Boolean isEndOfPath ) {
                CharacterNode<Parser.ParserFrameOp> node = path.head().getValue();

                node.setPayload( op );
            }
        } );
    }





    private static ParserFrameOp NULL_OP = new ParserFrameOp() {
        public ParserFrame justArrived( ParserFrame nextState ) {
            return nextState;
        }

        public ParserFrame consumed( char c, ParserFrame nextState ) {
            return nextState;
        }

        public ParserFrame productionRuleFinished( ParserFrame nextState ) {
            return null;
        }

        public void appendOpCodesTo( StringBuilder buf ) {
            buf.append( "NoOp" );
        }
    };

    private static class WrappedParserFrameOp implements Parser.ParserFrameOp {
        private Parser.ParserFrameOp wrappedOpNbl;
        private String desc;

        public WrappedParserFrameOp( Parser.ParserFrameOp wrappedOpNbl, String desc ) {
            this.wrappedOpNbl = wrappedOpNbl;
            this.desc         = desc;
        }

        public ParserFrame justArrived( ParserFrame nextState ) {
            return wrappedOpNbl == null ? nextState : wrappedOpNbl.justArrived( nextState );
        }

        public ParserFrame consumed( char c, ParserFrame nextState ) {
            return wrappedOpNbl == null ? nextState : wrappedOpNbl.consumed( c, nextState );
        }

        public ParserFrame productionRuleFinished( ParserFrame nextState ) {
            return wrappedOpNbl == null ? nextState : wrappedOpNbl.productionRuleFinished( nextState );
        }

        public void appendOpCodesTo( StringBuilder buf ) {
            if ( wrappedOpNbl != null ) {
                wrappedOpNbl.appendOpCodesTo( buf );
            }

            buf.append( desc );
        }

    }

    private static class PushRuleParserFrameOp extends WrappedParserFrameOp {
        private ProductionRule                 nextRule;
        private CharacterNode<Parser.ParserFrameOp> returnNode;

        public PushRuleParserFrameOp( ProductionRule nextRule, Parser.ParserFrameOp wrappedOp, CharacterNode<Parser.ParserFrameOp> returnNode ) {
            super( wrappedOp, "Psh" );

            Validate.notNull( nextRule, "nextRule" );
            Validate.notNull( returnNode, "returnNode" );

            this.nextRule   = nextRule;
            this.returnNode = returnNode;
        }

        public ParserFrame justArrived( ParserFrame nextState ) {
            return super.justArrived(nextState).push( nextRule.name(), nextRule.startingNode(), returnNode );
        }
    }

    private static class PopFrameOp extends WrappedParserFrameOp {
        private ProductionRule                 nextRule;
        private CharacterNode<Parser.ParserFrameOp> returnNode;

        public PopFrameOp() {
            super( null, "Pop" );
        }

        public PopFrameOp(Parser.ParserFrameOp wrappedOp ) {
            super( wrappedOp, "Pop" );
        }

        public ParserFrame justArrived( ParserFrame nextState ) {
            return super.justArrived(nextState).pop();
        }
    }

    private static class CaptureOp extends WrappedParserFrameOp {
        public CaptureOp() {
            this(null);
        }

        public CaptureOp( ParserFrameOp wrappedOpNbl ) {
            super( wrappedOpNbl, "Cap" );
        }

        public ParserFrame consumed( char c, ParserFrame nextState ) {
            return super.consumed(c,nextState).appendInputValue( c );
        }
    }

    private static class ToStringOp extends WrappedParserFrameOp {
        public ToStringOp( ParserFrameOp wrappedOpNbl ) {
            super( wrappedOpNbl, "ToStr" );
        }

        public ParserFrame productionRuleFinished( ParserFrame nextState ) {
            String str = super.productionRuleFinished(nextState).getValue().reverse().join( "" );

            return nextState.setValue( Nil.cons(str) );
        }
    }

    static class CallbackParserFrameOp extends WrappedParserFrameOp {
        private MethodRef callbackMethodRef;

        public CallbackParserFrameOp( ParserFrameOp wrappedOp, MethodRef action ) {
            super( wrappedOp, "Cb" );

            this.callbackMethodRef = action;
        }

        public ParserFrame productionRuleFinished( ParserFrame nextState ) {
            nextState = super.productionRuleFinished( nextState );

            if ( nextState != null ) {
                nextState = nextState.appendAction( callbackMethodRef );
            }

            return nextState;
        }
    }

}
