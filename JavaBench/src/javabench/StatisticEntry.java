/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Class for return detail info with array statistics values.
 *
 */

package javabench;

public class StatisticEntry 
{
public final double median, median1, median2, median3;
public final double average, min, max;
public final int medianIndex1, medianIndex2, medianIndex3;

public StatisticEntry
        ( double x1, double x2, double x3, double x4,
          double y1, double y2, double y3, 
          int z1, int z2, int z3 )
    {
    median = x1;
    median1 = x2;
    median2 = x3;
    median3 = x4;
    average = y1;
    min = y2;
    max = y3;
    medianIndex1 = z1;
    medianIndex2 = z2;
    medianIndex3 = z3;
    }
}
