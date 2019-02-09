/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Package for support functions Y=F(X) drawings.
 * Frame for benchmarks drawings as function Y=F(X).
 *
 */

package javabench.drawings;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class ActionDraw extends JFrame
{
private final JFrame parentFrame;
private final JPanel childPanel;
private final FunctionController controller;
private boolean childActive = false;

// frame class constructor, f = parent frame
public ActionDraw( JFrame f )
    {
    super( "JVM benchmarks drawings" );
    parentFrame = f;
    controller = new FunctionController();
    childPanel = controller.getView().getPanel();
    }

// get controller for access MVC pattern data
public FunctionController getController()
    {
    return controller;
    }

// this point for start drawings, checks if currently active
public void start()
    {
    if ( ! childActive )
        {
        childActive = true;
        add( childPanel );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new ChildWindowListener() );
        Point p = parentFrame.getLocation();
        p.x = p.x + 30;
        p.y = p.y + 30;
        this.setLocation( p );
        setSize( new Dimension ( 740, 540 ) );
        setVisible( true );
        setResizable( true );
        }
    }

// this point for stop drawings
private class ChildWindowListener extends WindowAdapter 
    {
    @Override public void windowClosing( WindowEvent e )
        {
        childActive = false;
        }
    }
    
}
