/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Package for support functions Y=F(X) drawings.
 * Model interface for MVC (Model, View, Controller) pattern.
 * Note. Start/Stop model is not same as Start/Stop benchmarking process.
 *
 */

package javabench.drawings;

import java.math.BigDecimal;

public interface FunctionModelInterface 
{
public BigDecimal[][] getFunction();  // get function array { x, y1, ,,, yn }

public int[] getCurrentIndexes();     // get current indexes per each y-array
public int[] getMaximumIndexes();     // get maximum indexes per each y-array

public void startModel();             // start model, reset defaults
public void stopModel();              // stop model, yet reserved, no actions

public String     getXname();          // get name for X axis units
public String[]   getYnames();         // get names array for Y, per functions
public BigDecimal getXmin();           // get minimum X
public BigDecimal getXmax();           // get maximum X
public BigDecimal getXsmallUnits();    // get small graduation for X axis
public BigDecimal getXbigUnits();      // get big graduation for X axis
public BigDecimal getYmin();           // get minimum Y
public BigDecimal getYmax();           // get maximum Y
public BigDecimal getYsmallUnits();    // get small graduation for Y axis
public BigDecimal getYbigUnits();      // get big graduation for Y axis

public void rescaleXmax( int x );        // set x-scale by input x
public void rescaleYmax();               // set y-scale automatically by values
public void updateValue( BigDecimal[] x );  // add new element to array

}
