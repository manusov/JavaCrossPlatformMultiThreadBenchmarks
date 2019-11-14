package jgsp.iochannelsmbps;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.DSYNC;
import static java.nio.file.StandardOpenOption.SPARSE;
import static java.nio.file.StandardOpenOption.WRITE;
import static jgsp.helpers.Delay.delay;
import jgsp.statistics.StatusEntry;
import jgsp.templates.IOscenario;

public class MBPSsingleThreadChannels extends IOscenario
{
/*
Options variables default settings, support scenario-specific extensions
*/
private final static boolean DEFAULT_FAST_COPY    = false;
private final static boolean DEFAULT_WRITE_SYNC   = false;
private final static boolean DEFAULT_WRITE_SPARSE = false;
private final static boolean DEFAULT_COPY_SYNC    = false;
private final static boolean DEFAULT_COPY_SPARSE  = false;
/*
Fields for support scenario-specific extensions
*/
final boolean fastCopy;
final boolean writeSync;
final boolean copySync;
final boolean writeSparse;
final boolean copySparse;
/*
File channels and buffers support, this fields used in the overridable methods.
*/
final Path[] srcPaths;
final Path[] dstPaths;
final FileChannel[] srcChannels;
final FileChannel[] dstChannels;
final FileSystem fileSystem;
final ByteBuffer byteBuffer;
final ByteBuffer byteBufferTail;
long total;
/*
Methods selection support for non-sync or sync write and copy, 
use Lambda expressions.
Interface definition
*/
interface Opener
    { FileChannel open( int index, Path[] p ) throws IOException; }
/*
Methods definitions
*/
Opener openAsyncForWrite = ( int index, Path[] p ) -> 
    { return FileChannel.open( p[index], APPEND ); };
Opener openSyncForWrite = ( int index, Path[] p ) -> 
    { return FileChannel.open( p[index], APPEND, DSYNC ); };
Opener openSparseForWrite = ( int index, Path[] p ) -> 
    { return FileChannel.open( p[index], APPEND, DSYNC, SPARSE ); };
Opener openAsyncForCopy = ( int index, Path[] p ) -> 
    { return FileChannel.open( p[index], CREATE, WRITE ); };
Opener openSyncForCopy = ( int index, Path[] p ) -> 
    { return FileChannel.open( p[index], CREATE, WRITE, DSYNC ); };
Opener openSparseForCopy = ( int index, Path[] p ) ->
    { return FileChannel.open( p[index], CREATE, WRITE, DSYNC, SPARSE ); };
/*
Methods variables
*/
final Opener writeOpener;
final Opener copyOpener;
/*
Constructor for options settings by internal defaults
*/    
public MBPSsingleThreadChannels()
    {
    super();
    fastCopy    = DEFAULT_FAST_COPY;
    writeSync   = DEFAULT_WRITE_SYNC;
    copySync    = DEFAULT_COPY_SYNC;
    writeSparse = DEFAULT_WRITE_SPARSE;
    copySparse  = DEFAULT_COPY_SPARSE;
    
    writeOpener = writeSparse 
        ? openSparseForWrite 
        : writeSync ? openSyncForWrite : openAsyncForWrite;
    copyOpener  = copySparse
        ? openSparseForCopy
        : copySync  ? openSyncForCopy  : openAsyncForCopy;
    
    srcPaths = new Path[fileCount];
    dstPaths = new Path[fileCount];
    srcChannels = new FileChannel[fileCount];
    dstChannels = new FileChannel[fileCount];
    fileSystem = FileSystems.getDefault();
    byteBuffer = ByteBuffer.allocateDirect( blockSize );
    byteBuffer.put( dataBlock, 0, blockSize );
    int tailSize = fileSize % blockSize;
    byteBufferTail = ByteBuffer.allocateDirect( tailSize );
    byteBufferTail.put( dataBlock, 0, tailSize );
    total = fileCount * fileSize;
    }
/*
Constructor for options settings by input parameters
*/
public MBPSsingleThreadChannels
        ( String pathSrc, String prefixSrc, String postfixSrc,
          String pathDst, String prefixDst, String postfixDst,
          int mode, int fileCount, int fileSize, int blockSize,
          int readDelay, int writeDelay, int copyDelay,
          byte[] dataBlock,
          boolean fastCopy, 
          boolean writeSync, boolean copySync, 
          boolean writeSparse, boolean copySparse )
    {
    super( pathSrc, prefixSrc, postfixSrc,
           pathDst, prefixDst, postfixDst,
           mode, fileCount, fileSize, blockSize,
           readDelay, writeDelay, copyDelay,
           dataBlock );
    this.fastCopy    = fastCopy;
    this.writeSync   = writeSync;
    this.copySync    = copySync;
    this.writeSparse = writeSparse;
    this.copySparse  = copySparse;
    
    writeOpener = writeSparse 
        ? openSparseForWrite 
        : writeSync ? openSyncForWrite : openAsyncForWrite;
    copyOpener  = copySparse
        ? openSparseForCopy
        : copySync  ? openSyncForCopy  : openAsyncForCopy;

    srcPaths = new Path[fileCount];
    dstPaths = new Path[fileCount];
    srcChannels = new FileChannel[fileCount];
    dstChannels = new FileChannel[fileCount];
    fileSystem = FileSystems.getDefault();
    byteBuffer = ByteBuffer.allocateDirect( blockSize );
    byteBuffer.put( dataBlock, 0, blockSize );
    int tailSize = fileSize % blockSize;
    byteBufferTail = ByteBuffer.allocateDirect( tailSize );
    byteBufferTail.put( dataBlock, 0, tailSize );
    total = fileCount * fileSize;
    }

/*
Run benchmarks performance
*/
@Override public void run()
    {
    phaseID = -1;
    phaseName = "starting...";
    percentage = 0.0;
    lastError = new StatusEntry( true, "OK" );
    clearSync();
    
    if ( mode != READ_ONLY )
        {
        /*
        Phase #1 = Write source files
        */
        phaseID = WRITE_ID;
        phaseName = "pre-write wait...";
        delay( writeDelay );
        phaseName = "write...";
        phaseWrite();
        /*
        Phase #2 = Copy source files to destination files
        */
        phaseID = COPY_ID;
        phaseName = "pre-copy wait...";
        delay( copyDelay );
        phaseName = "copy...";
        phaseCopy();
        }

    if ( mode != WRITE_ONLY )
        {
        /*
        Phase #3 = Read source files
        */
        phaseID = READ_ID;
        phaseName = "pre-read wait...";
        delay( readDelay );
        phaseName = "read...";
        phaseRead();
        }
    
    phaseID = -1;
    if ( mode == READ_WRITE )
        {
        phaseName = "delete...";
        /*
        Phase = Delete, note about files not deleted in WRITE_ONLY mode.
        Note delete operation cycles for all files is not interruptable.
        */
        helperDelete( srcPaths, srcChannels );
        helperDelete( dstPaths, dstChannels );
        }
    }

/*
Write, Copy and Read phases handlers for optional override by child classes.
*/

/*
Handler for Write files
*/
void phaseWrite()
    {
    try
        {
        /*
        All files total measured write cycle start
        */
        statistics.startInterval( TOTAL_WRITE_ID, System.nanoTime() );
        for( int i=0; i<fileCount; i++ )
            {
            if ( isInterrupted() ) break;
            srcPaths[i] = fileSystem.getPath
                ( pathSrc + prefixSrc + i + postfixSrc );
            Files.createFile( srcPaths[i] );
            srcChannels[i] = writeOpener.open( i, srcPaths );
            int j = fileSize;
            /*
            Single file measured write start
            */
            statistics.startInterval( WRITE_ID, System.nanoTime() );
            while( j >= blockSize )
                {
                byteBuffer.rewind();
                int k = 0;
                while( k < blockSize )
                    {
                    k += srcChannels[i].write( byteBuffer );
                    }
                j -= blockSize;
                }
            if ( j > 0 )
                {
                byteBufferTail.rewind();
                int k = 0;
                while ( k < j )
                    {
                    k += srcChannels[i].write( byteBufferTail );
                    }
                }
            if ( writeSync )
                srcChannels[i].force( true );
            statistics.sendMBPS( WRITE_ID, fileSize, System.nanoTime() );
            setSync( i+1, lastError, phaseID, phaseName );
            /*
            Single file measured write end
            */
            srcChannels[i].close();
            }
        statistics.sendMBPS( TOTAL_WRITE_ID, total, System.nanoTime() );
        /*
        All files total measured write cycle end
        */
        }
    catch( IOException e )
        {
        helperDelete( srcPaths, srcChannels );
        lastError = 
            new StatusEntry( false, "Write error: " + e.getMessage() );
        }
    }

/*
Handler for Copy files
*/
void phaseCopy()
    {
        try
        {
        /*
        All files total measured copy cycle start
        */
        statistics.startInterval( TOTAL_COPY_ID, System.nanoTime() );
        for( int i=0; i<fileCount; i++ )
            {
            if ( isInterrupted() ) break;
            if ( fastCopy )
                {
                srcChannels[i] = FileChannel.open( srcPaths[i] );
                dstPaths[i] = fileSystem.getPath
                    ( pathDst + prefixDst + i + postfixDst );
                dstChannels[i] = copyOpener.open( i, dstPaths );
                int k = 0;
                /*
                Single file measured copy start
                */
                statistics.startInterval( WRITE_ID, System.nanoTime() );
                while( k < fileSize )
                    {
                    k += srcChannels[i].transferTo
                        ( 0, srcChannels[i].size(), dstChannels[i] );
                    }
                if ( copySync )
                    dstChannels[i].force( true );
                statistics.sendMBPS( COPY_ID, fileSize, System.nanoTime() );
                setSync( i+1, lastError, phaseID, phaseName );
                /*
                Single file measured copy end
                */
                }
            else
                {
                srcChannels[i] = FileChannel.open( srcPaths[i] );
                dstPaths[i] = fileSystem.getPath
                    ( pathDst + prefixDst + i + postfixDst );
                dstChannels[i] = copyOpener.open( i, dstPaths );
                int k = 0;
                /*
                Single file measured copy start
                */
                statistics.startInterval( COPY_ID, System.nanoTime() );
                while( k < fileSize )
                    {
                    int n = blockSize;
                    int m = fileSize - k;
                    if ( n > m ) n = m;
                    k += srcChannels[i].transferTo( 0, n, dstChannels[i] );
                    if ( copySync )
                        dstChannels[i].force( true );
                    }
                statistics.sendMBPS( COPY_ID, fileSize, System.nanoTime() );
                setSync( i+1, lastError, phaseID, phaseName );
                /*
                Single file measured copy end
                */
                }
            }
        statistics.sendMBPS( TOTAL_COPY_ID, total, System.nanoTime() );
        /*
        All files total measured copy cycle end
        */
        }
    catch( IOException e )
        {
        helperDelete( srcPaths, srcChannels );
        helperDelete( dstPaths, dstChannels );
        lastError = 
            new StatusEntry( false, "Copy error: " + e.getMessage() );
        }
    }

/*
Handler for Read files
*/
void phaseRead()
    {
    try
        {
        /*
        All files total measured read cycle start
        */
        statistics.startInterval( TOTAL_READ_ID, System.nanoTime() );
        for( int i=0; i<fileCount; i++ )
            {
            if ( isInterrupted() ) break;
            int k;
            /*
            Single file measured read start
            */
            statistics.startInterval( READ_ID, System.nanoTime() );
            do  {
                byteBuffer.rewind();
                k = srcChannels[i].read( byteBuffer );
                } while ( k != -1);
            statistics.sendMBPS( READ_ID, fileSize, System.nanoTime() );
            setSync( i+1, lastError, phaseID, phaseName );
            /*
            Single file measured read end
            */
            }
        statistics.sendMBPS( TOTAL_READ_ID, total, System.nanoTime() );
        /*
        All files total measured copy cycle end
        */
        }
    catch( IOException e )
        {
        helperDelete( srcPaths, srcChannels );
        helperDelete( dstPaths, dstChannels );
        lastError = 
            new StatusEntry( false, "Read error: " + e.getMessage() );
        }
    }

/*
Helper for Delete files
*/
void helperDelete( Path[] path, FileChannel[] channel )
    {
    if ( ( path == null )||( channel == null )||
         ( path.length == 0 )||( channel.length != path.length ) )
        {
        lastError = 
            new StatusEntry( false, "Delete context bad" );
        }
    else
        {
        for( int i=0; i<path.length; i++ )
        try
            {
            if ( path[i]    != null )  Files.delete( path[i] );
            if ( channel[i] != null )  channel[i].close();
            }
        catch ( IOException e )
            {
            lastError = 
                new StatusEntry( false, "Delete error: " + e.getMessage() );
            }
        }
    }

}
