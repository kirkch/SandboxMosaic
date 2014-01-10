package com.mosaic.lang;

/**
 *
 */
public enum CaseSensitivity {
    CaseSensitive, CaseInsensitive;

    public boolean ignoreCase() {
        return this == CaseInsensitive;
    }
}