package com.mosaic.io.cli;

import com.mosaic.lang.functional.Function1;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class CLOption<T> {

    private final String               shortName;
    private final String               longName;
    private final String               flagDescription;
    private final Function1<String, T> valueParser;

    private T       value;

    private final List<String> aliases = new ArrayList<>();


    public CLOption( String shortName, String longName, String flagDescription, T initialValue, Function1<String, T> valueParser ) {
        this.shortName       = shortName;
        this.longName        = longName;
        this.flagDescription = flagDescription;
        this.valueParser     = valueParser;
        this.value           = initialValue;

        aliases.add( "-" + shortName );
        aliases.add( "--" + longName );
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setValue( String v ) {
        this.value = valueParser.invoke( v );
    }

    public T getValue() {
        return value;
    }

    public String getLongName() {
        return longName;
    }

    public String getDescription() {
        return flagDescription;
    }

}
