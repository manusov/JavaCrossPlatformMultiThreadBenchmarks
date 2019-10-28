/*
Multithread math calculations benchmark utility. (C)2019 IC Book Labs.
-----------------------------------------------------------------------
Mathematics test pattern for single-thread and multi-thread modes.
Parent template class.
*/

package javabench.math;

abstract class FunctionThread 
{
abstract void blankArray();
abstract void tabulate();
abstract void releaseArray();
void stop() { }
}
