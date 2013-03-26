package com.mosaic.io;

import com.mosaic.lang.PullDataSource;


import java.io.Reader;

/**
 *
 */
public class CSVReader<T> extends PullDataSource<T> {

//    private final Tokenizer       in;
    private final CSVRowMapper<T> mapper;

    private String[] headers;

    public CSVReader( Reader in, CSVRowMapper<T> mapper ) {
//        this.in     = Tokenizer.createWhitespaceSkippingTokenizerFor(in);
        this.mapper = mapper;
    }

    public String[] getHeaders() {
        return lazyLoadHeaders();
    }

    @Override
    public boolean hasNext() {

        return false;
    }

    @Override
    public T next() {
        return null;
    }


    private String[] lazyLoadHeaders() {
        if ( this.headers == null ) {
            this.headers = readNextRow();
        }

        return this.headers;
    }

    private String[] readNextRow() {
        return null;
    }
}
