package com.softwaremosaic.parsers.trie;

import com.mosaic.collections.ConsList;
import com.mosaic.collections.FastStack;
import com.mosaic.collections.InitialValueMap;
import com.mosaic.collections.KV;
import com.mosaic.io.CharPredicate;
import com.mosaic.lang.functional.Function1;
import com.mosaic.lang.functional.Pair;
import com.mosaic.lang.functional.VoidFunction2;
import com.mosaic.utils.ListUtils;
import com.softwaremosaic.parsers.automata.Label;
import com.softwaremosaic.parsers.trie.regexp.CharPredicates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Represents a graph used to present a finite state machine for parsing characters.
 * The nodes represent state, and the edges are predicates that match characters.
 * Walking the graph involves matching the predicates that represent that edge.
 *
 * The type parameter of the node is the type of the payload stored within the
 * node.  The payload can be anything that you like.
 */
@SuppressWarnings("unchecked")
public class CharacterNode<T> implements Iterable<Pair<CharPredicate,CharacterNode<T>>> {

    private T payload;

    private List<Pair<CharPredicate,CharacterNode<T>>> edges = new ArrayList();


    /**
     * Custom data stored with this node.
     */
    public T getPayload() {
        return payload;
    }

    /**
     * Custom data stored with this node.
     */
    public void setPayload() {
        this.payload = payload;
    }


    /**
     * Retrieves the nodes that can be transitioned to by consuming the specified
     * character.  If there are no edges, then an empty list will be returned.
     */
    public CharacterNodes<T> fetch( char c ) {
        List<CharacterNode<T>> nextNodes = new ArrayList();

        for ( Pair<CharPredicate,CharacterNode<T>> edge : edges ) {
            if ( edge.getFirst().matches(c) ) {
                nextNodes.add( edge.getSecond() );
            }
        }

        return new CharacterNodes(nextNodes);
    }


    public boolean hasOutEdges() {
        return !edges.isEmpty();
    }

    public List<Pair<CharPredicate,CharacterNode<T>>> getOutEdges() {
        return Collections.unmodifiableList( edges );
    }

    public CharacterNodes<T> getOutNodes() {
        List outNodes = ListUtils.map(edges, new Function1<Pair<CharPredicate, CharacterNode<T>>, CharacterNode<T>>() {
            public CharacterNode<T> invoke( Pair<CharPredicate, CharacterNode<T>> edge ) {
                return edge.getSecond();
            }
        });

        return new CharacterNodes(outNodes);
    }



    public void append( char c, CharacterNode<T> node ) {
        this.append( CharPredicates.constant(c), node );
    }

    public void append( CharPredicate predicate, CharacterNode<T> node ) {
        edges.add( new Pair(predicate,node) );
    }

    public void append( CharPredicate predicate, CharacterNodes<T> nodes ) {
        for ( CharacterNode<T> n : nodes ) {
            append( predicate, n );
        }
    }

    public void append( char c, CharacterNodes<T> nodes ) {
        append( CharPredicates.constant( c ), nodes );
    }

    public CharacterNodes append( CharPredicate predicate ) {
        CharacterNode<T> newNode = new CharacterNode();

        append( predicate, newNode );

        return new CharacterNodes( newNode );
    }

    public CharacterNodes append( char c ) {
        return append( CharPredicates.constant( c ) );
    }



    /**
     * Scan this nodes out edges for the transition with character c to a specified
     * destination node, then replace the node with a replacement.
     */
    public int replace( CharPredicate predicate, CharacterNode<T> expectedCurrentNode, CharacterNode<T> replacementNode ) {
        int count = 0;

        for ( int i=0; i<edges.size(); i++ ) {
            Pair<CharPredicate,CharacterNode<T>> edge = edges.get(i);

            if ( edge.getSecond() == expectedCurrentNode && edge.getFirst() == predicate ) {
                edges.set( i, new Pair(edge.getFirst(),replacementNode) );

                count++;
            }
        }

        return count;
    }

    public int replace( CharPredicate predicate, CharacterNode<T> expectedCurrentNode, CharacterNodes<T> replacementNodes ) {
        int count = remove( predicate, expectedCurrentNode );

        if ( count > 0 ) {
            append( predicate, replacementNodes );
        }

        return count;
    }

    public int remove( CharPredicate predicate, CharacterNode<T> targetNode ) {
        int count = 0;

        Iterator<Pair<CharPredicate,CharacterNode<T>>> it = edges.iterator();
        while ( it.hasNext() ) {
            Pair<CharPredicate,CharacterNode<T>> edge = it.next();

            if ( edge.getSecond() == targetNode && edge.getFirst() == predicate ) {
                it.remove();

                count++;
            }
        }

        return count;
    }

    public Iterator<Pair<CharPredicate,CharacterNode<T>>> iterator() {
        return edges.iterator();
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
    public void depthFirstPrefixTraversal( VoidFunction2<ConsList<KV<Set<CharPredicate>,CharacterNode<T>>>, Boolean> callbackFunction ) {
        DepthFirstTraverser<T> t = new DepthFirstTraverser(this);

        for ( Path path : t ) {
            callbackFunction.invoke( path.edges, path.isEndOfPath );
        }
    }


    private static class Path<T> {

        public static <T> Path createPath( Path pathUpToNode, Set<CharPredicate> predicates, CharacterNode<T> destinationNode, Set<CharacterNode<T>> alreadyVisitedNodes ) {
            boolean isEndOfPath = !destinationNode.hasOutEdges() || alreadyVisitedNodes.contains(destinationNode);

            return new Path( pathUpToNode, predicates, destinationNode, isEndOfPath );
        }

        private ConsList<KV<Set<CharPredicate>,CharacterNode<T>>> edges;
        private boolean                             isEndOfPath;

        public Path( ConsList<KV<Set<CharPredicate>, CharacterNode<T>>> edges, boolean isEndOfPath ) {
            this.edges       = edges;
            this.isEndOfPath = isEndOfPath;
        }

        public Path( Path pathUpToNode, Set<CharPredicate> labels, CharacterNode<T> destinationNode, boolean isEndOfPath ) {
            KV<Set<Label<T>>,CharacterNode<T>> newEdge = new KV( labels, destinationNode );

            this.edges       = pathUpToNode.edges.cons(newEdge);
            this.isEndOfPath = isEndOfPath;
        }

        public CharacterNode<T> getDestinationNode() {
            return edges.head().getValue();
        }
    }

    private static class DepthFirstTraverser<T> implements Iterable<Path<T>> {

        public static <T> Map<CharacterNode<T>,Set<CharPredicate>> getDestinationsGroupedByCharacters( CharacterNode<T> n ) {
            Map<CharacterNode<T>,Set<CharPredicate>> map = InitialValueMap.identityMapOfSets();

            for ( Pair<CharPredicate,CharacterNode<T>> e : n.getOutEdges() ) {
                Set<CharPredicate> labelsSoFar = map.get(e.getSecond());

                labelsSoFar.add( e.getFirst() );
            }

            return map;
        }


        private Set<CharacterNode<T>> nodesVisitedSoFar = new HashSet();
        private FastStack<Path<T>>    pathsYetToVisit   = new FastStack();


        public DepthFirstTraverser( CharacterNode<T> startingNode ) {
            Path path = new Path(
                ConsList.newConsList(new KV<Set<CharPredicate>, CharacterNode<T>>( Collections.EMPTY_SET, startingNode)),
                !startingNode.hasOutEdges()
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
                    CharacterNode<T> nextNode       = pathUpToNode.getDestinationNode();
                    boolean alreadyVisited = !nodesVisitedSoFar.add(nextNode);
                    if ( alreadyVisited ) {
                        return;
                    }


                    Map<CharacterNode<T>,Set<CharPredicate>> edges = getDestinationsGroupedByCharacters(nextNode);

                    for ( Map.Entry<CharacterNode<T>,Set<CharPredicate>> e : sortEdges(edges) ) {
                        pathsYetToVisit.push( Path.createPath( pathUpToNode, e.getValue(), e.getKey(), nodesVisitedSoFar ));
                    }
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                private List<Map.Entry<CharacterNode<T>,Set<CharPredicate>>> sortEdges( Map<CharacterNode<T>,Set<CharPredicate>> edges ) {
                    List<Map.Entry<CharacterNode<T>, Set<CharPredicate>>> entries = ListUtils.asList( edges.entrySet() );

                    Collections.sort( entries, new Comparator<Map.Entry<CharacterNode<T>, Set<CharPredicate>>>() {
                        public int compare( Map.Entry<CharacterNode<T>, Set<CharPredicate>> a, Map.Entry<CharacterNode<T>, Set<CharPredicate>> b ) {
                            return b.getValue().iterator().next().compareTo(a.getValue().iterator().next());
                        }
                    });

                    return entries;
                }

            };
        }
    }

}
