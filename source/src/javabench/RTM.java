/*
Multithread math calculations benchmark utility. (C)2021 IC Book Labs.
-----------------------------------------------------------------------
RTM, Report Table Model, used for options values and log add to report.
Unify data representation by AbstractTableModel.
*/

package javabench;

import javax.swing.table.AbstractTableModel;

class RTM extends AbstractTableModel
{
// table model class fields
private String[] columnsNames;
private String[][] rowsValues;
// table model this application-specific public methods
public void setColumnsNames( String[] s )  { columnsNames = s; }
public void setRowsValues( String[][] s )  { rowsValues = s; }
public String[] getColumnsNames()          { return columnsNames; }
public String[][] getRowsValues()          { return rowsValues; }
// table model standard required public methods
@Override public int getColumnCount()      { return columnsNames.length; }
@Override public int getRowCount()         { return rowsValues.length; }
@Override public String getColumnName( int column )
    { return columnsNames[column]; }
@Override public String getValueAt( int row, int column )
    { 
    if ( row < rowsValues.length ) 
         { return rowsValues[row][column]; }
    else { return ""; }
    }
}
