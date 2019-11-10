package jgsp.testconsole;

import jgsp.memorymappedfiles.WorkSequentalMBPS;
import jgsp.statistics.StateAsync;
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
    
    WorkSequentalMBPS wsm = new WorkSequentalMBPS();
    wsm.start();

    while( wsm.isAlive() )
        {
        delay( 1000 );
        double r1,r2,r3,r4,r5, w1,w2,w3,w4,w5, c1,c2,c3,c4,c5;
        r1=r2=r3=r4=r5 = w1=w2=w3=w4=w5 = c1=c2=c3=c4=c5 = Double.NaN;
        boolean rstatus=false, wstatus=false, cstatus=false;
        StateAsync[] entries = wsm.getIOStatistics();
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
        String s = String.format( "%-9.3f%-9.3f%-9.3f%-9.3f%-9.3f%-5b   | " +
                                  "%-9.3f%-9.3f%-9.3f%-9.3f%-9.3f%-5b   | " +
                                  "%-9.3f%-9.3f%-9.3f%-9.3f%-9.3f%-5b" ,
                                  r1,r2,r3,r4,r5, rstatus ,
                                  w1,w2,w3,w4,w5, wstatus ,
                                  c1,c2,c3,c4,c5, cstatus );      
        System.out.println( s );
        }
    System.out.println( lines );
    System.out.println( "[STOP ASYNCHRONOUS MONITOR]" );
    }
    
}
