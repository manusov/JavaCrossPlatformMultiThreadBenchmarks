// Java multithread benchmark and parallelism ratio measurement.
// Main file.

package javabench;

public class JavaBench 
{

public static void main(String[] args) 
    {
    // first message
    System.out.println
        ( "\r\nJava multithread benchmark v0.03. (C)2018 IC Book Labs." );
    // select simple or complex scenario
    // Scenario1 scenario = new Scenario1();
    Scenario2 scenario = new Scenario2();
    //
    boolean b = scenario.start();
    String s;
    if ( b ) s = "Done OK.";
    else s = "Test FAILED.";
    System.out.println( s + "\r\n" );
    }
    
}
