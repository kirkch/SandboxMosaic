package com.softwaremosaic.parsers.automata;

/**
 * A finite state automata used to match text.  Finite State Automatas are
 * commonly used behind the scenes to represent and execute regular expressions.
 * They are graphs of nodes, connected together by the characters required
 * to transition the graph between states.  For example a FSA that represents
 * the regular expression 'abc' would be represented by the graph 'ROOT->a->b->c'.
 * And the expression 'a|b' would be a graph with the following two edges both
 * coming from the root node 'ROOT->a' and 'ROOT->b'.
 */
public class Automata {

    private Node startingNode = new Node();

    public Node getStartingNode() {
        return startingNode;
    }

}
