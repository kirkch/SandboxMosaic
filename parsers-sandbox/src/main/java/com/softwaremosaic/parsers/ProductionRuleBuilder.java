package com.softwaremosaic.parsers;

import com.mosaic.collections.ConsList;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.reflect.MethodRef;
import com.softwaremosaic.parsers.automata.LabelNode;
import com.softwaremosaic.parsers.automata.Node;
import com.softwaremosaic.parsers.automata.Nodes;
import com.softwaremosaic.parsers.automata.ProductionRule;
import com.softwaremosaic.parsers.automata.regexp.AndOp;
import com.softwaremosaic.parsers.automata.regexp.GraphBuilder;
import com.softwaremosaic.parsers.automata.regexp.RegexpParser;
import com.softwaremosaic.parsers.automata.regexp.StringOp;

import java.util.ArrayList;
import java.util.List;

import static com.mosaic.lang.CaseSensitivity.CaseInsensitive;
import static com.mosaic.lang.CaseSensitivity.CaseSensitive;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ProductionRuleBuilder {

    private static Function1 CONSLIST_TOSTRING = new Function1() {
        public Object invoke( Object arg ) {
            ConsList<Character> l = (ConsList<Character>) arg;

            StringBuilder buf = new StringBuilder(  );

            append( buf, l );

            return ConsList.newConsList(buf.toString());
        }


        private void append( StringBuilder buf, ConsList<Character> l ) {
            if ( l.isEmpty() ) {
                return;
            }

            append( buf, l.tail() );

            buf.append( l.head().charValue() );
        }
    };


    private static Node build( GraphBuilder builder ) {
        Node n = new LabelNode();

        Nodes endNodes = builder.appendTo( n );
        endNodes.isValidEndNode( true );

        return n;
    }

    public static ProductionRule terminalConstant( String str ) {
        Node n = build( new StringOp( str, CaseSensitive ) );

        return ProductionRule.terminal( n )
            .withPostProcess( CONSLIST_TOSTRING );
    }

    public static ProductionRule terminalRegexp( String str ) {
        Node n = build( RegexpParser.compile( str ) );

        return ProductionRule.terminal( n )
            .withPostProcess( CONSLIST_TOSTRING );
    }




    private static GraphBuilder SKIPWHITESPACE = RegexpParser.compile( "[ \t\n\r]*" );


    private RegexpParser       regexpParser = new RegexpParser();
    private List<GraphBuilder> builders     = new ArrayList<>();

    private MethodRef          callback;


    public ProductionRuleBuilder appendConstant( String constant ) {
        builders.add( new StringOp(constant, CaseSensitive) );

        return this;
    }

    public ProductionRuleBuilder appendConstantIC( String constant ) {
        builders.add( new StringOp(constant, CaseInsensitive) );

        return this;
    }

    public ProductionRuleBuilder skipWhitespace() {
        builders.add( SKIPWHITESPACE );

        return this;
    }

    public ProductionRuleBuilder appendRegexp( String regexp ) {
        builders.add( regexpParser.parse(regexp) );

        return this;
    }

    public ProductionRuleBuilder withCallback( Class listenerClass, String callbackMethodName ) {
        this.callback = MethodRef.create( listenerClass, callbackMethodName, Integer.TYPE, Integer.TYPE, String.class );

        return this;
    }

    public ProductionRuleBuilder appendRef( String name ) {
        return this;
    }

    public ProductionRule build() {
        Node n = new LabelNode();

        Nodes endNodes = new AndOp( builders ).appendTo( n );

        endNodes.isValidEndNode(true);

//        return new ProductionRule( n, callback );
        return null;
    }

}
