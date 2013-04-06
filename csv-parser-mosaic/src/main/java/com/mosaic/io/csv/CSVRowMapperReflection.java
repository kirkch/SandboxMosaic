package com.mosaic.io.csv;

/**
 *
 */
public class CSVRowMapperReflection<T> implements CSVRowMapper<T> {

    protected CSVRowMapperReflection( Class<T> dtoType ) {

    }

    protected void registerMapping( String header, String setterMethodName ) {

    }

    @Override
    public void processHeaders( String[] headers ) {
    }

    @Override
    public T processRow( String[] row ) {
        return null;
    }

}
