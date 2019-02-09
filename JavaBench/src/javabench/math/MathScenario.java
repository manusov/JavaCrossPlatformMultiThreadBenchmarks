/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Mathematics test scenario, run as separate thread, initiate new threads.
 *
 */

package javabench.math;

public class MathScenario extends Thread 
{
private final int INTERNAL_REPEATS = 100;   // measurement repeats
private final int externalRepeats;          // repeats for visualized progress
private final double operations;            // number of operations for MOPS

private int counter;                        // test phase count
private int index;                          // test phase pairs (MT+ST) count
private boolean taskInterrupt;              // flag for interrupt test
private boolean taskDone;                   // flag set when test terminated

private final double mopsSingleThread[];  // MOPS arrays for visualized repeats
private final double mopsMultiThread[];
private final double mopsRatio[];

private final FunctionThread functionSingleThread;  // Scenarios
private final FunctionThread functionMultiThread;

public MathScenario
        ( int arraySize, int threadCount, int repeatCount, 
          int patternSelect, int operandSizeSelect )
    {
    externalRepeats = repeatCount;
    operations = arraySize * INTERNAL_REPEATS;
    counter = 0;
    index = -1;
    taskInterrupt = false;
    taskDone = false;
    mopsSingleThread = new double[externalRepeats];
    mopsMultiThread = new double[externalRepeats];
    mopsRatio = new double[externalRepeats];
    for(int i=0; i<externalRepeats; i++)
        {
        mopsSingleThread[i] = 0.0;
        mopsMultiThread[i] = 0.0;
        mopsRatio[i] = 0.0;
        }
    
    if (operandSizeSelect==0 )
        {   // double precision branch
        functionSingleThread = new FunctionSingleThreadDouble
            ( arraySize, INTERNAL_REPEATS, patternSelect );
        functionMultiThread = new FunctionMultiThreadDouble
            ( arraySize, INTERNAL_REPEATS, threadCount, patternSelect );
        }
    else
        {   // single precision branch
        functionSingleThread = new FunctionSingleThreadFloat
            ( arraySize, INTERNAL_REPEATS, patternSelect );
        functionMultiThread = new FunctionMultiThreadFloat
            ( arraySize, INTERNAL_REPEATS, threadCount, patternSelect );
        }
    }

@Override public void run()   // parallel thread entry point
    {
    for( int i=0; (i<externalRepeats)&&(!taskInterrupt); i++ )
        {
        functionSingleThread.blankArray();
        TimerUtil.timerStart();
        functionSingleThread.tabulate();
        double microseconds = TimerUtil.timerStop();
        double mops = operations / microseconds;
        mopsSingleThread[i] = mops;   // phase count after single-thread
        
        if(taskInterrupt) break;
        counter++;
        
        functionMultiThread.blankArray();
        TimerUtil.timerStart();
        functionMultiThread.tabulate();
        microseconds = TimerUtil.timerStop();
        mops = operations / microseconds;
        mopsMultiThread[i] = mops;
        
        mopsRatio[i] = mopsMultiThread[i] / mopsSingleThread[i];
        
        counter++;   // phase count after multi-thread
        index++;     // phase pairs count
        }
    
    functionMultiThread.stop();        // stop threads executor
    
    functionSingleThread.releaseArray();  // hint to garbage collection
    functionMultiThread.releaseArray();
    
    taskDone = true;                   // termination signal
    }

public double[] getMopsMultiThread()    { return mopsMultiThread;  }
public double[] getMopsSingleThread()   { return mopsSingleThread; }
public double[] getMopsRatio()          { return mopsRatio;        }

public int getCounter()                 { return counter;          }
public int getIndex()                   { return index;            }
public void setTaskInterrupt(boolean b) { taskInterrupt = b;       }
public boolean getTaskDone()            { return taskDone;         }

}