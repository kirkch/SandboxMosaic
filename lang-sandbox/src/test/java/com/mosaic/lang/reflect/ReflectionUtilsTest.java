package com.mosaic.lang.reflect;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;


/**
 *
 */
public class ReflectionUtilsTest {

    @Test
    public void testClone() {
        Car c = new Car("BMW");
        Car clone = ReflectionUtils.clone( c );

        assertEquals( new Car("BMW"),  clone );
        assertTrue( c != clone );
    }

}

class Car implements Cloneable {
    public String make;

    public Car( String make ) {
        this.make = make;
    }

    public boolean equals( Object o ) {
        if ( !(o instanceof Car) ) {
            return false;
        }

        Car other = (Car) o;
        return Objects.equals( this.make, other.make );
    }

    public int hashCode() {
        return Objects.hashCode(make.hashCode());
    }
}