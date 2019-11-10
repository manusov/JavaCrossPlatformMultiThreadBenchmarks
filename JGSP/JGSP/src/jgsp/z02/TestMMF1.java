/*
Test for memory-mapped files.
*/

package jgsp.z02;

import java.nio.MappedByteBuffer;
import jgsp.z01.MappedStatusEntry;
import jgsp.z01.MemoryMappedFile;
import static jgsp.z01.MemoryMappedFile.COPY_ID;
import static jgsp.z01.MemoryMappedFile.READ_ID;
import static jgsp.z01.MemoryMappedFile.WRITE_ID;
import jgsp.statistics.StateAsync;
import jgsp.statistics.StatisticsModel;
import jgsp.statistics.StatusEntry;

public class TestMMF1 
{
private final static String SRC_FILE = "C:\\TEMP\\test_source.bin";
private final static String DST_FILE = "C:\\TEMP\\test_destination.bin";
private final static int FILE_SIZE = 4096 + 5;
private final static int BLOCK_SIZE = 4096;
    
public void execSimple()
    {
    System.out.println( "Starting SIMPLE TEST MEMORY MAPPED FILES..." );
    MemoryMappedFile mmf = new MemoryMappedFile( null );
    
    byte[] array = new byte[BLOCK_SIZE];
    for( int i=0; i<BLOCK_SIZE; i++ )
        {
        array[i] = (byte)i;
        }
    
    MappedStatusEntry entry1 = mmf.mappedWrite( SRC_FILE, FILE_SIZE, array );
    String s = String.format
        ( "Write status=%b, ", entry1.flag ) + entry1.string;
    System.out.println( s );

    StatusEntry entry2 = mmf.unmapAndDelete( null, entry1.buffer );
    s = String.format
        ( "Unmap status=%b, ", entry2.flag ) + entry2.string;
    System.out.println( s );

    entry1 = mmf.mappedRead( SRC_FILE, BLOCK_SIZE );
    s = String.format( "Read status=%b, ", entry1.flag ) + entry1.string;
    System.out.println( s );

    entry2 = mmf.unmapAndDelete( null, entry1.buffer );
    s = String.format
        ( "Unmap status=%b, ", entry2.flag ) + entry2.string;
    System.out.println( s );

    entry1 = mmf.mappedCopy( SRC_FILE, DST_FILE, BLOCK_SIZE );
    s = String.format( "Copy status=%b, ", entry1.flag ) + entry1.string;
    System.out.println( s );

    entry2 = mmf.unmapAndDelete( SRC_FILE, entry1.buffer );
    s = String.format
        ( "Delete source file status=%b, ", entry2.flag ) + entry2.string;
    System.out.println( s );
    
    entry2 = mmf.unmapAndDelete( DST_FILE, entry1.bufferDual );
    s = String.format
        ( "Delete destination file status=%b, ", entry2.flag ) + entry2.string;
    System.out.println( s );
    }

/*
private class ProgressAgent extends StatisticsModel
    {
    private ProgressAgent( int arraysCount )
        {
        super( arraysCount );
        }
    }
*/

public void execScenario()
    {
    final int COUNT = 20;
    final String PATH = "C:\\TEMP\\";
    final String SRC = "src";
    final String DST = "dst";
    final String EXT = ".bin";
    final int FSIZE = 100*1024*1024;
    final int BSIZE = 10*1024*1024;
    final byte[] DATA = new byte[BSIZE];
    for( int i=0; i<BSIZE; i++ )
        {
        DATA[i] = (byte)i;
        }

    StatisticsModel sm = new StatisticsModel( 3 );
    MemoryMappedFile mmf = new MemoryMappedFile( sm );
        
    System.out.println( "Starting SCENARIO TEST MEMORY MAPPED FILES..." );
    
    String[] srcNames = new String[COUNT];
    String[] dstNames = new String[COUNT];
    MappedByteBuffer[] srcBuffers = new MappedByteBuffer[COUNT];
    MappedByteBuffer[] dstBuffers = new MappedByteBuffer[COUNT];
    
    String line = 
        "-----------------------------------------------------------------";
    String uptext =  "current  min      max      average  median   status";

    
    /*
    Write phase.
    */
    System.out.printf
        ( "\r\nWrite phase...\r\n%s\r\n%s\r\n%s\r\n", line, uptext, line );
    for( int i=0; i<COUNT; i++ )
        {
        srcNames[i] = PATH + SRC + i + EXT;
        MappedStatusEntry mse = mmf.mappedWrite( srcNames[i], FSIZE, DATA );
        boolean status = mse.flag;
        if ( ! status )
            {
            System.out.println( mse.string );
            }
        srcBuffers[i] = mse.buffer;
        StateAsync entry = sm.receive( WRITE_ID );
        if ( entry != null )
            {
            String s = String.format
                ( "%-9.3f%-9.3f%-9.3f%-9.3f%-9.3f%b", 
                entry.current, 
                entry.min, entry.max, entry.average, entry.median,
                status );
            System.out.println( s );
            }
        }
    System.out.println( line );
    helperUnmapAndDelete
        ( mmf, COUNT, srcNames, dstNames, srcBuffers, dstBuffers, false );
    
    
    /*
    Copy phase.
    */
    System.out.printf
        ( "\r\nCopy phase...\r\n%s\r\n%s\r\n%s\r\n", line, uptext, line );
    for( int i=0; i<COUNT; i++ )
        {
        srcNames[i] = PATH + SRC + i + EXT;
        dstNames[i] = PATH + DST + i + EXT;
        MappedStatusEntry mse = 
            mmf.mappedCopy( srcNames[i], dstNames[i], BSIZE );
        boolean status = mse.flag;
        if ( ! status )
            {
            System.out.println( mse.string );
            }
        srcBuffers[i] = mse.buffer;
        dstBuffers[i] = mse.bufferDual;
        StateAsync entry = sm.receive( COPY_ID );
        if ( entry != null )
            {
            String s = String.format
                ( "%-9.3f%-9.3f%-9.3f%-9.3f%-9.3f%b", 
                entry.current, 
                entry.min, entry.max, entry.average, entry.median,
                status );
            System.out.println( s );
            }
        }
    System.out.println( line );
    helperUnmapAndDelete
        ( mmf, COUNT, srcNames, dstNames, srcBuffers, dstBuffers, false );

    /*
    Read phase.
    */
    System.out.printf
        ( "\r\nRead phase...\r\n%s\r\n%s\r\n%s\r\n", line, uptext, line );
    for( int i=0; i<COUNT; i++ )
        {
        srcNames[i] = PATH + SRC + i + EXT;
        MappedStatusEntry mse = 
            mmf.mappedRead( srcNames[i], BSIZE );
        boolean status = mse.flag;
        if ( ! status )
            {
            System.out.println( mse.string );
            }
        srcBuffers[i] = mse.buffer;
        //dstBuffers[i] = null;
        StateAsync entry = sm.receive( READ_ID );
        if ( entry != null )
            {
            String s = String.format
                ( "%-9.3f%-9.3f%-9.3f%-9.3f%-9.3f%b", 
                entry.current, 
                entry.min, entry.max, entry.average, entry.median,
                status );
            System.out.println( s );
            }
        }
    System.out.println( line );
    helperUnmapAndDelete
        ( mmf, COUNT, srcNames, dstNames, srcBuffers, dstBuffers, true );
    }




private void helperUnmapAndDelete
        ( MemoryMappedFile mmf,
          int COUNT, String[] srcNames, String[] dstNames, 
          MappedByteBuffer[] srcBuffers, MappedByteBuffer[] dstBuffers,
          boolean deleteMode )
    {
    System.out.println( "Delete phase..." );
    for( int i=0; i<COUNT; i++ )
        {
        if ( ( srcNames[i] != null )&&( srcBuffers[i] != null ) )
            {
            StatusEntry se;
            String s1;
            if ( deleteMode )
                {
                se = mmf.unmapAndDelete( srcNames[i], srcBuffers[i] );
                s1 = "Delete";
                }
            else
                {
                se = mmf.unmapAndDelete( null, srcBuffers[i] );
                s1 = "Unmap";
                }
            if ( se != null )
                {
                String s = String.format
                    ( "%s %s, status=%b, status string=%s",
                      s1, srcNames[i], se.flag, se.string );
                System.out.println( s );
                }
            else
                {
                System.out.println( "status object is null" );
                }
            }
        }
    for( int i=0; i<COUNT; i++ )
        {
        if ( ( dstNames[i] != null )&&( dstBuffers[i] != null ) )
            {
            StatusEntry se;
            String s1;
            if ( deleteMode )
                {
                se = mmf.unmapAndDelete( dstNames[i], dstBuffers[i] );
                s1 = "Delete";
                }
            else
                {
                se = mmf.unmapAndDelete( null, dstBuffers[i] );
                s1 = "Unmap";
                }
            if ( se != null )
                {
                String s = String.format
                    ( "%s %s, status=%b, status string=%s",
                      s1, dstNames[i], se.flag, se.string );
                System.out.println( s );
                }
            else
                {
                System.out.println( "status object is null" );
                }
            }
        }
    }
}
