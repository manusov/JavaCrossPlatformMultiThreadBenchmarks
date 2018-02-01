// Benchmark scenario 2, complex with statistics.

package javabench;

import java.util.Locale;

public class Scenario2 extends Scenario1
{
private final static int STATISTIC_COUNT = 10;  // statistics iterations count
private final double[] stData;     // single thread data, units = milliseconds
private final double[] mtData;     // multi thread data, units = milliseconds
private final double[] ratioData;  // ratio data
public Scenario2()
    {
    super();
    stData = new double[STATISTIC_COUNT];
    mtData = new double[STATISTIC_COUNT];
    ratioData = new double[STATISTIC_COUNT];
    for ( int i=0; i<STATISTIC_COUNT; i++ )
        {
        stData[i] = Double.NaN;
        mtData[i] = Double.NaN;
        ratioData[i] = Double.NaN;
        }
    }
@Override public boolean start()
    {
    // validity flag and temporary string
    boolean validity = true;
    String s;
    // table up lines
    System.out.println
        ( "Time in milliseconds." +
          "\r\n----------------------------------------------------------" +
          "\r\n| pass     | single-thread | multi-thread  | ratio       |" + 
          "\r\n----------------------------------------------------------" );
    // measurements
    for( int i=0; i<STATISTIC_COUNT; i++ )
        {
        // single-thread
        TimerUtil.timerStart();
        st.tabulate(); 
        stData[i] = TimerUtil.timerStop();
        // multi-thread
        TimerUtil.timerStart();
        mt.tabulate(); 
        mtData[i] = TimerUtil.timerStop();
        // ratio
        ratioData[i] = stData[i] / mtData[i];
        // build and print string    
        s = String.format( Locale.US ,
                           "  %-4d       %-10.3f      %-10.3f      %-10.3f" , 
                           i+1 , stData[i] , mtData[i] , ratioData[i] );
        System.out.println(s);
        // update validity status
        boolean b = ( ( Double.isFinite(stData[i]) ) && 
                      ( Double.isFinite(mtData[i]) ) && 
                      ( Double.isFinite(ratioData[i]) ) );
        validity &= b;
        }
    // table middle line
    System.out.println
        ( "----------------------------------------------------------" );
    // statistics, median
    double x1 = TimerUtil.findMedian(stData);
    double x2 = TimerUtil.findMedian(mtData);
    double x3 = TimerUtil.findMedian(ratioData);
    s = String.format( Locale.US ,
                       " median      %-10.3f      %-10.3f      %-10.3f" , 
                       x1 , x2, x3 );
    System.out.println(s);
    // statistics, average
    x1 = TimerUtil.findAverage(stData);
    x2 = TimerUtil.findAverage(mtData);
    x3 = TimerUtil.findAverage(ratioData);
    s = String.format( Locale.US ,
                       " average     %-10.3f      %-10.3f      %-10.3f" ,
                       x1 , x2, x3 );
    System.out.println(s);
    // statistics, minimum
    x1 = TimerUtil.findMin(stData);
    x2 = TimerUtil.findMin(mtData);
    x3 = TimerUtil.findMin(ratioData);
    s = String.format( Locale.US ,
                       " minimum     %-10.3f      %-10.3f      %-10.3f" ,
                       x1 , x2, x3 );
    System.out.println(s);
    // statistics, maximum
    x1 = TimerUtil.findMax(stData);
    x2 = TimerUtil.findMax(mtData);
    x3 = TimerUtil.findMax(ratioData);
    s = String.format( Locale.US ,
                       " maximum     %-10.3f      %-10.3f      %-10.3f" ,
                       x1 , x2, x3 );
    System.out.println(s);
    // table down line
    System.out.println
        ( "----------------------------------------------------------" );
    // stop threads executor and return
    mt.stop();
    return validity;
    }

}
