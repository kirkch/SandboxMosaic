package com.softwaremosaic.parsers.automata;

import com.mosaic.collections.ConsList;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.reflect.MethodRef;

import java.util.List;


/**
 * Describes how to map a stream of inputs to an output.
 */
@SuppressWarnings("unchecked")
public abstract class ProductionRule<I extends Comparable<I>, O extends Comparable<O>> {

    public static ProductionRule terminal( Node n ) {
        return new TerminalProductionRule( n );
    }

    public static ProductionRule nonTerminal( ConsList<ProductionRule> productionRules ) {
        return new NonTerminalProductionRule( productionRules );
    }




    private Function1<I,I>       prefilter   = Function1.PASSTHROUGH;
    private Function1<List<I>,O> postProcess = Function1.PASSTHROUGH;


    /**
     * Terminals capture their input, NonTerminals capture their childrens output.
     */
    private boolean capture = true;

    private MethodRef listenerCallback;

    public abstract boolean isTerminal();
    public abstract boolean isNonTerminal();

    public abstract Node<I> getNode();
    public abstract ConsList<ProductionRule> getChildRules();



    public void setPrefilter( Function1<I, I> prefilter ) {
        this.prefilter = prefilter;
    }

    public void setPostProcess( Function1<List<I>, O> postProcess ) {
        this.postProcess = postProcess;
    }

    public void setListenerCallback( MethodRef listenerCallback ) {
        this.listenerCallback = listenerCallback;
    }

    public void setCapture( boolean capture ) {
        this.capture = capture;
    }

    public Function1<I, I> getPrefilter() {
        return prefilter;
    }

    public Function1<List<I>, O> getPostProcess() {
        return postProcess;
    }

    public boolean isCapture() {
        return capture;
    }

    public MethodRef getListenerCallback() {
        return listenerCallback;
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
