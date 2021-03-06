/*
Multithread math calculations benchmark utility. (C)2021 IC Book Labs.
-----------------------------------------------------------------------
Handler for "Run" and "Stop" buttons. 
Measurement iterations executed and real time dynamical
visualization supported in this class.
Test execution supported at separate thread.
*/

package javabench;

import static java.lang.Thread.sleep;
import java.awt.event.*;
import java.math.BigDecimal;
import javabench.drawings.ActionDraw;
import javabench.drawings.FunctionControllerInterface;
import javabench.drawings.FunctionModelInterface;
import javabench.drawings.FunctionViewInterface;
import javax.swing.*;
import static javax.swing.JFrame.*;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javabench.math.MathScenario;

class ActionRun extends Thread 
{
private final MathScenario mathScenario;
private String[][] rowsValues;
private double percentage;
private final double weight;
private static boolean running, interrupted;

private final BTM tableModel;
private final DefaultBoundedRangeModel progressModel;
private final JProgressBar progressBar;

private final JButton runStopButton;
private String runStopName;
private final ActionListener runStopListener;
private ActionListener bstop;

private final Object[] userInterface;
private final JFrame benchFrame;

private final double[][] logValues;
private final boolean [][] logTags;

private final int indexLimit;
private boolean lastRequired;

private final ActionDraw childFrame;

ActionRun
    ( BTM tm , DefaultBoundedRangeModel dbrm , JProgressBar pb ,
      JButton rsb , ActionListener rsl ,
      Object[] ui ,
      JFrame fr ,
      int arraySize,
      int threadCount, 
      int externalRepeatCount, 
      int patternSelect ,
      int operandSize ,
      double[][] lv , boolean[][] lt ,
      ActionDraw ad )
    {
    // create target operation class
    mathScenario = new MathScenario
        ( arraySize, threadCount, externalRepeatCount, 
          patternSelect, operandSize );
    // setup variables by constructor input
    tableModel = tm;
    progressModel = dbrm;
    progressBar = pb;
    runStopButton = rsb;
    runStopListener = rsl;
    userInterface = ui;
    benchFrame = fr;
    percentage = 0.0;
    weight = 98.5 / ( externalRepeatCount * 2 );
    running = false;
    interrupted = false;
    logValues = lv;
    logTags = lt;
    
    indexLimit = externalRepeatCount - 1;
    lastRequired = true;
    
    childFrame = ad;
    }

// Get running status
static boolean getRunning() 
    {
    return running; 
    }
    
// Thread execution
@Override public void run()
    {
    lastRequired = true;    
    // create temporary arrays for results
    double[] array1 = null, array2 = null, array3 = null;
    int[][] medianIndexes = new int[3][3];
    // change run flag and button text from "Run" to "Stop"
    running = true;
    // this for benchmarks interruptable, setup context
    runStopName = runStopButton.getText();
    runStopButton.setText( "Stop" );
    bstop = new LstStop();
    runStopButton.removeActionListener( runStopListener );
    runStopButton.addActionListener( bstop );
    // end of prepare interruptable context
    // disable (make gray) some GUI objects during test
    for ( Object uitemp : userInterface ) 
        {
        ( (JComponent) uitemp ).setEnabled( false );
        ( (JComponent) uitemp ).repaint();
        ( (JComponent) uitemp ).revalidate();
        }
    // prevent application window close during test
    benchFrame.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
    // show progress indicator 0%
    progressModel.setValue( (int)percentage );
    progressBar.setString ( progressModel.getValue() + "%" );
    // initializing context for next dynamical updates
    rowsValues = tableModel.getRowsValues();
    // blank table
    int n = rowsValues.length;
    int m = rowsValues[0].length;
    for( int i=0; i<n; i++ )
        {
        for( int j=1; j<m; j++ )
            {
            rowsValues[i][j] = "-";
            }
        }
    // --- start calculations ---
    FunctionControllerInterface controller = childFrame.getController();
    FunctionModelInterface model = controller.getModel();
    FunctionViewInterface view = controller.getView();
    JPanel drawPanel = view.getPanel();
    // controller.startController();
    model.startModel();
    model.rescaleXmax( indexLimit + 1 );
    drawPanel.repaint();
    BigDecimal[] value = new BigDecimal[]
        { new BigDecimal(0), new BigDecimal(0), new BigDecimal(0) };
    // model.updateValue( value );
    mathScenario.start();
    int backIndex = -2;
    // start progress visual cycle
    while ( ( !mathScenario.getTaskDone() ) || lastRequired )
        {
        int index = mathScenario.getIndex();
        
        if ( ( index >= indexLimit ) || interrupted )
            {
            lastRequired = false;
            }
        if ( index >= 0 ) 
            {
            array1 = mathScenario.getMopsMultiThread();
            medianIndexes[0] = updateLinesStatistics( array1, index, 0 );
            array2 = mathScenario.getMopsSingleThread();
            medianIndexes[1] = updateLinesStatistics( array2, index, 1 );
            array3 = mathScenario.getMopsRatio();
            medianIndexes[2] = updateLinesStatistics( array3, index, 2 );
            }
        // revisual main frame of application
        percentage = weight * mathScenario.getCounter();
        progressModel.setValue( (int)percentage );
        progressBar.setString ( progressModel.getValue() + "%" );
        tableModel.setRowsValues( rowsValues );
        tableModel.fireTableDataChanged();
        // revisual benchmark drawings frame of application, Y=F(X)
        if ( ( index != backIndex ) && ( index >= 0 ) )
            {
            backIndex = index;
            double vs = mathScenario.getMopsSingleThread()[index] ;
            double vm = mathScenario.getMopsMultiThread()[index];
            value[0] = new BigDecimal( index );
            value[1] = new BigDecimal( vs );
            value[2] = new BigDecimal( vm );
            model.updateValue( value );       // send next point (X,Y)
            model.rescaleYmax();              // change scale if required
            drawPanel.repaint();              // synchronous revisual
            }
        // some wait reduce CPU/JVM utilization
        try 
            {
            sleep( 50 ); 
            } 
        catch ( InterruptedException e )
            {
            }
        }
    // controller.stopController();
    model.stopModel();
    // --- end calculations ---
     
    // write "skipped" if interrupted by user click "Stop"
    if( interrupted )
        {
        for( int i=0; i<n; i++ )
            {
            for( int j=1; j<m; j++ )
                {
                rowsValues[i][j] = "skipped";
                }
            }
        tableModel.setRowsValues( rowsValues );
        tableModel.fireTableDataChanged();
        }
    // end progress visual cycle
    
    percentage = 100.0;
    progressModel.setValue( (int)percentage );
    progressBar.setString ( progressModel.getValue() + "%" );
    // this for benchmarks interruptable
    runStopButton.setText( runStopName );
    runStopButton.removeActionListener( bstop );
    runStopButton.addActionListener( runStopListener );
    // end of prepare interruptable context
    // enable (make non-gray) some GUI objects during test
    for ( Object uitemp : userInterface ) 
        {
        ( (JComponent) uitemp ).setEnabled( true );
        ( (JComponent) uitemp ).repaint();
        ( (JComponent) uitemp ).revalidate();
        }
    // re-enable application window close after test
    benchFrame.setDefaultCloseOperation( EXIT_ON_CLOSE );
    // output results to log
    if ( array1 != null ) logValues[0] = array1;
    if ( array2 != null ) logValues[1] = array2;
    if ( array3 != null ) logValues[2] = array3;
    for( int i=0; i<3; i++ )
        {
        for( int j=0; j<3; j++ )
            {
            int k = medianIndexes[i][j];
            if ( k >= 0 ) logTags[i][k] = true;
            }
        }
    // end thread execution
    running = false;                                    // EXIT FROM CRITICAL 
    }

// Handler for "Stop" button, redefined from "Run" button when test in-progress
private class LstStop implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        mathScenario.setTaskInterrupt( true );
        interrupted = true;
        }
    }

// helper method for table update
private int[] updateLinesStatistics( double[] array, int index, int row )
    {
    // calculate statistics
    StatisticEntry entry = StatisticUtil.getStatistic( array, index+1 );
    // store statistics
    rowsValues[row][1]   = String.format( "%.2f", entry.median );
    rowsValues[row][2]   = String.format( "%.2f", entry.median1 );
    rowsValues[row][3]   = String.format( "%.2f", entry.median2 );
    rowsValues[row+3][1] = String.format( "%.2f", entry.average );
    rowsValues[row+3][2] = String.format( "%.2f", entry.min );
    rowsValues[row+3][3] = String.format( "%.2f", entry.max );
    return new int[] 
            { entry.medianIndex1, entry.medianIndex2, entry.medianIndex3 };
    }

}
