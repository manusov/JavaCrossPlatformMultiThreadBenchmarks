/*
Multithread math calculations benchmark utility. (C)2021 IC Book Labs.
-----------------------------------------------------------------------
Class for return detail info with array statistics values.
Note. Lower, higher and center median elements required for
selected element marking in the text report table.
*/

package javabench;

class StatisticEntry 
{
final double median, median1, median2, median3;
final double average, min, max;
final int medianIndex1, medianIndex2, medianIndex3;

StatisticEntry( double x1, double x2, double x3, double x4,
                double y1, double y2, double y3, 
                int z1, int z2, int z3 )
    {
    median = x1;        // calculated median
    median1 = x2;       // lower median element
    median2 = x3;       // higher median element
    median3 = x4;       // center median element
    average = y1;       // average
    min = y2;           // minimum
    max = y3;           // maximum
    medianIndex1 = z1;  // index of lower median element
    medianIndex2 = z2;  // index of higher median element
    medianIndex3 = z3;  // index of center median element
    }
}
