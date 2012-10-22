package com.mosaic.lang.reflect;

import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 *
 */
public class JavaFieldTest {
    @Test
    public void demoFetchingAndSettingAPublicField() {
        Map<String,JavaField<Pojo>> fields =  JavaField.getAllJavaFieldsFor( JavaClass.toJavaClass( Pojo.class ) );
        assertEquals( 2, fields.size() );

        Pojo o = new Pojo();
        JavaField<Pojo> field = fields.get( "publicField" );

        assertEquals( "publicField", field.getName() );
        assertNull( field.getValue(o) );
        field.setValue( o, "twinkle twinkle little star" );
        assertEquals( "twinkle twinkle little star", field.getValue(o) );
    }
    
    @Test
    public void demoFetchingAndSettingAPrivateField() {
        Map<String,JavaField<Pojo>> fields =  JavaField.getAllJavaFieldsFor( JavaClass.toJavaClass( Pojo.class ) );
        assertEquals( 2, fields.size() );

        Pojo o = new Pojo();
        JavaField<Pojo> field = fields.get( "privateField" );
        assertNull( field.getValue(o) );
        field.setValue( o, "twinkle twinkle little star" );
        assertEquals( "twinkle twinkle little star", field.getValue(o) );
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static class Pojo {
        public String  publicField;
        private String privateField;
    }
}
