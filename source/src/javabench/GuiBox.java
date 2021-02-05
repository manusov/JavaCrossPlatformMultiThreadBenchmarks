/*
Multithread math calculations benchmark utility. (C)2021 IC Book Labs.
-----------------------------------------------------------------------
Application GUI panel with parameters getters and
buttons events listeners: Run, About, Report, Cancle, Draw.
Main window GUI elements supported here.
*/

package javabench;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javabench.drawings.ActionDraw;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

class GuiBox extends JFrame 
{
// sizes of GUI elements
private final static Dimension SIZE_WINDOW   = new Dimension ( 400, 390);
private final static Dimension SIZE_COMBO    = new Dimension ( 185, 21);
private final static Dimension SIZE_PROGRESS = new Dimension ( 189, 21);
private final static Dimension SIZE_BUTTON   = new Dimension ( 89, 24);
private final static Dimension SIZE_RUN      = new Dimension ( 78, 24);
private final static Dimension SIZE_LABEL    = new Dimension ( 80, 21);
// intervals between GUI elements
private final static int AX1 = 1;   // results table positioning geometry
private final static int AX2 = 0;
private final static int AY1 = 1;
private final static int AY2 = - SIZE_WINDOW.height / 2 - 28 - 15;
private final static int BX1 = 22;   // labels, combos, buttons positioning
private final static int BX2 = 1;
private final static int BX3 = 5;
private final static int BY1 = 26;
private final static int BY2 = 6;
private final static int CX1 = -3;   // 3 down buttons positioning
private final static int CX2 = -3;
private final static int CY1 = -3;
// data for test options
private final static int ARRAY_SIZE[] =
    { 1000000, 2000000, 3000000, 5000000 };
private final static int THREADS_COUNT[] = 
    { 1, 2, 4, 6, 8, 10, 50, 100, 500, 1000 };
private final static int REPEATS_COUNT[] = 
    { 5, 10, 20, 50, 100, 1000 };
private final static String MATH_OPERATION[] =
    { "Addition" , "Sqrt" , "Sine" };
private final static String OPERAND_SIZE[] =
    { "Double precision (double)" , "Single precision (float)" };
// arrays and threads parameters
private int arraySize, threadsCount, repeatsCount;
private final static int DEFAULT_AS = 0;         // default combo settings
private final static int DEFAULT_TC = 7;
private final static int DEFAULT_RC = 1;
private final static int DEFAULT_MO = 2;
private final static int DEFAULT_OZ = 0;
// GUI base components
private final SpringLayout sl;                   // panel and table
private final JPanel p;
private final JTable table;
// Tables models
private final BTM benchmarkTableModel;           // tables data models
private RTM optionsTableModel, logTableModel;
private RTM resultsTableModel = null;
// GUI components
private final JScrollPane sp;                            // GUI elements
private final DefaultTableCellRenderer tableRenderer;
private final JLabel l1, l2, l3, l4, l5, l6;
private final JComboBox c1, c2, c3, c4, c5;
private final JProgressBar pb;
private final DefaultBoundedRangeModel rangeModel;
private final JButton b1, b2, b3, b4, b5;
// Buttons listeners
private final LstRun    lst1;    // buttons events listeners
private final LstAbout  lst2;
private final LstReport lst3;
private final LstCancel lst4;
private final LstDraw   lst5;
// This JFrame reference
final Object thisFrame = this;  // frame for GUI elements positioning
// Classes with buttons handlers
private ActionRun    aRun;     // Run benchmark
private ActionAbout  aAbout;   // "About" window
private ActionReport aReport;  // Text report
// Arrays for text report
private boolean logValid = false;   // log statistics arrays
private double[][]  logValues;
private boolean[][] logTags;
// Strings for text report
private static String logHeader = null;
private final String logName =
    "\r\nDetail log.\r\n" +
    "All values in MOPS = Mega Operations per Second.\r\n" +
    "Tag notes: M = Median\r\n\r\n";
private final String[] logColumns = 
    { "Multi-Thread", "Tag  ", "Single-Thread", "Tag  ", "Ratio", "Tag  " };
private final String logV = "M", logN = " ";
// GUI and report parameters definitions
private static final int NUM_LOG = 3;        // 0=MT, 1=ST, 2=Ratio
private static final int NUM_OPTIONS = 5;    // Array Size, Threads Count
                                             // Repeats count, Operation,
                                             // Operand size
// Child frame, create it for performance action
private final ActionDraw childFrame;

GuiBox() 
    {
    super( About.getShortName() );
    sl = new SpringLayout();
    p = new JPanel(sl);
    benchmarkTableModel = new BTM();
    table = new JTable( benchmarkTableModel );
    sp = new JScrollPane( table );
    tableRenderer = new DefaultTableCellRenderer();
    tableRenderer.setHorizontalAlignment( SwingConstants.CENTER );
    for ( int i=0; i<table.getColumnCount(); i++ )
        { 
        table.getColumnModel().getColumn(i).setCellRenderer( tableRenderer );
        }
    l1 = new JLabel( "Array"     );
    l2 = new JLabel( "Threads"   );
    l3 = new JLabel( "Repeats"   );
    l4 = new JLabel( "Operation" );
    l5 = new JLabel( "Operand"   );
    l6 = new JLabel( "Run"       );
    l1.setPreferredSize( SIZE_LABEL );
    l2.setPreferredSize( SIZE_LABEL );
    l3.setPreferredSize( SIZE_LABEL );
    l4.setPreferredSize( SIZE_LABEL );
    l5.setPreferredSize( SIZE_LABEL );
    l6.setPreferredSize( SIZE_LABEL );
    c1 = new JComboBox();
    c2 = new JComboBox();
    c3 = new JComboBox();
    c4 = new JComboBox();
    c5 = new JComboBox();
    c1.setPreferredSize( SIZE_COMBO );
    c2.setPreferredSize( SIZE_COMBO );
    c3.setPreferredSize( SIZE_COMBO );
    c4.setPreferredSize( SIZE_COMBO );
    c5.setPreferredSize( SIZE_COMBO );
    
    for ( int i=0; i<ARRAY_SIZE.length; i++ )
        {
        c1.addItem( Integer.toString( ARRAY_SIZE[i] ) + " numbers" ); 
        }
    c1.setSelectedIndex( DEFAULT_AS );
    
    for ( int i=0; i<THREADS_COUNT.length; i++ )
        {
        c2.addItem( Integer.toString( THREADS_COUNT[i] ) ); 
        }
    c2.setSelectedIndex( DEFAULT_TC );
    
    for ( int i=0; i<REPEATS_COUNT.length; i++ )
        {
        c3.addItem( Integer.toString( REPEATS_COUNT[i] ) ); 
        }
    c3.setSelectedIndex( DEFAULT_RC );
    
    for (String MATH_OPERATION1 : MATH_OPERATION) 
        {
        c4.addItem( MATH_OPERATION1 );
        }
    c4.setSelectedIndex( DEFAULT_MO );

    for (String OPERAND_SIZE1 : OPERAND_SIZE) 
        {
        c5.addItem( OPERAND_SIZE1 );
        }
    c5.setSelectedIndex( DEFAULT_OZ );
    
    rangeModel = new DefaultBoundedRangeModel( 0, 0, 0, 100 );
    pb = new JProgressBar( rangeModel );
    pb.setPreferredSize( SIZE_PROGRESS );
    pb.setStringPainted( true );
    pb.setString( "Please run..." );
    b1 = new JButton( "Run"    );
    b2 = new JButton( "About"  );
    b3 = new JButton( "Report" );
    b4 = new JButton( "Cancel" );
    b5 = new JButton( "Draw"   );
    
    b1.setPreferredSize( SIZE_RUN );
    b2.setPreferredSize( SIZE_BUTTON );
    b3.setPreferredSize( SIZE_BUTTON );
    b4.setPreferredSize( SIZE_BUTTON );
    b5.setPreferredSize( SIZE_RUN );
    lst1 = new LstRun();
    lst2 = new LstAbout();
    lst3 = new LstReport();
    lst4 = new LstCancel();
    lst5 = new LstDraw();
    b1.addActionListener( lst1 );
    b2.addActionListener( lst2 );
    b3.addActionListener( lst3 );
    b4.addActionListener( lst4 );
    b5.addActionListener( lst5 );
    // start layout setup, results table
    sl.putConstraint ( SpringLayout.NORTH, sp, AY1, SpringLayout.NORTH, p );
    sl.putConstraint ( SpringLayout.SOUTH, sp, AY2, SpringLayout.SOUTH, p );
    sl.putConstraint ( SpringLayout.WEST,  sp, AX1, SpringLayout.WEST,  p );
    sl.putConstraint ( SpringLayout.EAST,  sp, AX2, SpringLayout.EAST,  p );
    // left labels
    sl.putConstraint ( SpringLayout.NORTH, l1,  BY1, SpringLayout.SOUTH, sp );
    sl.putConstraint ( SpringLayout.WEST,  l1,  BX1, SpringLayout.WEST,  p  );
    sl.putConstraint ( SpringLayout.NORTH, l2,  BY2, SpringLayout.SOUTH, l1 );
    sl.putConstraint ( SpringLayout.WEST,  l2,  BX1, SpringLayout.WEST,  p  );
    sl.putConstraint ( SpringLayout.NORTH, l3,  BY2, SpringLayout.SOUTH, l2 );
    sl.putConstraint ( SpringLayout.WEST,  l3,  BX1, SpringLayout.WEST,  p  );
    sl.putConstraint ( SpringLayout.NORTH, l4,  BY2, SpringLayout.SOUTH, l3 );
    sl.putConstraint ( SpringLayout.WEST,  l4,  BX1, SpringLayout.WEST,  p  );
    sl.putConstraint ( SpringLayout.NORTH, l5,  BY2, SpringLayout.SOUTH, l4 );
    sl.putConstraint ( SpringLayout.WEST,  l5,  BX1, SpringLayout.WEST,  p  );
    sl.putConstraint ( SpringLayout.NORTH, l6,  BY2, SpringLayout.SOUTH, l5 );
    sl.putConstraint ( SpringLayout.WEST,  l6,  BX1, SpringLayout.WEST,  p  );
    // combo boxes
    sl.putConstraint ( SpringLayout.NORTH, c1,  BY1, SpringLayout.SOUTH, sp );
    sl.putConstraint ( SpringLayout.WEST,  c1,  BX2, SpringLayout.EAST,  l1 );
    sl.putConstraint ( SpringLayout.NORTH, c2,  BY2, SpringLayout.SOUTH, l1 );
    sl.putConstraint ( SpringLayout.WEST,  c2,  BX2, SpringLayout.EAST,  l2 );
    sl.putConstraint ( SpringLayout.NORTH, c3,  BY2, SpringLayout.SOUTH, l2 );
    sl.putConstraint ( SpringLayout.WEST,  c3,  BX2, SpringLayout.EAST,  l3 );
    sl.putConstraint ( SpringLayout.NORTH, c4,  BY2, SpringLayout.SOUTH, l3 );
    sl.putConstraint ( SpringLayout.WEST,  c4,  BX2, SpringLayout.EAST,  l4 );
    sl.putConstraint ( SpringLayout.NORTH, c5,  BY2, SpringLayout.SOUTH, l4 );
    sl.putConstraint ( SpringLayout.WEST,  c5,  BX2, SpringLayout.EAST,  l5 );
    // progress indicator
    sl.putConstraint ( SpringLayout.NORTH, pb,  BY2, SpringLayout.SOUTH, l5 );
    sl.putConstraint ( SpringLayout.WEST,  pb,  BX2, SpringLayout.EAST,  l6 );
    // run button
    sl.putConstraint ( SpringLayout.NORTH, b1,    0, SpringLayout.NORTH, pb );
    sl.putConstraint ( SpringLayout.WEST,  b1,  BX3, SpringLayout.EAST,  pb );
    // 3 down buttons from left to right
    sl.putConstraint ( SpringLayout.SOUTH, b4,  CY1, SpringLayout.SOUTH, p  );
    sl.putConstraint ( SpringLayout.EAST,  b4,  CX1, SpringLayout.EAST,  p  );
    sl.putConstraint ( SpringLayout.SOUTH, b3,  CY1, SpringLayout.SOUTH, p  );
    sl.putConstraint ( SpringLayout.EAST,  b3,  CX2, SpringLayout.WEST,  b4 );
    sl.putConstraint ( SpringLayout.SOUTH, b2,  CY1, SpringLayout.SOUTH, p  );
    sl.putConstraint ( SpringLayout.EAST,  b2,  CX2, SpringLayout.WEST,  b3 );
    // draw button, located above run button
    sl.putConstraint ( SpringLayout.SOUTH, b5,   -3, SpringLayout.NORTH, b1 );
    sl.putConstraint ( SpringLayout.WEST,  b5,    0, SpringLayout.WEST,  b1 );
    // add all to panel
    p.add( sp );
    p.add( l1 );
    p.add( l2 );
    p.add( l3 );
    p.add( l4 );
    p.add( l5 );
    p.add( l6 );
    p.add( c1 );
    p.add( c2 );
    p.add( c3 );
    p.add( c4 );
    p.add( c5 );
    p.add( pb );
    p.add( b1 );
    p.add( b2 );
    p.add( b3 );
    p.add( b4 );
    p.add( b5 );
    // pre-initializing drawings window support
    childFrame = new ActionDraw( (JFrame)thisFrame );
    }
    
Dimension getApplicationDimension()
    {
    return SIZE_WINDOW;
    }
   
JPanel getApplicationPanel()
    {
    return p;
    }

// Buttons listeners, handlers for buttons press

// RUN button
private class LstRun implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        if ( !(ActionRun.getRunning()) )
            {
            int i = c1.getSelectedIndex();   // array size option
            int j = c2.getSelectedIndex();   // thread count option
            int k = c3.getSelectedIndex();   // repeats count option
            int m = c4.getSelectedIndex();   // math operation option
            int d = c5.getSelectedIndex();   // operand size
            arraySize  = ARRAY_SIZE[i];
            threadsCount = THREADS_COUNT[j];
            repeatsCount = REPEATS_COUNT[k];
            Object[] disabledComponents = new Object[8];
            disabledComponents[0] = b2;   // skip b1=run, for redefine to stop
            disabledComponents[1] = b3;
            disabledComponents[2] = b4;
            disabledComponents[3] = c1;
            disabledComponents[4] = c2;
            disabledComponents[5] = c3;
            disabledComponents[6] = c4;
            disabledComponents[7] = c5;
            // pre-initializing arrays for log data
            logValid = true;
            logValues = new double  [NUM_LOG][repeatsCount];
            logTags   = new boolean [NUM_LOG][repeatsCount];
            for ( int a=0; a<NUM_LOG; a++ )
                {
                for ( int b=0; b<repeatsCount; b++ )
                    {
                    logValues[a][b] = 0.0;
                    logTags[a][b] = false;
                    }
                }
            // run test
            aRun = new ActionRun
                ( benchmarkTableModel , rangeModel , pb , 
                  b1 , lst1 ,
                  disabledComponents , (JFrame)thisFrame ,
                  arraySize , threadsCount , repeatsCount , m , d ,
                  logValues , logTags ,
                  childFrame );
            aRun.start();
            }
        }
    }

// ABOUT button
private class LstAbout implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        aAbout = new ActionAbout();
        final JDialog dialog = aAbout.createDialog
            ( (JFrame)thisFrame ,
        About.getLongName() , About.getVendorName() );
        dialog.setLocationRelativeTo( null );
        dialog.setVisible( true );
        }
    }
            
// REPORT button 
private class LstReport implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        String[] s1;
        String[][] s2;
        int n, m;
        // prepare first table model for options
        // options names
        s1 = benchmarkTableModel.getColumnsNames();
        // options values
        n = benchmarkTableModel.getRowCount();
        m = benchmarkTableModel.getColumnCount();
        s2 = new String[n][m];
        for ( int i=0; i<n; i++ )
            {
            for ( int j=0; j<m; j++ )
                {
                s2[i][j] = benchmarkTableModel.getValueAt( i, j );
                }
            }
        // make table for report measurement results
        optionsTableModel = new RTM();
        optionsTableModel.setColumnsNames( s1 );
        optionsTableModel.setRowsValues( s2 );
        // prepare second table model for options
        s1 = new String[] { "Option" , "Value" };
        // options names
        s2 = new String[NUM_OPTIONS][2];
        s2[0][0] = l1.getText();
        s2[1][0] = l2.getText();
        s2[2][0] = l3.getText();
        s2[3][0] = l4.getText();
        s2[4][0] = l5.getText();
        // options values
        s2[0][1] = (String) c1.getSelectedItem();
        s2[1][1] = (String) c2.getSelectedItem();
        s2[2][1] = (String) c3.getSelectedItem();
        s2[3][1] = (String) c4.getSelectedItem();
        s2[4][1] = (String) c5.getSelectedItem();
        // make table for report options values
        logTableModel = new RTM();
        logTableModel.setColumnsNames( s1 );
        logTableModel.setRowsValues( s2 );
        // prepare second table model for results log
        resultsTableModel = null;
        logHeader = null;
        if ( logValid == true )
            {
            logHeader = logName;
            s1 = logColumns;
            n = logValues[0].length;
            m = logValues.length + logTags.length;
            s2 = new String[n][m];
            for ( int i=0; i<n; i++ )
                {
                s2[i][0] = String.format( "%.2f", logValues[0][i] );  // MT
                if ( logTags[0][i] ) 
                    { s2[i][1] = logV; } else { s2[i][1] = logN; }
                s2[i][2] = String.format( "%.2f", logValues[1][i] );  // ST
                if ( logTags[1][i] ) 
                    { s2[i][3] = logV; } else { s2[i][3] = logN; }
                s2[i][4] = String.format( "%.2f", logValues[2][i] );  // Ratio
                if ( logTags[2][i] ) 
                    { s2[i][5] = logV; } else { s2[i][5] = logN; }
                }
            // make table for report options values
            resultsTableModel = new RTM();
            resultsTableModel.setColumnsNames( s1 );
            resultsTableModel.setRowsValues( s2 );
            }
        // call report dialogue and report save
        aReport = new ActionReport();
        aReport.createDialogRT
            ( (JFrame)thisFrame , optionsTableModel , logTableModel ,
               About.getLongName() , About.getVendorName() , 
              logHeader , resultsTableModel );
        }
    }

// CANCEL button
private class LstCancel implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        System.exit( 0 );
        }
    }

// DRAW button
private class LstDraw implements ActionListener
    {
    @Override public void actionPerformed ( ActionEvent e )
        {
        childFrame.start();
        }
    }

}
