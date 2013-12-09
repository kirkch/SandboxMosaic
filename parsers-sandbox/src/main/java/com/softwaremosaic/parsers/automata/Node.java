package com.softwaremosaic.parsers.automata;

import com.mosaic.collections.KV;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.utils.ListUtils;

import java.util.*;

/**
 * A Node within a Finite State Automata.  Transitions between nodes are made
 * by traversing characters.  This particular graph supports cycles, multiple
 * edges to the same node, duplicate key edges and self cycles.  In short it is
 * a full fat character graph.
 */
@SuppressWarnings("unchecked")
public class Node {

    private String label;

    private Map<Character,List<Node>> edges = new HashMap();


    public Node() {}

    public Node( String label ) {
        this.label = label;
    }


    /**
     * Retrieve a descriptive label for this node.  A Parser will probably
     * put the name of the BNF expression that this node belongs to in it to
     * aid debugging and error messages.
     */
    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    /**
     * Creates a new transition for the specified character.  If a transition
     * already exists for the character, and it shares the same label as this
     * node then the node will be reused.  Else a new node will be created.
     * Each transition will reuse the label from this node.
     */
    public List<Node> appendCharacter( char c ) {
        return appendCharacter(this.label, c);
    }

    /**
     * Creates a new transition for the specified character.  If a transition
     * already exists for the character, and it shares the same label as this
     * node then the node will be reused.  Else a new node will be created.
     */
    public List<Node> appendCharacter( final String newLabel, char c ) {
        List<Node> nextNodes = touchEdges(c);

        List<Node> nextNodesByLabel = ListUtils.filter( nextNodes, new Function1<Node, Boolean>() {
            public Boolean invoke(Node n) {
                boolean equals = Objects.equals(newLabel, n.getLabel());
                return equals;
            }
        });

        if ( nextNodesByLabel.isEmpty() ) {
            Node newNode = newNode(newLabel);

            nextNodes.add(newNode);

            return Arrays.asList(newNode);
        } else {
            return nextNodesByLabel;
        }
    }

    /**
     * Creates a new transition for the specified characters.  If a transition
     * already exists for the character, and it shares the same label as this
     * node then the node will be reused.  Else a new node will be created.
     * Each transition will reuse the label from this node.
     */
    public List<Node> appendConstant( String a ) {
        return appendConstant( this.label, a );
    }

    /**
     * Creates a new transition for the specified characters.  If a transition
     * already exists for the character, and it shares the same label as this
     * node then the node will be reused.  Else a new node will be created.
     */
    public List<Node> appendConstant( String newLabel, CharSequence constant ) {
        Validate.notEmpty( constant, "constant" );

        List<Node> currentNodes = Arrays.asList(this);
        for ( int i=0; i<constant.length(); i++ ) {
            List<Node> flattenedNextNodes = new ArrayList<>();

            for ( Node n : currentNodes ) {
                List<Node> nextNodes = n.appendCharacter( newLabel, constant.charAt(i) );

                flattenedNextNodes.addAll( nextNodes );
            }

            currentNodes = flattenedNextNodes;
        }

        return currentNodes;
    }

    /**
     * Retrieves the nodes that can be transitioned to by consuming the specified
     * character.  If there are no edges, then an empty list will be returned.
     */
    public List<Node> walk( char c ) {
        List<Node> nextNodes = edges.get(c);
        if ( nextNodes == null ) {
            nextNodes = Collections.EMPTY_LIST;
        }

        return nextNodes;
    }

    /**
     * A terminal node is a leaf node.  Thus it has no out going connections.
     */
    public boolean isTerminal() {
        return edges.isEmpty();
    }

    private Node newNode( String newLabel ) {
        return new Node(newLabel);
    }



    public List<Node> walk( String path ) {
        List<Node> currentNodes = Arrays.asList(this);

        for ( final char c : path.toCharArray() ) {
            List<List<Node>> nextNodes = ListUtils.map(currentNodes, new Function1<Node,List<Node>>() {
                public List<Node> invoke( Node n ) {
                    return n.walk(c);
                }
            });

            currentNodes = ListUtils.flatten( nextNodes );
        }

        return currentNodes;
    }

    public List<KV<Character,Node>> getOutEdges() {
        List<KV<Character,Node>> out = new ArrayList<>();

        for ( Map.Entry<Character,List<Node>> e : edges.entrySet() ) {
            for ( Node n : e.getValue() ) {
                out.add( new KV(e.getKey(), n) );
            }
        }

        return out;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append( "Node(" );
        buf.append( label );
        buf.append( ", " );
        buf.append( edges.keySet() );
        buf.append( ')' );

        return buf.toString();
    }

    private List<Node> touchEdges( char c ) {
        List<Node> nodes = edges.get(c);
        if ( nodes == null ) {
            nodes = new ArrayList<>();

            edges.put( c, nodes );
        }

        return nodes;
    }

}
