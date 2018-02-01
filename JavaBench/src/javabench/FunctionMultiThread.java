// Multi thread benchmark class.

package javabench;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FunctionMultiThread extends FunctionSingleThread 
{
private final int THREADS_COUNT = 1000;  // measurement repeats count
private final ExecutorService ex;        // executor object
private final WorkerTask[] wt;           // worker for target operation
private final FutureTask[] ft;           // task interface
    
public FunctionMultiThread()  // constructor creates x-array, blank y-array,
    {                         // and creates threads management context
    super();
    ex = Executors.newCachedThreadPool();
    wt = new WorkerTask[THREADS_COUNT];
    ft = new FutureTask[THREADS_COUNT];
    int taskBase = 0;  // address incremental for workers
    int taskSize = N / THREADS_COUNT;  // numbers per worker
    for( int i=0; i<THREADS_COUNT; i++ )
        {
        wt[i] = new WorkerTask( taskBase , taskSize );  // create workers array
        taskBase += taskSize;
        }
    }

public void stop()  // stop executor, otherwise application still active
    {
    ex.shutdown();
    }

@Override public void tabulate()  // function tabulation y[i] = f( x[i] )
    {                       // this method is benchmarking object, multi-thread
    for( int j=0; j<MEASURE_COUNT; j++ )
        {
        // create daughter tasks list and run daughter tasks
        for( int i=0; i<THREADS_COUNT; i++ )
            {
            ft[i] = new FutureTask( wt[i] );  // create tasks array
            ex.execute( ft[i] );              // run tasks
            }
        // wait for daughter tasks termination
        boolean mtReady = false;  // set "not ready"
        while( !mtReady )
            {
            mtReady = true;  // set "ready" possible clear below
            for( int i=0; i<THREADS_COUNT; i++ )
                {
                mtReady &= ft[i].isDone(); // check for all threads ready
                if( !mtReady ) break;      // this for fast detection not-ready
                }
            }
        }
    }

// worker runned by each thread
class WorkerTask implements Callable<String>
    {
    private final int base, size;
    WorkerTask( int x1, int x2 )  // constructor assigns base and size
        {
        base = x1;
        size = x2;
        }
    @Override public String call()  // entry point for run task
        {
        for(int i=0; i<size; i++)
            {
            array[1][base+i] = Math.sin( array[0][base+i] );
            }
        return null;
        }
    }

}
