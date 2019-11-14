/*
Helper for Read, Write, Copy, Delete operations,
use memory-mapped files (MMF).
*/

package jgsp.iommfmbps;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static jgsp.iommfmbps.MBPSsingleThreadMMF.COPY_ID;
import static jgsp.iommfmbps.MBPSsingleThreadMMF.READ_ID;
import static jgsp.iommfmbps.MBPSsingleThreadMMF.WRITE_ID;
import jgsp.statistics.StatisticsModel;
import jgsp.statistics.StatusEntry;

class HelperIO 
{
private final HelperUnmap helperUnmap;
private final StatisticsModel model;

HelperIO( StatisticsModel model )
    {
    helperUnmap = new HelperUnmap();
    this.model = model;
    }

/*
Write file, use memory-mapped buffer.
Overloaded method with auto-unmap buffer.
*/
MappedStatusEntry mappedWrite
        ( String dstPath, int size, byte[] dataBlock, boolean unmap )
    {
    MappedStatusEntry e1 = mappedWrite( dstPath, size, dataBlock );
    if ( unmap )
        {
        StatusEntry e2 = helperUnmap.unmap( e1.buffer );
        if ( ( e1.flag )&&( ! e2.flag ) )
            e1 = new MappedStatusEntry( e2.flag, e2.string, null );
        else
            e1 = new MappedStatusEntry( e1.flag, e1.string, null );
        }
    return e1;
    }

/*
Write file, use memory-mapped buffer.
*/
MappedStatusEntry mappedWrite( String dstPath, int size, byte[] dataBlock )
    {
    /*
    Initializing fields for returned object    
    */
    boolean statusFlag;
    String statusString = "N/A";
    MappedByteBuffer buffer = null;
    /*
    Initializing blocks and tail sizes
    */
    block = dataBlock;
    int blockCount = size / block.length;
    int tailSize = size % block.length;
    blockTail = null;
    if ( tailSize > 0 )
        {
        blockTail = new byte[tailSize];
        System.arraycopy ( block, 0, blockTail, 0, tailSize );
        }
    /*
    Start file I/O
    */
    try ( RandomAccessFile raf = new RandomAccessFile( dstPath, "rw" );
          FileChannel channel = raf.getChannel(); )
        {
        buffer = channel.map( FileChannel.MapMode.READ_WRITE, 0, size );
        /*
        Get timer
        */
        if ( model != null ) 
            model.startInterval( WRITE_ID, System.nanoTime() );
        /*
        Start of time measured interval
        */
        for( int i=0; i<blockCount; i++ ) 
            buffer.put( block );
        if ( tailSize > 0 ) 
            buffer.put( blockTail );
        channel.force( true );
        buffer.force();
        /*
        End of time measured interval
        */
        if ( model != null ) 
            model.sendMBPS( WRITE_ID, size, System.nanoTime() );
        statusFlag = true;
        }
    catch( Exception e )            
        {
        statusFlag = false;
        statusString = "Mapped write failed: " + e.getMessage();
        }
    return new MappedStatusEntry( statusFlag, statusString, buffer );
    }

/*
This PUBLIC for prevent speculative cancels.
*/
public byte[] block;
public byte[] blockTail;

/*
Read file, use memory-mapped buffer.
Overloaded method with auto-unmap buffer.
*/
MappedStatusEntry mappedRead( String dstPath, int blockSize, boolean unmap )
    {
    MappedStatusEntry e1 = mappedRead( dstPath, blockSize );
    if ( unmap )
        {
        StatusEntry e2 = helperUnmap.unmap( e1.buffer );
        if ( ( e1.flag )&&( ! e2.flag ) )
            e1 = new MappedStatusEntry( e2.flag, e2.string, null );
        else
            e1 = new MappedStatusEntry( e1.flag, e1.string, null );
        }
    return e1;
    }

/*
Read file, use memory-mapped buffer.
*/
MappedStatusEntry mappedRead( String dstPath, int blockSize )
    {
    /*
    Initializing fields for returned object    
    */
    boolean statusFlag;
    String statusString = "N/A";
    MappedByteBuffer buffer = null;
    /*
    Start file I/O
    */
    try ( RandomAccessFile raf = new RandomAccessFile( dstPath, "r" );
          FileChannel channel = raf.getChannel(); )
        {
        long size = channel.size();
        buffer = channel.map( FileChannel.MapMode.READ_ONLY, 0, size );
        /*
        Initializing blocks and tail sizes
        */
        int blockCount = (int)( size / blockSize );
        int tailSize   = (int)( size % blockSize );
        block = null;
        blockTail = null;
        if ( blockSize > 0 )
            block = new byte[blockSize];
        if ( tailSize > 0 )
            blockTail = new byte[tailSize];
        /*
        Get timer
        */
        if ( model != null ) 
            model.startInterval( READ_ID, System.nanoTime() );
        /*
        Start of time measured interval
        */
        buffer.load();
        for( int i=0; i<blockCount; i++ )
            buffer.get( block );
        if ( tailSize > 0 )
            buffer.get( blockTail );
        /*
        End of time measured interval
        */
        if ( model != null ) 
            model.sendMBPS( READ_ID, size, System.nanoTime() );
        statusFlag = true;
        }
    catch( Exception e )            
        {
        statusFlag = false;
        statusString = "Mapped read failed: " + e.getMessage();
        }
    return new MappedStatusEntry( statusFlag, statusString, buffer );
    }

/*
Copy file, use memory-mapped buffer.
Overloaded method with auto-unmap buffer.
*/
MappedStatusEntry mappedCopy
        ( String srcPath, String dstPath, int blockSize, boolean unmap )
    {
    MappedStatusEntry e1 = mappedCopy( srcPath, dstPath, blockSize );
    if ( unmap )
        {
        StatusEntry e2 = helperUnmap.unmap( e1.buffer );
        StatusEntry e3 = helperUnmap.unmap( e1.bufferDual );
        if ( ( e1.flag )&&( ! e2.flag ) )
            e1 = new MappedStatusEntry( e2.flag, e2.string, null );
        else if ( ( e1.flag )&&( ! e3.flag ) )
            e1 = new MappedStatusEntry( e3.flag, e3.string, null );
        else
            e1 = new MappedStatusEntry( e1.flag, e1.string, null );
        }
    return e1;
    }

/*
Copy file, use memory-mapped buffers.
*/
MappedStatusEntry mappedCopy( String srcPath, String dstPath, int blockSize )
    {
    /*
    Initializing fields for returned object    
    */
    boolean statusFlag;
    String statusString = "N/A";
    MappedByteBuffer srcBuffer = null;
    MappedByteBuffer dstBuffer = null;
    /*
    Start file I/O
    */
    try ( RandomAccessFile srcRaf = new RandomAccessFile( srcPath, "r" );
          RandomAccessFile dstRaf = new RandomAccessFile( dstPath, "rw" );
          FileChannel srcChannel = srcRaf.getChannel(); 
          FileChannel dstChannel = dstRaf.getChannel(); )
        {
        long size = srcChannel.size();
        srcBuffer = srcChannel.map( FileChannel.MapMode.READ_ONLY, 0, size );
        dstBuffer = dstChannel.map( FileChannel.MapMode.READ_WRITE, 0, size );
        /*
        Get timer
        */
        if ( model != null ) 
            model.startInterval( COPY_ID, System.nanoTime() );
        /*
        Initializing blocks and tail sizes
        */
        int blockCount = (int)( size / blockSize );
        int tailSize   = (int)( size % blockSize );
        block = null;
        blockTail = null;
        if ( blockSize > 0 )
            block = new byte[blockSize];
        if ( tailSize > 0 )
            blockTail = new byte[tailSize];
        /*
        Start of time measured interval
        */
        for( int i=0; i<blockCount; i++ )
            {
            srcBuffer.get( block );
            dstBuffer.put( block );
            }
        if ( tailSize > 0 ) 
            {
            srcBuffer.get( blockTail );
            dstBuffer.put( blockTail );
            }
        dstChannel.force( true );
        dstBuffer.force();
        /*
        End of time measured interval
        */
        if ( model != null ) 
            model.sendMBPS( COPY_ID, size, System.nanoTime() );
        statusFlag = true;
        }
    catch( Exception e )            
        {
        statusFlag = false;
        statusString = "Mapped copy failed: " + e.getMessage();
        }
    return new MappedStatusEntry
        ( statusFlag, statusString, srcBuffer, dstBuffer );
    }

/*
Delete file
*/
StatusEntry mappedDelete( String path )
    {
    return unmapAndDelete( path, null );
    }

/*
Delete file, required java version-specific unmap buffer.
This variant with more detail error reporting and buffer unmap feature.
Can call this method with filePath = null for unmap only without delete file.
Can call this method with buffer = null for delete file without unmap buffer.
*/
StatusEntry unmapAndDelete( String path, MappedByteBuffer buffer )
    {
    StatusEntry entry = null;
    /*
    Unmap buffer, if not null, exit with status details if error
    */
    if ( buffer != null )
        {
        entry = helperUnmap.unmap( buffer );
        if ( ! entry.flag )
            {
            return entry;  // exit with status, if error
            }
        }
    /*
    Delete file with status string extraction, if error
    */
    if ( path != null )
        {
        FileSystem fs = FileSystems.getDefault();
        Path filePath = fs.getPath( path );
        boolean statusFlag = true;
        String statusString = "N/A";
        try 
            {
            Files.delete( filePath );
            }
        catch ( IOException e )
            {
            statusFlag = false;
            statusString = "Delete error: " + e.getMessage();
            }
        entry = new StatusEntry( statusFlag, statusString );
        }
    return entry;
    }

}
