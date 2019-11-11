/*
Sequental tasks dispatcher for memory-mapped files benchmark.
*/

package jgsp.memorymappedfiles;

import java.util.LinkedList;
import jgsp.statistics.StateAsync;
import jgsp.statistics.StateSync;
import jgsp.statistics.StatisticsModel;
import jgsp.statistics.StatusEntry;
import static jgsp.timings.Delay.delay;

public class WorkSequentalMBPS extends Thread
{
/*
Test phases constants definition
*/
final static int READ_ID  = 0;
final static int WRITE_ID = 1;
final static int COPY_ID  = 2;
final static int TOTAL_READ_ID  = 3;
final static int TOTAL_WRITE_ID = 4;
final static int TOTAL_COPY_ID  = 5;
final static int ID_COUNT = 6;
/*
Options constants definitions    
*/
final static int READ_WRITE = 0;
final static int READ_ONLY  = 1;
final static int WRITE_ONLY = 2;
/*
Default settings for options variables
*/
private final static String DEFAULT_PATH_SRC    = "C:\\TEMP\\";
private final static String DEFAULT_PREFIX_SRC  = "src";
private final static String DEFAULT_POSTFIX_SRC = ".bin";
private final static String DEFAULT_PATH_DST    = "C:\\TEMP\\";
private final static String DEFAULT_PREFIX_DST  = "dst";
private final static String DEFAULT_POSTFIX_DST = ".bin";
private final static int    DEFAULT_MODE        = READ_WRITE;
private final static int    DEFAULT_FILE_COUNT  = 20;
private final static int    DEFAULT_FILE_SIZE   = 100*1024*1024;
private final static int    DEFAULT_BLOCK_SIZE  = 10*1024*1024;
private final static int    DEFAULT_READ_DELAY  = 0;
private final static int    DEFAULT_WRITE_DELAY = 0;
private final static int    DEFAULT_COPY_DELAY  = 0;
/*
Options variables, can't be private because used by child class
*/
final String pathSrc;
final String prefixSrc;
final String postfixSrc;
final String pathDst;
final String prefixDst;
final String postfixDst;
final int mode;
final int fileCount;
final int fileSize;
final int blockSize;
final int readDelay;
final int writeDelay;
final int copyDelay;
final byte[] dataBlock;
/*
Read, Write, Copy, Delete helper and benchmarks results statistics support,
entries list (queue) for process synchronous monitoring.
*/
final StatisticsModel statistics;
final HelperIO io;
final LinkedList<StateSync> syncQueue;
/*
Current executed phase id and name string, percentage, last error data,
*/
int phaseID = -1;
String phaseName = "";
double percentage = 0.0;
StatusEntry lastError;
/*
Constructor for options settings by internal defaults
*/    
public WorkSequentalMBPS()
    {
    pathSrc    = DEFAULT_PATH_SRC;
    prefixSrc  = DEFAULT_PREFIX_SRC;
    postfixSrc = DEFAULT_POSTFIX_SRC;
    pathDst    = DEFAULT_PATH_DST;
    prefixDst  = DEFAULT_PREFIX_DST;
    postfixDst = DEFAULT_POSTFIX_DST;
    mode       = DEFAULT_MODE;
    fileCount  = DEFAULT_FILE_COUNT;
    fileSize   = DEFAULT_FILE_SIZE;
    blockSize  = DEFAULT_BLOCK_SIZE;
    readDelay  = DEFAULT_READ_DELAY;
    writeDelay = DEFAULT_WRITE_DELAY;
    copyDelay  = DEFAULT_COPY_DELAY;
    dataBlock  = new byte[blockSize];
    for( int i=0; i<blockSize; i++ )
        dataBlock[i] = 0;
    statistics = new StatisticsModel( ID_COUNT );
    io = new HelperIO( statistics );
    syncQueue = new LinkedList();
    }
/*
Constructor for options settings by input parameters
*/
public WorkSequentalMBPS
        ( String pathSrc, String prefixSrc, String postfixSrc,
          String pathDst, String prefixDst, String postfixDst,
          int mode, int fileCount, int fileSize, int blockSize,
          int readDelay, int writeDelay, int copyDelay,
          byte[] dataBlock )
    {
    this.pathSrc    = ( pathSrc == null    ) ? DEFAULT_PATH_SRC    : pathSrc;
    this.prefixSrc  = ( prefixSrc == null  ) ? DEFAULT_PREFIX_SRC  : prefixSrc;
    this.postfixSrc = ( postfixSrc == null ) ? DEFAULT_POSTFIX_SRC : postfixSrc;
    this.pathDst    = ( pathDst == null    ) ? DEFAULT_PATH_DST    : pathDst;
    this.prefixDst  = ( prefixDst == null  ) ? DEFAULT_PREFIX_DST  : prefixDst;
    this.postfixDst = ( postfixDst == null ) ? DEFAULT_POSTFIX_DST : postfixDst;
    this.mode       = ( mode == -1         ) ? DEFAULT_MODE        : mode;
    this.fileCount  = ( fileCount == -1    ) ? DEFAULT_FILE_COUNT  : fileCount;
    this.fileSize   = ( fileSize == -1     ) ? DEFAULT_FILE_SIZE   : fileSize;
    this.blockSize  = ( blockSize == -1    ) ? DEFAULT_BLOCK_SIZE  : blockSize;
    this.readDelay  = ( readDelay == -1    ) ? DEFAULT_READ_DELAY  : readDelay;
    this.writeDelay = ( writeDelay == -1   ) ? DEFAULT_WRITE_DELAY : writeDelay;
    this.copyDelay  = ( copyDelay == -1    ) ? DEFAULT_COPY_DELAY  : copyDelay;
    
    if ( dataBlock  == null )
        {
        this.dataBlock = new byte[blockSize];
        for( int i=0; i<blockSize; i++ )
            dataBlock[i] = 0;
        }
    else
        {
        this.dataBlock = dataBlock;
        }
    statistics = new StatisticsModel( ID_COUNT );
    io = new HelperIO( statistics );
    syncQueue = new LinkedList();
    }
/*
Asynchronous get I/O performance statistics
*/
public StateAsync[] getAsync()
    {
    StateAsync[] entries = new StateAsync[ID_COUNT];
    for( int i=0; i<ID_COUNT; i++ )
        {
        entries[i] = statistics.receive( i );
        }
    return entries;    
    }
/*
Asynchronous get current phase name
*/
public String getPhaseName()
    {
    return phaseName;
    }
/*
Asynchronous get current percentage
*/
public double getPercentage()
    {
    return percentage;
    }
/*
Asynchronous get last error
*/
public StatusEntry getLastError()
    {
    return lastError;
    }

/*
Get entry of synchronous statistics
*/
public StateSync getSync()
    {
    synchronized( syncQueue )
        {
        if ( syncQueue.isEmpty() )
            {
            return null;
            }
        else
            {
            return syncQueue.removeFirst();
            }
        }
    }

/*
Add entry of synchronous statistics, also used by child class
*/
void setSync( int count, StatusEntry se, int id, String name  )
    {
    if ( id >= 0 )
        {
        StateAsync a = statistics.receive( id );
        StateSync s = new StateSync
            ( count, se, id, name, 
              a.current, a.min, a.max, a.average, a.median );
        synchronized( syncQueue )
            {
            syncQueue.add( s );
            }
        }
    }

/*
Clear synchronous statistics, also used by child class
*/
void clearSync()
    {
    synchronized( syncQueue )
        {
        syncQueue.clear();
        }
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
    long total = fileCount * fileSize;
    
    if ( mode != READ_ONLY )
        {
        /*
        Phase = Write
        */
        phaseID = WRITE_ID;
        phaseName = "pre-write wait...";
        delay( writeDelay );
        phaseName = "write...";
        
        statistics.startInterval( TOTAL_WRITE_ID, System.nanoTime() );
        for( int i=0; i<fileCount; i++ )
            {
            if ( isInterrupted() )
                break;
            String src = pathSrc + prefixSrc + i + postfixSrc;
            MappedStatusEntry statusEntry = 
                        io.mappedWrite( src, fileSize, dataBlock, true );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            setSync( i+1, statusEntry, phaseID, phaseName );
            }
        statistics.sendMBPS( TOTAL_WRITE_ID, total, System.nanoTime() );
        
        /*
        Phase = Copy
        */
        phaseID = COPY_ID;
        phaseName = "pre-copy wait...";
        delay( copyDelay );
        phaseName = "copy...";
        
        statistics.startInterval( TOTAL_COPY_ID, System.nanoTime() );
        for( int i=0; i<fileCount; i++ )
            {
            if ( isInterrupted() )
                break;
            String src = pathSrc + prefixSrc + i + postfixSrc;
            String dst = pathDst + prefixDst + i + postfixDst;
            MappedStatusEntry statusEntry =
                        io.mappedCopy( src, dst, blockSize, true );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            setSync( i+1, statusEntry, phaseID, phaseName );
            }
        statistics.sendMBPS( TOTAL_COPY_ID, total, System.nanoTime() );

        }
    
    if ( mode != WRITE_ONLY )
        {
        /*
        Phase = Read
        */
        phaseID = READ_ID;
        phaseName = "pre-read wait...";
        delay( readDelay );
        phaseName = "read...";
        
        statistics.startInterval( TOTAL_READ_ID, System.nanoTime() );
        for( int i=0; i<fileCount; i++ )
            {
            if ( isInterrupted() )
                break;
            String src = pathSrc + prefixSrc + i + postfixSrc;
            MappedStatusEntry statusEntry =
                        io.mappedRead( src, blockSize, true );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            setSync( i+1, statusEntry, phaseID, phaseName );
            }
        statistics.sendMBPS( TOTAL_READ_ID, total, System.nanoTime() );

        }
    
    phaseID = -1;
    if ( mode == READ_WRITE )
        {
        phaseName = "delete...";
        /*
        Phase = Delete, note about files not deleted in WRITE_ONLY mode.
        Note delete operation cycles for all files is not interruptable.
        Use 2 separate cycles for src and dst, for delete sequence same as
        write sequence, performance reasons.
        */
        for( int i=0; i<fileCount; i++ )
            {
            String src = pathSrc + prefixSrc + i + postfixSrc;
            StatusEntry statusEntry = io.mappedDelete( src );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            }
        for( int i=0; i<fileCount; i++ )
            {
            String dst = pathDst + prefixDst + i + postfixDst;
            StatusEntry statusEntry = io.mappedDelete( dst );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            }
        }
    }
}
