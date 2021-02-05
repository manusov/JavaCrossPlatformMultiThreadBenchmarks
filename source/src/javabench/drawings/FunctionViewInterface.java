/*
Multithread math calculations benchmark utility. (C)2019 IC Book Labs.
-----------------------------------------------------------------------
Package for support functions Y=F(X) drawings.
View interface for MVC (Model, View, Controller) pattern.
*/

package javabench.drawings;

import javax.swing.JPanel;

public interface FunctionViewInterface 
{
public JPanel getPanel();  // get drawings view panel with own paint method
}
