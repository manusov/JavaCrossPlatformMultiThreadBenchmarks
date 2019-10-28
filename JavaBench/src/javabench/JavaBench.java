/*
 Multithread math calculations benchmark utility. (C)2019 IC Book Labs.
-----------------------------------------------------------------------
 Main module with application entry point.
 */

package javabench;

public class JavaBench 
{
private final static String  CONSOLE_KEY = "console";
private static boolean       consoleMode = false;

public static void main( String[] args ) 
    {
    // check command line, select Console or GUI mode
    if ( (args != null )&&( args.length > 0 )&&( args[0] != null ) )
        {
        if ( args[0].equals( CONSOLE_KEY ) ) 
            {
            consoleMode = true;
            }
        }
    // run selected scenario: console or GUI    
    if ( consoleMode )
        {  // branch for console mode
        ScenarioConsole sc = new ScenarioConsole();
        sc.runScenario();
        }
    else
        {  // branch for GUI mode
        ScenarioGui sg = new ScenarioGui();
        sg.runScenario();
        }
    }
}
