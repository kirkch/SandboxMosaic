package com.mosaic.collections.map.chronicle;

import com.mosaic.lang.text.UTF8;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ExcerptCommon;
import net.openhft.chronicle.tools.WrappedExcerpt;

import java.io.IOException;


/**
 *
 */
public class ExcerptTailerX extends WrappedExcerpt {
    public ExcerptTailerX( ExcerptCommon excerptCommon ) {
        super( excerptCommon );
    }

    public ExcerptTailerX( Chronicle chronicle ) throws IOException {
        super( chronicle.createTailer() );
    }


    public UTF8 readUTF8() {
        int numBytes = super.readUnsignedShort();

        byte[] bytes = new byte[numBytes];
        super.read( bytes );

        return new UTF8( bytes );
    }
}
