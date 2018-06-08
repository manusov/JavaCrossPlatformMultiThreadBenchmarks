/*
 *
 * Multithread math calculations benchmark. (C)2018 IC Book Labs.
 * Mathematics test pattern for multi-thread mode.
 *
 */


package javabench.math;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FunctionMultiThread extends FunctionSingleThread 
{
private final int m;                     // measurement repeats count
private final ExecutorService ex;        // executor object
private final Callable<String> wt[];     // worker for target operation
private final FutureTask[] ft;           // task interface
    
// constructor creates x-array, blank y-array,
// and creates threads management context
public FunctionMultiThread
        ( int arraySize, int internalRepeats, 
          int threadCount, int patternSelect )
    {                         
    super(arraySize, internalRepeats, patternSelect);
    m = threadCount;
    ex = Executors.newCachedThreadPool();
    ft = new FutureTask[m];
    int taskBase = 0;                       // address incremental for workers
    int taskSize = n / m;                   // numbers per worker
    // create and initializing workers array = f(pattern)
    if ( p==0 )
        {
        wt = new WorkerTaskAdd[m];
        for( int i=0; i<m; i++ )
            {
            wt[i] = new WorkerTaskAdd( taskBase , taskSize );
            taskBase += taskSize;
            }
        }
    else
        {
        wt = new WorkerTaskSin[m];
        for( int i=0; i<m; i++ )
            {
            wt[i] = new WorkerTaskSin( taskBase , taskSize );
            taskBase += taskSize;
            }
        }
    }

// stop executor, otherwise application still active
public void stop()
    {
    ex.shutdown();
    }

// function tabulation y[i] = f( x[i] )
// this method is benchmarking object, multi-thread
@Override public void tabulate()
    {
    for( int j=0; j<r; j++ )
        {
        // create daughter tasks list and run daughter tasks
        for( int i=0; i<m; i++ )
            {
            ft[i] = new FutureTask( wt[i] );  // create tasks array
            ex.execute( ft[i] );              // run tasks
            }
        // wait for daughter tasks termination
        boolean mtReady = false;  // set "not ready"
        while( !mtReady )
            {
            mtReady = true;  // set "ready" possible clear below
            for( int i=0; i<m; i++ )
                {
                mtReady &= ft[i].isDone(); // check for all threads ready
                if( !mtReady ) break;      // this for fast detection not-ready
                }
            }
        }
    }

// workers runned by each thread, f(pattern select)

class WorkerTaskAdd implements Callable<String>
    {
    private final int base, size;
    WorkerTaskAdd( int x1, int x2 )  // constructor assigns base and size
        {
        base = x1;
        size = x2;
        }
    @Override public String call()  // entry point for run task
        {
        for(int i=0; i<size; i++)
            {
            array[1][base+i] = array[0][base+i] + 1.0;
            array[0][base+i] += DXR;                     // prevent speculation
            }
        return null;
        }
    }

class WorkerTaskSin implements Callable<String>
    {
    private final int base, size;
    WorkerTaskSin( int x1, int x2 )  // constructor assigns base and size
        {
        base = x1;
        size = x2;
        }
    @Override public String call()  // entry point for run task
        {
        for(int i=0; i<size; i++)
            {
            array[1][base+i] = Math.sin( array[0][base+i] );
            array[0][base+i] += DXR;                     // prevent speculation
            }
        return null;
        }
    }

}