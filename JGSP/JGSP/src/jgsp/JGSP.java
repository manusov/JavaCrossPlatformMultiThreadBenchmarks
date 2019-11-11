/*
JGSP = Java Global Solutions Pool.
*/

package jgsp;

import jgsp.memorymappedfiles.WorkParallelMBPS;
import jgsp.testconsole.TestMemoryMappedFiles;
import jgsp.testconsole.TestStatistics;

public class JGSP 
{

public static void main(String[] args) 
    {
    System.out.println( "Starting JGSP..." );
    
    // TestStatistics.execConstants();
    
    //
    // TestMemoryMappedFiles.AsynchronousMonitor();
       TestMemoryMappedFiles.SynchronousMonitor();
    
    // WorkParallelMBPS wpm = new WorkParallelMBPS();
    // wpm.start();
    
    
    
    }
}
