package com.mosaic.io.cli;

import com.mosaic.lang.functional.Function1;


/**
 *
 */
@SuppressWarnings("unchecked")
public class CLArgument<T> {

    private static final Function1<String,String> NO_OP_PARSER = new Function1<String, String>() {
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

    private T value;


    public CLArgument( String argumentName, String argumentDescription, Function1<String,T> valueParser ) {
        this.argumentName        = argumentName;
        this.argumentDescription = argumentDescription;
        this.valueParser         = valueParser;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public String getArgumentDescription() {
        return argumentDescription;
    }

    public void setValue( String value ) {
        this.value = valueParser.invoke(value);
    }

    public T getValue() {
        return value;
    }

    public boolean isMandatory() {
        return true;
    }

    public boolean isEmpty() {
        return value == null;
    }

}
