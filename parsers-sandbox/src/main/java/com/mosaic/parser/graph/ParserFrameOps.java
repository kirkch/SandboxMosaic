package com.mosaic.parser.graph;

import com.mosaic.lang.Validate;
import com.mosaic.lang.reflect.MethodRef;

import static com.mosaic.collections.ConsList.Nil;

/**
 *
 */
public class ParserFrameOps {

    private static ParserFrameOp CAPTURE_INPUT_OP = new CaptureOp();
    private static ParserFrameOp CAPTURE_END_OP   = new ToStringOp(CAPTURE_INPUT_OP);


    public static ParserFrameOp noOp() {
        return NO_OP;
    }

    public static ParserFrameOp captureInputOp() {
        return CAPTURE_INPUT_OP;
    }

    public static ParserFrameOp captureEndOp() {
        return CAPTURE_END_OP;
    }

    public static ParserFrameOp pushOp( String name, Node jumpToNode, ParserFrameOp wrappedOp, Node returnNode ) {
        return new PushRuleParserFrameOp( name, jumpToNode, wrappedOp, returnNode );
    }



    private static ParserFrameOp NO_OP = new ParserFrameOp() {
        public ParserFrame justArrived( ParserFrame nextState ) {
            return nextState;
        }

        public ParserFrame consumed( char c, ParserFrame nextState ) {
            return nextState;
        }

        public ParserFrame productionRuleFinished( ParserFrame nextState ) {
            return nextState.getCurrentNode().isEndNode() ? nextState : null;
        }

        public void appendOpCodesTo( StringBuilder buf ) {
            buf.append( toString() );
        }

        public String toString() {
            return "NoOp";
        }
    };

    public static ParserFrameOp callbackOp( ParserFrameOp currentOp, MethodRef action ) {
        return new CallbackParserFrameOp( currentOp, action );
    }

    private static class WrappedParserFrameOp implements ParserFrameOp {
        private ParserFrameOp wrappedOpNbl;
        private String desc;

        public WrappedParserFrameOp( ParserFrameOp wrappedOpNbl, String desc ) {
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
            if ( wrappedOpNbl == null ) {
                return nextState.getCurrentNode().isEndNode() ? nextState : null;
            } else {
                return wrappedOpNbl.productionRuleFinished( nextState );
            }
        }

        public void appendOpCodesTo( StringBuilder buf ) {
            if ( wrappedOpNbl != null && wrappedOpNbl != NO_OP) {
                wrappedOpNbl.appendOpCodesTo( buf );
                buf.append( ',' );
            }

            buf.append( desc );
        }

    }

    private static class PushRuleParserFrameOp extends WrappedParserFrameOp {
        private String name;
        private Node   jumpToNode;
        private Node   returnNode;

        public PushRuleParserFrameOp( String name, Node jumpToNode, ParserFrameOp wrappedOp, Node returnNode ) {
            super( wrappedOp, "Push" );

            Validate.notNull( jumpToNode, "jumpToNode" );
            Validate.notNull( returnNode, "returnNode" );

            this.name       = name;
            this.jumpToNode = jumpToNode;
            this.returnNode = returnNode;
        }

        public ParserFrame justArrived( ParserFrame nextState ) {
            return super.justArrived(nextState).push( name, jumpToNode, returnNode );
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

    private static class CallbackParserFrameOp extends WrappedParserFrameOp {
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
