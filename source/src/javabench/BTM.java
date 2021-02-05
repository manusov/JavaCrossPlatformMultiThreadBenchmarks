/*
Multithread math calculations benchmark utility. (C)2021 IC Book Labs.
-----------------------------------------------------------------------
BTM, Benchmark Table Model, used for results visual and report.
Unify data representation by AbstractTableModel.
*/

package javabench;

import javax.swing.table.AbstractTableModel;

class BTM extends AbstractTableModel
{
// table model class fields
private final static String[] COLUMNS_NAMES = 
    { "Value, MOPS" , "Actual" , "Minimum" , "Maximum" };
private static String[][] rowsValues =
    { { "Median, MT"   , "-" , "-" , "-" } ,
      { "ST"           , "-" , "-" , "-" } ,
      { "Ratio"        , "-" , "-" , "-" } ,
      { "Average, MT"  , "-" , "-" , "-" } ,
      { "ST"           , "-" , "-" , "-" } ,
      { "Ratio"        , "-" , "-" , "-" } };
// table model this application-specific public methods
String[] getColumnsNames()        { return COLUMNS_NAMES; }
String[][] getRowsValues()        { return rowsValues;    }
void setRowsValues(String[][] s)  { rowsValues = s;       }
// table model standard required public methods
@Override public int getColumnCount()    { return COLUMNS_NAMES.length; }
@Override public int getRowCount()       { return rowsValues.length; }
@Override public String getColumnName( int column )
    {
    if ( column < COLUMNS_NAMES.length )   
        return COLUMNS_NAMES[column];
    else
        return "?";
    }
@Override public String getValueAt( int row, int column )
    { 
    if ( ( row < rowsValues.length ) & ( column < COLUMNS_NAMES.length ) )
        return rowsValues[row][column];
    else
        return "";
    }
@Override public boolean isCellEditable( int row, int column )
    { return false; }
}
