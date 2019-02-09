/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Mathematics test pattern for multi-thread mode.
 * Double precision operands.
 *
 */


package javabench.math;

public class FunctionSingleThreadDouble extends FunctionThread
{
final int n;                     // double numbers per array
final int r;                     // internal measurement repeats count
final int p;                     // pattern selector
final double X  = 0.0;           // start value for function tabulation
final double DX  = 0.000001;     // step for function tabulation
final double DXR = 0.0000001;    // step for argument change in runtime
double[][] array;                // array of (x,y) pairs

// constructor creates x-array, blank y-array
public FunctionSingleThreadDouble
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
@Override public void blankArray()
    {
    double x = X;
    for(int i=0; i<n; i++)
        {
        array[0][i] = x;     // set x[i] = argument
        array[1][i] = 0.0;   // blank y[i] = prepare for function value
        x += DX;             // modify argument
        }
    }

// this for explicit release memory to activate garbage collection,
// otherwise out of memory errors exceptions
@Override public void releaseArray()
    {
    array = null;
    }

// this public get array channel for prevent speculations
public double[][] getDoubleArray()
    {
    return array;
    }

// function tabulation y[i] = f( x[i] )
// this method is benchmarking object, single-thread
@Override public void tabulate()
    {
    switch (p) 
        {
        case 0:
            tabulateAdd();
            break;
        case 1:
            tabulateSqrt();
            break;
        default:
            tabulateSin();
            break;
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

private void tabulateSqrt()
    {
    for( int j=0; j<r; j++ )
        {
        for(int i=0; i<n; i++)
            {
            array[1][i] = Math.sqrt( array[0][i] );  // set y[i] = sin ( x[i] )
            array[0][i] += DXR;                      // prevent speculation
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
