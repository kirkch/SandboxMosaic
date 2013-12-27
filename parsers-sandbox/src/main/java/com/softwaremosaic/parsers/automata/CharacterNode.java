//package com.softwaremosaic.parsers.automata;
//
//import com.mosaic.collections.ConsList;
//import com.mosaic.collections.FastStack;
//import com.mosaic.collections.InitialValueMap;
//import com.mosaic.collections.KV;
//import com.mosaic.lang.Validate;
//import com.mosaic.lang.functional.Function1;
//import com.mosaic.lang.functional.VoidFunction2;
//import com.mosaic.utils.ListUtils;
//
//import java.util.*;
//
//
///**
// * A Node within a Finite State Automata.  Transitions between nodes are made
// * by traversing characters.  This particular graph supports cycles, multiple
// * edges to the same node, duplicate key edges and self cycles.  In short it is
// * a full fat character graph.
// */
//@SuppressWarnings("unchecked")
//public class CharacterNode<T> implements Node<T> {
//
//    private Map<Character,List<ObjectNode>> edges = new HashMap();
//
//
//    /**
//     * Creates a new transition for the specified character.  If a transition
//     * already exists for the character, and it shares the same label as this
//     * node then the node will be reused.  Else a new node will be created.
//     * Each transition will reuse the label from this node.
//     */
//    public Nodes append(char c) {
//        return append(this.label, c);
//    }
//
//    /**
//     * Creates a new transition for the specified character.  If a transition
//     * already exists for the character, and it shares the same label as this
//     * node then the node will be reused.  Else a new node will be created.
//     */
//    public Nodes append( final String newLabel, char c ) {
//        List<ObjectNode> nextNodes = touchEdges(c);
//
//        List<ObjectNode> nextNodesByLabel = ListUtils.filter( nextNodes, new Function1<ObjectNode, Boolean>() {
//            public Boolean invoke(ObjectNode n) {
//                return Objects.equals(newLabel, n.getLabel());
//            }
//        });
//
//        if ( nextNodesByLabel.isEmpty() ) {
//            ObjectNode newNode = newNode(newLabel);
//
//            nextNodes.add(newNode);
//
//            return new Nodes(Arrays.asList(newNode));
//        } else {
//            return new Nodes(nextNodesByLabel);
//        }
//    }
//
//    /**
//     * Creates a new transition for the specified characters.  If a transition
//     * already exists for the character, and it shares the same label as this
//     * node then the node will be reused.  Else a new node will be created.
//     * Each transition will reuse the label from this node.
//     */
//    public Nodes appendConstant(String a) {
//        return appendConstant( this.label, a );
//    }
//
//    /**
//     * Creates a new transition for the specified characters.  If a transition
//     * already exists for the character, and it shares the same label as this
//     * node then the node will be reused.  Else a new node will be created.
//     */
//    public Nodes appendConstant(String newLabel, CharSequence constant) {
//        Validate.notEmpty( constant, "constant" );
//
//        List<ObjectNode> currentNodes = Arrays.asList(this);
//        for ( int i=0; i<constant.length(); i++ ) {
//            List<ObjectNode> flattenedNextNodes = new ArrayList<>();
//
//            for ( ObjectNode n : currentNodes ) {
//                Nodes nextNodes = n.append( newLabel, constant.charAt(i) );
//
//                flattenedNextNodes.addAll( nextNodes );
//            }
//
//            currentNodes = flattenedNextNodes;
//        }
//
//        return new Nodes(currentNodes);
//    }
//
//    /**
//     * Scan this nodes out edges for the transition with character c to a specified
//     * destination node, then replace the node with a replacement.
//     */
//    public int replace( char c, ObjectNode expectedCurrentNode, ObjectNode replacementNode ) {
//        List<ObjectNode> destinationNodes = edges.get(c);
//        if ( destinationNodes == null ) {
//            return 0;
//        }
//
//
//        int count = 0;
//        for ( int i=0; i<destinationNodes.size(); i++ ) {
//            if ( destinationNodes.get(i) == expectedCurrentNode ) {
//                destinationNodes.set( i, replacementNode );
//
//                count++;
//            }
//        }
//
//        return count;
//    }
//
//    public int replace( char c, ObjectNode expectedCurrentNode, Nodes replacementNodes ) {
//        int count = removeEdge( c, expectedCurrentNode );
//
//        if ( count > 0 ) {
//            appendEdge( c, replacementNodes );
//        }
//
//        return count;
//    }
//
//    public int removeEdge( char c, ObjectNode targetNode ) {
//        List<ObjectNode> destinationNodes = edges.get(c);
//        if ( destinationNodes == null ) {
//            return 0;
//        }
//
//        destinationNodes.remove(targetNode);
//
//        return 1;
//    }
//
//
//    /**
//     * Retrieves the nodes that can be transitioned to by consuming the specified
//     * character.  If there are no edges, then an empty list will be returned.
//     */
//    public Nodes walk( char c ) {
//        List<ObjectNode> nextNodes = edges.get(c);
//        if ( nextNodes == null ) {
//            return Nodes.EMPTY;
//        }
//
//        return new Nodes(nextNodes);
//    }
//
//    /**
//     * A terminal node is a leaf node.  Thus it has no out going connections.
//     */
//    public boolean isTerminal() {
//        return edges.isEmpty();
//    }
//
//    public Nodes walk(String path) {
//        List<ObjectNode> currentNodes = Arrays.asList(this);
//
//        for ( final char c : path.toCharArray() ) {
//            List<Nodes> nextNodes = ListUtils.map(currentNodes, new Function1<ObjectNode,Nodes>() {
//                public Nodes invoke( ObjectNode n ) {
//                    return n.walk(c);
//                }
//            });
//
//            currentNodes = ListUtils.flatten( nextNodes );
//        }
//
//        return new Nodes(currentNodes);
//    }
//
//
//    public List<KV<Character,ObjectNode>> getOutEdges() {
//        List<KV<Character,ObjectNode>> out = new ArrayList<>();
//
//        for ( Map.Entry<Character,List<ObjectNode>> e : edges.entrySet() ) {
//            for ( ObjectNode n : e.getValue() ) {
//                out.add( new KV(e.getKey(), n) );
//            }
//        }
//
//        return out;
//    }
//
//    private Map<ObjectNode,Set<Character>> getDestinationsGroupedByCharacters() {
//        Map<ObjectNode,Set<Character>> map = InitialValueMap.identityMapOfSets();
//
//        for ( Map.Entry<Character,List<ObjectNode>> e : edges.entrySet() ) {
//            for ( ObjectNode n : e.getValue() ) {
//                Set<Character> charactersSoFar = map.get(n);
//                charactersSoFar.add( e.getKey() );
//            }
//        }
//
//        return map;
//    }
//
//    public String toString() {
//        StringBuilder buf = new StringBuilder();
//
//        buf.append( "Node(" );
//        buf.append( label );
//        buf.append( ", " );
//        buf.append( edges.keySet() );
//        buf.append( ')' );
//
//        return buf.toString();
//    }
//
//    private ObjectNode newNode( String newLabel ) {
//        return new ObjectNode(newLabel);
//    }
//
//    private List<ObjectNode> touchEdges( char c ) {
//        List<ObjectNode> nodes = edges.get(c);
//        if ( nodes == null ) {
//            nodes = new ArrayList<>();
//
//            edges.put( c, nodes );
//        }
//
//        return nodes;
//    }
//
//    public ObjectNode skipWhiteSpace() {
//        appendEdge(  ' ', this );
//        appendEdge( '\t', this );
//        appendEdge( '\n', this );
//        appendEdge( '\r', this );
//
//        return this;
//    }
//
//    public void appendEdge( char c, Nodes nodes ) {
//        for ( ObjectNode n : nodes ) {
//            appendEdge( c, n );
//        }
//    }
//
//    public void appendEdge( char c, ObjectNode node ) {
//        List<ObjectNode> nextNodes = touchEdges(c);
//
//        nextNodes.add( node );
//    }
//
//    public Nodes appendRegexpIC( String regexp ) {
//        Nodes pos = new Nodes(this);
//
//        int regexpLength = regexp.length();
//        for ( int i=0; i < regexpLength; i++ ) {
//            char lc = Character.toLowerCase(regexp.charAt(i));
//            char uc = Character.toUpperCase(lc);
//
//            ObjectNode next = newNode(this.label);
//            pos.appendEdge(lc, next);
//            pos.appendEdge(uc, next);
//
//            pos = new Nodes(next);
//        }
//
//        return pos;
//    }
//
//
//    public Nodes getOutNodes() {
//        List<ObjectNode> outNodes = new ArrayList<>();
//
//        for ( List<ObjectNode> nodes : edges.values() ) {
//            outNodes.addAll( nodes );
//        }
//
//        return new Nodes(outNodes);
//    }
//
//    /**
//     * Walks the graph calling the specified callbackFunction for each node past,
//     * as they are past.  The traversal will take the edges sorted by the traversal
//     * characters (lowest first) and grouped so that a traversal to the same node will
//     * not be repeated.<p/>
//     *
//     * The call to the callback function supplies the path walked so far and
//     * a boolean which will be true if the path has reached the last node in the
//     * traversal before it backtracks to another path.<p/>
//     *
//     * The first path will have an empty set of characters, then for each traversal
//     * taken a set of characters for that edge (or edges if more than one) that
//     * lead to the next node.
//     */
//    public void depthFirstPrefixTraversal( VoidFunction2<ConsList<KV<Set<Character>,ObjectNode>>, Boolean> callbackFunction ) {
//        DepthFirstTraverser t = new DepthFirstTraverser(this);
//
//        for ( Path path : t ) {
//            callbackFunction.invoke( path.edges, path.isEndOfPath );
//        }
//    }
//
//    private static class Path {
//
//        public static Path create( Path pathUpToNode, Set<Character> characters, ObjectNode destinationNode, Set<ObjectNode> alreadyVisitedNodes ) {
//            boolean isEndOfPath = destinationNode.isTerminal() || alreadyVisitedNodes.contains(destinationNode);
//
//            return new Path( pathUpToNode, characters, destinationNode, isEndOfPath );
//        }
//
//
//
//        private ConsList<KV<Set<Character>,ObjectNode>> edges;
//        private boolean                           isEndOfPath;
//
//        public Path( ConsList<KV<Set<Character>, ObjectNode>> edges, boolean isEndOfPath ) {
//            this.edges       = edges;
//            this.isEndOfPath = isEndOfPath;
//        }
//
//        public Path( Path pathUpToNode, Set<Character> characters, ObjectNode destinationNode, boolean isEndOfPath ) {
//            assert !pathUpToNode.isEndOfPath : "cannot extend a path that is already marked as complete";
//
//            KV<Set<Character>,ObjectNode> newEdge = new KV( characters, destinationNode );
//
//            this.edges       = pathUpToNode.edges.cons(newEdge);
//            this.isEndOfPath = isEndOfPath;
//        }
//
//        public ObjectNode getDestinationNode() {
//            return edges.head().getValue();
//        }
//    }
//
//    private static class DepthFirstTraverser implements Iterable<Path> {
//
//        private Set<ObjectNode>       nodesVisitedSoFar = new HashSet();
//        private FastStack<Path> pathsYetToVisit   = new FastStack();
//
//
//        public DepthFirstTraverser(ObjectNode startingNode) {
//            Path path = new Path(
//                ConsList.newConsList(new KV<Set<Character>, ObjectNode>(Collections.EMPTY_SET, startingNode)),
//                startingNode.isTerminal()
//            );
//
//            pathsYetToVisit.push(path);
//        }
//
//        @Override
//        public Iterator<Path> iterator() {
//            return new Iterator<Path>() {
//                public boolean hasNext() {
//                    return !pathsYetToVisit.isEmpty();
//                }
//
//                public Path next() {
//                    Path pathSoFar = pathsYetToVisit.pop();
//
//                    scheduleOutEdgesFrom( pathSoFar );
//
//                    return pathSoFar;
//                }
//
//                private void scheduleOutEdgesFrom( Path pathUpToNode ) {
//                    ObjectNode nextNode  = pathUpToNode.getDestinationNode();
//                    boolean alreadyVisited = !nodesVisitedSoFar.add(nextNode);
//                    if ( alreadyVisited ) {
//                        return;
//                    }
//
//
//                    Map<ObjectNode,Set<Character>> edges = nextNode.getDestinationsGroupedByCharacters();
//
//                    for ( Map.Entry<ObjectNode,Set<Character>> e : sortEdges(edges) ) {
//                        pathsYetToVisit.push( Path.create(pathUpToNode, e.getValue(), e.getKey(), nodesVisitedSoFar));
//                    }
//                }
//
//                public void remove() {
//                    throw new UnsupportedOperationException();
//                }
//
//                private List<Map.Entry<ObjectNode,Set<Character>>> sortEdges( Map<ObjectNode,Set<Character>> edges ) {
//                    List<Map.Entry<ObjectNode, Set<Character>>> entries = ListUtils.asList(edges.entrySet());
//
//                    Collections.sort( entries, new Comparator<Map.Entry<ObjectNode, Set<Character>>>() {
//                        public int compare( Map.Entry<ObjectNode, Set<Character>> a, Map.Entry<ObjectNode, Set<Character>> b ) {
//                            return b.getValue().iterator().next() - a.getValue().iterator().next();
//                        }
//                    });
//
//                    return entries;
//                }
//
//            };
//        }
//
//    }
//
//}
