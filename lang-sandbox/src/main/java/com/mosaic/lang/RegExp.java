package com.mosaic.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegExp {

    private final Pattern pattern;

    public RegExp( String regexp ) {
        this.pattern = Pattern.compile( regexp );
    }

    public String extractMatchFrom( String text ) {
        Matcher m = pattern.matcher(text);

        return m.find() ? m.group(0) : null;
    }

    public String extractMatchFrom( String text, int group ) {
        Matcher m = pattern.matcher(text);

        return m.find() ? m.group(group) : null;
    }

}
