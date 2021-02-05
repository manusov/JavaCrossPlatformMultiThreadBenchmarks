/*
Multithread math calculations benchmark utility. (C)2019 IC Book Labs.
-----------------------------------------------------------------------
Package for support functions Y=F(X) drawings.
Function Y=F(X) drawing controller.
*/

package javabench.drawings;

public class FunctionController implements FunctionControllerInterface
{
private final FunctionModelInterface model;
private final FunctionViewInterface view;

public FunctionController()
    {
    model = new FunctionModel( this );
    view = new FunctionView( this );
    }

@Override public FunctionModelInterface getModel()
    {
    return model;
    }

@Override public FunctionViewInterface getView()
    {
    return view;
    }
}
