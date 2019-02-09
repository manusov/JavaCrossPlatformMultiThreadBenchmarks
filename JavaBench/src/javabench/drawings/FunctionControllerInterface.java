/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Package for support functions Y=F(X) drawings.
 * Controller interface for MVC (Model, View, Controller) pattern.
 *
 */

package javabench.drawings;

public interface FunctionControllerInterface 
{
public FunctionModelInterface getModel();
public FunctionViewInterface getView();
// public FunctionControllerInterface getController();
public void startController();
public void stopController();
}
