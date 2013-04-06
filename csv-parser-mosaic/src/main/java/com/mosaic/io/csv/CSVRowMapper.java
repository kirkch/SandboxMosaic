package com.mosaic.io.csv;

/**
 * Maps rows from a CSV file into an object.
 */
public interface CSVRowMapper<T> {

    /**
     * Called once before any rows are processed. Specifies the column values in the first row of the csv file, in
     * the order that they were specified in. This order will match the order of the columns in each row.
     *
     * @param headers the name of each column in the order that they were declared in
     */
    public void processHeaders( String[] headers );

    /**
     * Convert the specified row into an object. Each element of the row holds a column, and is declared in the same
     * order as the headers
     */
    public T processRow( String[] row );

}
