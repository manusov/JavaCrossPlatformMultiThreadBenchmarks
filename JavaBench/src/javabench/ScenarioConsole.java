/*
Multithread math calculations benchmark utility. (C)2019 IC Book Labs.
-----------------------------------------------------------------------
Scenario for Console mode.
This is alternative for default GUI mode, run as:
" java -jar <name>.jar console ".
*/

package javabench;

import static java.lang.Thread.sleep;
import java.util.Locale;
import javabench.math.MathScenario;

class ScenarioConsole 
{  // defaults for console mode
private final static int CON_ARRAY_SIZE = 1000000;       // array size, numbers
private final static int CON_THREAD_COUNT = 100;         // threads count
private final static int CON_EXTERNAL_REPEAT_COUNT = 10; // measur. iterations
private final static int CON_PATTERN_SELECT = 2;         // operation: y=sin(x)
private final static int CON_OPERAND_SELECT = 0;         // double precision

void runScenario()
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
        try { sleep(50); } catch ( InterruptedException e ) { }
        // get current progress index
        int i = mathScenario.getIndex();
        if ( ( i == j )||( i < 0 ) ) continue;  // wait for index changed
        j = i;
        // get results as arrays
        mtData    = mathScenario.getMopsMultiThread();
        stData    = mathScenario.getMopsSingleThread();
        ratioData = mathScenario.getMopsRatio();
        // visual results, actual data selected from arrays by index
        resultLine = String.format( Locale.US ,
                   "  %-4d       %-10.3f      %-10.3f      %-10.3f" , 
                   i+1 , mtData[i] , stData[i] , ratioData[i] );
        System.out.println( resultLine );
        }

    // table middle line
    System.out.println
        ( "----------------------------------------------------------" );
    // calculate statistics, store data at statistics entries
    StatisticEntry mtLog    = StatisticUtil.getStatistic( mtData );
    StatisticEntry stLog    = StatisticUtil.getStatistic( stData );
    StatisticEntry ratioLog = StatisticUtil.getStatistic( ratioData );
    // statistics, median
    double x1 = mtLog.median;
    double x2 = stLog.median;
    double x3 = ratioLog.median;
    String statisticLine = String.format( Locale.US , 
        " median      %-10.3f      %-10.3f      %-10.3f" , x1 , x2, x3 );
    System.out.println( statisticLine );
    // statistics, average
    x1 = mtLog.average;
    x2 = stLog.average;
    x3 = ratioLog.average;
    statisticLine = String.format( Locale.US , 
        " average     %-10.3f      %-10.3f      %-10.3f" , x1 , x2, x3 );
    System.out.println( statisticLine );
    // statistics, minimum
    x1 = mtLog.min;
    x2 = stLog.min;
    x3 = ratioLog.min;
    statisticLine = String.format( Locale.US , 
        " minimum     %-10.3f      %-10.3f      %-10.3f" , x1 , x2, x3 );
    System.out.println( statisticLine );
    // statistics, maximum
    x1 = mtLog.max;
    x2 = stLog.max;
    x3 = ratioLog.max;
    statisticLine = String.format( Locale.US , 
        " maximum     %-10.3f      %-10.3f      %-10.3f" , x1 , x2, x3 );
    System.out.println( statisticLine );
    // table down line
    System.out.println
        ( "----------------------------------------------------------" );
    }
}
