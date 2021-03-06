package com.codegears.getable.util;

import java.math.BigDecimal;

public class RoundScaleNumber {
	
	public static double round(double unrounded, int precision, int roundingMode)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    
	    return rounded.doubleValue();
	}
	
}
