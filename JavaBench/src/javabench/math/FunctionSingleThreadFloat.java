/*
Multithread math calculations benchmark utility. (C)2019 IC Book Labs.
-----------------------------------------------------------------------
Mathematics test pattern for single-thread mode.
Single precision (float) operands.
*/

package javabench.math;

class FunctionSingleThreadFloat extends FunctionThread
{
final int n;                     // float numbers per array
final int r;                     // internal measurement repeats count
final int p;                     // pattern selector
final float X  = 0.0f;           // start value for function tabulation
final float DX  = 0.000001f;     // step for function tabulation
final float DXR = 0.0000001f;    // step for argument change in runtime
float[][] array;                 // array of (x,y) pairs

// constructor creates x-array, blank y-array
FunctionSingleThreadFloat
        ( int arraySize, int internalRepeats, int patternSelect )
    {
    n = arraySize;
    r = internalRepeats;
    p = patternSelect;
    array = new float[2][n];
    float x = X;
    for(int i=0; i<n; i++)
        {
        array[0][i] = x;      // set x[i] = argument
        array[1][i] = 0.0f;   // blank y[i] = prepare for function value
        x += DX;              // modify argument
        }
    } 

// blank array
@Override void blankArray()
    {
    float x = X;
    for(int i=0; i<n; i++)
        {
        array[0][i] = x;      // set x[i] = argument
        array[1][i] = 0.0f;   // blank y[i] = prepare for function value
        x += DX;              // modify argument
        }
    }

// this for explicit release memory to activate garbage collection,
// otherwise out of memory errors exceptions
@Override void releaseArray()
    {
    array = null;
    }

// this get array channel must be PUBLIC for prevent JVM speculations
public float[][] getFloatArray()
    {
    return array;
    }

// function tabulation y[i] = f( x[i] )
// this method is benchmarking object, single-thread
@Override void tabulate()
    {
    switch ( p ) 
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
        for( int i=0; i<n; i++ )
            {
            array[1][i] = array[0][i] + 1.0f;   // set y[i] = x[i] + 1.0
            array[0][i] += DXR;                 // prevent speculation
            }
        }
    }

private void tabulateSqrt()
    {
    for( int j=0; j<r; j++ )
        {
        for( int i=0; i<n; i++ )
            {
            // set y[i] = sin ( x[i] )
            array[1][i] = (float) Math.sqrt( array[0][i] );
            // prevent speculation
            array[0][i] += DXR;
            }
        }
    }

private void tabulateSin()
    {
    for( int j=0; j<r; j++ )
        {
        for( int i=0; i<n; i++ )
            {
            // set y[i] = sin ( x[i] )
            array[1][i] = (float) Math.sin( array[0][i] );
            // prevent speculation
            array[0][i] += DXR;
            }
        }
    }
}
