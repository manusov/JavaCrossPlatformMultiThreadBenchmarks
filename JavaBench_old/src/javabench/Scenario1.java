// Benchmark scenario 1, simple without statistics.

package javabench;

import java.util.Locale;

public class Scenario1 
{
final FunctionSingleThread st;
final FunctionMultiThread  mt;
public Scenario1()
    {
    // create classes for single-thread and multi-thread benchmark
    st = new FunctionSingleThread();
    mt = new FunctionMultiThread();
    }
public boolean start()
    {
    // benchmarking at single-thread mode
    TimerUtil.timerStart();
    st.tabulate(); 
    double stResult = TimerUtil.timerStop();
    String resultString = String.format( Locale.US , "%.3f ms" , stResult );
    System.out.println( "Single thread = " + resultString );
    // benchmarking at multi-thread mode
    TimerUtil.timerStart();
    mt.tabulate(); 
    double mtResult = TimerUtil.timerStop();
    resultString = String.format( Locale.US , "%.3f ms" , mtResult );
    System.out.println( "Multi thread = " + resultString );
    mt.stop();
    // calculate and print parallelism ratio
    double ratio = stResult / mtResult;
    resultString = String.format( Locale.US , "%.3f" , ratio );
    System.out.println( "Ratio = " + resultString );
    // results check
    boolean b1 = Double.isFinite(stResult);
    boolean b2 = Double.isFinite(mtResult);
    boolean b3 = Double.isFinite(ratio);
    return b1 && b2 && b3;
    }
    
}
