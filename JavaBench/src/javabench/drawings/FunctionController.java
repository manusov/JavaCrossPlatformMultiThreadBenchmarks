/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Package for support functions Y=F(X) drawings.
 * Function Y=F(X) drawing controller.
 *
 */

package javabench.drawings;

import java.util.Timer;
import java.util.TimerTask;

public class FunctionController implements FunctionControllerInterface
{
private final FunctionModelInterface model;
private final FunctionViewInterface view;

private final TimerTask task;
private final Timer timer;
    
public FunctionController()
    {
    model = new FunctionModel( this );
    view = new FunctionView( this );
    // controller = this;
    task = new VisualTimerTask();
    timer = new Timer ( true );  // true means daemon mode
    timer.scheduleAtFixedRate( task, 0, 50 );
    }
    
@Override public FunctionModelInterface getModel()
    {
    return model;
    }

@Override public FunctionViewInterface getView()
    {
    return view;
    }

@Override public void startController()
    {
    model.startModel();
    }

@Override public void stopController()
    {
    model.stopModel();
    }

private class VisualTimerTask extends TimerTask
    {
    @Override public void run()
        {
        view.getPanel().repaint();
        }
    }

}
