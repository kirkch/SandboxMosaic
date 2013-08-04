package com.mosaic.lang.reflect;

import java.beans.PropertyEditor;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * A thread centric PropertyEditor factory. Don't share between threads.
 */
@SuppressWarnings("unchecked")
public class PropertyEditorManager {
    static {
        primePropertyEditorManager( Boolean.TYPE, Boolean.class );
        primePropertyEditorManager( Short.TYPE, Short.class );
        primePropertyEditorManager( Character.TYPE, Character.class );
        primePropertyEditorManager( Integer.TYPE, Integer.class );
        primePropertyEditorManager( Long.TYPE, Long.class );
        primePropertyEditorManager( Float.TYPE, Float.class );
        primePropertyEditorManager( Double.TYPE, Double.class );
    }

    private static void primePropertyEditorManager(Class a, Class b) {
        PropertyEditor editor = java.beans.PropertyEditorManager.findEditor(a);
        if ( editor != null ) {
            java.beans.PropertyEditorManager.registerEditor(b, editor.getClass() );
        }
    }



    private final Map<Class,PropertyEditor> editors = new HashMap<Class, PropertyEditor>();

    public PropertyEditor getEditorFor( Class propertyType ) {
        PropertyEditor editor = editors.get(propertyType);
        if ( editor != null ) return editor;

        editor = searchForEditor( propertyType );
        registerEditor( propertyType, editor );

        return editor;
    }

    public PropertyEditor getEditorFor( Object o ) {
        return getEditorFor( o == null ? null : o.getClass() );
    }

    public void registerEditor( Class propertyType, PropertyEditor editor ) {
        editors.put( propertyType, editor );
    }

    private PropertyEditor searchForEditor(Class propertyType) {
        if ( propertyType == null ) {
            return new NullPropertyEditor();
        }

        PropertyEditor editor = java.beans.PropertyEditorManager.findEditor( propertyType );
        if ( editor != null ) return editor;

        try {
            Constructor c = propertyType.getConstructor(String.class);

            return new DefaultConstructorPropertyEditor( c );
        } catch (NoSuchMethodException e) {
            return null;
        }

    }
}
