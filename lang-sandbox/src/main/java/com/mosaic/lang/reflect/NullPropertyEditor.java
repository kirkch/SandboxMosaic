package com.mosaic.lang.reflect;

import java.beans.PropertyEditorSupport;

/**
 *
 */
public class NullPropertyEditor extends PropertyEditorSupport {
    private String nullText;

    public NullPropertyEditor() {
        this("null");
    }

    public NullPropertyEditor(String nullText) {

        this.nullText = nullText;
    }

    @Override
    public String getAsText() {
        return nullText;
    }
}
