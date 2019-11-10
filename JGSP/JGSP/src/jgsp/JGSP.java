/*
JGSP = Java Global Solutions Pool.
*/

package jgsp;

import jgsp.testconsole.TestMemoryMappedFiles;
import jgsp.testconsole.TestStatistics;

public class JGSP 
{

public static void main(String[] args) 
    {
    System.out.println( "Starting JGSP..." );
    TestStatistics.execConstants();
    TestMemoryMappedFiles.AsynchronousMonitor();
    }
}
