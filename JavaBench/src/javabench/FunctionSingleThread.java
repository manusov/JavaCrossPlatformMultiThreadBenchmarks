// Single thread benchmark class.

package javabench;

public class FunctionSingleThread 
{
final int N     = 5000000;       // double numbers per array
final double X  = 0.0;           // start value for function tabulation
final double DX = 0.000001;      // step for function tabulation
final double[][]  array;         // array of (x,y) pairs
final int MEASURE_COUNT = 100;   // measurement repeats count

public FunctionSingleThread()   // constructor creates x-array, blank y-array
    {
    array = new double[2][N];
    double x = X;
    for(int i=0; i<N; i++)
        {
        array[0][i] = x;     // set x[i] = argument
        array[1][i] = 0.0;   // blank y[i] = prepare for function value
        x += DX;             // modify argument
        }
    } 

public double[][] getArray()  // this public channel for prevent speculations
    {
    return array;
    }

public void tabulate()  // function tabulation y[i] = f( x[i] )
    {                   // this method is benchmarking object, single-thread
    for( int j=0; j<MEASURE_COUNT; j++ )
        {
        for(int i=0; i<N; i++)
            {
            array[1][i] = Math.sin( array[0][i] );  // set y[i] = sin ( x[i] )
            }
        }
    }

    
}
