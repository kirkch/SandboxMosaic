package com.mosaic.lang.reflect;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Constructor;

/**
 *
 */
public class DefaultConstructorPropertyEditor extends PropertyEditorSupport {
    private Constructor c;

    public DefaultConstructorPropertyEditor(Constructor c) {
        this.c = c;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue( c.newInstance(text) );
        } catch (Exception e) {
            throw new RuntimeException(c.getDeclaringClass()+" '"+text+"'", e);
        }
    }
}
