/*
Multithread math calculations benchmark utility. (C)2021 IC Book Labs.
-----------------------------------------------------------------------
Package for support functions Y=F(X) drawings.
Controller interface for MVC (Model, View, Controller) pattern.
*/

package javabench.drawings;

public interface FunctionControllerInterface 
{
public FunctionModelInterface getModel(); // get connected model = function
public FunctionViewInterface getView();   // get connected view = panel
}
