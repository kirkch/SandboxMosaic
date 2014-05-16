package com.mosaic.io.cli;

import com.mosaic.lang.functional.Function1;


/**
 *
 */
@SuppressWarnings("unchecked")
public class CLArgument<T> {

    public static final Function1<String,String> NO_OP_PARSER = new Function1<String, String>() {
        public String invoke( String arg ) {
            return arg;
        }
    };


    public static CLArgument<String> stringArgument( String argumentName, String argumentDescription ) {
        return new CLArgument( argumentName, argumentDescription, NO_OP_PARSER );
    }


    private final String               argumentName;
    private final String               argumentDescription;
    private final Function1<String, T> valueParser;

    private T       value;
    private boolean isOptional;
    private T       defaultValue;


    public CLArgument( String argumentName, String argumentDescription, Function1<String,T> valueParser ) {
        this.argumentName        = argumentName;
        this.argumentDescription = argumentDescription;
        this.valueParser         = valueParser;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public String getArgumentDescription() {
        String desc = argumentDescription;

        if ( defaultValue != null ) {
            desc = desc + " Defaults to '"+defaultValue+"'.";
        }

        return desc;
    }

    public void setValue( String value ) {
        this.value = valueParser.invoke(value);
    }

    public T getValue() {
        return value == null ? defaultValue : value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean isMandatory() {
        return !isOptional;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public boolean isEmpty() {
        return value == null;
    }

    void setOptional( boolean isOptional ) {
        this.isOptional = isOptional;
    }

    /**
     * Specify a default value for this optional argument.  Will error if the argument has not
     * been marked as optional.
     */
    public CLArgument<T> withDefaultValue( T defaultValue ) {
        if ( !isOptional ) {
            throw new IllegalArgumentException( "Only optional arguments may have default values" );
        }

        this.defaultValue = defaultValue;

        return this;
    }
}
