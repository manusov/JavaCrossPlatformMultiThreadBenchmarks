/*
Multithread math calculations benchmark utility. (C)2021 IC Book Labs.
-----------------------------------------------------------------------
Handler for "About" button.
Output application name and logo, support web site link button.
*/

package javabench;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

class ActionAbout 
{
private final Color LOGO_COLOR = new Color( 143, 49, 40 );
private final static Dimension SIZE_BUTTON_HTTP   = new Dimension ( 198, 25 );
private final static Dimension SIZE_BUTTON_CANCEL = new Dimension ( 75, 25 );

// Entry point for "About" dialogue method, setup GUI
JDialog createDialog
    ( JFrame parentWin , String longName , String vendorVersion )
    {
    final JDialog dialog = new JDialog( parentWin, "About", true );
    dialog.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    // Create GUI components
    SpringLayout sl1 = new SpringLayout();
    JPanel p1 = new JPanel( sl1 );
    // Start build content
    final String sHttp = About.getWebSite();
    String sCancel = "Cancel";
    JLabel l1 = new JLabel ();
    try { l1.setIcon(new javax.swing.ImageIcon( getClass().
           getResource( About.getVendorIcon() ))); } 
    catch ( Exception e ) { }
    JLabel l2 = new JLabel  ( About.getLongName()   );
    JLabel l3 = new JLabel  ( About.getVendorName() );
    l2.setForeground( LOGO_COLOR );
    l3.setForeground( LOGO_COLOR );
    Font font1 = new Font ( "Verdana", Font.PLAIN, 12 );  // font for main text
    l2.setFont( font1 );
    l3.setFont( font1 );
    JButton b1 = new JButton( sHttp );
    JButton b2 = new JButton( sCancel );
    // set buttons sizes
    b1.setPreferredSize( SIZE_BUTTON_HTTP );
    b2.setPreferredSize( SIZE_BUTTON_CANCEL );
    Font font2 = new Font ( "Verdana", Font.PLAIN, 11 );  // font for buttons
    b1.setFont( font2 );
    b2.setFont( font2 );
    // labels layout
    sl1.putConstraint ( SpringLayout.NORTH, l1,  24, SpringLayout.NORTH, p1 );
    sl1.putConstraint ( SpringLayout.WEST,  l1,  28, SpringLayout.WEST,  p1 );
    sl1.putConstraint ( SpringLayout.NORTH, l2,  24, SpringLayout.NORTH, p1 );
    sl1.putConstraint ( SpringLayout.WEST,  l2,   4, SpringLayout.EAST,  l1 );
    sl1.putConstraint ( SpringLayout.NORTH, l3,   0, SpringLayout.SOUTH, l2 );
    sl1.putConstraint ( SpringLayout.WEST,  l3,   4, SpringLayout.EAST,  l1 );
    // buttons layout
    sl1.putConstraint ( SpringLayout.SOUTH, b1, -10, SpringLayout.SOUTH, p1 );
    sl1.putConstraint ( SpringLayout.WEST,  b1,   8, SpringLayout.WEST,  p1 );
    sl1.putConstraint ( SpringLayout.SOUTH, b2, -10, SpringLayout.SOUTH, p1 );
    sl1.putConstraint ( SpringLayout.WEST,  b2,   3, SpringLayout.EAST,  b1 );
    // add labels and buttons to panel
    p1.add( l1 );
    p1.add( l2 );
    p1.add( l3 );
    p1.add( b1 );
    p1.add( b2 );
    // Action listener for web button
    b1.addActionListener( ( ActionEvent e ) -> 
        {
        if( Desktop.isDesktopSupported() )
        { 
        try 
            {
            Desktop.getDesktop().browse( new URI( sHttp ) );
            }
        catch ( URISyntaxException | IOException ex1 )
            { 
            System.out.println(ex1); 
            } }
        } );
    // Action listener for cancel button
    b2.addActionListener( ( ActionEvent e ) ->
        {
        dialog.dispose();
        } );
    // Visual window and return
    dialog.setContentPane( p1 );
    dialog.setSize( 300, 150 );
    dialog.setResizable( false );
    return dialog;  
    }
}
