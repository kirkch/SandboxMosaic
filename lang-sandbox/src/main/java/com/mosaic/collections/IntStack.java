package com.mosaic.collections;

import com.mosaic.lang.QA;
import com.mosaic.lang.QA;

/**
 * Stack of int primitives.
 *
 * @Motivation to provide an alternative to java auto boxing and unboxing primitive ints
 * @NotThreadSafe
 */
public class IntStack {

    private int[] stack;
    private int   pos;

    public IntStack() {
        this(3);
    }

    public IntStack( int initialCapacity ) {
        this.stack = new int[initialCapacity];
    }

    public void push( int v ) {
        if ( pos == stack.length ) {
            int[] enlargedStack = new int[stack.length*2];
            System.arraycopy( this.stack, 0, enlargedStack, 0, stack.length );

            this.stack = enlargedStack;
        }

        stack[pos] = v;
        pos++;
    }

    public int pop() {
        if ( pos == 0 ) {
            throw new IllegalStateException( "cannot pop from an empty stack" );
        }

        pos--;

        return stack[pos];
    }

    public int size() {
        return pos;
    }

    public int peek() {
        if ( pos == 0 ) {
            throw new IllegalStateException( "cannot peek from an empty stack" );
        }

        return stack[pos-1];
    }

    /**
     * @param peekOffset zero is the head, one is the second element, two the third etc
     */
    public int peek( int peekOffset ) {
        QA.isTrue( peekOffset >= 0, "peekOffset (%d) must be greater than 0", peekOffset );
        QA.isTrue( peekOffset < pos, "peekOffset (%d) must be less the number of items in the stack (%d)", peekOffset, pos );


        return stack[pos-peekOffset-1];
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
