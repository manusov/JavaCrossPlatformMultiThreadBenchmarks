/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Package for support functions Y=F(X) drawings.
 * Model interface for MVC (Model, View, Controller) pattern.
 *
 */

package javabench.drawings;

import java.math.BigDecimal;

public interface FunctionModelInterface 
{
public BigDecimal[][] getFunction();

public int[] getCurrentIndexes();
public int[] getMaximumIndexes();

public void startModel();
public void stopModel();

public String     getXname();
public String[]   getYnames();
public BigDecimal getXmin();
public BigDecimal getXmax();
public BigDecimal getXsmallUnits();
public BigDecimal getXbigUnits();
public BigDecimal getYmin();
public BigDecimal getYmax();
public BigDecimal getYsmallUnits();
public BigDecimal getYbigUnits();

public void rescaleXmax( int x );
public void rescaleYmax();
public void updateValue( BigDecimal[] x );

}
