/*
Sequental tasks dispatcher for memory-mapped files (MMF) benchmark.
*/

package jgsp.iommfmbps;

import jgsp.statistics.StatusEntry;
import static jgsp.helpers.Delay.delay;
import jgsp.templates.IOscenario;

public class MBPSsingleThreadMMF extends IOscenario
{
/*
Helper for Read, Write, Copy, Delete files    
*/
final HelperIO io;
/*
Constructor for options settings by internal defaults
*/    
public MBPSsingleThreadMMF()
    {
    super();
    io = new HelperIO( statistics );
    }
/*
Constructor for options settings by input parameters
*/
public MBPSsingleThreadMMF
        ( String pathSrc, String prefixSrc, String postfixSrc,
          String pathDst, String prefixDst, String postfixDst,
          int mode, int fileCount, int fileSize, int blockSize,
          int readDelay, int writeDelay, int copyDelay,
          byte[] dataBlock )
    {
    super( pathSrc, prefixSrc, postfixSrc,
           pathDst, prefixDst, postfixDst,
           mode, fileCount, fileSize, blockSize,
           readDelay, writeDelay, copyDelay,
           dataBlock );
    io = new HelperIO( statistics );
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
