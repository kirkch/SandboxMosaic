package com.mosaic.collections;

import java.util.Objects;

/**
 * A key/value pair.
 */
public class KV<K,V> {

    private K key;
    private V value;

    public KV( K key, V value ) {
        this.key   = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public int hashCode() {
        return key.hashCode();
    }

    public boolean equals( Object o ) {
        if ( !(o instanceof KV) ) {
            return false;
        } else if ( o == this ) {
            return true;
        }

        KV other = (KV) o;
        return Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value);
    }

    public String toString() {
        return key+"="+value;
    }

}
