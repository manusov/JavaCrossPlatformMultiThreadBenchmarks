/*

JGSP = Java Global Solutions Pool.
Some fragments for mass-storage I/O benchmarking.

Note for SNIA SSD Preconditioning and Testing recommendations, 
native-centric project required, include OS kernel mode programming and 
direct access to NVMe MMIO and structures at RAM,
but this project (JGSP) is java-centric.

Possible model of other java project =
    Java Application  +  Native Library  +  Native Kernel Mode Driver.

Forks required for cross-platform capability support.

*/

package jgsp;

import jgsp.iochannelsmbps.MBPSsingleThreadChannels;
import jgsp.iommfmbps.IOPSsingleThreadMMF;
import jgsp.iommfmbps.MBPSmultiThreadMMF;
import jgsp.templates.IOscenario;
import jgsp.testconsole.TestIO;
import jgsp.testconsole.TestStatistics;

public class JGSP 
{

public static void main(String[] args) 
    {
    System.out.println( "Starting JGSP..." );

    // IOPSsingleThread x = new IOPSsingleThread();
    // x.start();
    
    
    // USE HELPERLESS VARIANT FROM NIOBENCHREFACTORING, PERFORMANCE OPTIMAL,
    // SMALL WORK PER FILE CALL
    
    // IOscenario ios = new MBPSsingleThreadAsync();
    // ios.start();
    
    // TestStatistics.execConstants();
    // TestIO.AsynchronousMonitor();
       TestIO.SynchronousMonitor();
    
    }
}
