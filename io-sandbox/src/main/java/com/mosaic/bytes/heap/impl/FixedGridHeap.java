//package com.mosaic.bytes.heap.impl;
//
//import com.mosaic.bytes.ByteMarkerUtils;
//import com.mosaic.bytes.ByteView;
//import com.mosaic.bytes.Bytes;
//import com.mosaic.bytes.heap.GridHeap;
//import com.mosaic.lang.IllegalStateExceptionX;
//import com.mosaic.lang.system.SystemX;
//
//
//// NB not implemented yet.. sketched out to get a feel for the domain
//
///**
// * A fixed size heap that can only be appended to.  FixedGridHeap is very good at representing
// * trees with fixed width nodes.  The storage is compact as the length of each node does not have
// * to be stored, and the location of a trees node can be calculated algorithmically.
// */
//public class FixedGridHeap implements GridHeap {
//
//    private static final byte[] FILE_MARK  = ByteMarkerUtils.FIXED_GRID_HEAP_MARKER;
//    private static final byte   V1         = 1;
//
//    public static FixedGridHeap initNewHeap( Bytes heap, int perRecordSize ) {
//        heap.writeBytes(         MARK_INDEX,                ENDOFHEADER_INDEX_EXC, FILE_MARK );
//        heap.writeByte(          LAYOUTVERSION_INDEX,       ENDOFHEADER_INDEX_EXC, V1 );
//        heap.writeLong(          NUMALLOCATEDRECORDS_INDEX, ENDOFHEADER_INDEX_EXC, 0 );
//        heap.writeUnsignedShort( PERRECORDSIZE_INDEX,       ENDOFHEADER_INDEX_EXC, 0 );
//
//        return new FixedGridHeap( heap );
//    }
//
//    public static FixedGridHeap reloadHeap( Bytes heap ) {
//        return new FixedGridHeap( heap );
//    }
//
//
//    // Memory Layout
//    //
//    // |MARK:byte[3]|layoutVersion:byte|numAllocatedRecords:long|perRecordSize:ushort|..reserved...| ..fixed width records... |
//    //                                                                             10bytes       200bytes
//
//    private static final long MARK_INDEX                = 0;
//    private static final long LAYOUTVERSION_INDEX       = SystemX.SIZEOF_BYTE*3;
//    private static final long NUMALLOCATEDRECORDS_INDEX = SystemX.SIZEOF_BYTE;
//    private static final long PERRECORDSIZE_INDEX       = SystemX.SIZEOF_BYTE + SystemX.SIZEOF_LONG;
//    private static final long ENDOFHEADER_INDEX_EXC     = SystemX.SIZEOF_BYTE*200;
//
//    private static final long FIRSTRECORD_INDEX         = ENDOFHEADER_INDEX_EXC;
//
//
//
//    private           Bytes heap;
//
//    private transient long  maxExc;
//
//    private FixedGridHeap( Bytes heap ) {
//        if ( !heap.compareBytes(MARK_INDEX, FILE_MARK.length, FILE_MARK) ) {
//            throw new IllegalStateExceptionX( "" );
//        }
//
//        this.heap   = heap;
//        this.maxExc = heap.sizeBytes();
//    }
//
//
//    public long allocateRecord( int numBytes ) {
//        if ( numBytes != fixedRecordSize() ) {
//
//        }
//
//        return 0;
//    }
//
//
//    public void selectIntoView( ByteView view, long ptr ) {
//
//    }
//
//    public boolean releaseRecord( long ptr ) {
//        return false;
//    }
//
//    public long sizeBytes() {
//        return 0;
//    }
//
//    public long remainingBytes() {
//        return 0;
//    }
//
//    public long usedBytes() {
//        return 0;
//    }
//
//    public long numAllocatedRecords() {
//        return heap.readLong( NUMALLOCATEDRECORDS_INDEX, ENDOFHEADER_INDEX_EXC );
//    }
//
//    public void flush() {
//        heap.flush();
//    }
//
//
//
//    private int fixedRecordSize() {
//        return heap.readUnsignedShort( PERRECORDSIZE_INDEX, ENDOFHEADER_INDEX_EXC );
//    }
//
//}
