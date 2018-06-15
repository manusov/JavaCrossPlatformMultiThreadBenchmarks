/*
 *
 * Multithread math calculations benchmark. (C)2018 IC Book Labs.
 * Scenario for GUI mode.
 *
 */

package javabench;

import javax.swing.JDialog;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class ScenarioGui 
{
public void runScenario()
    {
    JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);
    GuiBox application = new GuiBox();
    application.add(application.getApplicationPanel());
    application.setDefaultCloseOperation(EXIT_ON_CLOSE);
    application.setLocationRelativeTo(null);
    application.setSize( application.getApplicationDimension() );
    application.setVisible(true);
    application.setResizable(false);
    }
}
