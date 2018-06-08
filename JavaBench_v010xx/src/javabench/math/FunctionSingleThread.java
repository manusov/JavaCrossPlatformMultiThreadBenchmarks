/*
 *
 * Multithread math calculations benchmark. (C)2018 IC Book Labs.
 * Mathematics test pattern for single-thread mode.
 *
 */


package javabench.math;

public class FunctionSingleThread 
{
final int n;                     // double numbers per array
final int r;                     // internal measurement repeats count
final int p;                     // pattern selector
final double X  = 0.0;           // start value for function tabulation
final double DX  = 0.000001;     // step for function tabulation
final double DXR = 0.0000001;    // step for argument change in runtime
final double[][] array;          // array of (x,y) pairs

// constructor creates x-array, blank y-array
public FunctionSingleThread
        ( int arraySize, int internalRepeats, int patternSelect )
    {
    n = arraySize;
    r = internalRepeats;
    p = patternSelect;
    array = new double[2][n];
    double x = X;
    for(int i=0; i<n; i++)
        {
        array[0][i] = x;     // set x[i] = argument
        array[1][i] = 0.0;   // blank y[i] = prepare for function value
        x += DX;             // modify argument
        }
    } 

// blank array
public void blankArray()
    {
    double x = X;
    for(int i=0; i<n; i++)
        {
        array[0][i] = x;     // set x[i] = argument
        array[1][i] = 0.0;   // blank y[i] = prepare for function value
        x += DX;             // modify argument
        }
    }

// this public get array channel for prevent speculations
public double[][] getArray()
    {
    return array;
    }

// function tabulation y[i] = f( x[i] )
// this method is benchmarking object, single-thread
public void tabulate()
    {
    if ( p==0 )
        {
        tabulateAdd();
        }
    else
        {
        tabulateSin();
        }
    }

private void tabulateAdd()
    {
    for( int j=0; j<r; j++ )
        {
        for(int i=0; i<n; i++)
            {
            array[1][i] = array[0][i] + 1.0;   // set y[i] = x[i] + 1.0
            array[0][i] += DXR;                // prevent speculation
            }
        }
    }

private void tabulateSin()
    {
    for( int j=0; j<r; j++ )
        {
        for(int i=0; i<n; i++)
            {
            array[1][i] = Math.sin( array[0][i] );   // set y[i] = sin ( x[i] )
            array[0][i] += DXR;                      // prevent speculation
            }
        }
    }

}
