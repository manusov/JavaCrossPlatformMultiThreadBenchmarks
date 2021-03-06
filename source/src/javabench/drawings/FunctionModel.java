/*
Multithread math calculations benchmark utility. (C)2021 IC Book Labs.
-----------------------------------------------------------------------
Package for support functions Y=F(X) drawings.
Function Y=F(X) drawing model.
*/

package javabench.drawings;

import java.math.BigDecimal;

public class FunctionModel implements FunctionModelInterface
{
private BigDecimal[][] function;

private final int DEFAULT_SCALE_BASE  = 1000;
private final int DEFAULT_SCALE_DELTA = 100;
private final int DEFAULT_MAX_COUNT = 100;

private int scale;
private int maxCount;
private int currentCount;

public FunctionModel( FunctionControllerInterface x )
    {
    scale = DEFAULT_SCALE_BASE;
    maxCount = DEFAULT_MAX_COUNT;
    currentCount = 0;
    rescaleXmax( maxCount );
    }

@Override public BigDecimal[][] getFunction()
    {
    return function;
    }

@Override public int[] getCurrentIndexes()
    {
    return new int[] { currentCount, currentCount };
    }

@Override public final int[] getMaximumIndexes()
    {
    return new int[] { maxCount, maxCount };
    }

@Override public void startModel() 
    {
    scale = DEFAULT_SCALE_BASE;
    currentCount = 0;
    }

@Override public void stopModel()
    {
    }

@Override public String getXname()
    {
    return "measurement iterations";
    }

@Override public String[] getYnames()
    {
    return new String[] { "MOPS  single-thread", " multi-thread" };
    }

@Override public BigDecimal getXmin()
    {
    String s = "1";
    if ( getXmax().intValue() > 10 ) s = "0";
    return new BigDecimal( s );
    }
        
@Override public BigDecimal getXmax()
    {
    return new BigDecimal( maxCount );
    }
        
@Override public BigDecimal getXsmallUnits()
    {
    double x = maxCount / 50.0;
    return new BigDecimal( x );
    }
        
@Override public BigDecimal getXbigUnits()
    {
    double x = maxCount / 10.0;
    return new BigDecimal( x );
    }

@Override public BigDecimal getYmin()
    {
    return new BigDecimal( "0" );
    }
        
@Override public BigDecimal getYmax()
    {
    double x = scale;
    return new BigDecimal( x );
    }

@Override public BigDecimal getYsmallUnits()
    {
    double x = scale / 50.0;
    return new BigDecimal( x );
    }

@Override public BigDecimal getYbigUnits()
    {
    double x = scale / 10.0;
    return new BigDecimal( x );
    }

@Override public final void rescaleXmax( int x )
    {
    maxCount = x;
    function = new BigDecimal[3][maxCount];
    BigDecimal a = new BigDecimal(0);
    for( int i=0; i<maxCount; i++ )
        {
        function[0][i] = function[1][i] = function[2][i] = a;
        }
    }

@Override public void rescaleYmax()
    {
    double max = 0.0;
    int n = getCurrentIndexes().length;
    for( int i=0; i<n; i++ )
        {
        int m = getCurrentIndexes()[i];
        if ( m > 0 )
            {
            if ( i == 0 ) max = ( function[i+1][0] ).doubleValue();
            for ( int j=0; j<m; j++ )
                {
                double temp = ( function[i+1][j] ).doubleValue();
                if ( max < temp ) max = temp;
                }
            }
        int tempScale = 0;
        while ( max > tempScale )
            {
            tempScale += DEFAULT_SCALE_DELTA;
            }
        scale = tempScale;
        }
    }

@Override public void updateValue( BigDecimal[] x ) 
    {
    if ( currentCount < maxCount )
        {
        function[0][currentCount] = x[0];
        function[1][currentCount] = x[1];
        function[2][currentCount] = x[2];
        currentCount++;
        }
    }
}
