package com.softwaremosaic.parsers.automata;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.FastStack;
import com.mosaic.collections.InitialValueMap;
import com.mosaic.collections.KV;
import com.mosaic.lang.Validate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.VoidFunction1;
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

    public Node(String label) {
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

//    public void setLabel(String label) {
//        this.label = label;
//    }

    /**
     * Creates a new transition for the specified character.  If a transition
     * already exists for the character, and it shares the same label as this
     * node then the node will be reused.  Else a new node will be created.
     * Each transition will reuse the label from this node.
     */
    public Nodes appendCharacter( char c ) {
        return appendCharacter(this.label, c);
    }

    /**
     * Creates a new transition for the specified character.  If a transition
     * already exists for the character, and it shares the same label as this
     * node then the node will be reused.  Else a new node will be created.
     */
    public Nodes appendCharacter( final String newLabel, char c ) {
        List<Node> nextNodes = touchEdges(c);

        List<Node> nextNodesByLabel = ListUtils.filter( nextNodes, new Function1<Node, Boolean>() {
            public Boolean invoke(Node n) {
                return Objects.equals(newLabel, n.getLabel());
            }
        });

        if ( nextNodesByLabel.isEmpty() ) {
            Node newNode = newNode(newLabel);

            nextNodes.add(newNode);

            return new Nodes(Arrays.asList(newNode));
        } else {
            return new Nodes(nextNodesByLabel);
        }
    }

    /**
     * Creates a new transition for the specified characters.  If a transition
     * already exists for the character, and it shares the same label as this
     * node then the node will be reused.  Else a new node will be created.
     * Each transition will reuse the label from this node.
     */
    public Nodes appendConstant(String a) {
        return appendConstant( this.label, a );
    }

    /**
     * Creates a new transition for the specified characters.  If a transition
     * already exists for the character, and it shares the same label as this
     * node then the node will be reused.  Else a new node will be created.
     */
    public Nodes appendConstant(String newLabel, CharSequence constant) {
        Validate.notEmpty( constant, "constant" );

        List<Node> currentNodes = Arrays.asList(this);
        for ( int i=0; i<constant.length(); i++ ) {
            List<Node> flattenedNextNodes = new ArrayList<>();

            for ( Node n : currentNodes ) {
                Nodes nextNodes = n.appendCharacter( newLabel, constant.charAt(i) );

                flattenedNextNodes.addAll( nextNodes );
            }

            currentNodes = flattenedNextNodes;
        }

        return new Nodes(currentNodes);
    }

    /**
     * Retrieves the nodes that can be transitioned to by consuming the specified
     * character.  If there are no edges, then an empty list will be returned.
     */
    public Nodes walk( char c ) {
        List<Node> nextNodes = edges.get(c);
        if ( nextNodes == null ) {
            return Nodes.EMPTY;
        }

        return new Nodes(nextNodes);
    }

    /**
     * A terminal node is a leaf node.  Thus it has no out going connections.
     */
    public boolean isTerminal() {
        return edges.isEmpty();
    }

    public Nodes walk(String path) {
        List<Node> currentNodes = Arrays.asList(this);

        for ( final char c : path.toCharArray() ) {
            List<Nodes> nextNodes = ListUtils.map(currentNodes, new Function1<Node,Nodes>() {
                public Nodes invoke( Node n ) {
                    return n.walk(c);
                }
            });

            currentNodes = ListUtils.flatten( nextNodes );
        }

        return new Nodes(currentNodes);
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

    private Map<Node,Set<Character>> getDestinationsGroupedByCharacters() {
        Map<Node,Set<Character>> map = InitialValueMap.identityMapOfSets();

        for ( Map.Entry<Character,List<Node>> e : edges.entrySet() ) {
            for ( Node n : e.getValue() ) {
                Set<Character> charactersSoFar = map.get(n);
                charactersSoFar.add( e.getKey() );
            }
        }

        return map;
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

    private Node newNode( String newLabel ) {
        return new Node(newLabel);
    }

    private List<Node> touchEdges( char c ) {
        List<Node> nodes = edges.get(c);
        if ( nodes == null ) {
            nodes = new ArrayList<>();

            edges.put( c, nodes );
        }

        return nodes;
    }

    public Node skipWhiteSpace() {
        appendEdge(  ' ', this );
        appendEdge( '\t', this );
        appendEdge( '\n', this );
        appendEdge( '\r', this );

        return this;
    }

    void appendEdge( char c, Nodes nodes ) {
        for ( Node n : nodes ) {
            appendEdge( c, n );
        }
    }

    void appendEdge( char c, Node node ) {
        List<Node> nextNodes = touchEdges(c);

        nextNodes.add( node );
    }

    public Nodes appendRegexpIC( String regexp ) {
        Nodes pos = new Nodes(this);

        int regexpLength = regexp.length();
        for ( int i=0; i < regexpLength; i++ ) {
            char lc = Character.toLowerCase(regexp.charAt(i));
            char uc = Character.toUpperCase(lc);

            Node next = newNode(this.label);
            pos.appendEdge(lc, next);
            pos.appendEdge(uc, next);

            pos = new Nodes(next);
        }

        return pos;
    }


    public Nodes getOutNodes() {
        List<Node> outNodes = new ArrayList<>();

        for ( List<Node> nodes : edges.values() ) {
            outNodes.addAll( nodes );
        }

        return new Nodes(outNodes);
    }

    /**
     * Walks the graph calling the specified callbackFunction for each node past,
     * as they are past.  The traversal will take the edges sorted by the traversal
     * characters (lowest first) and grouped so that a traversal to the same node will
     * not be repeated.<p/>
     *
     * The call to the callback function supplies the path walked so far.  The
     * first not will have an empty set of characters, then for each traversal
     * taken a set of characters for that edge (or edges if more than one) that
     * lead to the next node.
     */
    public void depthFirstPrefixTraversal( VoidFunction1<ConsList<KV<Set<Character>,Node>>> callbackFunction ) {
        DepthFirstTraverser t = new DepthFirstTraverser(this);

        for ( ConsList<KV<Set<Character>,Node>> path : t ) {
            callbackFunction.invoke( path );
        }
    }


    private static class DepthFirstTraverser implements Iterable<ConsList<KV<Set<Character>,Node>>> {

        private Set<Node>                                    nodesVisitedSoFar = new HashSet();
        private FastStack<ConsList<KV<Set<Character>,Node>>> pathsYetToVisit   = new FastStack();


        public DepthFirstTraverser(Node startingNode) {
            pathsYetToVisit.push(ConsList.newConsList(new KV<Set<Character>, Node>(Collections.EMPTY_SET, startingNode)));
        }

        @Override
        public Iterator<ConsList<KV<Set<Character>, Node>>> iterator() {
            return new Iterator<ConsList<KV<Set<Character>, Node>>>() {
                public boolean hasNext() {
                    return !pathsYetToVisit.isEmpty();
                }

                public ConsList<KV<Set<Character>, Node>> next() {
                    ConsList<KV<Set<Character>,Node>> pathSoFar = pathsYetToVisit.pop();
                    Node                              nextNode  = pathSoFar.head().getValue();

                    scheduleOutEdgesFrom( nextNode, pathSoFar );

                    return pathSoFar;
                }

                private void scheduleOutEdgesFrom(Node nextNode, ConsList<KV<Set<Character>, Node>> pathUpToNode) {
                    boolean alreadyVisited = !nodesVisitedSoFar.add(nextNode);
                    if ( alreadyVisited ) {
                        return;
                    }


                    Map<Node,Set<Character>> edges = nextNode.getDestinationsGroupedByCharacters();

                    for ( Map.Entry<Node,Set<Character>> e : sortEdges(edges) ) {
                        KV<Set<Character>,Node> newEdge = new KV( e.getValue(), e.getKey() );
                        pathsYetToVisit.push(pathUpToNode.cons(newEdge));
                    }
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                private List<Map.Entry<Node,Set<Character>>> sortEdges( Map<Node,Set<Character>> edges ) {
                    List<Map.Entry<Node, Set<Character>>> entries = ListUtils.asList(edges.entrySet());

                    Collections.sort( entries, new Comparator<Map.Entry<Node, Set<Character>>>() {
                        public int compare( Map.Entry<Node, Set<Character>> a, Map.Entry<Node, Set<Character>> b ) {
                            return b.getValue().iterator().next() - a.getValue().iterator().next();
                        }
                    });

                    return entries;
                }

            };
        }
    }

}
