package com.mosaic.lang;


import com.mosaic.lang.function.Function0;
import com.mosaic.lang.function.Function1;

/**
 * Nullable represents a value that may legally be marked as having no value.  A typical
 * pattern in Java is to use the null value for an object reference to mean
 * 'none' or 'no value'.  This however results in a lack of compiler type
 * checking and lots of if statements to explicitly check for null to avoid
 * the ubitiquous NullPointerException errors at runtime.  Both functional
 * languages and OO languages that model everything including null as an object
 * have solved this problem.  This class copies their solution and makes it
 * available for pre-Java 8 code.  In Java 8 the class Optional offers the
 * similar capability.<p/>
 *
 * Note that there are two main ways of using this class.  Initially developers
 * will find some value (but limited) of wrapping nullable values purely to gain
 * compile time checking and documentation of values which are nullable.  These
 * developers will then typically use getValueNbl() to unwrap the class and perform
 * null checks manually as before.<p/>
 *
 * The other way of using this class is to defer the null check to this class,
 * thus reducing repeating the explicit null check over and over which is most
 * beneficial when chaining multiple operations together.  This approach
 * makes use of the methods mapValue and flatMapValue.  The downside here is that pre java 8
 * the passing of functions to methods involves the extra syntactic overhead
 * of creating an anonymous inner class.  Thus both approaches for using
 * this class have their benefits and drawbacks.  So why include this class
 * again?  It is to explicitly mark values as being nullable.  The drawbacks
 * caused by the limitations of the language can be worked around by emphasizing
 * code readability;  which means taking a step back and trying a couple of
 * different approaches to see which is the most readable for yourself and your
 * team in your situation.  Below is a set of examples showing each of the
 * main approaches for using this class.<p/>
 *
 * <h2>Example Usage</h2>
 *
 * <h3>Style 1 - unwrapping the value</h3>
 * <code>
 *
 * </code>
 *
 * <h3>Style 2 - switch statements</h3>
 * <code>
 *
 * </code>
 *
 * <h3>Style 3 - if statements</h3>
 * <code>
 *
 * </code>
 *
 * <h3>Style 4 - functional approach with embedded inner classes</h3>
 * <code>
 *
 * </code>
 *
 * <h3>Style 5 - functional approach with use of named variables for the functions</h3>
 * <code>
 *
 * </code>
 *
 * <h2>Mechanical Sympathy</h2>
 *
 * If you want to offer the abstraction offered by Nullable while also reducing
 * the runtime overhead of the abstraction, say you work in a low latency space.
 * Then the following pattern is recommended as it increases the opportunity
 * for the JVM to inline Nullable object.  Possibly even remove the object
 * allocation entirely.  For details on how Hotspot can perform this
 * kind of optimisation (and when) see the following presentation by ......  ......... TODO
 *
 * <code>
 *
 * </code>
 */
@SuppressWarnings("unchecked")
public abstract class Nullable<T> {

    public static final int NULL_ENUM     = 0;
    public static final int NOT_NULL_ENUM = 1;

    public static final Nullable NULL = createNull();


    /**
     * Factory method for wrapping Java values with a instance of Nullable.  If
     * the value is null then an instance of Null will be returned, else the value
     * will be wrapped in an instance of NotNull.
     */
    public static <T> Nullable<T> createNullable( T v ) {
        return v == null ? Null.INSTANCE : new NotNull(v);
    }

    public static <T> Nullable<T> createNull() {
        return Null.INSTANCE;
    }

    /**
     * Returns an int representing whether this instance represents a null
     * value or not.  This representation is intended to be switch statement
     * friendly.
     */
    public abstract int getStateEnum();

    /**
     * Unwrap this instance and return the underlying value.  If the value is
     * null then null will be returned, else the value itself.<p/>
     *
     * NB Nbl is short for Nullable.
     */
    public abstract T getValueNbl();

    /**
     * Unwrap this instance and return the underlying value.  If the value is
     * null then a NullPointerException will be thrown.  If you don't want
     * an exception have a look at getValueNbl.
     */
    public abstract T getValue();

    /**
     * Returns true if this instance contains a null value.
     */
    public abstract boolean isNull();

    /**
     * Returns true if this instance contains a value.
     */
    public abstract boolean isNotNull();


    public abstract <B> Nullable<B> mapValue(Function1<B,T> mappingFunction);
    public abstract <B> Nullable<B> flatMapValue(Function1<Nullable<B>,T> mappingFunction);

    public abstract Nullable<T> replaceNull(Function0<Nullable<T>> mappingFunction);

}


@SuppressWarnings("unchecked")
final class Null<T> extends Nullable<T> {

    public static final Nullable INSTANCE = new Null();

    private Null() {}

    public int getStateEnum() {
        return Null.NULL_ENUM;
    }

    public T getValueNbl() {
        return null;
    }

    public boolean isNull() {
        return true;
    }

    public boolean isNotNull() {
        return false;
    }

    public T getValue() {
        throw new NullPointerException();
    }

    public <B> Nullable<B> mapValue(Function1<B,T> mappingFunction) {
        return (Nullable<B>) this;
    }

    public <B> Nullable<B> flatMapValue( Function1<Nullable<B>,T> mappingFunction ) {
        return (Nullable<B>) this;
    }

    public Nullable<T> replaceNull( Function0<Nullable<T>> mappingFunction ) {
        Nullable<T> replacementValue = mappingFunction.invoke();

        return replacementValue == null ? this : replacementValue;
    }

    public int hashCode() {
        return 7;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals( Object o ) {
        return o == this;
    }

    public String toString() {
        return "Null";
    }

}

@SuppressWarnings("unchecked")
final class NotNull<T> extends Nullable<T> {

    private final T value;

    public NotNull( T v ) {
        assert v != null;

        this.value = v;
    }

    public int getStateEnum() {
        return Null.NOT_NULL_ENUM;
    }

    public T getValueNbl() {
        return value;
    }

    public T getValue() {
        return value;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isNotNull() {
        return true;
    }

    public <B> Nullable<B> mapValue(Function1<B,T> mappingFunction) {
        B mappedResult = mappingFunction.invoke(this.value);

        return Nullable.createNullable(mappedResult);
    }

    public <B> Nullable<B> flatMapValue( Function1<Nullable<B>,T> mappingFunction ) {
        Nullable<B> mappedResult = mappingFunction.invoke(this.value);

        return mappedResult == null ? NULL : mappedResult;
    }

    public Nullable<T> replaceNull( Function0<Nullable<T>> mappingFunction ) {
        return this;
    }

    public int hashCode() {
        return value.hashCode();
    }

    public boolean equals( Object o ) {
        if ( o == null || o.getClass() != NotNull.class) {
            return false;
        } else if ( o == this ) {
            return true;
        }

        NotNull<T> other = (NotNull<T>) o;
        return this.value.equals(other.value);
    }

    public String toString() {
        return this.value.toString();
    }

}