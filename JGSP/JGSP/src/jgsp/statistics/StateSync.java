/*
This class used for receive single operation ( Read, Write or Copy ) result.
*/

package jgsp.statistics;

public class StateSync 
{
public final int count;
public final StatusEntry statusEntry;
public final int phaseID;
public final String phaseName;
public final double current, min, max, average, median;

public StateSync
    ( int count, StatusEntry statusEntry, int phaseID, String phaseName, 
      double current, double min, double max, double average, double median )
    {
    this.count = count;
    this.statusEntry = statusEntry;
    this.phaseID = phaseID;
    this.current = current;
    this.min = min;
    this.max = max;
    this.average = average;
    this.median = median;
    this.phaseName = phaseName;
    }
}