package com.mosaic.collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class IntStackTest {

    @Test
    public void givenEmptyStack_pop_expectException() {
        IntStack stack = new IntStack();

        try {
            stack.pop();

            fail( "Expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "cannot pop from an empty stack", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyStack_peek_expectException() {
        IntStack stack = new IntStack();

        try {
            stack.peek();

            fail( "Expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "cannot peek from an empty stack", e.getMessage() );
        }
    }

    @Test
    public void givenEmptyStack_size_expectZero() {
        IntStack stack = new IntStack();

        assertEquals( 0, stack.size() );
    }

    @Test
    public void givenOneElementStack_size_expectOne() {
        IntStack stack = new IntStack();

        stack.push( 7 );

        assertEquals( 1, stack.size() );
    }

    @Test
    public void givenOneElementStack_peek_expectValueAndStackToNotChange() {
        IntStack stack = new IntStack();
        stack.push( 7 );

        assertEquals( 7, stack.peek() );
        assertEquals( 1, stack.size() );
    }

    @Test
    public void givenOneElementStack_pop_expectValue() {
        IntStack stack = new IntStack();
        stack.push( 7 );

        assertEquals( 7, stack.pop() );
    }

    @Test
    public void givenOneElementStack_pop_expectSizeToBecomeZero() {
        IntStack stack = new IntStack();

        stack.push( 7 );
        stack.pop();

        assertEquals( 0, stack.size() );
    }

    @Test
    public void givenOneElementStack_popTwice_expectException() {
        IntStack stack = new IntStack();

        stack.push( 7 );
        stack.pop();

        try {
            stack.pop();
            fail( "Expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "cannot pop from an empty stack", e.getMessage() );
        }
    }

    @Test
    public void givenTwoElementStack_popTwice_expectCorrectValues() {
        IntStack stack = new IntStack();

        stack.push( 7 );
        stack.push( 8 );

        assertEquals( 8, stack.pop() );
        assertEquals( 1, stack.size() );

        assertEquals( 7, stack.pop() );
        assertEquals( 0, stack.size() );
    }

    @Test
    public void givenTwoElementStack_popThreeTimes_expectException() {
        IntStack stack = new IntStack();

        stack.push( 7 );
        stack.push( 8 );

        stack.pop();
        stack.pop();

        try {
            stack.pop();
            fail( "Expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "cannot pop from an empty stack", e.getMessage() );
        }
    }

    @Test
    public void givenTwoElementStack_peekAtHead_expectValue() {
        IntStack stack = new IntStack();

        stack.push( 7 );
        stack.push( 8 );

        assertEquals( 8, stack.peek() );
    }

    @Test
    public void givenTwoElementStack_peekAtSecondValue_expectValue() {
        IntStack stack = new IntStack();

        stack.push( 7 );
        stack.push( 8 );

        assertEquals( 7, stack.peek(1) );
    }

    @Test
    public void givenTwoElementStack_peekAtThirdValue_expectException() {
        IntStack stack = new IntStack();

        stack.push( 7 );
        stack.push( 8 );

        try {
            stack.peek( 2 );
            fail( "Expected IllegalStateException" );
        } catch (IllegalStateException e) {
            assertEquals( "peekOffset (2) must be less the number of items in the stack (2)", e.getMessage() );
        }
    }

    @Test
    public void givenStackWithInitialCapacity3_pushFourItems_expectAllItemsToBeOnTheStack() {
        IntStack stack = new IntStack(3);

        stack.push(  7 );
        stack.push(  8 );
        stack.push(  9 );
        stack.push( 10 );

        assertEquals(  4, stack.size() );
        assertEquals( 10, stack.peek(0) );
        assertEquals(  9, stack.peek(1) );
        assertEquals(  8, stack.peek(2) );
        assertEquals(  7, stack.peek(3) );
    }

}
