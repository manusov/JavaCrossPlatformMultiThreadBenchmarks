// Timer helper class include statistics methods.

package javabench;

public class TimerUtil 
{
private static long timer;

// Start timer,
// called before measured fragment
protected static void timerStart()
    {
    timer = System.nanoTime();
    }

// Stop timer, return delta time in milliseconds as double,
// called after measured fragment
protected static double timerStop()
    {
    long t = System.nanoTime();
    double ms = t - timer;
    ms /= 1000000.0;
    return ms;
    }

// find minimum value of array
protected static double findMin( double[] array )
    {
    double min = Double.NaN;
    if ( ( array != null ) && ( array.length > 0 ) )
        {
        int n = array.length;
        min = array[0];
        for( int i=0; i<n; i++ )
            {
            if ( min > array[i] ) min = array[i];
            }
        }
    return min;
    }

// find maximum value of array
protected static double findMax( double[] array )
    {
    double max = Double.NaN;
    if ( ( array != null ) && ( array.length > 0 ) )
        {
        int n = array.length;
        max = array[0];
        for( int i=0; i<n; i++ )
            {
            if ( max < array[i] ) max = array[i];
            }
        }
    return max;
    }

// find average value of array
protected static double findAverage( double[] array )
    {
    double average = Double.NaN;
    if ( ( array != null ) && ( array.length > 0 ) )
        {
        int n = array.length;
        average = 0.0;
        for( int i=0; i<n; i++ )
            {
            average += array[i];
            }
        average /= n;
        }
    return average;
    }

// find median value of array
protected static double findMedian( double[] array )
    {
    double median = Double.NaN;
    if ( ( array != null ) && ( array.length == 1 ) )
        {  // median for one element array
        median = array[0];
        }
    else if ( ( array != null ) && ( array.length > 1 ) )
        {
        // sorting array
        int n = array.length;
        boolean flag = true;
        while (flag)
            {
            flag = false;
            for( int i=0; i<n-1; i++ )
                {
                if ( array[i] > array[i+1] )
                    {
                    double temp = array[i];
                    array[i] = array[i+1];
                    array[i+1] = temp;
                    flag = true;
                    }
                }
            }
        // get median from sorted array
        int i = n/2;
        if ( n % 2 == 0 )
            {
            median = ( array[i-1] + array[i] ) / 2.0;
            }
        else
            {
            median = array[i];
            }
        }
    return median;
    }


}
