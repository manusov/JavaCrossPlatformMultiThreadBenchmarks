/*
Multithread math calculations benchmark utility. (C)2021 IC Book Labs.
-----------------------------------------------------------------------
Class for return detail info about array median value.
Note. Lower, higher and center median elements required for
selected element marking in the text report table.
*/

package javabench;

class MedianEntry 
{
final double median, median1, median2, median3;
final int medianIndex1, medianIndex2, medianIndex3;

MedianEntry( double x1, double x2, double x3, double x4,
             int y1, int y2, int y3 )
    {
    median = x1;         // calculated median
    median1 = x2;        // lower median element
    median2 = x3;        // higher median element
    median3 = x4;        // center median element
    medianIndex1 = y1;   // index of lower median element
    medianIndex2 = y2;   // index of higher median element
    medianIndex3 = y3;   // index of center median element
    }
}
