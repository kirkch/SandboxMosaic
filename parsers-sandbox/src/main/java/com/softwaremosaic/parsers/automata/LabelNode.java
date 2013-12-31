package com.softwaremosaic.parsers.automata;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.FastStack;
import com.mosaic.collections.InitialValueMap;
import com.mosaic.collections.KV;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.utils.ListUtils;
import com.mosaic.utils.StringUtils;

import java.util.*;


/**
 *
 */
@SuppressWarnings("unchecked")
public class LabelNode<T extends Comparable<T>> implements Node<T> {

    private List<KV<Label<T>,Node<T>>> edges = new ArrayList();

    private boolean isValidEndNode;



    public Nodes append( T label ) {
        return append( Labels.singleValue(label) );
    }

    public Nodes append( Label<T> label ) {
        Node<T> newNode = new LabelNode();

        append( label, newNode );

        return new Nodes( newNode );
    }

    public void append( T label, Nodes<T> nodes ) {
        append( Labels.singleValue(label), nodes );
    }

    public void append( Label<T> label, Nodes<T> nodes ) {
        for ( Node<T> n : nodes ) {
            append( label, n );
        }
    }

    public void append( T label, Node<T> node ) {
        append( Labels.singleValue(label), node );
    }

    public void append( Label<T> label, Node<T> newNode ) {
        edges.add( new KV(label,newNode) );
    }

    public int replace( Label<T> label, Node<T> expectedCurrentNode, Node<T> replacementNode ) {
        int count = 0;

        for ( int i=0; i<edges.size(); i++ ) {
            KV<Label<T>,Node<T>> edge = edges.get(i);

            if ( edge.getValue() == expectedCurrentNode && edge.getKey() == label ) {
                edges.set( i, new KV(edge.getKey(),replacementNode) );

                count++;
            }
        }

        return count;
    }

    public int replace( Label<T> label, Node<T> expectedCurrentNode, Nodes<T> replacementNodes ) {
        int count = remove( label, expectedCurrentNode );

        if ( count > 0 ) {
            append( label, replacementNodes );
        }

        return count;
    }

    public int remove( Label<T> label, Node<T> targetNode ) {
        int count = 0;

        Iterator<KV<Label<T>,Node<T>>> it = edges.iterator();
        while ( it.hasNext() ) {
            KV<Label<T>,Node<T>> edge = it.next();

            if ( edge.getValue() == targetNode && edge.getKey() == label ) {
                it.remove();

                count++;
            }
        }

        return count;
    }

    public Nodes<T> walk( T label ) {
        List<Node<T>> nextNodes = new ArrayList();

        for ( KV<Label<T>,Node<T>> edge : edges ) {
            if ( edge.getKey().matches(label) ) {
                nextNodes.add( edge.getValue() );
            }
        }

        return new Nodes(nextNodes);
    }

    public boolean isValidEndNode() {
        return edges.isEmpty() || isValidEndNode;
    }

    public boolean hasOutEdges() {
        return !edges.isEmpty();
    }

    public void isValidEndNode( boolean flag ) {
        this.isValidEndNode = flag;
    }

    public Nodes<T> walk( T...path ) {
        return this.walk( Arrays.asList( path ) );
    }

    public Nodes<T> walk( Iterable<T> path ) {
        List<Node<T>> currentNodes = Arrays.asList( (Node<T>) this );

        for ( final T label : path ) {
            List<Nodes<T>> nextNodes = ListUtils.map( currentNodes, new Function1<Node<T>, Nodes<T>>() {
                public Nodes<T> invoke( Node<T> n ) {
                    return n.walk( label );
                }
            } );

            currentNodes = ListUtils.flatten( nextNodes );
        }

        return new Nodes(currentNodes);
    }


    public List<Label<T>> getOutLabels() {
        return ListUtils.map(edges, new Function1<KV<Label<T>, Node<T>>, Label<T>>() {
            public Label<T> invoke( KV<Label<T>, Node<T>> edge ) {
                return edge.getKey();
            }
        });
    }

    public List<KV<Label<T>,Node<T>>> getOutEdges() {
        return Collections.unmodifiableList( edges );
    }

    public Nodes<T> getOutNodes() {
        List outNodes = ListUtils.map(edges, new Function1<KV<Label<T>, Node<T>>, Node<T>>() {
            public Node<T> invoke( KV<Label<T>, Node<T>> edge ) {
                return edge.getValue();
            }
        });

        return new Nodes(outNodes);
    }

    public Iterator<KV<Label<T>,Node<T>>> iterator() {
        return edges.iterator();
    }


    public String toString() {
        StringBuilder buf = new StringBuilder();

        List labels = ListUtils.map(edges, new Function1<KV<Label<T>, Node<T>>, Label<T>>() {
            public Label<T> invoke( KV<Label<T>, Node<T>> edge ) {
                return edge.getKey();
            }
        });

        buf.append( "Node(" );
        StringUtils.join( buf, labels, ", ");
        buf.append( ')' );

        return buf.toString();
    }

    /**
     * Walks the graph calling the specified callbackFunction for each node past,
     * as they are past.  The traversal will take the edges sorted by the traversal
     * characters (lowest first) and grouped so that a traversal to the same node will
     * not be repeated.<p/>
     *
     * The call to the callback function supplies the path walked so far and
     * a boolean which will be true if the path has reached the last node in the
     * traversal before it backtracks to another path.<p/>
     *
     * The first path will have an empty set of characters, then for each traversal
     * taken a set of characters for that edge (or edges if more than one) that
     * lead to the next node.
     */
    public void depthFirstPrefixTraversal( VoidFunction2<ConsList<KV<Set<Label<T>>,Node<T>>>, Boolean> callbackFunction ) {
        DepthFirstTraverser<T> t = new DepthFirstTraverser(this);

        for ( Path path : t ) {
            callbackFunction.invoke( path.edges, path.isEndOfPath );
        }
    }


    private static class Path<T extends Comparable<T>> {

        public static <T extends Comparable<T>> Path createPath( Path pathUpToNode, Set<Label<T>> labels, Node<T> destinationNode, Set<Node<T>> alreadyVisitedNodes ) {
            boolean isEndOfPath = destinationNode.isValidEndNode() || alreadyVisitedNodes.contains(destinationNode);

            return new Path( pathUpToNode, labels, destinationNode, isEndOfPath );
        }

        private ConsList<KV<Set<Label<T>>,Node<T>>> edges;
        private boolean                             isEndOfPath;

        public Path( ConsList<KV<Set<Label<T>>, Node<T>>> edges, boolean isEndOfPath ) {
            this.edges       = edges;
            this.isEndOfPath = isEndOfPath;
        }

        public Path( Path pathUpToNode, Set<Label<T>> labels, Node<T> destinationNode, boolean isEndOfPath ) {
            assert !pathUpToNode.isEndOfPath : "cannot extend a path that is already marked as complete";

            KV<Set<Label<T>>,Node<T>> newEdge = new KV( labels, destinationNode );

            this.edges       = pathUpToNode.edges.cons(newEdge);
            this.isEndOfPath = isEndOfPath;
        }

        public Node getDestinationNode() {
            return edges.head().getValue();
        }
    }

    private static class DepthFirstTraverser<T extends Comparable<T>> implements Iterable<Path<T>> {

        public static <T extends Comparable<T>> Map<Node<T>,Set<Label<T>>> getDestinationsGroupedByCharacters( Node<T> n ) {
            Map<Node<T>,Set<Label<T>>> map = InitialValueMap.identityMapOfSets();

            for ( KV<Label<T>,Node<T>> e : n.getOutEdges() ) {
                Set<Label<T>> labelsSoFar = map.get(e.getValue());

                labelsSoFar.add( e.getKey() );
            }

            return map;
        }


        private Set<Node<T>>       nodesVisitedSoFar = new HashSet();
        private FastStack<Path<T>> pathsYetToVisit   = new FastStack();


        public DepthFirstTraverser( Node startingNode ) {
            Path path = new Path(
                ConsList.newConsList(new KV<Set<Label<T>>, Node<T>>( Collections.EMPTY_SET, startingNode)),
                startingNode.isValidEndNode()
            );

            pathsYetToVisit.push(path);
        }

        public Iterator<Path<T>> iterator() {
            return new Iterator<Path<T>>() {
                public boolean hasNext() {
                    return !pathsYetToVisit.isEmpty();
                }

                public Path<T> next() {
                    Path pathSoFar = pathsYetToVisit.pop();

                    scheduleOutEdgesFrom( pathSoFar );

                    return pathSoFar;
                }

                private void scheduleOutEdgesFrom( Path<T> pathUpToNode ) {
                    Node<T> nextNode       = pathUpToNode.getDestinationNode();
                    boolean alreadyVisited = !nodesVisitedSoFar.add(nextNode);
                    if ( alreadyVisited ) {
                        return;
                    }


                    Map<Node<T>,Set<Label<T>>> edges = getDestinationsGroupedByCharacters(nextNode);

                    for ( Map.Entry<Node<T>,Set<Label<T>>> e : sortEdges(edges) ) {
//                    for ( KV<T,Node<T>> edge : nextNode ) {
                        pathsYetToVisit.push( Path.createPath( pathUpToNode, e.getValue(), e.getKey(), nodesVisitedSoFar ));
                    }
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                private List<Map.Entry<Node<T>,Set<Label<T>>>> sortEdges( Map<Node<T>,Set<Label<T>>> edges ) {
//                    return ListUtils.asList(edges.entrySet());

                    List<Map.Entry<Node<T>, Set<Label<T>>>> entries = ListUtils.asList(edges.entrySet());

                    Collections.sort( entries, new Comparator<Map.Entry<Node<T>, Set<Label<T>>>>() {
                        public int compare( Map.Entry<Node<T>, Set<Label<T>>> a, Map.Entry<Node<T>, Set<Label<T>>> b ) {
                            return b.getValue().iterator().next().compareTo(a.getValue().iterator().next());
                        }
                    });

                    return entries;
                }

            };
        }

    }

}
