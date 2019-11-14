/*

Remember executor shutdown.

*/

package jgsp.iochannelsmbps;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import static jgsp.helpers.Delay.delay;
import jgsp.statistics.StatusEntry;
import static jgsp.templates.IOscenario.COPY_ID;
import static jgsp.templates.IOscenario.READ_ID;
import static jgsp.templates.IOscenario.TOTAL_COPY_ID;
import static jgsp.templates.IOscenario.WRITE_ID;

public class MBPSmultiThreadChannels extends MBPSsingleThreadChannels
{
/*
Default settings for options variables
*/
private final static int DEFAULT_THREAD_COUNT = 4;
/*
Options variables
*/
private final int threadCount;
/*
Executor service for run parallel task and task pools arrays
*/
private final ExecutorService writeExecutor;
private final ExecutorService copyExecutor;
private final ExecutorService readExecutor;
private final ExecutorService deleteExecutor;
private final WriteTask[] writeTasks;
private final CopyTask[] copyTasks;
private final ReadTask[] readTasks;
private final DeleteTask[] deleteTasks;
private final FutureTask[] futureWriteTasks;
private final FutureTask[] futureCopyTasks;
private final FutureTask[] futureReadTasks;
private final FutureTask[] futureDeleteTasks;
/*
Constructor for options settings by internal defaults
*/    
public MBPSmultiThreadChannels()
    {
    super();
    threadCount = DEFAULT_THREAD_COUNT;
    writeExecutor     = Executors.newFixedThreadPool( threadCount );
    copyExecutor      = Executors.newFixedThreadPool( threadCount );
    readExecutor      = Executors.newFixedThreadPool( threadCount );
    deleteExecutor    = Executors.newFixedThreadPool( threadCount );
    writeTasks        = new WriteTask[fileCount];
    copyTasks         = new CopyTask[fileCount];
    readTasks         = new ReadTask[fileCount];
    deleteTasks       = new DeleteTask[fileCount*2];
    futureWriteTasks  = new FutureTask[fileCount];
    futureCopyTasks   = new FutureTask[fileCount];
    futureReadTasks   = new FutureTask[fileCount];
    futureDeleteTasks = new FutureTask[fileCount*2];
    }
/*
Constructor for options settings by input parameters
*/
public MBPSmultiThreadChannels
        ( String pathSrc, String prefixSrc, String postfixSrc,
          String pathDst, String prefixDst, String postfixDst,
          int mode, int fileCount, int fileSize, int blockSize,
          int readDelay, int writeDelay, int copyDelay,
          byte[] dataBlock,
          boolean fastCopy, 
          boolean writeSync, boolean copySync, 
          boolean writeSparse, boolean copySparse,
          int threadCount )
    {
    super( pathSrc, prefixSrc, postfixSrc,
           pathDst, prefixDst, postfixDst,
           mode, fileCount, fileSize, blockSize,
           readDelay, writeDelay, copyDelay,
           dataBlock,
           fastCopy, 
           writeSync, copySync, 
           writeSparse, copySparse );
    this.threadCount = 
        ( threadCount == -1 ) ? DEFAULT_THREAD_COUNT : threadCount;
    
    writeExecutor     = Executors.newFixedThreadPool( threadCount );
    copyExecutor      = Executors.newFixedThreadPool( threadCount );
    readExecutor      = Executors.newFixedThreadPool( threadCount );
    deleteExecutor    = Executors.newFixedThreadPool( threadCount );
    writeTasks        = new WriteTask[fileCount];
    copyTasks         = new CopyTask[fileCount];
    readTasks         = new ReadTask[fileCount];
    deleteTasks       = new DeleteTask[fileCount*2];
    futureWriteTasks  = new FutureTask[fileCount];
    futureCopyTasks   = new FutureTask[fileCount];
    futureReadTasks   = new FutureTask[fileCount];
    futureDeleteTasks = new FutureTask[fileCount*2];
    }

/*
Run benchmarks performance
*/
@Override public void run()
    {
    phaseID = -1;
    phaseName = "starting parallel...";
    percentage = 0.0;
    lastError = new StatusEntry( true, "OK" );
    clearSync();

    /*
    Create fields for parallel tasks management
    */        
    for( int i=0; i<fileCount; i++ )
        {
        writeTasks[i] = new WriteTask( i );
        copyTasks[i] = new CopyTask( i );
        readTasks[i] = new ReadTask( i );
        deleteTasks[i] = new DeleteTask( i, true );
        deleteTasks[i+fileCount] = new DeleteTask( i, false );
        futureWriteTasks[i] = new FutureTask( writeTasks[i] );
        futureCopyTasks[i] = new FutureTask( copyTasks[i] );
        futureReadTasks[i] = new FutureTask( readTasks[i] );
        futureDeleteTasks[i] = new FutureTask( deleteTasks[i] );
        futureDeleteTasks[i+fileCount] = 
                    new FutureTask( deleteTasks[i+fileCount] );
        }
    
    if ( mode != READ_ONLY )
        {
        /*
        Phase #1 = Write source files
        */
        phaseID = WRITE_ID;
        phaseName = "pre-write wait...";
        delay( writeDelay );
        phaseName = "write parallel...";
        phaseWrite();
        /*
        Phase #2 = Copy source files to destination files
        */
        phaseID = COPY_ID;
        phaseName = "pre-copy wait...";
        delay( copyDelay );
        phaseName = "copy parallel...";
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
        phaseName = "read parallel...";
        phaseRead();
        }
    
    phaseID = -1;
    if ( mode == READ_WRITE )
        {
        phaseName = "delete parallel...";
        /*
        Phase = Delete, note about files not deleted in WRITE_ONLY mode.
        Note delete operation cycles for all files is not interruptable.
        Note parallel-delete functionality yet reserved, not used.
        */
        helperDelete( srcPaths, srcChannels );
        helperDelete( dstPaths, dstChannels );
        }

    /*
    Executor shutdown
    */
    ExecutorService[] executors = new ExecutorService[]
        { writeExecutor, copyExecutor, readExecutor, deleteExecutor };
    for ( ExecutorService executor : executors ) 
        {
        StatusEntry statusEntry = executorShutdown( executor );
        if ( ( ! statusEntry.flag )&&( lastError.flag ) )
            lastError = statusEntry;
        }
    }
        
/*
Handler for Write files,
override single thread handler for make same work parallel
*/
@Override void phaseWrite()
    {
    /*
    Create files, yet zero size, make this work outside of measured interval,
    cycle for required number of files
    */
    try
        {
        for( int i=0; i<fileCount; i++ )
            {
            srcPaths[i] = fileSystem.getPath
                ( pathSrc + prefixSrc + i + postfixSrc );
            Files.createFile( srcPaths[i] );
            srcChannels[i] = writeOpener.open( i, srcPaths );
            }
        }
    catch( IOException e )
        {
        helperDelete( srcPaths, srcChannels );
        lastError = 
            new StatusEntry( false, "Write error: " + e.getMessage() );
        }
    /*
    Write files
    */
    statistics.startInterval( TOTAL_WRITE_ID, System.nanoTime() );
    for( int i=0; i<fileCount; i++ )
        writeExecutor.execute( futureWriteTasks[i] );
    StatusEntry statusEntry = executorShutdownAndWait( writeExecutor );
    statistics.sendMBPS( TOTAL_WRITE_ID, total, System.nanoTime() );
    /*
    Store status
    */
    if ( ( ! statusEntry.flag )&&( lastError.flag ) )
        lastError = statusEntry;
    }

/*
Handler for Copy files,
override single thread handler for make same work parallel
*/
@Override void phaseCopy()
    {
    /*
    Prepare for copy files, open source and destination channels    
    */
    try
        {
        for( int i=0; i<fileCount; i++ )
            {
            srcChannels[i] = FileChannel.open( srcPaths[i] );
            dstPaths[i] = fileSystem.getPath
                ( pathDst + prefixDst + i + postfixDst );
            dstChannels[i] = copyOpener.open( i, dstPaths );
            }
        }
    catch( IOException e )
        {
        helperDelete( srcPaths, srcChannels );
        helperDelete( dstPaths, dstChannels );
        lastError = 
            new StatusEntry( false, "Copy error: " + e.getMessage() );
        }
    /*
    Copy files
    */
    statistics.startInterval( TOTAL_COPY_ID, System.nanoTime() );
    for( int i=0; i<fileCount; i++ )
        copyExecutor.execute( futureCopyTasks[i] );
    StatusEntry statusEntry = executorShutdownAndWait( copyExecutor );
    statistics.sendMBPS( TOTAL_COPY_ID, total, System.nanoTime() );
    /*
    Store status
    */
    if ( ( ! statusEntry.flag )&&( lastError.flag ) )
        lastError = statusEntry;
    }

/*
Handler for Read files,
override single thread handler for make same work parallel
*/
@Override void phaseRead()
    {
    /*
    Read files
    */
    statistics.startInterval( TOTAL_READ_ID, System.nanoTime() );
    for( int i=0; i<fileCount; i++ )
        readExecutor.execute( futureReadTasks[i] );
    StatusEntry statusEntry = executorShutdownAndWait( readExecutor );
    statistics.sendMBPS( TOTAL_READ_ID, total, System.nanoTime() );
    /*
    Store status
    */
    if ( ( ! statusEntry.flag )&&( lastError.flag ) )
        lastError = statusEntry;
    }


/*
Tasks for parallel execution
*/

/*
File Write task for parallel execution
*/
private class WriteTask implements Callable<StatusEntry>
    {
    private final int index;
    private WriteTask( int index ) { this.index = index; }
    @Override public StatusEntry call()
        {
        boolean statusFlag = true;
        String statusString = "OK";
        try
            {
            int j = fileSize;
            statistics.startInterval( WRITE_ID, System.nanoTime() );
            while( j >= blockSize )
                {
                byteBuffer.rewind();
                int k = 0;
                while( k < blockSize )
                    {
                    k += srcChannels[index].write( byteBuffer );
                    }
                j -= blockSize;
                }
            if ( j > 0 )
                {
                byteBufferTail.rewind();
                int k = 0;
                while ( k < j )
                    {
                    k += srcChannels[index].write( byteBufferTail );
                    }
                }
            if ( writeSync )
                srcChannels[index].force( true );
            statistics.sendMBPS( WRITE_ID, fileSize, System.nanoTime() );
            srcChannels[index].close();
            }
        catch( IOException e )
            {
            helperDelete( srcPaths, srcChannels );
            statusFlag = false;
            statusString = "Write error: " + e.getMessage();
            }
        StatusEntry statusEntry = new StatusEntry( statusFlag, statusString );
        if ( ! statusEntry.flag )
            lastError = statusEntry;
        setSync( index+1, statusEntry, phaseID, phaseName );
        return statusEntry;
        }
    }

/*
File Copy task for parallel execution
*/
private class CopyTask implements Callable<StatusEntry>
    {
    private final int index;
    private CopyTask( int index ) { this.index = index; }
    @Override public StatusEntry call()
        {
        boolean statusFlag = true;
        String statusString = "OK";
        try
            {
            int k = 0;
            if ( fastCopy )
                {
                statistics.startInterval( COPY_ID, System.nanoTime() );
                while( k < fileSize )
                    {
                    k += srcChannels[index].transferTo
                        ( 0, srcChannels[index].size(), dstChannels[index] );
                    }
                if ( copySync )
                    dstChannels[index].force( true );
                statistics.sendMBPS( COPY_ID, fileSize, System.nanoTime() );
                }
            else
                {
                statistics.startInterval( COPY_ID, System.nanoTime() );
                while( k < fileSize )
                    {
                    int n = blockSize;
                    int m = fileSize - k;
                    if ( n > m ) n = m;
                    k += srcChannels[index].
                        transferTo( 0, n, dstChannels[index] );
                    if ( copySync )
                        dstChannels[index].force( true );
                    }
                statistics.sendMBPS( COPY_ID, fileSize, System.nanoTime() );
                }
            }
        catch( IOException e )
            {
            helperDelete( srcPaths, srcChannels );
            helperDelete( dstPaths, dstChannels );
            statusFlag = false;
            statusString = "Copy error: " + e.getMessage();
            }
        StatusEntry statusEntry = new StatusEntry( statusFlag, statusString );
        if ( ! statusEntry.flag )
            lastError = statusEntry;
        setSync( index+1, statusEntry, phaseID, phaseName );
        return statusEntry;
        }
    }

/*
File Read task for parallel execution
*/
private class ReadTask implements Callable<StatusEntry>
    {
    private final int index;
    private ReadTask( int index ) { this.index = index; }
    @Override public StatusEntry call()
        {
        boolean statusFlag = true;
        String statusString = "OK";
        try
            {
            int k;
            statistics.startInterval( READ_ID, System.nanoTime() );
            do  {
                byteBuffer.rewind();
                k = srcChannels[index].read( byteBuffer );
                } while ( k != -1);
            statistics.sendMBPS( READ_ID, fileSize, System.nanoTime() );
            }
        catch( IOException e )
            {
            helperDelete( srcPaths, srcChannels );
            helperDelete( dstPaths, dstChannels );
            statusFlag = false;
            statusString = "Read error: " + e.getMessage();
            }
        StatusEntry statusEntry = new StatusEntry( statusFlag, statusString );
        if ( ! statusEntry.flag )
            lastError = statusEntry;
        setSync( index+1, statusEntry, phaseID, phaseName );
        return statusEntry;
        }
    }

/*
File Delete task for parallel execution
*/
private class DeleteTask implements Callable<StatusEntry>
    {
    private final int index;
    private final boolean srcOrDst;
    private DeleteTask( int index, boolean srcOrDst ) 
        {
        this.index = index;
        this.srcOrDst = srcOrDst;
        }
    @Override public StatusEntry call()
        {
        boolean statusFlag = true;
        String statusString = "OK";
            
        return new StatusEntry( statusFlag, statusString );
        }
    }

/*
Helper for parallel executor all running tasks execution wait
*/
StatusEntry executorShutdownAndWait( ExecutorService executor )
    {
    boolean statusFlag = true;
    String statusString = "OK";
    try
        {
        executor.shutdown();
        boolean b = executor.awaitTermination( 10, TimeUnit.MINUTES );
        if ( !b )
            {
            statusFlag = false;
            statusString = "Executor wait timeout";
            }
        }
    catch ( InterruptedException e )
        {
        statusFlag = false;
        statusString = "Executor wait: " + e.getMessage();
        }
    return new StatusEntry( statusFlag, statusString );
    }

/*
Helper for parallel executor shutdown, method can be used by child classes
*/
StatusEntry executorShutdown( ExecutorService executor )
    {
    boolean statusFlag = true;
    String statusString = "OK";
    if (( ! executor.isTerminated() )||( ! executor.isShutdown() ))
        {
        try
            {
            executor.shutdown();
            executor.awaitTermination( 5, TimeUnit.SECONDS );
            }
        catch (InterruptedException e )
            {
            statusFlag = false;
            statusString = "Executor shutdown: " + e.getMessage();
            }
        finally
            {
            if (( ! executor.isTerminated() )||( ! executor.isShutdown() ))
                {
                statusFlag = false;
                statusString = "Non-finished tasks termination forced";
                executor.shutdownNow();
                if (( ! executor.isTerminated() )||( ! executor.isShutdown() ))
                    {
                    statusString = "Cannot shutdown tasks";
                    }
                }
            }
        }
    return new StatusEntry( statusFlag, statusString );
    }

}
