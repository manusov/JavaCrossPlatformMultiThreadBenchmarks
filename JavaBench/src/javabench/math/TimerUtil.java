/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Static class with timer library: interval start, interval end, get delta time.
 *
 */

package javabench.math;

public class TimerUtil 
{
private static long timer;

// Start timer,
// called before measured fragment
protected static void timerStart()
    {
    timer = System.nanoTime();
    }

// Stop timer, return delta time in microseconds as double,
// called after measured fragment
protected static double timerStop()
    {
    long t = System.nanoTime();
    double microseconds = t - timer;
    microseconds /= 1000.0;
    return microseconds;
    }

}
