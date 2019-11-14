/*
Rejected (?), design MBPS and IOPS test separately, over-templating is BAD.
*/


package jgsp.iommfmbps;

import static jgsp.helpers.Delay.delay;
import static jgsp.helpers.Random.randomArray;
import jgsp.statistics.StatusEntry;

public class IOPSsingleThreadMMF extends MBPSsingleThreadMMF
{
/*
Default settings for options variables, 
override some MBPS defaults for IOPS mode
*/
private final static int    DEFAULT_IOPS_FILE_COUNT  = 1000; // 100000;
private final static int    DEFAULT_IOPS_FILE_SIZE   = 4096;
private final static int    DEFAULT_IOPS_BLOCK_SIZE  = 4096;
/*
Options variables, override some MBPS defaults for IOPS mode,
Note for IOPS mode, 
byte[] iopsDataBlock[]   must contain total data size for all (small) files,
int[]  iopsAddressBlock[] must contain randomized sequence of file numbers
*/
private final int iopsFileCount;
private final int iopsFileSize;
private final int iopsBlockSize;
private final byte[][] iopsDataBlock;
private final int[] iopsSrcAddressBlock;
private final int[] iopsDstAddressBlock;
/*
Constructor for options settings by internal defaults
*/    
public IOPSsingleThreadMMF()
    {
    super();
    iopsFileCount = DEFAULT_IOPS_FILE_COUNT;
    iopsFileSize = DEFAULT_IOPS_FILE_SIZE;
    iopsBlockSize = DEFAULT_IOPS_BLOCK_SIZE;
    iopsDataBlock = new byte[iopsFileCount][iopsFileSize];
    for( int i=0; i<iopsFileCount; i++ )
        {
        for( int j=0; j<iopsFileSize; j++ )
            {
            iopsDataBlock[i][j] = 0;
            }
        }
    iopsSrcAddressBlock = randomArray( iopsFileCount );
    iopsDstAddressBlock = randomArray( iopsFileCount );
    }
/*
Constructor for options settings by input parameters
*/
public IOPSsingleThreadMMF
        ( String pathSrc, String prefixSrc, String postfixSrc,
          String pathDst, String prefixDst, String postfixDst,
          int mode, int fileCount, int fileSize, int blockSize,
          int readDelay, int writeDelay, int copyDelay,
          byte[] dataBlock, int[] srcAddressBlock, int[] dstAddressBlock )
    {
    super( pathSrc, prefixSrc, postfixSrc, 
           pathDst, prefixDst, postfixDst,
           mode, fileCount, fileSize, blockSize,
           readDelay, writeDelay, copyDelay,
           dataBlock );
    iopsFileCount = ( fileCount == -1 ) ? DEFAULT_IOPS_FILE_COUNT : fileCount;
    iopsFileSize  = ( fileSize == -1  ) ? DEFAULT_IOPS_FILE_SIZE  : fileSize;
    iopsBlockSize = ( blockSize == -1 ) ? DEFAULT_IOPS_BLOCK_SIZE : blockSize;
    iopsDataBlock = new byte[iopsFileCount][iopsFileSize];
    int k = 0;
    for( int i=0; i<iopsFileCount; i++ )
        {
        for( int j=0; j<iopsFileSize; j++ )
            {
            if ( k >= dataBlock.length )
                {
                k = 0;
                }
            iopsDataBlock[i][j] = dataBlock[k++];
            }
        }
    iopsSrcAddressBlock = srcAddressBlock;
    iopsDstAddressBlock = dstAddressBlock;
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
        Phase = Prepare array of files, sequental operation at this phase    
        */
        phaseID = WRITE_ID;
        for( int i=0; i<iopsFileCount; i++ )
            {
            if ( isInterrupted() )
                break;
            String src = pathSrc + prefixSrc + i + postfixSrc;
            String dst = pathDst + prefixDst + i + postfixDst;
            MappedStatusEntry statusEntry =
                io.mappedWrite( src, iopsFileSize, iopsDataBlock[i], true );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            statusEntry =
                io.mappedWrite( dst, iopsFileSize, iopsDataBlock[i], true );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            }
        /*
        Phase = Write
        */
        phaseName = "pre-write wait...";
        delay( writeDelay );
        phaseName = "write...";
        clearSync();  // repeat because pre-clear statistics now stored
        
        // BUG because pre-clear statistics now stored
        
        statistics.startInterval( TOTAL_WRITE_ID, System.nanoTime() );
        for( int i=0; i<iopsFileCount; i++ )
            {
            if ( isInterrupted() )
                break;
            String src = 
                pathSrc + prefixSrc + iopsSrcAddressBlock[i] + postfixSrc;
            MappedStatusEntry statusEntry =
                io.mappedWrite( src, iopsFileSize, iopsDataBlock[i], true );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            setSync( i+1, statusEntry, phaseID, phaseName );
            }
        statistics.sendIOPS( TOTAL_WRITE_ID, total, System.nanoTime() );
        
        /*
        Phase = Copy
        */
        phaseID = COPY_ID;
        phaseName = "pre-copy wait...";
        delay( copyDelay );
        phaseName = "copy...";
        
        statistics.startInterval( TOTAL_COPY_ID, System.nanoTime() );
        for( int i=0; i<iopsFileCount; i++ )
            {
            if ( isInterrupted() )
                break;
            String src = 
                pathSrc + prefixSrc + iopsSrcAddressBlock[i] + postfixSrc;
            String dst = 
                pathDst + prefixDst + iopsDstAddressBlock[i] + postfixDst;
            MappedStatusEntry statusEntry =
                        io.mappedCopy( src, dst, blockSize, true );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            setSync( i+1, statusEntry, phaseID, phaseName );
            }
        statistics.sendIOPS( TOTAL_COPY_ID, total*2, System.nanoTime() );
        
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
        for( int i=0; i<iopsFileCount; i++ )
            {
            if ( isInterrupted() )
                break;
            String src = 
                pathSrc + prefixSrc + iopsSrcAddressBlock[i] + postfixSrc;
            MappedStatusEntry statusEntry =
                        io.mappedRead( src, blockSize, true );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            setSync( i+1, statusEntry, phaseID, phaseName );
            }
        statistics.sendIOPS( TOTAL_READ_ID, total, System.nanoTime() );
            
        }
    
    phaseID = -1;
    if ( mode == READ_WRITE )
        {
        phaseName = "delete...";
        /*
        Phase = Delete, note about files not deleted in WRITE_ONLY mode.
        Note delete operation cycles for all files is not interruptable.
        */
        for( int i=0; i<iopsFileCount; i++ )
            {
            String src = pathSrc + prefixSrc + i + postfixSrc;
            String dst = pathDst + prefixDst + i + postfixDst;
            StatusEntry statusEntry = io.mappedDelete( src );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            statusEntry = io.mappedDelete( dst );
            if ( ! statusEntry.flag )
                lastError = statusEntry;
            }
        }
    }
}
