/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Scenario for Console mode.
 *
 */

package javabench;

import static java.lang.Thread.sleep;
import java.util.Locale;
import javabench.math.MathScenario;

public class ScenarioConsole 
{
private final static int CON_ARRAY_SIZE = 1000000;
private final static int CON_THREAD_COUNT = 100;
private final static int CON_EXTERNAL_REPEAT_COUNT = 10;
private final static int CON_PATTERN_SELECT = 2;
private final static int CON_OPERAND_SELECT = 0;

public void runScenario()
    {
    String name = About.getLongName();
    String vendor = About.getVendorName();
    System.out.println( name + " CONSOLE MODE");
    System.out.println( vendor );
    System.out.println
        ( "Mega Operations per Second." +
          "\r\n----------------------------------------------------------" +
          "\r\n| pass     | multi-thread | single-thread  | ratio       |" + 
          "\r\n----------------------------------------------------------" );
    // start benchmark
    double[] mtData    = null;
    double[] stData    = null;
    double[] ratioData = null;
    String resultLine;
    MathScenario mathScenario = new MathScenario 
        ( CON_ARRAY_SIZE, CON_THREAD_COUNT, 
          CON_EXTERNAL_REPEAT_COUNT, 
          CON_PATTERN_SELECT, CON_OPERAND_SELECT );
    mathScenario.start();
    int j = -1;
    while ( !mathScenario.getTaskDone() )
        {
        // some wait
        try { sleep(50); } catch ( Exception e ) { }
        // get current progress index
        int i = mathScenario.getIndex();
        if ( ( i==j )||( i<0 ) ) continue;
        j=i;
        // get results
        mtData    = mathScenario.getMopsMultiThread();
        stData    = mathScenario.getMopsSingleThread();
        ratioData = mathScenario.getMopsRatio();
        // visual results
        resultLine = String.format( Locale.US ,
                   "  %-4d       %-10.3f      %-10.3f      %-10.3f" , 
                   i+1 , mtData[i] , stData[i] , ratioData[i] );
        System.out.println(resultLine);
        }
    // table middle line
    System.out.println
        ( "----------------------------------------------------------" );
    StatisticEntry mtLog    = StatisticUtil.getStatistic( mtData );
    StatisticEntry stLog    = StatisticUtil.getStatistic( stData );
    StatisticEntry ratioLog = StatisticUtil.getStatistic( ratioData );
    // statistics, median
    double x1 = mtLog.median;
    double x2 = stLog.median;
    double x3 = ratioLog.median;
    String statisticLine = String.format( Locale.US , 
        " median      %-10.3f      %-10.3f      %-10.3f" , x1 , x2, x3 );
    System.out.println(statisticLine);
    // statistics, average
    x1 = mtLog.average;
    x2 = stLog.average;
    x3 = ratioLog.average;
    statisticLine = String.format( Locale.US , 
        " average     %-10.3f      %-10.3f      %-10.3f" , x1 , x2, x3 );
    System.out.println(statisticLine);
    // statistics, minimum
    x1 = mtLog.min;
    x2 = stLog.min;
    x3 = ratioLog.min;
    statisticLine = String.format( Locale.US , 
        " minimum     %-10.3f      %-10.3f      %-10.3f" , x1 , x2, x3 );
    System.out.println(statisticLine);
    // statistics, maximum
    x1 = mtLog.max;
    x2 = stLog.max;
    x3 = ratioLog.max;
    statisticLine = String.format( Locale.US , 
        " maximum     %-10.3f      %-10.3f      %-10.3f" , x1 , x2, x3 );
    System.out.println(statisticLine);
    // table down line
    System.out.println
        ( "----------------------------------------------------------" );
    }
}
