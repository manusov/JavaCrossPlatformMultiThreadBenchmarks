/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Class for return detail info about array median value.
 *
 */

package javabench;

public class MedianEntry 
{
public final double median, median1, median2, median3;
public final int medianIndex1, medianIndex2, medianIndex3;

public MedianEntry( double x1, double x2, double x3, double x4,
                    int y1, int y2, int y3 )
    {
    median = x1;
    median1 = x2;
    median2 = x3;
    median3 = x4;
    medianIndex1 = y1;
    medianIndex2 = y2;
    medianIndex3 = y3;
    }
}
