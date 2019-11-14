/*

note required other scenario for IOPS, don't use measurement per file,
initialize by:

    HelperIO ( null );

Over-templating is bad. MBPS and IOPS tests must be separately designed.
Second bug: accumulate statistics when IOPS test pre-writing,
at first measured write statistics already exist, rule:

    current = min = max = average = median

is not works.

Result, this MMF test is MBPS only, write IOPS separately.

*/
package jgsp.testconsole;

import jgsp.iommfmbps.MBPSmultiThreadMMF;
import jgsp.iommfmbps.MBPSsingleThreadMMF;
import jgsp.statistics.StateAsync;
import jgsp.statistics.StateSync;
import static jgsp.helpers.Delay.delay;
import jgsp.iochannelsmbps.MBPSmultiThreadChannels;
import jgsp.iochannelsmbps.MBPSsingleThreadChannels;
import jgsp.iommfmbps.IOPSmultiThreadMMF;
import jgsp.iommfmbps.IOPSsingleThreadMMF;
import jgsp.templates.IOscenario;

public class TestIO 
{

public static void AsynchronousMonitor()
    {
    System.out.println( "\r\n[ START IO SCENARIO. ASYNCHRONOUS MONITOR ]" );
    System.out.println( "Table is MBPS for Read | Write | Copy" );
    String line =   "----------------------------------------------------";
    String uptext = "current  min      max      average  median   status ";
    String lines = line + "-|-" + line + "-|-" + line;
    String uptexts = uptext + " | " + uptext + " | " + uptext;
    System.out.println( lines );
    System.out.println( uptexts );
    System.out.println( lines );
    
    //
    // IOscenario x = new MBPSsingleThreadMMF();
    // IOscenario x = new MBPSmultiThreadMMF();
    // IOscenario x = new IOPSsingleThreadMMF();
    IOscenario x = new MBPSsingleThreadChannels();
    //
    String unitsString = "MBPS";  // "MBPS"; "IOPS";
    //
    
    x.start();

    while( x.isAlive() )
        {
        delay( 1000 );
        double r1,r2,r3,r4,r5, w1,w2,w3,w4,w5, c1,c2,c3,c4,c5;
        r1=r2=r3=r4=r5 = w1=w2=w3=w4=w5 = c1=c2=c3=c4=c5 = Double.NaN;
        boolean rstatus=false, wstatus=false, cstatus=false;
        StateAsync[] entries = x.getAsync();
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
    totalStatisticsHelper( x, unitsString );
    System.out.println( lines );
    System.out.println( "[ STOP ASYNCHRONOUS MONITOR ]" );
    }


public static void SynchronousMonitor()
    {
    System.out.println( "\r\n[ START IO SCENARIO. SYNCHRONOUS MONITOR ]" );
    String line =   
            "----------------------------------------------------------";
    String uptext = 
            " #    current  min      max      average  median   status ";
    
    //
    // IOscenario x = new MBPSsingleThreadMMF();
    // IOscenario x = new MBPSmultiThreadMMF();
    // IOscenario x = new IOPSsingleThreadMMF();
    // IOscenario x = new MBPSsingleThreadChannels();
    IOscenario x = new MBPSmultiThreadChannels();
    //
    String unitsString = "MPPS";  // "MBPS"; "IOPS";
    //
    
    x.start();
    int previousID = -1;
    int postCount = 3;

    while( postCount > 0 )
        {
        delay( 150 );
        StateSync e = x.getSync();
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
            e = x.getSync();
            }
        if ( ! x.isAlive() )
            {
            postCount--;
            }
        }
    System.out.println( line );
    totalStatisticsHelper( x, unitsString );
    System.out.println( line );
    System.out.println( "[ STOP SYNCHRONOUS MONITOR ]" );
    }

/*
Helper for summary statistics:
total MBPS per series of files Read, Write, Copy
*/
private static void totalStatisticsHelper
        ( IOscenario ios, String unitsString )
    {
    StateAsync e;
    StateAsync[] entries = ios.getAsync();
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
            ( "Total %s: Read=%.3f , Write=%.3f , Copy=%.3f", 
               unitsString, totalRead, totalWrite, totalCopy );
        System.out.println( s );
        }
    }

}
