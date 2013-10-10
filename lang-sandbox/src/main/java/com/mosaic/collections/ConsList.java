package com.mosaic.collections;

import com.mosaic.lang.function.Function1;

import java.util.Objects;

/**
 * A classic cons-list as made popular by functional programming.  Essentially
 * an immutable linked list supporting head, tail and cons operations.<p/>
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


    public ConsList<T> cons( T v ) {
        return new ElementNode( v, this );
    }


    public abstract <B> ConsList<T> map( Function1<B,T> mappingFunction );


    protected abstract boolean shallowEquals( ConsList<T> other );

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

        public <B> ConsList<T> map( Function1<B,T> mappingFunction ) {
            return this;
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

        public <B> ConsList<T> map( Function1<B,T> mappingFunction ) {
            return new ElementNode( mappingFunction.invoke(head), tail.map(mappingFunction) );
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


