package jgsp.testconsole;

import jgsp.memorymappedfiles.WorkParallelMBPS;
import jgsp.memorymappedfiles.WorkSequentalMBPS;
import jgsp.statistics.StateAsync;
import jgsp.statistics.StateSync;
import static jgsp.timings.Delay.delay;

public class TestMemoryMappedFiles 
{

public static void AsynchronousMonitor()
    {
    System.out.println
        ( "\r\n[START MEMORY-MAPPED FILES, SINGLE THREAD. " + 
          "ASYNCHRONOUS MONITOR]" );
    System.out.println( "Table is MBPS for Read | Write | Copy" );
    String line =   "----------------------------------------------------";
    String uptext = "current  min      max      average  median   status ";
    String lines = line + "-|-" + line + "-|-" + line;
    String uptexts = uptext + " | " + uptext + " | " + uptext;
    System.out.println( lines );
    System.out.println( uptexts );
    System.out.println( lines );
    
    //
       WorkSequentalMBPS wsm = new WorkSequentalMBPS();
    // WorkSequentalMBPS wsm = new WorkParallelMBPS();
    //
    
    wsm.start();

    while( wsm.isAlive() )
        {
        delay( 1000 );
        double r1,r2,r3,r4,r5, w1,w2,w3,w4,w5, c1,c2,c3,c4,c5;
        r1=r2=r3=r4=r5 = w1=w2=w3=w4=w5 = c1=c2=c3=c4=c5 = Double.NaN;
        boolean rstatus=false, wstatus=false, cstatus=false;
        StateAsync[] entries = wsm.getAsync();
        if ( entries != null )
            {
            StateAsync e;
            if ( ( entries.length > 0 )&&( ( e = entries[0] ) != null ) )
                {
                r1 = e.current;
                r2 = e.min;
                r3 = e.max;
                r4 = e.average;
                r5 = e.median;
                rstatus = true;
                }
            if ( ( entries.length > 1 )&&( ( e = entries[1] ) != null ) )
                {
                w1 = e.current;
                w2 = e.min;
                w3 = e.max;
                w4 = e.average;
                w5 = e.median;
                wstatus = true;
                }
            if ( ( entries.length > 2 )&&( ( e = entries[2] ) != null ) )
                {
                c1 = e.current;
                c2 = e.min;
                c3 = e.max;
                c4 = e.average;
                c5 = e.median;
                cstatus = true;
                }
            }
        String s = String.format( "%-9.2f%-9.2f%-9.2f%-9.2f%-9.2f%-5b   | " +
                                  "%-9.2f%-9.2f%-9.2f%-9.2f%-9.2f%-5b   | " +
                                  "%-9.2f%-9.2f%-9.2f%-9.2f%-9.2f%-5b" ,
                                  r1,r2,r3,r4,r5, rstatus ,
                                  w1,w2,w3,w4,w5, wstatus ,
                                  c1,c2,c3,c4,c5, cstatus );      
        System.out.println( s );
        }
    System.out.println( lines );
    totalStatisticsHelper( wsm );
    System.out.println( lines );
    System.out.println( "[STOP ASYNCHRONOUS MONITOR]" );
    }


public static void SynchronousMonitor()
    {
    System.out.println
        ( "\r\n[START MEMORY-MAPPED FILES, SINGLE THREAD. " + 
          "SYNCHRONOUS MONITOR]" );
    String line =   
            "----------------------------------------------------------";
    String uptext = 
            " #    current  min      max      average  median   status ";
    
    //
       WorkSequentalMBPS wsm = new WorkSequentalMBPS();
    // WorkSequentalMBPS wsm = new WorkParallelMBPS();
    //
    
    wsm.start();
    int previousID = -1;
    int postCount = 3;

    while( postCount > 0 )
        {
        delay( 150 );
        StateSync e = wsm.getSync();
        while ( e != null )
            {
            if ( e.phaseID != previousID )
                {
                previousID = e.phaseID;
                System.out.println( line );
                System.out.println( e.phaseName );
                System.out.println( uptext );
                System.out.println( line );
                }
            String s = String.format
                ( " %-5d%-9.2f%-9.2f%-9.2f%-9.2f%-9.2f%-5b",
                  e.count, e.current, e.min, e.max, e.average, e.median,
                  e.statusEntry.flag );
            System.out.println( s );
            e = wsm.getSync();
            }
        if ( ! wsm.isAlive() )
            {
            postCount--;
            }
        }
    System.out.println( line );
    totalStatisticsHelper( wsm );
    System.out.println( line );
    System.out.println( "[STOP SYNCHRONOUS MONITOR]" );
    }

/*
Helper for summary statistics:
total MBPS per series of files Read, Write, Copy
*/
private static void totalStatisticsHelper( WorkSequentalMBPS wsm )
    {
    StateAsync e;
    StateAsync[] entries = wsm.getAsync();
    double totalRead = Double.NaN;
    double totalWrite = Double.NaN;
    double totalCopy = Double.NaN;
    if ( entries != null )
        {
        if ( ( entries.length > 3 )&&( ( e = entries[3] ) != null ) )
            totalRead = e.current;
        if ( ( entries.length > 4 )&&( ( e = entries[4] ) != null ) )
            totalWrite = e.current;
        if ( ( entries.length > 5 )&&( ( e = entries[5] ) != null ) )
            totalCopy = e.current;
        String s = String.format
            ( "Total MBPS: Read=%.3f , Write=%.3f , Copy=%.3f", 
               totalRead, totalWrite, totalCopy );
        System.out.println( s );
        }
    }

}
