/*
 *
 * Multithread math calculations benchmark. (C)2018 IC Book Labs.
 * Static class with statistic library: min, max, median, average for arrays.
 *
 */

package javabench;

public class StatisticUtil 
{

// find minimum value of array

protected static double findMin( double[] array )
    {
    return findMin( array, array.length );
    }

protected static double findMin( double[] array, int n )
    {
    double min = Double.NaN;
    if ( ( array != null ) && ( n > 0 ) && ( n <= array.length ) )
        {
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
    return findMax( array, array.length );
    }

protected static double findMax( double[] array, int n )
    {
    double max = Double.NaN;
    if ( ( array != null ) && ( n > 0 ) && ( n <= array.length ) )
        {
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
    return findAverage( array, array.length );
    }

protected static double findAverage( double[] array, int n )
    {
    double average = Double.NaN;
    if ( ( array != null ) && ( n > 0 ) && ( n <= array.length ) )
        {
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
    return findMedian( array, array.length );
    }

protected static double findMedian( double[] array, int n )
    {
    double median = Double.NaN;
    if ( ( array != null ) && ( n == 1 ) && ( n <= array.length ) )
        {  // median for one element array
        median = array[0];
        }
    else if ( ( array != null ) && ( n > 1 ) && ( n <= array.length ) )
        {
        // sorting array
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
    
// find median value of array, additionally return indexes

protected static MedianEntry findMedianIndex( double[] array )
    {
    return findMedianIndex( array, array.length );
    }

protected static MedianEntry findMedianIndex( double[] arrayIn, int n )
    {
    // this copy required because input array modified (sorted) by method
    int m = arrayIn.length;
    double[] array = new double[m];
    System.arraycopy( arrayIn, 0, array, 0, m );
    // operation with copy of array
    int a=-1, b=-1, c=-1;                           // median position indexes
    int[] service = new int[n];
    for(int i=0; i<n; i++)
        {
        service[i] = i;
        }
    double median = Double.NaN;
    double median1 = Double.NaN;
    double median2 = Double.NaN;
    double median3 = Double.NaN;
    if ( ( array != null ) && ( n == 1 ) && ( n <= array.length ) )
        {  // median for one element array
        median = array[0];
        a = b = c = 0;
        median1 = array[a];
        median2 = array[b];
        median3 = array[c];
        }
    else if ( ( array != null ) && ( n > 1 ) && ( n <= array.length ) )
        {
        // sorting array
        boolean flag = true;
        while (flag)
            {
            flag = false;
            for( int i=0; i<n-1; i++ )
                {
                if ( array[i] > array[i+1] )
                    {
                    double temp1 = array[i];
                    array[i] = array[i+1];
                    array[i+1] = temp1;
                    
                    int temp2 = service[i];
                    service[i] = service[i+1];
                    service[i+1] = temp2;
                    
                    flag = true;
                    }
                }
            }
        // get median from sorted array
        int i = n/2;
        if ( n % 2 == 0 )
            {
            median = ( array[i-1] + array[i] ) / 2.0;
            a = i-1;
            b = i;
            median1 = array[a];
            median2 = array[b];
            }
        else
            {
            median = array[i];
            if ((i==0)||(i==n))
                {
                a = b = c = i;
                }
            else
                {
                a = i-1;
                b = i+1;
                c = i;
                }
            median1 = array[a];
            median2 = array[b];
            median3 = array[c];
            }
        }
    if ( a>=0 ) a = service[a];
    if ( b>=0 ) b = service[b];
    if ( c>=0 ) c = service[c];
    return new MedianEntry( median, median1, median2, median3, a, b, c );
    }

protected static StatisticEntry getStatistic( double[] array )
    {
    return getStatistic( array, array.length );
    }

protected static  StatisticEntry getStatistic( double[] array, int n )
    {
    MedianEntry medianEntry = findMedianIndex( array, n );
    double average = findAverage( array, n );
    double min = findMin( array, n );
    double max = findMax( array, n );
    StatisticEntry statisticEntry = new StatisticEntry
        ( medianEntry.median, 
          medianEntry.median1,
          medianEntry.median2,
          medianEntry.median3, 
          average, min, max,
          medianEntry.medianIndex1,
          medianEntry.medianIndex2,
          medianEntry.medianIndex3 );
    return statisticEntry;
    }

}
