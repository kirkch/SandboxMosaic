package com.softwaremosaic.parsers.automata;

import com.mosaic.collections.ConsList;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.reflect.MethodRef;


/**
 * Describes how to map a stream of inputs to an output.
 */
@SuppressWarnings("unchecked")
public abstract class ProductionRule<I extends Comparable<I>, O extends Comparable<O>> {

    public static ProductionRule terminal( Node n ) {
        return new TerminalProductionRule( n );
    }

    public static ProductionRule nonTerminal( ProductionRule...productionRules ) {
        return nonTerminal( ConsList.newConsList( productionRules ) );
    }

    public static ProductionRule nonTerminal( ConsList <ProductionRule> productionRules ) {
        return new NonTerminalProductionRule( productionRules );
    }



    private String               label;

    private Function1<ConsList<I>,ConsList<O>> postProcess = Function1.PASSTHROUGH;


    /**
     * Terminals capture their input, NonTerminals capture their children's output.
     */
    private boolean capture = false;

    private MethodRef listenerCallback;

    public abstract boolean isTerminal();
    public abstract boolean isNonTerminal();

    public abstract Node<I> getNode();
    public abstract ConsList<ProductionRule> getChildRules();



    public ProductionRule withPostProcess( Function1<ConsList<I>, ConsList<O>> postProcess ) {
        this.postProcess = postProcess;

        return this;
    }

    public ProductionRule withCallback( Class listenerClass, String methodName, Class expectedArgType ) {
        return withCallback( MethodRef.create(listenerClass, methodName, Integer.TYPE, Integer.TYPE, expectedArgType) );
    }

    public ProductionRule withCallback( MethodRef listenerCallback ) {
        this.listenerCallback = listenerCallback;

        return this;
    }

    public ProductionRule withCapture( boolean capture ) {
        this.capture = capture;

        return this;
    }

    public Function1<ConsList<I>, ConsList<O>> getPostProcess() {
        return postProcess;
    }

    public boolean isCapture() {
        return capture;
    }

    public MethodRef getListenerCallback() {
        return listenerCallback;
    }

    public String getLabel() {
        return label;
    }

    public ProductionRule withLabel( String label ) {
        this.label = label;

        return this;
    }

    public String toString() {
        return label == null ? "anonrule" : label;
    }


    private static class TerminalProductionRule<I extends Comparable<I>, O extends Comparable<O>> extends ProductionRule<I,O> {
        private Node startingNode;

        public TerminalProductionRule( Node n ) {
            startingNode = n;
        }

        public boolean isTerminal() {
            return true;
        }

        public boolean isNonTerminal() {
            return false;
        }

        public Node<I> getNode() {
            return startingNode;
        }

        public ConsList<ProductionRule> getChildRules() {
            throw new IllegalStateException( "Terminal rules do not have children" );
        }
    }

    private static class NonTerminalProductionRule<I extends Comparable<I>, O extends Comparable<O>> extends ProductionRule<I,O> {
        private ConsList<ProductionRule> childRules;

        public NonTerminalProductionRule( ConsList<ProductionRule> productionRules ) {
            childRules = productionRules;
        }

        public boolean isTerminal() {
            return false;
        }

        public boolean isNonTerminal() {
            return true;
        }

        public Node<I> getNode() {
            throw new IllegalStateException( "NonTerminal rules do not starting nodes" );
        }

        public ConsList<ProductionRule> getChildRules() {
            return childRules;
        }
    }
}
