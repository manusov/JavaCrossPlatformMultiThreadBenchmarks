/*
Multithread math calculations benchmark utility. (C)2019 IC Book Labs.
-----------------------------------------------------------------------
Scenario for GUI mode.
This scenario is default, console mode is alternative, it run by:
" java -jar <name>.jar console ".
*/

package javabench;

import javax.swing.JDialog;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

class ScenarioGui 
{
void runScenario()
    {
    JFrame.setDefaultLookAndFeelDecorated( true );
    JDialog.setDefaultLookAndFeelDecorated( true );
    GuiBox application = new GuiBox();
    application.add( application.getApplicationPanel() );
    application.setDefaultCloseOperation( EXIT_ON_CLOSE );
    application.setLocationRelativeTo( null );
    application.setSize( application.getApplicationDimension() );
    application.setResizable( false );
    application.setVisible( true );
    }
}
