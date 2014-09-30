package com.mosaic.collections.map.chronicle;

import com.mosaic.lang.text.UTF8;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ExcerptCommon;
import net.openhft.chronicle.tools.WrappedExcerpt;

import java.io.IOException;


/**
 *
 */
public class ExcerptAppenderX extends WrappedExcerpt {

    public ExcerptAppenderX( ExcerptCommon excerptCommon ) {
        super( excerptCommon );
    }

    public ExcerptAppenderX( Chronicle chronicle ) throws IOException {
        super( chronicle.createAppender() );
    }

    public void writeUTF8( UTF8 string ) {
        super.writeUnsignedShort( string.getByteCount() );
        super.write( string.getBytes() );
    }

    public long sizeOf( UTF8 str ) {
        return 2 + str.getByteCount();
    }

}
