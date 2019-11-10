package jgsp.testconsole;

import jgsp.statistics.StateAsync;
import jgsp.statistics.StatisticsModel;

public class TestStatistics 
{
private final static double[] TEST_DATA = { 20.381, 25.10, 2.87, 3.14, 1.86 };
    
public static void execConstants()
    {
    System.out.println( "\r\n[START TEST STATISTICS]" );
    String line = 
        "-----------------------------------------------------------------";
    System.out.println( line );
    System.out.println( "current  min      max      average  median   status" );
    System.out.println( line );
    StatisticsModel sm = new StatisticsModel( 1 );
    for( int i=0; i<TEST_DATA.length; i++ )
        {
        boolean status = sm.send( 0, TEST_DATA[i] );
        StateAsync entry = sm.receive( 0 );
        String s = String.format
            ( "%-9.3f%-9.3f%-9.3f%-9.3f%-9.3f%b", 
              entry.current, entry.min, entry.max, entry.average, entry.median,
              status );
        System.out.println( s );
        }
    System.out.println( line );
    System.out.println( "[STOP TEST STATISTICS]" );
    }

}
