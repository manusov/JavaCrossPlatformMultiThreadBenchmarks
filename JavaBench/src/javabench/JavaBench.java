/*

UNDER CONSTRUCTION

TODO:
1)  + Run-Stop button. Output "skipped".
2)  + Output "-" before first valid result, no "NaN". Why "NaN" visualized?
3)  + Improve numbers of iterations (array size) for accurate measure.
4)  + Statistics bugs.
5)  + Make helper method for rows values.
6)  + Log: LogData class: arrays, medians, averages, mins, maxs, median marks.
7)  + Median "always middle" bug.
8)  + Bug with 2/3 medians.
9)  + Bug with cannot re-run.
10) + Tuning modes by results validity. Verify MOPS results, calculate.
11) + Method blankArray() internal repeats. Prevent table store and speculation.
11) + Refactor. Median method must not sort array, make copy at MEDIAN method.
12) + Comments. Remove old locked code.
13) + Verify all.
14) + Assign v0.01 version id.

*/

/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Main module with application entry point.
 *
 */

package javabench;

public class JavaBench 
{
private final static String  CONSOLE_KEY = "console";
private static boolean       consoleMode    = false;

public static void main(String[] args) 
    {
    // check command line, select Console or GUI mode
    if ( (args != null )&&( args.length > 0 )&&( args[0] != null ) )
        {
        if ( args[0].equals(CONSOLE_KEY) ) 
            {
            consoleMode = true;
            }
        }
        
    if (consoleMode)
        {
        ScenarioConsole sc = new ScenarioConsole();
        sc.runScenario();
        }
    else
        {
        ScenarioGui sg = new ScenarioGui();
        sg.runScenario();
        }
    }
}
