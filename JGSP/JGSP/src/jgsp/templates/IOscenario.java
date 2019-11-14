package jgsp.templates;

import java.util.LinkedList;
import jgsp.statistics.StateAsync;
import jgsp.statistics.StateSync;
import jgsp.statistics.StatisticsModel;
import jgsp.statistics.StatusEntry;

public class IOscenario extends Thread
{
/*
Test phases constants definition
*/
public final static int READ_ID  = 0;
public final static int WRITE_ID = 1;
public final static int COPY_ID  = 2;
public final static int TOTAL_READ_ID  = 3;
public final static int TOTAL_WRITE_ID = 4;
public final static int TOTAL_COPY_ID  = 5;
public final static int ID_COUNT = 6;
/*
Options constants definitions    
*/
public final static int READ_WRITE = 0;
public final static int READ_ONLY  = 1;
public final static int WRITE_ONLY = 2;
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
protected final String pathSrc;
protected final String prefixSrc;
protected final String postfixSrc;
protected final String pathDst;
protected final String prefixDst;
protected final String postfixDst;
protected final int mode;
protected final int fileCount;
protected final int fileSize;
protected final int blockSize;
protected final int readDelay;
protected final int writeDelay;
protected final int copyDelay;
protected final byte[] dataBlock;
/*
Read, Write, Copy, Delete helper and benchmarks results statistics support,
entries list (queue) for process synchronous monitoring.
*/
final protected StatisticsModel statistics;
final LinkedList<StateSync> syncQueue;
/*
Current executed phase id and name string, percentage, last error data,
*/
protected int phaseID = -1;
protected String phaseName = "";
protected double percentage = 0.0;
protected StatusEntry lastError;
/*
Constructor for options settings by internal defaults
*/    
public IOscenario()
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
    syncQueue = new LinkedList();
    }
/*
Constructor for options settings by input parameters
*/
public IOscenario
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
protected void setSync( int count, StatusEntry se, int id, String name  )
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
protected void clearSync()
    {
    synchronized( syncQueue )
        {
        syncQueue.clear();
        }
    }
}
