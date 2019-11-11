/*
Parallel tasks dispatcher for memory-mapped files benchmark.
*/

package jgsp.memorymappedfiles;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import static jgsp.memorymappedfiles.WorkSequentalMBPS.WRITE_ID;
import jgsp.statistics.StatusEntry;
import static jgsp.timings.Delay.delay;

public class WorkParallelMBPS extends WorkSequentalMBPS
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
public WorkParallelMBPS()
    {
    super();
    threadCount       = DEFAULT_THREAD_COUNT;
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
public WorkParallelMBPS
        ( String pathSrc, String prefixSrc, String postfixSrc,
          String pathDst, String prefixDst, String postfixDst,
          int mode, int fileCount, int fileSize, int blockSize,
          int readDelay, int writeDelay, int copyDelay,
          byte[] dataBlock, int threadCount )
    {
    super( pathSrc, prefixSrc, postfixSrc, 
           pathDst, prefixDst, postfixDst,
           mode, fileCount, fileSize, blockSize,
           readDelay, writeDelay, copyDelay,
           dataBlock );
    this.threadCount  = threadCount;
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
    StatusEntry statusEntry;
    clearSync();
    long total = fileCount * fileSize;
    
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
        Phase = Write
        */
        phaseID = WRITE_ID;
        phaseName = "pre-write wait...";
        delay( writeDelay );
        phaseName = "write parallel...";
        
        statistics.startInterval( TOTAL_WRITE_ID, System.nanoTime() );
        for( int i=0; i<fileCount; i++ )
            writeExecutor.execute( futureWriteTasks[i] );
        statusEntry = executorShutdownAndWait( writeExecutor );
        statistics.sendMBPS( TOTAL_WRITE_ID, total, System.nanoTime() );
        
        if ( ( ! statusEntry.flag )&&( lastError.flag ) )
            lastError = statusEntry;
        /*
        Phase = Copy
        */
        phaseID = COPY_ID;
        phaseName = "pre-copy wait...";
        delay( copyDelay );
        phaseName = "copy parallel...";
        
        statistics.startInterval( TOTAL_COPY_ID, System.nanoTime() );
        for( int i=0; i<fileCount; i++ )
            copyExecutor.execute( futureCopyTasks[i] );
        statusEntry = executorShutdownAndWait( copyExecutor );
        statistics.sendMBPS( TOTAL_COPY_ID, total, System.nanoTime() );

        if ( ( ! statusEntry.flag )&&( lastError.flag ) )
            lastError = statusEntry;
        }
    
    if ( mode != WRITE_ONLY )
        {
        /*
        Phase = Read
        */
        phaseID = READ_ID;
        phaseName = "pre-read wait...";
        delay( readDelay );
        phaseName = "read parallel...";
        
        statistics.startInterval( TOTAL_READ_ID, System.nanoTime() );
        for( int i=0; i<fileCount; i++ )
            readExecutor.execute( futureReadTasks[i] );
        statusEntry = executorShutdownAndWait( readExecutor );
        statistics.sendMBPS( TOTAL_READ_ID, total, System.nanoTime() );
        
        if ( ( ! statusEntry.flag )&&( lastError.flag ) )
            lastError = statusEntry;
        }

    phaseID = -1;
    if ( mode == READ_WRITE )
        {
        phaseName = "delete parallel...";
        /*
        Phase = Delete, note about files not deleted in WRITE_ONLY mode.
        Note delete operation cycles for all files is not interruptable.
        Use 2 separate cycles for src and dst, for delete sequence same as
        write sequence, performance reasons.
        */
        for( int i=0; i < ( fileCount*2 ); i++ )
            deleteExecutor.execute( futureDeleteTasks[i] );
        statusEntry = executorShutdownAndWait( deleteExecutor );
        if ( ( ! statusEntry.flag )&&( lastError.flag ) )
            lastError = statusEntry;
        }

    /*
    Executors shutdown
    */
    ExecutorService[] executors = 
        new ExecutorService[]
            { writeExecutor, copyExecutor, readExecutor, deleteExecutor };
    for ( ExecutorService executor : executors ) 
        {
        statusEntry = executorShutdown( executor );
        if ( ( ! statusEntry.flag )&&( lastError.flag ) )
            lastError = statusEntry;
        }
    }

/*
File Write task for parallel execution
*/
private class WriteTask implements Callable<StatusEntry>
    {
    private final int index;
    private WriteTask( int index ) { this.index = index; }
    @Override public StatusEntry call()
        {
        String src = pathSrc + prefixSrc + index + postfixSrc;
        MappedStatusEntry statusEntry = 
                    io.mappedWrite( src, fileSize, dataBlock, true );
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
        String src = pathSrc + prefixSrc + index + postfixSrc;
        String dst = pathDst + prefixDst + index + postfixDst;
        MappedStatusEntry statusEntry =
                    io.mappedCopy( src, dst, blockSize, true );
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
        String src = pathSrc + prefixSrc + index + postfixSrc;
        MappedStatusEntry statusEntry =
                    io.mappedRead( src, blockSize, true );
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
        String srcdst = srcOrDst 
            ? ( pathSrc + prefixSrc + index + postfixSrc )
            : ( pathDst + prefixDst + index + postfixDst );
        StatusEntry statusEntry = io.mappedDelete( srcdst );
        if ( ! statusEntry.flag )
            lastError = statusEntry;
        return statusEntry;
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
