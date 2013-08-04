package com.mosaic.lang;

import com.mosaic.lang.reflect.ReflectionException;
import org.junit.Test;

import java.io.NotSerializableException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ClonerTest {
    @Test
    public void testDeepCopyNull() throws Exception {
        assertNull( new Cloner().deepCopy( null ) );
    }

    @Test
    public void testDeepCopyNonSerializableObject() throws Exception {
        try {
            new Cloner().deepCopy( new ClonerTest() );
        } catch ( ReflectionException e ) {
            assertEquals( "java.io.NotSerializableException: com.mosaic.lang.ClonerTest", e.getMessage() );
            assertTrue( e.getCause() instanceof NotSerializableException );
        }
    }

    @Test
    public void testDeepCopySerializableObject() throws Exception {
        Object originalValue = new ArrayList();
        Object clonedValue = new Cloner().deepCopy( originalValue );

        assertEquals( originalValue, clonedValue );
        assertTrue( originalValue != clonedValue );
    }
}
