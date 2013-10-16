package com.mosaic.collections;

import com.mosaic.lang.functional.Nullable;
import com.mosaic.lang.functional.Function1;

import java.util.Objects;

/**
 * A classic cons-list as made popular by functional programming.  Essentially
 * an immutable linked list made up of a head value and another cons-list called
 * the tail. <p/>
 *
 * NB uses recursive implementations that will overflow the stack if the
 * list is beyond a certain length.  For now avoid using in cases where
 * the list may go beyond a view hundred.  The effective max will probably
 * be around 2k; but it depends on the amount of space left on the stack.
 */
@SuppressWarnings("unchecked")
public abstract class ConsList<T> {
    public static final ConsList Nil = new NilNode();


    public abstract T head();
    public abstract ConsList<T> tail();

    public abstract boolean isEmpty();

    public boolean hasContents() {
        return !isEmpty();
    }

    /**
     * Create a new list that contains the mapped version of each value in this list.
     */
    public abstract <B> ConsList<T> map( Function1<T,B> mappingFunction );

    /**
     * Returns the first value in this list that satisfies the specified predicateFunction.
     * If predicateFunction returns false for every value, then this function will
     * return NULL.
     */
    public abstract Nullable<T> fetchFirstMatch( Function1<T,Boolean> predicateFunction );

    /**
     * Returns a single mapped value from this list.  The value returned will
     * be the first non-null result returned from the mappingFunction.  Values
     * from this list will be passed to the mappingFunction one at a time starting
     * from the head.
     */
    public abstract <B> Nullable<B> mapSingleValue( Function1<T,Nullable<B>> mappingFunction );

    /**
     * Creates a new list with the supplied value as its head and this list
     * as its tail.
     */
    public ConsList<T> cons( T v ) {
        return new ElementNode( v, this );
    }

    /**
     * Returns a new list that is has the same contents as this list but in the
     * opposite order.
     */
    public ConsList<T> reverse() {
        ConsList listToReverse = this;
        ConsList reversedList  = Nil;

        while ( listToReverse.hasContents() ) {
            reversedList = reversedList.cons( listToReverse.head() );
            listToReverse = listToReverse.tail();
        }

        return reversedList;
    }



    protected abstract boolean shallowEquals( ConsList<T> other );


    public abstract String toString();

    private static class NilNode<T> extends ConsList<T> {

        public T head() {
            throw new UnsupportedOperationException("Nil does not have a head value");
        }

        public ConsList<T> tail() {
            throw new UnsupportedOperationException("Nil does not have a tail value");
        }

        public boolean isEmpty() {
            return true;
        }

        public <B> ConsList<T> map( Function1<T,B> mappingFunction ) {
            return this;
        }

        public <B> Nullable<B> mapSingleValue( Function1<T,Nullable<B>> mappingFunction ) {
            return Nullable.NULL;
        }

        public Nullable<T> fetchFirstMatch( Function1<T,Boolean> predicateFunction ) {
            return Nullable.NULL;
        }

        public String toString() {
            return "Nil";
        }

        public int hashCode() {
            return 17;
        }

        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }

            return !(o instanceof NilNode);
        }

        protected boolean shallowEquals( ConsList<T> other ) {
            return other.getClass() == NilNode.class;
        }

    }

    private static class ElementNode<T> extends ConsList<T> {

        private T           head;
        private ConsList<T> tail;

        public ElementNode( T v, ConsList<T> tail ) {
            this.head  = v;
            this.tail = tail;
        }

        public T head() {
            return head;
        }

        public ConsList<T> tail() {
            return tail;
        }

        public boolean isEmpty() {
            return false;
        }

        public <B> ConsList<T> map( Function1<T,B> mappingFunction ) {
            return new ElementNode( mappingFunction.invoke(head), tail.map(mappingFunction) );
        }

        public <B> Nullable<B> mapSingleValue( Function1<T,Nullable<B>> mappingFunction ) {
            Nullable<B> mappedValueNbl = mappingFunction.invoke(head);

            if ( mappedValueNbl.isNotNull() ) {
                return mappedValueNbl;
            }

            return tail.mapSingleValue( mappingFunction );
        }

        public Nullable<T> fetchFirstMatch( Function1<T,Boolean> predicateFunction ) {
            boolean predicate = predicateFunction.invoke(head);

            if ( predicate ) {
                return Nullable.createNullable(head);
            }

            return tail.fetchFirstMatch( predicateFunction );
        }

        public String toString() {
            return head + "::" + tail;
        }

        public int hashCode() {
            return head.hashCode();
        }

        public boolean equals( Object o ) {
            if ( !(o instanceof ConsList) ) {
                return false;
            }

            ConsList a = this;
            ConsList b = (ConsList) o;
            while ( a.shallowEquals(b) ) {
                if ( o == this || a.isEmpty() ) {
                    return true;
                }

                a = a.tail();
                b = b.tail();
            }

            return false;
        }

        protected boolean shallowEquals( ConsList<T> o ) {
            if ( !(o instanceof ElementNode) ) {
                return false;
            }

            ElementNode other = (ElementNode) o;
            return Objects.equals( this.head, other.head );
        }

    }

}


