package com.mosaic.push;

import java.util.List;

/**
 * A node within a processing graph. Think of a processing node as being similar to a Unix command that was designed to
 * be chained; such as 'sort', 'head', 'grep', 'tail', or 'cut'. <p/>
 *
 * Each processing node solves exactly one problem, and it solves it very well. Just as with Unix commands, processing
 * nodes support being chained without any knowledge of the chain itself.<p/>
 *
 * Unlike Unix commands, processing nodes are allowed to be stateful and may have multiple recipients. The rule however
 * is that they must be immutable. This trade off empowers the runtime environment to offer more value add services
 * while staying simple. In order for a node to change its state, when processing its input the node must create a new
 * instance of itself with the new state and then return that new instance as the result of process(input). The runtime
 * framework will then manage the rest.
 */
public interface Node<I,O> {

    /**
     * Provides documentation for the processing node at runtime. Used by runtime maintenance and design tools.
     */
    public Description getDescription();

    /**
     * Returns the immutable value that will be sent on to the next processing node. Returns a list as it is possible
     * for a single input to a node to generate multiple output signals in a burst. The runtime environment will dispatch
     * each signal to the next nodes in the graph, one at a time. <p/>
     *
     * All nodes start with a null signal. After calling process(input) the runtime framework will call getSignal(). If
     * getSignal() returns the exactly the same instance as last time then processing stops there. If however it has changed
     * then the signal will be passed on as the input to the next processing node.
     */
    public List<O> getSignal();

    /**
     * Perform the processing nodes work. Given the supplied input, carry out the processing nodes function on the
     * input and then return a new instance of itself containing its output and any modified internal state. It is important
     * that nodes are immutable, so it is important that the node does not change itself or any object that it references.<p/>
     *
     * Nodes are allowed to return themselves or null. This means that the output of the node has not changed, and thus
     * no new signal will be sent on to the next node in the graph. This allows nodes to build up state incrementally
     * making processing of chunked data simpler.<p/>
     */
    public Node<I,O> process( I input );

}
