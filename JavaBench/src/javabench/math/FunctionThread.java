/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Mathematics test pattern for single-thread and multi-thread modes.
 * Parent template class.
 *
 */

package javabench.math;

public abstract class FunctionThread 
{
public abstract void blankArray();
public abstract void tabulate();
public abstract void releaseArray();
public void stop() { }
}
